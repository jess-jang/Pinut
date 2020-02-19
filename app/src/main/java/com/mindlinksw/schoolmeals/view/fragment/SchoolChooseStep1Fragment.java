package com.mindlinksw.schoolmeals.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.SchoolChooseStep1Binding;
import com.mindlinksw.schoolmeals.databinding.SchoolChooseStep1ItemBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.SchoolSearchItem;
import com.mindlinksw.schoolmeals.model.SchoolSearchModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.utils.Utils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchoolChooseStep1Fragment extends Fragment implements Initialize {

    private String TAG = SchoolChooseStep1Fragment.class.getName();

    private SchoolChooseStep1Binding mBinding;
    public SchoolSearchViewModel mViewModel;
    private GenericRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private ArrayList<SchoolSearchItem> mList;

    private int mCurPageNo = 1; // 페이지 넘버
    private int mPageSize = 10; // 가져올 아이템 갯수
    private boolean mIsLoading = false;
    private boolean mIsLast = false; // 마지막 페이지 여부
    private boolean mLastItemVisibleFlag = false;

    private String mInsttCode;
    private String mInsttNm;

    private SchoolChooseStep2Fragment mSchoolChooseStep2Fragment;
    private ViewPager mViewPager;

    @SuppressLint("ValidFragment")
    public SchoolChooseStep1Fragment(ViewPager vpSchoolChoose, SchoolChooseStep2Fragment schoolChooseStep2Fragment) {
        mViewPager = vpSchoolChoose;
        mSchoolChooseStep2Fragment = schoolChooseStep2Fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.school_choose_step1, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initLayout(view);
        initVariable();
        initEventListener();

    }

    @Override
    public void onStop() {
        mViewModel.onRemoveDelay();
        super.onStop();
    }

    @Override
    public void initDataBinding() {
        mViewModel = new SchoolSearchViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        // list
        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(getActivity(), R.layout.school_choose_step1_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                SchoolChooseStep1ItemBinding binding = (SchoolChooseStep1ItemBinding) dataBinding;

                SchoolSearchViewModel viewModel = new SchoolSearchViewModel();
                viewModel.mModel.set((SchoolSearchItem) model);
                viewModel.mSearchText.set(mViewModel.mSearchText.get());
                viewModel.mPosition.set(position);

                binding.setViewModel(viewModel);

            }
        };

        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvSchool.setLayoutManager(mLayoutManager);
        mBinding.rvSchool.setAdapter(mAdapter);

    }

    @Override
    public void initVariable() {

        mViewModel.setConfirm();

        // 인식 딜레이 초기화
        mViewModel.onRecognizeDelay();

        // 최초 검색
        mViewModel.onRunDelay();

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
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.setKeyboardShowHide(getActivity(), mBinding.getRoot(), false);
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

    public void onFocus() {

    }

    /**
     * View Model
     */
    public class SchoolSearchViewModel {

        public ObservableField<SchoolSearchItem> mModel = new ObservableField<>();
        public ObservableField<String> mSearchText = new ObservableField<>();
        public ObservableInt mPosition = new ObservableInt();

        public ObservableBoolean mIsConfirm = new ObservableBoolean(); // 컨펌 버튼 여부
        public ObservableBoolean mIsSubmit = new ObservableBoolean();

        // Delay Handler - 특정시간 입력 이후 서버 데이터 전송
        private Runnable mDelayRunnable;
        private Handler mDelayHandler;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    getActivity().finish();
                    break;

                case R.id.tv_submit:

                    // 비회원 학교 등록
                    SharedPreferencesUtils.save(getActivity(), DataConst.NONMEMBER_SCHOOL_CODE, mInsttCode);
                    SharedPreferencesUtils.save(getActivity(), DataConst.NONMEMBER_SCHOOL_NAME, mInsttNm);

                    Snackbar.make(mBinding.getRoot(), getString(R.string.school_success), Snackbar.LENGTH_SHORT).show();

                    Intent intent = getActivity().getIntent();
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                    break;
            }
        }

        public void onClick(View v, int position, SchoolSearchItem model) {
            switch (v.getId()) {
                case R.id.ll_school:
                    // 선택된 값 색상 변경

                    boolean isSelf = false;

                    for (SchoolSearchItem item : mList) {

                        if (item.isChecked() && item.equals(model)) {
                            isSelf = true;
                        }

                        item.setChecked(false);
                    }

                    if (!isSelf) {

                        model.setChecked(true);

                        if (mViewModel.mIsConfirm.get()) {
                            mInsttCode = model.getInsttCode();
                            mInsttNm = model.getInsttNm();

                            mViewModel.mIsSubmit.set(true);
                        } else {
                            // 컨펌 버튼이 있을 경우 넘김
                            mSchoolChooseStep2Fragment.setSchoolData(model);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mViewPager.setCurrentItem(1, true);
                                }
                            }, 1000);
                        }
                    } else {
                        mViewModel.mIsSubmit.set(false);
                    }

                    mAdapter.notifyItemChanged(position);
                    break;
            }
        }

        /**
         * 확인버튼 여부
         */
        public void setConfirm() {
            mIsConfirm.set(!SessionSingleton.getInstance(getActivity()).isExist());
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

                // 옵저버 String 에 데이터 저장
                mSearchText.set(s.toString());

                onRemoveDelay();
                onRunDelay();

            } catch (Exception e) {
                e.printStackTrace();
            }
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

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(getActivity(), SchoolMealsService.class, HostConst.apiHost());
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

                        Logger.e(TAG, s);

                        if (response.isSuccessful()) {
                            SchoolSearchModel resData = (SchoolSearchModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());

                                if (!isScroll) {
                                    mList.clear();
                                }

                                Logger.e(TAG, "resData.getList().size() ; " + resData.getList().size());

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
                }

                @Override
                public void onFailure(Call<SchoolSearchModel> call, Throwable t) {
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
                        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
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


