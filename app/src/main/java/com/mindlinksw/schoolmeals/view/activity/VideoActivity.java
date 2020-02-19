package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.VideoAdapter;
import com.mindlinksw.schoolmeals.consts.CategoryConst;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.databinding.VideoActivityBinding;
import com.mindlinksw.schoolmeals.bottomsheet.DetailBottomSheet;
import com.mindlinksw.schoolmeals.bottomsheet.VideoBottomSheet;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.manager.SchemeManager;
import com.mindlinksw.schoolmeals.model.BoardItem;
import com.mindlinksw.schoolmeals.model.BoardModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = VideoActivity.class.getName();

    public VideoActivityBinding mBinding;
    public ViewModel mViewModel;

    private VideoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    private ArrayList<BoardItem> mList = new ArrayList<>();
    private int mCurPageNo = 0; // 페이지 넘버
    private int mPageSize = 10; // 가져올 아이템 갯수
    private boolean mIsLoading = false;
    private boolean mIsLast = false; // 마지막 페이지 여부
    private boolean mLastItemVisibleFlag = false;

    private String mShareText; // 공유받은 텍스트
    private String mSort = VideoBottomSheet.CODE.NEWEST; // 기본값 인기순 정렬

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.video_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
        initIntent();

        onRefresh();

    }

    @Override
    public void onRefresh() {
        // 데이터 초기화
        mCurPageNo = 1;
        mViewModel.getData(true);
    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {
        mBinding.rvVideo.setLayoutManager(mLayoutManager);
        mBinding.refresh.setOnRefreshListener(this);
    }

    @Override
    public void initVariable() {
        mAdapter = new VideoAdapter(this, mList, getSupportFragmentManager());
        mBinding.rvVideo.setAdapter(mAdapter);
    }

    @Override
    public void initEventListener() {

    }

    private void initIntent() {

        try {

            Intent intent = getIntent();
            setIntent(intent);
            // 스킴 실행
            SchemeManager.Companion.run(this, intent);

            // 공유 받은 텍스트
            mShareText = intent.getStringExtra(Intent.EXTRA_TEXT);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            Intent intent;

            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            switch (requestCode) {
                case RequestConst.INTENT_WRITE:
                    // 새로고침
                    onRefresh();
                    break;

                case RequestConst.INTENT_DETAIL:
                    if (data != null) {
                        String action = data.getAction();
                        BoardItem boardModel = (BoardItem) data.getSerializableExtra(BoardItem.class.getName());

                        if (ObjectUtils.isEmpty(boardModel)) {
                            return;
                        }

                        int position = boardModel.getPosition();
                        if (position > -1) {
                            if (action.equals(String.valueOf(RequestConst.INTENT_DETAIL_DELETE))) {
                                // 삭제
                                mList.remove(position);
                                mAdapter.notifyDataSetChanged();
                            } else if (action.equals(String.valueOf(RequestConst.INTENT_DETAIL_UPDATE))) {
                                // 아이템 업데이트
                                mList.set(position, boardModel);
                                mAdapter.notifyItemChanged(position);
                            }
                        }
                    }
                    break;

                case RequestConst.LOGIN_RESULT_WRITE: {
                    // 로그인 후 글 작성
                    intent = new Intent(VideoActivity.this, VideoWriteActivity.class);
                    intent.putExtra(DataConst.TYPE, RequestConst.INTENT_WRITE);
                    intent.putExtra(Intent.EXTRA_TEXT, mShareText);
                    startActivityForResult(intent, RequestConst.INTENT_WRITE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * View Model
     */
    public class ViewModel {

        Intent intent = null;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.iv_upload:
                    if (SessionSingleton.getInstance(VideoActivity.this).isExist()) {
                        intent = new Intent(VideoActivity.this, VideoWriteActivity.class);
                        intent.putExtra(DataConst.TYPE, RequestConst.INTENT_WRITE);
                        startActivityForResult(intent, RequestConst.INTENT_WRITE);
                    } else {
                        startActivityForResult(new Intent(VideoActivity.this, LoginActivity.class), RequestConst.LOGIN_RESULT_WRITE);
                    }
                    break;

                case R.id.iv_more:
                    // 정렬
                    VideoBottomSheet sheet = new VideoBottomSheet();
                    sheet.setOnVideoBottomSheetListener(mSort, new VideoBottomSheet.OnVideoBottomSheetListener() {

                        @Override
                        public void onDismiss(String code) {
                            mSort = code;
                            onRefresh();
                        }
                    });
                    sheet.show(getSupportFragmentManager(), DetailBottomSheet.class.getName());
                    break;

            }
        }

        public void getData(final boolean isRefresh) {

            mBinding.refresh.setRefreshing(true);
            mIsLoading = true;

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(VideoActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardModel> call = service.reqVideoList(
                    CategoryConst.VIDEO,
                    mCurPageNo,
                    mPageSize,
                    mPageSize,
                    mSort);

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardModel>() {

                @Override
                public void onResponse(Call<BoardModel> call, Response<BoardModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            if (isRefresh) {
                                mList.clear();
                            }

                            BoardModel resData = (BoardModel) response.body();

                            if (resData != null) {

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

                    mBinding.refresh.setRefreshing(false);
                    mIsLoading = false;

                }

                @Override
                public void onFailure(Call<BoardModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    mIsLoading = false;
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
                    if (!mIsLoading && !mIsLast) {
                        getData(false);
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
