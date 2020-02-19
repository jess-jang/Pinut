package com.mindlinksw.schoolmeals.adapter;


import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;
import android.provider.MediaStore;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.BR;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.model.GalleryModel;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.resize.util.ImageUtils;
import com.mindlinksw.schoolmeals.view.activity.WriteActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Rooms Adapter - 객실
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final String TAG = GalleryAdapter.class.getName();

    private Activity mActivity;
    private ArrayList<GalleryModel> mList;
    private ArrayList<GalleryModel> mCheckedList = new ArrayList<>(); // 선택된 이미지 리스트
    private int mUploadedImageCount = 0; // 현재 업로드된 이미지 갯수

    private int mMaxImageMb = 10; // 최대 사진 업로드 용량
    private int mMaxHeight = 6000; // 최대 길이

    public GalleryAdapter(Activity activity, ArrayList<GalleryModel> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.gallery_item, parent, false).getRoot();
        return new GalleryAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final GalleryAdapter.ViewHolder holder, final int position) {

        try {

            GalleryModel item = mList.get(position);
            ViewModel viewModel = new ViewModel();
            viewModel.mModel.set(item);
            viewModel.mPosition.set(position);

            holder.getBinding().setVariable(BR.viewModel, viewModel);
            holder.getBinding().executePendingBindings();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 현재 Web 에 올라가있는 이미지 카운트
    public void setImageCount(int imageCount) {
        this.mUploadedImageCount = imageCount;
    }


    public ArrayList<GalleryModel> getCheckedList() {
        return mCheckedList;
    }

    /**
     * View Holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    /**
     * Binding Handler
     */
    public class ViewModel {

        public ObservableField<GalleryModel> mModel = new ObservableField<>();
        public ObservableInt mPosition = new ObservableInt();

        public void onClick(final View v, GalleryModel model, int position) {
            try {

                switch (v.getId()) {
                    case R.id.image_item:

                        boolean isSelf = false; // 같은거 클릭

                        // 사진 10개, 동영상 1개,  체크
                        int imageCount = 0;
                        for (GalleryModel media : mCheckedList) { // 현재 체크된 이미지

                            imageCount++;

                            if (media.getId() == model.getId()) {
                                isSelf = true;
                            }
                        }

                        Logger.e(TAG, "current image count : " + imageCount);

                        if (!isSelf) { // 이전에 자신이 클릭했던 것은 그냥 넘어감

                            if ((imageCount + mUploadedImageCount) >= WriteActivity.IMAGE_MAX_COUNT) {
                                Logger.e(TAG, "max image");
                                Snackbar.make(mActivity.findViewById(android.R.id.content),
                                        String.format(mActivity.getString(R.string.gallery_max_count_image), WriteActivity.IMAGE_MAX_COUNT), Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            if (model.getFile() != null) {

                                // 사진 용량체크
                                File file = model.getFile();
                                long sizeMb = file.length() / (1024 * 1024);
                                Logger.e(TAG, "size mb : " + sizeMb);

                                if (model.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                                        && sizeMb > mMaxImageMb) {
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            String.format(mActivity.getString(R.string.gallery_max_size_image), mMaxImageMb), Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                // 사진 길이체크
                                HashMap<String, Integer> map = ImageUtils.getImageSize(file.getPath());
                                int height = map.get("height");
                                if (height > mMaxHeight) {
                                    Snackbar.make(mActivity.findViewById(android.R.id.content),
                                            String.format(mActivity.getString(R.string.gallery_max_size_height), mMaxHeight), Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        }

                        setNumberCheck(position);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 이미지 클릭
        public void setNumberCheck(int position) {
            // 1. 현재 이미지의 true/false 여부
            // 2. true 일경우 이미지 fasle, view 처리

            GalleryModel model = mList.get(position);
            if (model.isChecked()) {

                // 선택됐었음
                model.setChecked(false);
                model.setNumber(0); // 초기화

                // 선택된 list 제거, 재정렬
                for (int i = 0; i < mCheckedList.size(); i++) {
                    // id 같은 것을 찾아 제거
                    if (mCheckedList.get(i).getId() == model.getId()) {
                        // 숫자 재정렬
                        // 1 2 3 4 5
                        // 1 2 _ 4 5 <-- 3 이상부터 숫자를 재정렬
                        for (int j = i; j < mCheckedList.size(); j++) {
                            GalleryModel resetItem = mCheckedList.get(j);
                            resetItem.setNumber(resetItem.getNumber() - 1);
                            mCheckedList.set(j, resetItem);
                        }
                        mCheckedList.remove(i);
                        break;
                    }
                }
            } else {
                // 선택되지않았음
                model.setChecked(true);
                model.setNumber(mCheckedList.size() + 1);

                mCheckedList.add(model);
            }

            notifyDataSetChanged();
        }
    }
}