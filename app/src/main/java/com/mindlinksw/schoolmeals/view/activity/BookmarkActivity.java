package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.ad.AdlibConstants;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.CategoryConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.BookmarkActivityBinding;
import com.mindlinksw.schoolmeals.databinding.BookmarkItemBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.BoardItem;
import com.mindlinksw.schoolmeals.model.BookmarkItem;
import com.mindlinksw.schoolmeals.model.BookmarkModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mocoplex.adlib.AdlibAdViewContainer;
import com.mocoplex.adlib.AdlibManager;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkActivity extends AppCompatActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = BookmarkActivity.class.getName();

    private BookmarkActivityBinding mBinding;
    private GenericRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<BookmarkItem> mList;
    private ViewModel mViewModel;

    private int mCurPageNo = 1; // 페이지 넘버
    private int mPageSize = 30; // 가져올 아이템 갯수
    private boolean mIsLoading = false;
    private boolean mIsLast = false; // 마지막 페이지 여부
    private boolean mLastItemVisibleFlag = false;
    // 광고
//    private AdlibManager mAdlibManager;
//    AdlibAdViewContainer adv;
//    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.bookmark_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mAdlibManager.onResume(this);
        onRefresh();
    }

    @Override
    protected void onPause() {
//        mAdlibManager.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        mAdlibManager.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void finish() {

        try {
            setResult(Activity.RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.finish();

    }


    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

//        adv = (AdlibAdViewContainer) findViewById(R.id.ads);
//        rv = (RecyclerView) findViewById(R.id.rv_notification);

        mBinding.refresh.setOnRefreshListener(this);

        // list
        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(this, R.layout.bookmark_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                BookmarkItemBinding binding = (BookmarkItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((BookmarkItem) model);
                viewModel.mPosition.set(position);

                binding.setViewModel(viewModel);

            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        mBinding.rvNotification.setLayoutManager(mLayoutManager);
        mBinding.rvNotification.setAdapter(mAdapter);
    }

    @Override
    public void initVariable() {
        // 각 애드립 액티비티에 애드립 앱 키값을 필수로 넣어주어야 합니다.
//        mAdlibManager = new AdlibManager(AdlibConstants.ADLIB_API_KEY);
//        mAdlibManager.onCreate(this);

        // 테스트 광고 노출
//        mAdlibManager.setAdlibTestMode(AdlibConstants.ADLIB_TEST_MODE);

        // 배너 스케쥴에 등록된 광고 모두 광고 요청 실패 시 대기 시간 설정(단위:초, 기본:10초, 최소:1초)
//        mAdlibManager.setBannerFailDelayTime(1);

// 이벤트 핸들러 등록
//        mAdlibManager.setAdsHandler(new Handler() {
//            public void handleMessage(Message message) {
//                try {
//                    switch (message.what) {
//                        case AdlibManager.DID_SUCCEED:
//                            adv.setVisibility(View.VISIBLE);
//                            rv.setClipToPadding(false);
//                            int dpValue = 50;
//                            float d = getResources().getDisplayMetrics().density;
//                            int margin = (int) (dpValue * d);
//                            rv.setPadding(0, 0, 0, margin);
//                            Log.d("ADLIBr", "[Banner] onReceiveAd " + (String) message.obj);
//                            break;
//                        case AdlibManager.DID_ERROR:
//                            adv.setVisibility(View.GONE);
//                            rv.setClipToPadding(false);
//                            rv.setPadding(0, 0, 0, 0);
//                            Log.d("ADLIBr", "[Banner] onFailedToReceiveAd " + (String) message.obj);
//                            break;
//                        case AdlibManager.BANNER_FAILED:
//                            adv.setVisibility(View.GONE);
//                            rv.setClipToPadding(false);
//                            rv.setPadding(0, 0, 0, 0);
//                            Log.d("ADLIBr", "[Banner] All Failed.");
//                            break;
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        });


        // 실제 광고 호출이 될 adview 를 연결합니다.
//        mAdlibManager.setAdsContainer(R.id.ads);

    }

    @Override
    public void initEventListener() {

    }

    @Override
    public void onRefresh() {

        mCurPageNo = 1;
        mList.clear();

        mViewModel.getData();

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<BookmarkItem> mModel = new ObservableField<>();
        public ObservableBoolean mIsExist = new ObservableBoolean();
        public ObservableInt mPosition = new ObservableInt();

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_all_delete:
                    new AlertDialog(BookmarkActivity.this)
                            .setMessage(getString(R.string.bookmark_all_delete_confirm))
                            .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {
                                    deleteAllBookmark();
                                }
                            })
                            .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {

                                }
                            })
                            .show();
                    break;
            }
        }

        public void onClick(View v, BookmarkItem model) {

            try {

                switch (v.getId()) {
                    case R.id.ll_bookmark:

                        Intent intent = null;

                        BoardItem item = new BoardItem();
                        item.setBoardId(model.getBoardId());

                        if (model.getCtgryCode().equals(CategoryConst.BOARD)) {
                            // 자유게시판
                            intent = new Intent(BookmarkActivity.this, DetailActivity.class);
                        } else if (model.getCtgryCode().equals(CategoryConst.VIDEO)) {
                            // 비디오
                            intent = new Intent(BookmarkActivity.this, VideoDetailActivity.class);
                        }

                        intent.putExtra(BoardItem.class.getName(), item);
                        startActivity(intent);

                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onClick(View v, final int position, final BookmarkItem model) {
            switch (v.getId()) {
                case R.id.iv_delete:
                    new AlertDialog(BookmarkActivity.this)
                            .setMessage(getString(R.string.bookmark_delete_confirm))
                            .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {
                                    deleteBookmark(position, model.getBoardId());
                                }
                            })
                            .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {

                                }
                            })
                            .show();
                    break;
            }
        }

        /**
         * 북마크 조회
         */
        public void getData() {

            mBinding.refresh.setRefreshing(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(BookmarkActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BookmarkModel> call = service.reqBookmarkList(
                    mCurPageNo,
                    mPageSize,
                    mPageSize);

            RetrofitCall.enqueueWithRetry(call, new Callback<BookmarkModel>() {

                @Override
                public void onResponse(Call<BookmarkModel> call, Response<BookmarkModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            BookmarkModel resData = (BookmarkModel) response.body();

                            if (resData != null) {

                                Logger.e(TAG, resData.toString());

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

                    mIsExist.set(mList.size() > 0);

                    mBinding.refresh.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<BookmarkModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    mBinding.refresh.setRefreshing(false);
                    mIsExist.set(false);
                }
            });
        }

        /**
         * 북마크 삭제
         */
        public void deleteBookmark(final int position, int boardId) {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(BookmarkActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqDeleteBookmark(boardId);

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {

                                Logger.e(TAG, resData.toString());

                                if ("success".equals(resData.getCode())) {

                                    mAdapter.notifyItemRemoved(position);
                                    mList.remove(position);

                                    mViewModel.mIsExist.set(mList.size() > 0);

                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e(TAG, e.getMessage());
                    }

                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 전체 북마크 삭제
         */
        public void deleteAllBookmark() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(BookmarkActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqDeleteAllBookmark();

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {
                                if ("success".equals(resData.getCode())) {

                                    mList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    mViewModel.mIsExist.set(mList.size() > 0);

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
                        mViewModel.getData();
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
