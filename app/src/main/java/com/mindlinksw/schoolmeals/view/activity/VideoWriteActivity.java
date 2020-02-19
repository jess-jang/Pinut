package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.CategoryConst;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.databinding.VideoWriteActivityBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.BoardData;
import com.mindlinksw.schoolmeals.model.BoardDetailModel;
import com.mindlinksw.schoolmeals.model.BoardInsertModel;
import com.mindlinksw.schoolmeals.model.VideoThumbnailModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.FragmentUtils;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.utils.TextFormatUtils;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mindlinksw.schoolmeals.view.custom.ProgressDialog;
import com.mindlinksw.schoolmeals.view.fragment.VideoGuideFragment;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 작성하기
 */
public class VideoWriteActivity extends AppCompatActivity implements Initialize {

    private final String TAG = VideoWriteActivity.class.getName();

    private VideoWriteActivityBinding mBinding;
    private ViewModel mViewModel;
    private Handler mHandler = new Handler();

    private int mType = RequestConst.INTENT_WRITE;
    private BoardData mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.video_write_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
        initIntent();

//        mBinding.etContent.setText("https://www.youtube.com/watch?v=EFrUZP5ttx0");
//        mViewModel.search("https://www.youtube.com/watch?v=EFrUZP5ttx0");

    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mViewModel.search(null);
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        // 가이드
        if (!SharedPreferencesUtils.read(VideoWriteActivity.this, DataConst.IS_VIDEO_GUIDE_SHOW, false)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 가이드 노출
                    FragmentUtils.replaceFragment(getSupportFragmentManager(), android.R.id.content, new VideoGuideFragment(), FragmentUtils.Transition.BOTTOM_IN);
                }
            }, 500);
        }
    }

    @Override
    public void initVariable() {

        try {

            mViewModel.onRecognizeDelay();

            Intent intent = getIntent();
            mType = intent.getIntExtra(DataConst.TYPE, RequestConst.INTENT_WRITE);
            mModel = (BoardData) intent.getSerializableExtra(BoardDetailModel.class.getName());

            // 등록, 수정 판단
            mViewModel.mIsWrite.set(mType == RequestConst.INTENT_WRITE);

            // 수정
            if (mType == RequestConst.INTENT_MODIFY && mModel != null) {
                // 글 내용
                mBinding.etTitle.setText(mModel.getTit());
                mBinding.etContent.setText(mModel.getYoutubeMediaUrl());
                mViewModel.search(mBinding.etContent.getText().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void initEventListener() {

        mBinding.etContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mBinding.etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {

                        if (v.getText().toString().trim().length() > 0) {
                            // 딜레이 해제
                            mViewModel.onRemoveDelay();
                            // 검색
                            mViewModel.search(v.getText().toString().trim());

                            // 키보드 내림
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.setKeyboardShowHide(VideoWriteActivity.this, mBinding.getRoot(), false);
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }

                return false;
            }
        });

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
            setIntent(intent);

            // 공유받은 텍스트 세팅
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (ObjectUtils.isEmpty(text)) {
                return;
            }
            mBinding.etContent.setText(text);
            mViewModel.search(text);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<VideoThumbnailModel> mThumbnailModel = new ObservableField<>();
        public ObservableField<String> mError = new ObservableField<>();
        public ObservableBoolean mIsWrite = new ObservableBoolean(); // 수정, 등록 여부
        public ObservableBoolean mIsSubmit = new ObservableBoolean(); // 등록가능 여부
        public ObservableBoolean mIsBlock = new ObservableBoolean(); // et_content block 여부
        public ObservableBoolean mIsLoading = new ObservableBoolean();

        // Delay Handler - 특정시간 입력 이후 서버 데이터 전송
        private Runnable mDelayRunnable;
        private Handler mDelayHandler;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_submit:
                    // Validation Check
                    String title = mBinding.etTitle.getText().toString();
                    String content = mBinding.etContent.getText().toString();

                    if (!TextFormatUtils.isYoutubeUrl(content) || ObjectUtils.isEmpty(content) || !mIsSubmit.get()) {
                        Snackbar.make(mBinding.getRoot(), getString(R.string.video_write_url_pattern), Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (ObjectUtils.isEmpty(title)) {
                        Snackbar.make(mBinding.getRoot(), getString(R.string.video_write_title), Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (mIsWrite.get()) {
                        submit();
                    } else {
                        modify();
                    }
                    break;
            }
        }

        /**
         * 딜레이
         */
        public void onRecognizeDelay() {
            mDelayHandler = new Handler();
            mDelayRunnable = new Runnable() {
                @Override
                public void run() {
                    // 자동완성 데이터 통신
                    search(mBinding.etContent.getText().toString());
                }
            };
        }

        /**
         * 딜레이 제거
         */
        public void onRemoveDelay() {
            // Handler Remove
            if (mDelayHandler != null) {
                mDelayHandler.removeCallbacks(mDelayRunnable);
            }
        }

        /**
         * 딜레이 On
         */
        public void onRunDelay() {
            // Handler Remove
            if (mDelayHandler != null) {
                mDelayHandler.postDelayed(mDelayRunnable, 300);
            }
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {

                mIsSubmit.set(false);

                onRemoveDelay();
                onRunDelay();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * youtube search
         *
         * @param s
         */
        public void search(final String s) {

            // 딜레이 해제
            onRemoveDelay();

            if (ObjectUtils.isEmpty(s)) {
                mIsSubmit.set(false);
                mIsBlock.set(false);
                mError.set(null);
                return;
            }

            if (!TextFormatUtils.isYoutubeUrl(s)) {
                mIsSubmit.set(false);
                mIsBlock.set(true);
                mError.set(getString(R.string.video_write_url_pattern));
                return;
            }

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(VideoWriteActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<VideoThumbnailModel> call = service.reqVideoThumbnail(s);

            RetrofitCall.enqueueWithRetry(call, new Callback<VideoThumbnailModel>() {

                @Override
                public void onResponse(Call<VideoThumbnailModel> call, Response<VideoThumbnailModel> response) {

                    try {

                        Logger.e(TAG, s);

                        if (response.isSuccessful()) {
                            VideoThumbnailModel resData = (VideoThumbnailModel) response.body();

                            if (resData != null) {
                                if ("success".equals(resData.getCode()) && !ObjectUtils.isEmpty(resData.getTitle())) {
                                    mIsSubmit.set(true);
                                    mThumbnailModel.set(resData);
                                    mIsBlock.set(false);
                                } else {
                                    mIsSubmit.set(false);
                                    mIsBlock.set(true);
                                    if (!ObjectUtils.isEmpty(resData.getError())) {
                                        mError.set(resData.getError());
                                    } else {
                                        mError.set(getString(R.string.video_write_url_pattern));
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<VideoThumbnailModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 동영상 등록
         */
        public void submit() {

            String title = mBinding.etTitle.getText().toString();
            String content = mBinding.etContent.getText().toString();

            final ProgressDialog dialog = new ProgressDialog(VideoWriteActivity.this);
            dialog.show();
            mViewModel.mIsLoading.set(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(VideoWriteActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardInsertModel> call = service.reqVideoCreate(
                    CategoryConst.VIDEO,
                    title,
                    content
            );

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardInsertModel>() {

                @Override
                public void onResponse(Call<BoardInsertModel> call, Response<BoardInsertModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            BoardInsertModel resData = (BoardInsertModel) response.body();

                            if (resData != null) {
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

        /**
         * 동영상 수정
         */
        public void modify() {

            String title = mBinding.etTitle.getText().toString();
            String content = mBinding.etContent.getText().toString();

            final ProgressDialog dialog = new ProgressDialog(VideoWriteActivity.this);
            dialog.show();
            mViewModel.mIsLoading.set(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(VideoWriteActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardInsertModel> call = service.reqVideoModify(
                    String.valueOf(mModel.getBoardId()),
                    title,
                    content
            );

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardInsertModel>() {

                @Override
                public void onResponse(Call<BoardInsertModel> call, Response<BoardInsertModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            BoardInsertModel resData = (BoardInsertModel) response.body();

                            if (resData != null) {
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
}
