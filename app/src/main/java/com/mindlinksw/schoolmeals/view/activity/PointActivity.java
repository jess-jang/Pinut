package com.mindlinksw.schoolmeals.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.PointActivityBinding;
import com.mindlinksw.schoolmeals.databinding.PointItemBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.PointItem;
import com.mindlinksw.schoolmeals.model.PointModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointActivity extends AppCompatActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = PointActivity.class.getName();

    private PointActivityBinding mBinding;
    private GenericRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<PointItem> mList = new ArrayList<>();
    private ViewModel mViewModel;

    private int mCurPageNo = 1; // 페이지 넘버
    private int mPageSize = 30; // 가져올 아이템 갯수
    private boolean mIsLoading = false;
    private boolean mIsLast = false; // 마지막 페이지 여부
    private boolean mLastItemVisibleFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.point_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();

        onRefresh();

    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        mBinding.refresh.setOnRefreshListener(this);

        mAdapter = new GenericRecyclerViewAdapter(this, R.layout.point_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                PointItemBinding binding = (PointItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((PointItem) model);

                binding.setViewModel(viewModel);

            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        mBinding.rvPoint.setLayoutManager(mLayoutManager);
        mBinding.rvPoint.setAdapter(mAdapter);
    }

    @Override
    public void initVariable() {

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

        public ObservableField<PointItem> mModel = new ObservableField<>();
        public ObservableBoolean mIsExist = new ObservableBoolean();
        public ObservableInt mUserPoint = new ObservableInt();

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
            }
        }

        /**
         * 알림 조회
         */
        public void getData() {

            try {

                mBinding.refresh.setRefreshing(true);

                SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(PointActivity.this, SchoolMealsService.class, HostConst.apiHost());
                Call<PointModel> call = service.reqPointList(
                        mCurPageNo,
                        mPageSize,
                        mPageSize);

                RetrofitCall.enqueueWithRetry(call, new Callback<PointModel>() {

                    @Override
                    public void onResponse(Call<PointModel> call, Response<PointModel> response) {

                        try {

                            if (response.isSuccessful()) {
                                PointModel resData = (PointModel) response.body();

                                if (resData != null) {

                                    Logger.e(TAG, resData.toString());

                                    mUserPoint.set(resData.getUserPoint());

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
                        Logger.e(TAG, "mIsExist.get() : " + mIsExist.get());

                        mBinding.refresh.setRefreshing(false);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(Call<PointModel> call, Throwable t) {
                        Logger.e(TAG, t.getMessage());
                        mBinding.refresh.setRefreshing(false);
                        mIsExist.set(false);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
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
