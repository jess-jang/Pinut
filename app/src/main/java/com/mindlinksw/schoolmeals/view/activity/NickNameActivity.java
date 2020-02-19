package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.NicknameActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.NickNameChangeModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.Utils;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NickNameActivity extends AppCompatActivity implements Initialize {

    private final String TAG = NickNameActivity.class.getName();

    private NicknameActivityBinding mBinding;
    private ViewModel mViewModel;
    private boolean mIsNewMember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.nickname_activity);

        initDataBinding();
        initLayout(null);
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

    }

    @Override
    public void initVariable() {

        try {

            Intent intent = getIntent();
            mIsNewMember = intent.getBooleanExtra("isNewMember", false);

            mViewModel.getMember();
            mViewModel.onRecognizeDelay();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initEventListener() {

        try {

            mBinding.etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            mBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                                        Utils.setKeyboardShowHide(NickNameActivity.this, mBinding.getRoot(), false);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<SessionModel> mModel = new ObservableField<>();
        public ObservableField<String> mMessage = new ObservableField();
        public ObservableField<String> mInfo = new ObservableField();
        public ObservableBoolean mIsSubmit = new ObservableBoolean();
        public ObservableInt mMessageColor = new ObservableInt();

        private String mOriginalName;

        // Delay Handler - 특정시간 입력 이후 서버 데이터 전송
        private Runnable mDelayRunnable;
        private Handler mDelayHandler;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_submit:
                    submit(mBinding.etSearch.getText().toString());
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
                    search(mBinding.etSearch.getText().toString());
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
                mMessage.set(null);

                onRemoveDelay();
                onRunDelay();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void getMember() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(NickNameActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<SessionModel> call = service.reqMember();

            RetrofitCall.enqueueWithRetry(call, new Callback<SessionModel>() {

                @Override
                public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            SessionModel resData = (SessionModel) response.body();
                            if (resData != null) {

                                mOriginalName = resData.getNckNm();
                                mModel.set(resData);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.etSearch.setSelection(mBinding.etSearch.getText().toString().length());
                                    }
                                }, 500);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<SessionModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 닉네임 중복 체크
         *
         * @param s
         */
        public void search(final String s) {

            // 딜레이 해제
            onRemoveDelay();

            if (ObjectUtils.isEmpty(s)) {
                mIsSubmit.set(false);
                mMessage.set(null);
                return;
            }

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(NickNameActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<NickNameChangeModel> call = service.reqDuplicateNickName(s);

            RetrofitCall.enqueueWithRetry(call, new Callback<NickNameChangeModel>() {

                @Override
                public void onResponse(Call<NickNameChangeModel> call, Response<NickNameChangeModel> response) {

                    try {

                        Logger.e(TAG, s);

                        if (response.isSuccessful()) {
                            NickNameChangeModel resData = (NickNameChangeModel) response.body();

                            if (resData != null) {
                                if ("success".equals(resData.getCode())) {
                                    mIsSubmit.set(true);
                                    mMessage.set(String.format(getString(R.string.nickname_count), resData.getNckCnt()));
                                    mInfo.set(String.format(getString(R.string.nickname_max), resData.getNckOprtnCnt()));
                                    mMessageColor.set(ContextCompat.getColor(NickNameActivity.this, R.color.nickname_count));
                                } else {
                                    mIsSubmit.set(false);
                                    mMessage.set(resData.getError());
                                    mInfo.set(getString(R.string.nickname_info));
                                    mMessageColor.set(ContextCompat.getColor(NickNameActivity.this, R.color.nickname_red));
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<NickNameChangeModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 닉네임 변경
         *
         * @param s
         */
        public void submit(final String s) {

            if (ObjectUtils.isEmpty(s)) {
                mIsSubmit.set(false);
                mMessage.set(null);
                return;
            }

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(NickNameActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqUpdateNickName(s, mIsNewMember ? "Y" : "N");

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        Logger.e(TAG, s);

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {
                                if ("success".equals(resData.getCode())) {

                                    // 반환
                                    Intent intent = getIntent();
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();

                                    Snackbar.make(mBinding.getRoot(), getString(R.string.nickname_submit_success), Snackbar.LENGTH_SHORT).show();

                                } else {

                                    mIsSubmit.set(false);
                                    mMessage.set(resData.getError());

                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }
    }

}
