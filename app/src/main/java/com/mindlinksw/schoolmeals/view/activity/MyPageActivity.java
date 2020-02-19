package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.databinding.MypageActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends AppCompatActivity implements Initialize {

    private final String TAG = MyPageActivity.class.getName();

    private MypageActivityBinding mBinding;
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.mypage_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();

    }

    @Override
    public void finish() {

        try {
            setResult(Activity.RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.finish();

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
        mViewModel.getMember();
    }

    @Override
    public void initEventListener() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if (resultCode == RESULT_OK &&
                    (requestCode == RequestConst.INTENT_NICKNAME
                            || requestCode == RequestConst.INTENT_SCHOOL)) {
                mViewModel.getMember();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<SessionModel> mModel = new ObservableField<>();

        public void onClick(View v) {

            Intent intent = null;

            switch (v.getId()) {

                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_setting:
                    intent = new Intent(MyPageActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;

                case R.id.ll_member_school:
                case R.id.ll_member_grade:
                case R.id.ll_school:
                    final String insttCode = SessionSingleton.getInstance(MyPageActivity.this).getInsttCode();
                    if (insttCode != null) {
                        intent = new Intent(MyPageActivity.this, SchoolChangeActivity.class);
                    } else {
                        intent = new Intent(MyPageActivity.this, SchoolChooseActivity.class);
                    }
                    startActivityForResult(intent, RequestConst.INTENT_SCHOOL);

                    break;

                case R.id.ll_my_story:
                    intent = new Intent(MyPageActivity.this, MyStoryActivity.class);
                    startActivity(intent);
                    break;

                case R.id.ll_nickname:
                    intent = new Intent(MyPageActivity.this, NickNameActivity.class);
                    startActivityForResult(intent, RequestConst.INTENT_NICKNAME);
                    break;

                case R.id.ll_allergy:
                    intent = new Intent(MyPageActivity.this, AllergyActivity.class);
                    startActivity(intent);
                    break;

                case R.id.ll_point:
                case R.id.ll_member_point:
                    intent = new Intent(MyPageActivity.this, PointActivity.class);
                    startActivity(intent);
                    break;

                case R.id.ll_notice:
                    intent = new Intent(MyPageActivity.this, NoticeActivity.class);
                    startActivity(intent);
                    break;

                case R.id.ll_cs:
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    String[] address = {"kinglunch1@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, address);
                    startActivity(intent);
                    break;

                case R.id.ll_version:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    break;
            }
        }

        public void getMember() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(MyPageActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<SessionModel> call = service.reqMember();

            RetrofitCall.enqueueWithRetry(call, new Callback<SessionModel>() {

                @Override
                public void onResponse(Call<SessionModel> call, Response<SessionModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            SessionModel resData = (SessionModel) response.body();
                            if (resData != null) {
                                mModel.set(resData);
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
    }

}
