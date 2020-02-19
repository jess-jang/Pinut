package com.mindlinksw.schoolmeals.bottomsheet;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.databinding.DetailBottomSheetBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;

public class DetailBottomSheet extends BottomSheetDialogFragment implements Initialize {

    public interface OnDetailBottomSheetListener {
        public void onDismiss(int code);
    }

    public @interface CODE {
        int DISMISS = 0;
        int MODIFY = 1;
        int DELETE = 2;
    }

    private DetailBottomSheetBinding mBinding;
    private ViewModel mViewModel;

    private OnDetailBottomSheetListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.detail_bottom_sheet, container, false);
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

    public void setOnDetailBottomSheetListener(OnDetailBottomSheetListener listener) {
        this.mListener = listener;
    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
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

        public void onClick(View v) {

            if (mListener != null) {
                switch (v.getId()) {
                    case R.id.tv_modify:
                        mListener.onDismiss(CODE.MODIFY);
                        break;

                    case R.id.tv_delete:
                        mListener.onDismiss(CODE.DELETE);
                        break;

                    case R.id.tv_dismiss:
                        mListener.onDismiss(CODE.DISMISS);
                        break;
                }
            }

            dismiss();
        }

    }
}
