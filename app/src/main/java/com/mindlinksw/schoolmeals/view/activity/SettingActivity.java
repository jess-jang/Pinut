package com.mindlinksw.schoolmeals.view.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;

import com.mindlinksw.schoolmeals.BuildConfig;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.Style;
import com.mindlinksw.schoolmeals.databinding.SettingActivityBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.PushStatusModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.model.WebModel;
import com.mindlinksw.schoolmeals.network.LoginAPI;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.Utils;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity implements Initialize {

    private final String TAG = SettingActivity.class.getName();

    private SettingActivityBinding mBinding;
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.setting_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();

    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mViewModel.mModel.set(SessionSingleton.getInstance(SettingActivity.this).select());
        mViewModel.getSocialIcon();
        mViewModel.getPushStatus();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

    }

    @Override
    public void initVariable() {
        mViewModel.getVersion();
    }

    @Override
    public void initEventListener() {

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<String> mStoreVersion = new ObservableField<>();
        public ObservableField<SessionModel> mModel = new ObservableField<>();
        public ObservableInt mSocialIcon = new ObservableInt();

        public void onClick(View v) {

            Intent intent = null;
            WebModel web = null;

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.sc_marketing:
                case R.id.sc_comment:
                    setPushStatus();
                    break;

                case R.id.ll_term:
                    intent = new Intent(SettingActivity.this, TermDetailActivity.class);
                    intent.putExtra(DataConst.TYPE, Style.TERM_TYPE.TERM);
                    startActivity(intent);
                    break;

                case R.id.ll_private:
                    intent = new Intent(SettingActivity.this, TermDetailActivity.class);
                    intent.putExtra(DataConst.TYPE, Style.TERM_TYPE.PRIVATE);
                    startActivity(intent);
                    break;

                case R.id.ll_logout:
                    new AlertDialog(SettingActivity.this)
                            .setMessage(getString(R.string.setting_logout_alert))
                            .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {

                                    try {

                                        LoginAPI.logout(SettingActivity.this);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
         * push 여부 조회
         */
        public void getPushStatus() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(SettingActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<PushStatusModel> call = service.reqPushStatus();

            RetrofitCall.enqueueWithRetry(call, new Callback<PushStatusModel>() {

                @Override
                public void onResponse(Call<PushStatusModel> call, Response<PushStatusModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            PushStatusModel resData = (PushStatusModel) response.body();

                            if (resData != null) {

                                mBinding.scMarketing.setChecked("Y".equals(resData.getMarketingAlertYn()));
                                mBinding.scComment.setChecked("Y".equals(resData.getPersonalAlertYn()));

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PushStatusModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * push 여부 세팅
         */
        public void setPushStatus() {

            Logger.e(TAG, "마케팅 : " + (mBinding.scMarketing.isChecked() ? "Y" : "N"));
            Logger.e(TAG, "댓글 : " + (mBinding.scComment.isChecked() ? "Y" : "N"));

            final SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(SettingActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqUpdatePushStatus(
                    mBinding.scMarketing.isChecked() ? "Y" : "N",
                    mBinding.scComment.isChecked() ? "Y" : "N");

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {
                                if ("success".equals(resData.getCode())) {

                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
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

        /**
         * 마켓 버전 조회
         */
        public void getVersion() {

            SchoolMealsService service = RetrofitRequest.createRetrofitScalarsService(SettingActivity.this, SchoolMealsService.class, "https://play.google.com/");
            Call<String> call = service.reqStoreVersion(
                    BuildConfig.APPLICATION_ID);

            RetrofitCall.enqueueWithRetry(call, new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {

                        String ver = null;

                        if (response.isSuccessful()) {

                            String data = (String) response.body();
                            if (data != null) {
                                Logger.e(TAG, data.toString());


                                String startToken = "softwareVersion\">";
                                String endToken = "<";
                                int index = data.indexOf(startToken);

                                // 1st. softwareVersion
                                if (index == -1) {
                                    ver = null;
                                } else {
                                    ver = data.substring(index + startToken.length(), index + startToken.length() + 100);
                                    ver = ver.substring(0, ver.indexOf(endToken)).trim();
                                }

                                // 2nd. Current Version
                                if (ver == null) {

                                    startToken = "Current Version</div>";
                                    index = data.indexOf(startToken);

                                    if (index == -1) {
                                        ver = null;
                                    } else {
                                        ver = data.substring(index, index + 1000);

                                        // 정규식 패턴
                                        String regex = "[0-9]\\.[0-9][0-9]\\.[0-9][0-9]";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(ver);
                                        boolean result = matcher.find();
                                        if (result) {
                                            ver = matcher.group(0);
                                        } else {
                                            ver = null;
                                        }
                                    }
                                }

                                Logger.e(TAG, "mStoreVersion : " + ver);

                                if (ver != null) {
                                    if (Utils.isVersionUpdated(ver)) {
                                        // 업데이트 필요
                                        mStoreVersion.set(String.format(getString(R.string.mypage_version_need), ver));
                                    } else {
                                        // 최신버전
                                        mStoreVersion.set(String.format(getString(R.string.mypage_version), ver));
                                    }
                                } else {
                                    mStoreVersion.set(String.format(getString(R.string.mypage_version), BuildConfig.VERSION_NAME));
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        mStoreVersion.set(String.format(getString(R.string.mypage_version), BuildConfig.VERSION_NAME));
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });

        }

        public void getSocialIcon() {

            if ("naver".equals(mModel.get().getUserSocial())) {
                mSocialIcon.set(R.drawable.ico_login_naver);
            } else if ("google".equals(mModel.get().getUserSocial())) {
                mSocialIcon.set(R.drawable.ico_login_google);
            } else if ("kakao".equals(mModel.get().getUserSocial())) {
                mSocialIcon.set(R.drawable.ico_login_kakao);
            } else if ("facebook".equals(mModel.get().getUserSocial())) {
                mSocialIcon.set(R.drawable.ico_login_facebook);
            }
        }

    }

}
