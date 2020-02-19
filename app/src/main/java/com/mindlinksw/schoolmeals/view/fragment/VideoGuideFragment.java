package com.mindlinksw.schoolmeals.view.fragment;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.databinding.VideoGuideFragmentBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.utils.FragmentUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.utils.Utils;


public class VideoGuideFragment extends Fragment implements Initialize {

    private static final String TAG = VideoGuideFragment.class.getName();

    private VideoGuideFragmentBinding mBinding;
    private ViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.video_guide_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
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
    public void initLayout(View view) {

    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initEventListener() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.setKeyboardShowHide(getActivity(), mBinding.getRoot(), false);
    }

    public class ViewModel {

        public void onClick(View v) {

            try {

                switch (v.getId()) {
                    case R.id.tv_dont_show:
                        // 다시보지 않기
                        SharedPreferencesUtils.save(getActivity(), DataConst.IS_VIDEO_GUIDE_SHOW, true);
                    case R.id.tv_close:
                        FragmentUtils.removeFragment(getFragmentManager());
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}