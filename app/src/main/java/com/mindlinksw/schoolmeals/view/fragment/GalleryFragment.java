package com.mindlinksw.schoolmeals.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GalleryAdapter;
import com.mindlinksw.schoolmeals.databinding.GalleryFragmentBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.GalleryModel;
import com.mindlinksw.schoolmeals.utils.FragmentUtils;
import com.mindlinksw.schoolmeals.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * 갤러리 Fragment
 */
@SuppressLint("ValidFragment")
public class GalleryFragment extends Fragment implements Initialize, View.OnKeyListener {

    public interface OnGetMediaListener {
        public void getMedia(ArrayList<GalleryModel> list);
    }

    private static final String TAG = GalleryFragment.class.getName();

    private GalleryFragmentBinding mBinding;
    private ViewModel mViewModel;
    private GalleryAdapter mAdapter;
    private ArrayList<GalleryModel> mList;

    private OnGetMediaListener mListener;
    private int mImageCount; // 현재 첨부된 사진 갯수

    @SuppressLint("ValidFragment")
    public GalleryFragment(OnGetMediaListener listener, int imageCount) {
        this.mListener = listener;
        this.mImageCount = imageCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.gallery_fragment, container, false);
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
    public void onStart() {
        super.onStart();
        Utils.setKeyboardShowHide(getActivity(), mBinding.getRoot(), false);
    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);

    }

    @Override
    public void initLayout(View view) {

        mList = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(), mList);
        mAdapter.setImageCount(mImageCount);
        mBinding.rvGallery.setAdapter(mAdapter);

    }

    @Override
    public void initVariable() {

        try {

            getMediaList(getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initEventListener() {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    FragmentUtils.removeFragment(getFragmentManager());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 이미지, 동영상  가져오기
     */
    public void getMediaList(Activity activity) {

        try {

            Uri contentUri = MediaStore.Files.getContentUri("external");

            String[] projection = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Video.Media.DURATION
            };

            // 이미지 가져오기
            String selection =
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
//                            + " OR "
//                            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            // 최신날짜순으로
            String orderBy = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
            // String limit = " LIMIT " + startAt + ", " + page;

            CursorLoader cursorLoader = new CursorLoader(activity, contentUri, projection, selection, null, orderBy);
            Cursor cursor = cursorLoader.loadInBackground();

            if (cursor.moveToFirst()) {

                do {

                    GalleryModel media = new GalleryModel();

                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    media.setId(id);

                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    media.setData(path);
                    media.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)));

                    int mediaType = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                    media.setMediaType(mediaType);

                    media.setMineType(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)));

                    if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        media.setDuration(duration);
                        media.setDurationDisplay(String.format(Locale.getDefault(), "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
                    }

                    // uri
                    Uri uri = MediaStore.Files.getContentUri("external", id);
                    media.setUri(uri);

                    // file
                    File file = new File(path);
                    media.setFile(file);

                    mList.add(media);

                } while (cursor.moveToNext());
            }
            cursor.close();
            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewModel {

        public void onClick(View v) {

            try {

                switch (v.getId()) {
                    case R.id.tv_submit:
                        if (mListener != null) {
                            mListener.getMedia(mAdapter.getCheckedList());
                        }
                        FragmentUtils.removeFragment(getFragmentManager());
                        break;

                    case R.id.tv_cancel:
                        FragmentUtils.removeFragment(getFragmentManager());
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}