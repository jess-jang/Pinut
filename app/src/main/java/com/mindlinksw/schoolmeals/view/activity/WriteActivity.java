package com.mindlinksw.schoolmeals.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.CategoryConst;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.PermissionConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.consts.Style;
import com.mindlinksw.schoolmeals.databinding.WriteActivityBinding;
import com.mindlinksw.schoolmeals.databinding.WritePictureItemBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.AttachItem;
import com.mindlinksw.schoolmeals.model.AttachModel;
import com.mindlinksw.schoolmeals.model.BoardData;
import com.mindlinksw.schoolmeals.model.BoardDetailModel;
import com.mindlinksw.schoolmeals.model.BoardInsertModel;
import com.mindlinksw.schoolmeals.model.GalleryModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.FragmentUtils;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.PermissionUtils;
import com.mindlinksw.schoolmeals.utils.TextFormatUtils;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mindlinksw.schoolmeals.utils.resize.Resizer;
import com.mindlinksw.schoolmeals.view.custom.ProgressDialog;
import com.mindlinksw.schoolmeals.view.fragment.GalleryFragment;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 작성하기
 */
public class WriteActivity extends AppCompatActivity implements Initialize {

    public static int IMAGE_MAX_COUNT = 10;

    private final String TAG = WriteActivity.class.getName();

    private WriteActivityBinding mBinding;
    private ViewModel mViewModel;

    private GenericRecyclerViewAdapter mAttachAdapter;
    private ArrayList<AttachModel> mAttachList;

    private int mType = RequestConst.INTENT_WRITE;
    private int mAttachStyle = Style.WRITE.WRITE;
    private BoardData mModel;

    private ImageUploadTask mImageUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.write_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
        initIntent();

    }

    @Override
    public void onBackPressed() {
        if (!ObjectUtils.isEmpty(mBinding.etContent.getText().toString())) {
            new AlertDialog(this)
                    .setMessage(getString(R.string.write_close_confirm))
                    .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick() {

                        }
                    })
                    .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick() {
                            finish();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void initIntent() {

        try {

            Intent intent = getIntent();

            // 공유받은 텍스트 세팅
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (ObjectUtils.isEmpty(text)) {
                return;
            }
            mBinding.etContent.setText(text);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {
        mAttachList = new ArrayList<>();
        mAttachAdapter = new GenericRecyclerViewAdapter(this, R.layout.write_picture_item, mAttachList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                WritePictureItemBinding binding = (WritePictureItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mAttachModel.set((AttachModel) model);
                viewModel.mPosition.set(position);

                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvPicture.setAdapter(mAttachAdapter);
    }

    @Override
    public void initVariable() {

        try {

            Intent intent = getIntent();
            mType = intent.getIntExtra(DataConst.TYPE, RequestConst.INTENT_WRITE);
            mModel = (BoardData) intent.getSerializableExtra(BoardDetailModel.class.getName());

            // 등록, 수정 판단
            mViewModel.mIsWrite.set(mType == RequestConst.INTENT_WRITE);
            mAttachStyle = mViewModel.mIsWrite.get() ? Style.WRITE.WRITE : Style.WRITE.MODIFY;

            // 수정
            if (mType == RequestConst.INTENT_MODIFY && mModel != null) {

                // 글 내용
                mBinding.etTitle.setText(mModel.getTit());
                mBinding.etContent.setText(mModel.getContent());

                setImageList();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void initEventListener() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // gallery
            case PermissionConst.PERMISSION_REQUEST_GALLERY:
                if (PermissionUtils.isCheckPermission(grantResults)) {
                    openGallery(RequestConst.GALLERY);
                } else {
                    Snackbar.make(mBinding.getRoot(), getString(R.string.write_picture_permission), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // 사진 업로드 갯수 체크
    public void openGallery(int source) {

        if (mAttachList.size() < IMAGE_MAX_COUNT) {
            if (source == RequestConst.GALLERY) {

                FragmentUtils.replaceFragment(
                        getSupportFragmentManager(),
                        R.id.fl_main,
                        new GalleryFragment(new GalleryFragment.OnGetMediaListener() {
                            @Override
                            public void getMedia(ArrayList<GalleryModel> list) {

                                for (GalleryModel model : list) {
                                    AttachModel attach = new AttachModel(mAttachStyle, RequestConst.GALLERY, model.getFile());
                                    mAttachList.add(attach);
                                }

                                mAttachAdapter.notifyDataSetChanged();

                                // 리스트 맨 밑으로 이동
                                mBinding.rvPicture.smoothScrollToPosition(mAttachAdapter.getItemCount() - 1);

                            }
                        }, mAttachList.size())
                );
            }
        } else {
            Snackbar.make(mBinding.getRoot(), String.format(getString(R.string.write_max_picture), IMAGE_MAX_COUNT), Snackbar.LENGTH_SHORT).show();
        }
    }

    // image list
    private void setImageList() {
        try {

            mAttachList.clear();

            for (AttachItem item : mModel.getMediaList()) {
                AttachModel attach = new AttachModel(mAttachStyle, RequestConst.SERVER, item.getFileUrl());
                mAttachList.add(attach);
            }

            mAttachAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<AttachModel> mAttachModel = new ObservableField<>();
        public ObservableInt mPosition = new ObservableInt();
        public ObservableBoolean mIsWrite = new ObservableBoolean();
        public ObservableBoolean mIsLoading = new ObservableBoolean();

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_submit:

                    if (mAttachList.size() > IMAGE_MAX_COUNT) {
                        Snackbar.make(mBinding.getRoot(), String.format(getString(R.string.write_max_picture), IMAGE_MAX_COUNT), Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    String title = mBinding.etTitle.getText().toString();
                    String content = mBinding.etContent.getText().toString();
                    if (ObjectUtils.isEmpty(title)) {
                        Snackbar.make(mBinding.getRoot(), getString(R.string.write_empty), Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (mIsWrite.get()) {
                        write();
                    } else {
                        modify();
                    }

                    break;

                case R.id.iv_picture:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(WriteActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionConst.PERMISSION_REQUEST_GALLERY);
                    } else {
                        openGallery(RequestConst.GALLERY);
                    }
                    break;
            }
        }

        public void onClick(View v, final int position) {
            switch (v.getId()) {
                case R.id.iv_remove:
                    // 사진삭제
                    new AlertDialog(WriteActivity.this)
                            .setMessage(getString(R.string.write_image_delete_confirm))
                            .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {
                                    mAttachList.remove(position);
                                    mAttachAdapter.notifyItemRemoved(position);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAttachAdapter.notifyDataSetChanged();
                                        }
                                    }, 250);
                                }
                            })
                            .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {

                                }
                            })
                            .show();
                    break;
            }
        }


        /**
         * 글등록
         */
        public void write() {

            String title = mBinding.etTitle.getText().toString();
            String content = mBinding.etContent.getText().toString();

            mImageUploadTask = new ImageUploadTask(WriteActivity.this, true, mAttachList, title, content);
            mImageUploadTask.execute(new Void[0]);

        }

        /**
         * 업로드
         *
         * @param requestBody
         */
        public void writeProc(RequestBody requestBody) {

            if (ObjectUtils.isEmpty(requestBody)) {
                return;
            }

            final ProgressDialog dialog = new ProgressDialog(WriteActivity.this);
            dialog.show();
            mIsLoading.set(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(WriteActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardInsertModel> call = service.reqBoardCreate(requestBody);

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardInsertModel>() {

                @Override
                public void onResponse(Call<BoardInsertModel> call, Response<BoardInsertModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            BoardInsertModel resData = (BoardInsertModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());
                                if ("success".equals(resData.getCode())) {
                                    Intent result = new Intent();
                                    setResult(Activity.RESULT_OK, result);
                                    finish();
                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                    mIsLoading.set(false);

                }

                @Override
                public void onFailure(Call<BoardInsertModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    dialog.dismiss();
                    mIsLoading.set(false);
                }
            });
        }

        /**
         * 글 수정
         */
        public void modify() {

            String title = mBinding.etTitle.getText().toString();
            String content = mBinding.etContent.getText().toString();

            mImageUploadTask = new ImageUploadTask(WriteActivity.this, false, mAttachList, title, content);
            mImageUploadTask.execute(new Void[0]);

        }

        /**
         * 글 수정
         */
        public void modifyProc(RequestBody requestBody) {

            if (ObjectUtils.isEmpty(requestBody)) {
                return;
            }

            final ProgressDialog dialog = new ProgressDialog(WriteActivity.this);
            dialog.show();
            mViewModel.mIsLoading.set(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(WriteActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardInsertModel> call = service.reqBoardModify(requestBody);

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardInsertModel>() {

                @Override
                public void onResponse(Call<BoardInsertModel> call, Response<BoardInsertModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            BoardInsertModel resData = (BoardInsertModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());
                                if ("success".equals(resData.getCode())) {
                                    Intent result = new Intent();
                                    setResult(Activity.RESULT_OK, result);
                                    finish();
                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                    mViewModel.mIsLoading.set(false);

                }

                @Override
                public void onFailure(Call<BoardInsertModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    dialog.dismiss();
                    mViewModel.mIsLoading.set(false);
                }
            });
        }
    }

    /**
     * Image Upload
     */
    private class ImageUploadTask extends AsyncTask<Void, Void, Boolean> {

        private Activity mActivity;
        private boolean mIsWrite;
        private ArrayList<AttachModel> mList;
        private String mTitle;
        private String mContent;

        private ProgressDialog mProgress;
        private MultipartBody.Builder mBuilder;

        public ImageUploadTask(Activity activity, boolean isWrite, ArrayList<AttachModel> list, String title, String content) {
            this.mActivity = activity;
            this.mIsWrite = isWrite;
            this.mList = list;
            this.mTitle = title;
            this.mContent = content;
        }

        @Override
        protected void onPreExecute() {

            try {

                mProgress = new ProgressDialog(mActivity);
                mProgress.show();

                // Builder
                mBuilder = new MultipartBody.Builder();
                mBuilder.setType(MultipartBody.FORM);
                mBuilder.addFormDataPart("ctgryCode", CategoryConst.BOARD);
                mBuilder.addFormDataPart("tit", mTitle);
                mBuilder.addFormDataPart("content", mContent);

                if (!mIsWrite) {
                    mBuilder.addFormDataPart("boardId", String.valueOf(mModel.getBoardId()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean isExistImage = false;

            try {

                int index = 1;
                for (AttachModel media : mList) {

                    try {

                        if (media.getSource() == RequestConst.GALLERY) {
                            File file = (File) media.getUri();
                            String extension = Utils.getExtension(file.getName());

                            if (!ObjectUtils.isEmpty(extension)) {
                                if (!"gif".equals(extension.toLowerCase())) {
                                    file = new Resizer(mActivity)
                                            .setTargetLength(1440)
                                            .setQuality(80)
                                            .setOutputFormat("PNG")
                                            .setOutputFilename(TextFormatUtils.getFileName())
                                            .setSourceImage(file)
                                            .getResizedFile();
                                }
                            }

                            // file
                            mBuilder.addFormDataPart("imageFile_" + index,
                                    file.getName(),
                                    RequestBody.create(MultipartBody.FORM, file));

                        } else {
                            // 서버 이미지 주소
                            mBuilder.addFormDataPart("imageFile_" + index, String.valueOf(media.getUri()));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    index++;

                    isExistImage = true;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return isExistImage;
        }

        @Override
        protected void onPostExecute(Boolean isExistImage) {

            try {

                if (mIsWrite) {
                    mViewModel.writeProc(mBuilder.build());
                } else {
                    mViewModel.modifyProc(mBuilder.build());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgress.dismiss();
        }
    }
}
