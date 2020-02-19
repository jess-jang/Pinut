package com.mindlinksw.schoolmeals.view.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.databinding.NoticeDetailFragmentBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.NoticeItem;
import com.mindlinksw.schoolmeals.utils.FragmentUtils;

import org.jetbrains.annotations.Nullable;

public class NoticeDetailFragment extends Fragment implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private String TAG = NoticeDetailFragment.class.getName();

    private NoticeDetailFragmentBinding mBinding;
    public ViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.notice_detail_fragment, container, false);
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
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {
        mBinding.webView.initView(getActivity());
    }

    @Override
    public void initVariable() {
        if (getArguments() != null) {
            NoticeItem model = (NoticeItem) getArguments().getSerializable(NoticeItem.class.getName());
            mViewModel.mModel.set(model);
            mBinding.webView.initView(getActivity()).loadUrl(model.getWebLinkUrl());
        } else {
            FragmentUtils.removeFragment(getFragmentManager());
        }
    }

    @Override
    public void initEventListener() {    }

    @Override
    public void onRefresh() {
        mBinding.webView.reload();
    }

    /**
     * View Model
     */
    public class ViewModel {
        public ObservableField<NoticeItem> mModel = new ObservableField<>();
    }
}