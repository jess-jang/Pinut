package com.mindlinksw.schoolmeals.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.content.Intent;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.NoticeActivityBinding;
import com.mindlinksw.schoolmeals.databinding.NoticeItemBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.NoticeItem;
import com.mindlinksw.schoolmeals.model.NoticeModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.FragmentUtils;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.view.fragment.NoticeDetailFragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = NoticeActivity.class.getName();

    private NoticeActivityBinding mBinding;
    private ViewModel mViewModel;
    private GenericRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<NoticeItem> mList;

    private int mCurPageNo = 1; // 페이지 넘버
    private int mPageSize = 100; // 가져올 아이템 갯수
    private boolean mIsLoading = false;
    private boolean mIsLast = false; // 마지막 페이지 여부
    private boolean mLastItemVisibleFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.notice_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
        initIntent();

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

        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(NoticeActivity.this, R.layout.notice_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                NoticeItemBinding binding = (NoticeItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((NoticeItem) model);
                binding.setViewModel(viewModel);

            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        mBinding.rvNotice.setLayoutManager(mLayoutManager);
        mBinding.rvNotice.setAdapter(mAdapter);
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
     * Intent
     */
    private void initIntent() {
        try {

            Intent intent = getIntent();
            if (intent != null) {
                NoticeItem item = (NoticeItem) intent.getSerializableExtra(NoticeItem.class.getName());

                Bundle bundle = new Bundle();
                bundle.putSerializable(NoticeItem.class.getName(), item);

                NoticeDetailFragment fragment = new NoticeDetailFragment();
                fragment.setArguments(bundle);

                FragmentUtils.replaceFragment(getSupportFragmentManager(), R.id.main_container, fragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<NoticeItem> mModel = new ObservableField<>();

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
            }
        }

        public void onClick(View v, NoticeItem model) {
            try {
                switch (v.getId()) {
                    case R.id.ll_notice:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(NoticeItem.class.getName(), model);

                        NoticeDetailFragment fragment = new NoticeDetailFragment();
                        fragment.setArguments(bundle);

                        FragmentUtils.replaceFragment(getSupportFragmentManager(), R.id.main_container, fragment);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 알림 조회
         */
        public void getData() {

            mIsLoading = true;
            mBinding.refresh.setRefreshing(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(NoticeActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<NoticeModel> call = service.reqNotice(
                    mCurPageNo,
                    mPageSize,
                    mPageSize);

            RetrofitCall.enqueueWithRetry(call, new Callback<NoticeModel>() {

                @Override
                public void onResponse(Call<NoticeModel> call, Response<NoticeModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            NoticeModel resData = (NoticeModel) response.body();

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

                    mIsLoading = false;
                    mBinding.refresh.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<NoticeModel> call, Throwable t) {
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
