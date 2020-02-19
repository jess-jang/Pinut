package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.SchoolChangeActivityBinding;
import com.mindlinksw.schoolmeals.databinding.SchoolChangeItemBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.model.SchoolSearchItem;
import com.mindlinksw.schoolmeals.model.SchoolSearchModel;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mindlinksw.schoolmeals.viewholder.DateMealsViewHolder;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchoolChangeActivity extends AppCompatActivity implements Initialize {

    private final String TAG = SchoolChangeActivity.class.getName();

    private SchoolChangeActivityBinding mBinding;
    private ViewModel mViewModel;
    private GenericRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<SchoolSearchItem> mList;

    private int mCurPageNo = 1; // 페이지 넘버
    private int mPageSize = 10; // 가져올 아이템 갯수
    private boolean mIsLoading = false;
    private boolean mIsLast = false; // 마지막 페이지 여부
    private boolean mLastItemVisibleFlag = false;
    private boolean mIsEnable = false; // 검색 가능한가?

    private String mInsttCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.school_change_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mViewModel.mGrade.set(1);

        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        mViewModel.getMember();

        // list
        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(SchoolChangeActivity.this, R.layout.school_change_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                SchoolChangeItemBinding binding = (SchoolChangeItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((SchoolSearchItem) model);
                viewModel.mSearchText.set(mViewModel.mSearchText.get());
                viewModel.mPosition.set(position);

                binding.setViewModel(viewModel);

            }
        };

        mLayoutManager = new LinearLayoutManager(SchoolChangeActivity.this);
        mBinding.rvSearch.setLayoutManager(mLayoutManager);
        mBinding.rvSearch.setAdapter(mAdapter);
    }

    @Override
    public void initVariable() {

        // 인식 딜레이 초기화
        mViewModel.onRecognizeDelay();

    }

    @Override
    public void initEventListener() {

        try {

            mBinding.etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            mBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        try {

                            if (v.getText().toString().trim().length() > 0) {

                                // 딜레이 해제
                                mViewModel.onRemoveDelay();
                                // 검색
                                mViewModel.search(false, v.getText().toString().trim());

                                // 키보드 내림
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.setKeyboardShowHide(SchoolChangeActivity.this, mBinding.getRoot(), false);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return true;
                    }

                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<SchoolSearchItem> mModel = new ObservableField<>();
        public ObservableField<String> mSearchText = new ObservableField<>();
        public ObservableBoolean mIsSubmit = new ObservableBoolean();
        public ObservableBoolean mIsExist = new ObservableBoolean(); // 리스트 여부
        public ObservableInt mPosition = new ObservableInt();
        public ObservableInt mGrade = new ObservableInt();

        private int mMaxGrade = 3;
        private Runnable mDelayRunnable;
        private Handler mDelayHandler;

        public void onClick(View v) {

            try {

                switch (v.getId()) {
                    case R.id.iv_back:
                        finish();
                        break;

                    case R.id.iv_clear:
                        // 지우고 포커싱
                        mBinding.etSearch.setText(null);
                        mBinding.etSearch.requestFocus();
                        Utils.setKeyboardShowHide(SchoolChangeActivity.this, mBinding.getRoot(), true);
                        break;

                    case R.id.iv_search:
                        // 강제 검색
                        onRemoveDelay();
                        onRunDelay();
                        break;

                    case R.id.tv_submit:
                        // 학교 정보 수정
                        DateMealsViewHolder.isRefresh = true;
                        setData();
                        break;

                    case R.id.iv_minus:
                        // 학년 -
                        if (mGrade.get() > 1) {
                            mGrade.set(mGrade.get() - 1);
                        }
                        mIsSubmit.set(true);
                        break;

                    case R.id.iv_plus:
                        // 학년 +
                        if (mGrade.get() < mMaxGrade) {
                            mGrade.set(mGrade.get() + 1);
                        }
                        mIsSubmit.set(true);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onClick(View v, SchoolSearchItem model) {

            try {

                switch (v.getId()) {
                    case R.id.ll_choose:
                        // 학교 선택
                        mList.clear();
                        mAdapter.notifyDataSetChanged();

                        // 학교 정보 세팅
                        mIsEnable = false;
                        mViewModel.mSearchText.set(model.getInsttNm());
                        mBinding.etSearch.clearFocus(); // 포커스 해제

                        // 변경할 학교 코드 세팅
                        mInsttCode = model.getInsttCode();
                        mViewModel.mMaxGrade = model.getMaxGrade(); // 최대 학년 세팅

                        // 현재 학년이 mMaxGrade 보다 클경우 mMaxGrade 로 세팅
                        if (mViewModel.mGrade.get() > mMaxGrade) {
                            mViewModel.mGrade.set(mMaxGrade);
                        }

                        mViewModel.mIsSubmit.set(true);

                        // 키보드 내림
                        Utils.setKeyboardShowHide(SchoolChangeActivity.this, mBinding.getRoot(), false);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 딜레이
         */
        public void onRecognizeDelay() {
            mDelayHandler = new Handler();
            mDelayRunnable = new Runnable() {
                @Override
                public void run() {
                    // 자동완성 데이터 통신
                    search(false, mBinding.etSearch.getText().toString());
                }
            };
        }

        /**
         * 딜레이 제거
         */
        public void onRemoveDelay() {
            // Handler Remove
            if (mDelayHandler != null) {
                mDelayHandler.removeCallbacks(mDelayRunnable);
            }
        }

        /**
         * 딜레이 On
         */
        public void onRunDelay() {
            // Handler Remove
            if (mDelayHandler != null) {
                mDelayHandler.postDelayed(mDelayRunnable, 150);
            }
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {

                // onCreate 검색 방지
                if (mIsEnable) {

                    // 옵저버 String 에 데이터 저장
                    mSearchText.set(s.toString());

                    onRemoveDelay();
                    onRunDelay();

                } else {
                    mIsExist.set(false);
                }

                mIsEnable = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void getMember() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(SchoolChangeActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<SessionModel> call = service.reqMember();

            RetrofitCall.enqueueWithRetry(call, new Callback<SessionModel>() {

                @Override
                public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            SessionModel resData = (SessionModel) response.body();
                            if (resData != null) {

                                // 학교정보
                                mInsttCode = resData.getInsttCode();
                                mSearchText.set(resData.getInsttNm());

                                // 학년정보
                                mGrade.set(resData.getYearly());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<SessionModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * @param isScroll 스크롤 여부
         *                 true : arrayList 초기화 x
         *                 false : arrayList 초기화 o
         * @param s
         */
        public void search(final boolean isScroll, final String s) {

            // 스크롤 하지 않을 경우 text 세팅
            if (!isScroll) {
                mCurPageNo = 1; // 현재 페이지 초기화
                mSearchText.set(s);
            }

            // 딜레이 해제
            onRemoveDelay();

            // 전체 검색 안되게 막음
            if (ObjectUtils.isEmpty(s)) {
                setListExist();
                return;
            }

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(SchoolChangeActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<SchoolSearchModel> call = service.reqSchoolNameSpecific(
                    s,
                    null,
                    null,
                    null,
                    mCurPageNo,
                    mPageSize,
                    mPageSize // Default
            );

            RetrofitCall.enqueueWithRetry(call, new Callback<SchoolSearchModel>() {

                @Override
                public void onResponse(Call<SchoolSearchModel> call, Response<SchoolSearchModel> response) {

                    try {

                        // 전체 검색 안되게 막음
                        if (ObjectUtils.isEmpty(s)) {
                            setListExist();
                            return;
                        }

                        Logger.e(TAG, s);

                        if (response.isSuccessful()) {
                            SchoolSearchModel resData = (SchoolSearchModel) response.body();

                            if (resData != null) {

                                if (!isScroll) {
                                    mList.clear();
                                }

                                mList.addAll(resData.getList());

                                if (resData.getCurPage() == resData.getTotalPage()) {
                                    mIsLast = true;
                                } else {
                                    mIsLast = false;
                                }

                                mCurPageNo++;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mAdapter.notifyDataSetChanged();

                    mIsExist.set(mList.size() > 0);
                }

                @Override
                public void onFailure(Call<SchoolSearchModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        private void setListExist() {
            mList.clear();
            mAdapter.notifyDataSetChanged();
            mIsExist.set(mList.size() > 0);
        }

        /**
         * 학교, 학년 변경
         */
        public void setData() {

            final SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(SchoolChangeActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqUpdateSchool(String.valueOf(mInsttCode), String.valueOf(mGrade.get()));

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {
                                if ("success".equals(resData.getCode())) {

                                    Snackbar.make(mBinding.getRoot(), getString(R.string.school_change_success), Snackbar.LENGTH_SHORT).show();

                                    // 학교 코드 삽입
                                    SessionModel session = SessionSingleton.getInstance(SchoolChangeActivity.this).select();
                                    session.setInsttCode(mInsttCode);
                                    session.setInsttNm(mSearchText.get());
                                    Logger.e(TAG, "mInsttCode : " + mInsttCode);

                                    SessionSingleton.getInstance(SchoolChangeActivity.this).insert(session);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = getIntent();
                                            setResult(Activity.RESULT_OK, intent);
                                            finish();
                                        }
                                    }, Snackbar.LENGTH_SHORT + 300);

                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * RecyclerView.OnScrollListener
         */
        public RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastItemVisibleFlag) {
                    if (!mIsLast) {
                        mViewModel.search(true, mViewModel.mSearchText.get());
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (!mIsLoading) {

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (dy > 0) {
                        // 현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                        mLastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                    } else {
                        // 전체 카운트랑 화면에 보여지는 카운트랑 같을때
                        mLastItemVisibleFlag = visibleItemCount == totalItemCount;
                    }

                }


            }

        };

    }
}
