package com.mindlinksw.schoolmeals.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.databinding.LoginActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.interfaces.OnLoginResponseListener;
import com.mindlinksw.schoolmeals.model.FacebookLogin;
import com.mindlinksw.schoolmeals.model.MemberCheckModel;
import com.mindlinksw.schoolmeals.model.NaverLogin;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.network.FCMAPI;
import com.mindlinksw.schoolmeals.network.LoginAPI;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.AnimateUtils;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.view.custom.ProgressDialog;
import com.mindlinksw.schoolmeals.viewholder.DateMealsViewHolder;
import com.mindlinksw.security.AES256Util;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Initialize {

    interface OnIsMemberCheckListener {
        void isMember(boolean isMember);
    }

    private final String TAG = LoginActivity.class.getName();

    @interface LOGIN_TYPE {
        String NAVER = "naver";
        String KAKAO = "kakao";
        String FACEBOOK = "facebook";
        String GOOGLE = "google";
    }

    // DataBinding, ViewModel
    private LoginActivityBinding mBinding;
    public LoginViewModel mViewModel;

    private OAuthLogin mNaverOAuthLoginModule; // naver
    private OAuthLoginHandler mNaverOAuthLoginHandler; // naver
    private CallbackManager mFacebookManager; // facebook
    private GoogleApiClient mGoogleApiClient; // google
    private KaKaoSessionCallback mKakaoCallBack; // kakao

    private String mSocialType;
    private String mSocialId;
    private String mSocialEmail;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.login_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();

    }

    @Override
    protected void onDestroy() {
        Session.getCurrentSession().removeCallback(mKakaoCallBack);
        super.onDestroy();
    }


    @Override
    public void initDataBinding() {
        mViewModel = new LoginViewModel(this);
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        mBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {

                int logoHeight = mBinding.llLogo.getHeight();

                // 로고 처음 시작 위치 세팅
                mBinding.llLogo.setTranslationY(logoHeight / 2);

                // 로고 올라옴
                AnimateUtils.setAlpha(mBinding.llLogo, 800, 0.0f, 1.0f);
                AnimateUtils.setTranslationY(mBinding.llLogo, 400, logoHeight / 2, 0, 500);

                // 로그인 버튼
                AnimateUtils.setAlpha(mBinding.llType, 800, 0.0f, 1.0f, 800);

            }
        });

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void initVariable() {

        try {

            // naver
            {
                mNaverOAuthLoginModule = OAuthLogin.getInstance();
                mNaverOAuthLoginModule.init(
                        LoginActivity.this,
                        getString(R.string.naver_client_id),
                        getString(R.string.naver_client_secret),
                        getString(R.string.app_name)
                );

                /**
                 * Login CallBack - Naver
                 * */
                mNaverOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        Logger.e(TAG, "naver : " + success);
                        if (success) {
                            String accessToken = mNaverOAuthLoginModule.getAccessToken(LoginActivity.this);
                            Logger.e(TAG, "accessToken : " + accessToken);

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {

                                    String url = "https://openapi.naver.com/v1/nid/me";
                                    String at = mNaverOAuthLoginModule.getAccessToken(LoginActivity.this);

                                    String result = mNaverOAuthLoginModule.requestApi(LoginActivity.this, at, url);
                                    Logger.e(TAG, result);

                                    Gson gson = new Gson();
                                    NaverLogin naver = gson.fromJson(result, NaverLogin.class);

                                    login(LOGIN_TYPE.NAVER, LOGIN_TYPE.NAVER + "_" + naver.getResponse().getId(), naver.getResponse().getEmail());

                                }
                            });
                        } else {
                            String errorCode = mNaverOAuthLoginModule.getLastErrorCode(LoginActivity.this).getCode();
                            String errorDesc = mNaverOAuthLoginModule.getLastErrorDesc(LoginActivity.this);
                            Logger.e(TAG, "errorCode:" + errorCode + ", errorDesc:" + errorDesc);
                        }
                    }
                };
            }

            // kakao
            {
                mKakaoCallBack = new KaKaoSessionCallback();
                Session.getCurrentSession().addCallback(mKakaoCallBack);
                Session.getCurrentSession().checkAndImplicitOpen();
            }

            // facebook
            {
                mFacebookManager = CallbackManager.Factory.create();
            }

            // google
            {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .requestScopes(new Scope(Scopes.PROFILE), new Scope(Scopes.PLUS_ME))
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initEventListener() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult : " + requestCode + " / " + resultCode);

        try {

            // kakao
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                return;
            }

            if (requestCode == RequestConst.LOGIN_FACEBOOK) {
                // facebook
                mFacebookManager.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == RequestConst.LOGIN_GOOGLE) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    login(LOGIN_TYPE.GOOGLE, LOGIN_TYPE.GOOGLE + "_" + account.getId(), account.getEmail());
                } else {
                    Logger.e(TAG, "error : " + result.getStatus().getStatusMessage());
                }
            } else if (requestCode == RequestConst.INTENT_TERM && resultCode == RESULT_OK) {
                accessLogin(true, mSocialType, mSocialId, mSocialEmail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Login ViewModel
     */
    public class LoginViewModel {

        private Activity mActivity;

        public LoginViewModel(Activity mActivity) {
            this.mActivity = mActivity;
        }

        public void onClick(View v) {

            mProgress = new ProgressDialog(LoginActivity.this);
            mProgress.setCancelable(true);
            mProgress.show();

            switch (v.getId()) {
                case R.id.v_naver:
                    naver();
                    break;

                case R.id.v_kakao:
                    kakao();
                    break;

                case R.id.v_facebook:
                    facebook();
                    break;

                case R.id.v_google:
                    google();
                    break;

            }
        }

        // naver
        private void naver() {
            try {
                mNaverOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mNaverOAuthLoginHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // kakao
        private void kakao() {
            mBinding.lbKakaoLogin.performClick();
        }

        // facebook
        private void facebook() {
            try {

                /**
                 * Login CallBack - Facebook
                 * */
                LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "email", "user_birthday", "user_posts", "user_gender"));
                LoginManager.getInstance().registerCallback(mFacebookManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                try {

                                    Logger.e(TAG, "facebook getAccessToken : " + loginResult.getAccessToken());
                                    Logger.e(TAG, "facebook getRecentlyDeniedPermissions : " + loginResult.getRecentlyDeniedPermissions());
                                    Logger.e(TAG, "facebook getRecentlyGrantedPermissions : " + loginResult.getRecentlyGrantedPermissions());

                                    // 사용자 데이터
                                    GraphRequest request = GraphRequest.newMeRequest(
                                            loginResult.getAccessToken(),
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(JSONObject object, GraphResponse response) {

                                                    try {

                                                        Logger.e(TAG, object.toString());

                                                        Gson gson = new Gson();
                                                        FacebookLogin facebook = gson.fromJson(object.toString(), FacebookLogin.class);

                                                        login(LOGIN_TYPE.FACEBOOK, LOGIN_TYPE.FACEBOOK + "_" + facebook.getId(), facebook.getEmail());

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                                    Bundle bundle = new Bundle();
                                    bundle.putString("fields", "id,name,email,gender,birthday");

                                    request.setParameters(bundle);
                                    request.executeAsync();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onCancel() {
                                Logger.e(TAG, "facebook : " + "onCancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Logger.e(TAG, "facebook : " + exception.toString());
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // google
        private void google() {
            try {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RequestConst.LOGIN_GOOGLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Login CallBack - Kakao
     */
    public class KaKaoSessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(TAG, "kakao : " + exception.toString());
            }
        }

        // kakao requestMe
        private void requestMe() {

            List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("properties.profile_image");
            keys.add("kakao_account.email");
            keys.add("kakao_account.age_range");
            keys.add("kakao_account.birthday");
            keys.add("kakao_account.gender");

            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.e(TAG, "KaKao : " + message);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Logger.e(TAG, "KaKao : " + errorResult.getErrorMessage());
                }

                @Override
                public void onSuccess(MeV2Response response) {
                    try {

                        // {"id":976854323,"properties":{"nickname":"투투"},"kakao_account":{"has_email":true,"has_age_range":true,"has_birthday":true,"has_gender":false}}
                        Logger.e(TAG, "KaKao : " + response.toString());
                        response.getProperties();
                        login(LOGIN_TYPE.KAKAO, LOGIN_TYPE.KAKAO + "_" + response.getId(), response.getNickname());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 로그인
     */
    private void login(final String socialType, final String userId, final String email) {

        mSocialType = socialType;
        mSocialId = userId;
        mSocialEmail = email;

        isMemberCheck(socialType, userId, new OnIsMemberCheckListener() {
            @Override
            public void isMember(boolean isMember) {
                if (isMember) {
                    // 로그인 실시 - 기존 회원
                    accessLogin(false, socialType, userId, email);
                } else {
                    // 약관 동의 후 회원 가입 실시
                    startActivityForResult(new Intent(LoginActivity.this, TermAgreeActivity.class), RequestConst.INTENT_TERM);
                }
            }
        });
    }

    /**
     * 회원 등록 여부 체크
     *
     * @param social
     * @param userId
     */
    private void isMemberCheck(String social, String userId, final OnIsMemberCheckListener listener) {

        try {

            String aesUserId = AES256Util.encode(getString(R.string.aes256_key), userId);
            Logger.e(TAG, "isMemberCheck : " + social + " / " + aesUserId + " / " + AES256Util.decode(getString(R.string.aes256_key), aesUserId));

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(LoginActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<MemberCheckModel> call = service.reqIsMemberCheck(
                    social,
                    aesUserId);

            RetrofitCall.enqueueWithRetry(call, new Callback<MemberCheckModel>() {

                @Override
                public void onResponse(Call<MemberCheckModel> call, Response<MemberCheckModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            MemberCheckModel resData = (MemberCheckModel) response.body();

                            int count = resData.getResultCnt();

                            if (count < 1) {
                                // 회원가입 실시
                                listener.isMember(false);
                            } else {
                                // 로그인 실시
                                listener.isMember(true);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<MemberCheckModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 멤버 로그인
     *
     * @param socialType
     * @param userId
     */
    private void accessLogin(final boolean isNewMember, final String socialType, String userId, String email) {

        try {

            if (userId == null) {
                Snackbar.make(mBinding.getRoot(), R.string.login_fail, Snackbar.LENGTH_SHORT).show();
                return;
            }

            userId = AES256Util.encode(getString(R.string.aes256_key), userId);
            LoginAPI.access(LoginActivity.this, socialType, userId, email, new OnLoginResponseListener() {

                @Override
                public void onSuccess(SessionModel session) {
                    onLoginSuccess(isNewMember, socialType, session);
                }

                @Override
                public void onFail(String error) {

                    if (ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mBinding.getRoot(), R.string.login_fail, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                    }

                    SessionSingleton.getInstance(LoginActivity.this).delete();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            onLoginFail();
        }
    }

    /**
     * 로그인 실패
     */
    private void onLoginFail() {

        try {

            if (mProgress != null) {
                mProgress.dismiss();
            }

            Snackbar.make(mBinding.getRoot(), R.string.login_fail, Snackbar.LENGTH_SHORT).show();
            SessionSingleton.getInstance(LoginActivity.this).delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 로그인
     *
     * @param isNewMember
     * @param socialType
     * @param resData
     */
    private void onLoginSuccess(boolean isNewMember, String socialType, SessionModel resData) {

        try {

            SessionSingleton.getInstance(LoginActivity.this).insert(resData);

            // 급식 데이터 새로고침
            DateMealsViewHolder.isRefresh = true;

            // 비회원 코드 삭제
            SharedPreferencesUtils.remove(LoginActivity.this, DataConst.NONMEMBER_SCHOOL_CODE);

            // FCM
            FCMAPI.registerToken(LoginActivity.this, null);

            // Intent
            if (isNewMember) {
                // 닉네임 설정화면으로 이동
                Intent intent = new Intent(LoginActivity.this, NickNameActivity.class);
                intent.putExtra("isNewMember", true);
                startActivity(intent);
            } else {
                setResult(Activity.RESULT_OK);
            }

            // disconnect
            switch (socialType) {
                case LOGIN_TYPE.NAVER:
                    // disconnect
                    mNaverOAuthLoginModule.logout(LoginActivity.this);
                    break;

                case LOGIN_TYPE.KAKAO:
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {

                        }
                    });
                    break;

                case LOGIN_TYPE.FACEBOOK:
                    LoginManager.getInstance().logOut();
                    break;

                case LOGIN_TYPE.GOOGLE:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mProgress != null) {
            mProgress.dismiss();
        }

        finish();

    }

}