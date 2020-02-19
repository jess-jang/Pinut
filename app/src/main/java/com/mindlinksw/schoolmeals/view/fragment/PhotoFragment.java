package com.mindlinksw.schoolmeals.view.fragment;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.databinding.PhotoFragmentBinding;
import com.mindlinksw.schoolmeals.model.AttachItem;
import com.mindlinksw.schoolmeals.utils.ImageLoadUtils;

import java.util.ArrayList;

public class PhotoFragment extends Fragment {

    private PhotoFragmentBinding mBinding;
    private ArrayList<AttachItem> mAttachList;
    private int mPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.photo_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayout(view);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    private void initLayout(View view) {

        try {

            if (getArguments() != null) {
                mAttachList = (ArrayList<AttachItem>) getArguments().getSerializable(AttachItem.class.getName());
                mPosition = getArguments().getInt(DataConst.POSITION, 0);

                ViewPager viewPager = view.findViewById(R.id.vp_photo);
                viewPager.setAdapter(new PhotoPagerAdapter());
                viewPager.setCurrentItem(mPosition);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class PhotoPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mAttachList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // 이미지 로드
            ImageLoadUtils.loadImage(photoView, mAttachList.get(position).getFileUrl());
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
