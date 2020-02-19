package com.mindlinksw.schoolmeals.bottomsheet;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.databinding.VideoBottomSheetBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;

public class VideoBottomSheet extends BottomSheetDialogFragment implements Initialize {

    public interface OnVideoBottomSheetListener {
        public void onDismiss(String code);
    }

    public @interface CODE {
        String POPULARITY = "hot"; // 인기순
        String NEWEST = "new"; // 최신순
    }

    private VideoBottomSheetBinding mBinding;
    private ViewModel mViewModel;
    private OnVideoBottomSheetListener mListener;

    private String mSort = CODE.POPULARITY;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.video_bottom_sheet, container, false);
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

    public void setOnVideoBottomSheetListener(String sort, OnVideoBottomSheetListener listener) {
        this.mSort = sort;
        this.mListener = listener;
    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mViewModel.mSort.set(mSort); // 정렬값 세팅
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(View v) {

    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initEventListener() {

    }

    public class ViewModel {

        public ObservableField<String> mSort = new ObservableField<>();

        public void onClick(View v) {

            if (mListener != null) {
                switch (v.getId()) {
                    case R.id.tv_popularity:
                        mListener.onDismiss(CODE.POPULARITY);
                        break;

                    case R.id.tv_newest:
                        mListener.onDismiss(CODE.NEWEST);
                        break;
                }
            }

            dismiss();
        }

    }
}
