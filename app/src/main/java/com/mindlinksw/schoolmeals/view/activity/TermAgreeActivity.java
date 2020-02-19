package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.TermAgreeActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.model.TermModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermAgreeActivity extends AppCompatActivity implements Initialize {

    private final String TAG = TermAgreeActivity.class.getName();

    public TermAgreeActivityBinding mBinding;
    public ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.term_agree_activity);

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

        mBinding.tvPrivate.setMovementMethod(new ScrollingMovementMethod());
        mBinding.tvTerm.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void initVariable() {
        mViewModel.getData();
    }

    @Override
    public void initEventListener() {

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableBoolean mIsSubmit = new ObservableBoolean();
        public ObservableField<TermModel> mModel = new ObservableField();

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cb_all_agree:
                    if (mBinding.cbAllAgree.isChecked()) {
                        mBinding.cbTerms.setChecked(true);
                        mBinding.cbPrivate.setChecked(true);
                        mBinding.cbPush.setChecked(true);
                        mIsSubmit.set(true);
                    } else {
                        mBinding.cbTerms.setChecked(false);
                        mBinding.cbPrivate.setChecked(false);
                        mBinding.cbPush.setChecked(false);
                        mIsSubmit.set(false);
                    }
                    break;
                case R.id.cb_private:
                case R.id.cb_terms:
                    mBinding.cbAllAgree.setChecked(false);
                    if (mBinding.cbTerms.isChecked() && mBinding.cbPrivate.isChecked()) {
                        mIsSubmit.set(true);
                    } else {
                        mIsSubmit.set(false);
                    }
                    break;
                case R.id.cb_push:
                    break;

                case R.id.tv_submit:
                    if (mBinding.cbTerms.isChecked() && mBinding.cbPrivate.isChecked()) {

                        // 알림값 전송
                        setPushStatus();

                        Intent result = new Intent();
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    }
                    break;

            }
        }

        public void getData() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(TermAgreeActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<TermModel> call = service.reqTerm();

            RetrofitCall.enqueueWithRetry(call, new Callback<TermModel>() {

                @Override
                public void onResponse(Call<TermModel> call, Response<TermModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            TermModel resData = (TermModel) response.body();

                            if (resData != null) {

                                mModel.set(resData);

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<TermModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * push 여부 세팅
         */
        public void setPushStatus() {

            final SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(TermAgreeActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqUpdatePushStatus(
                    mBinding.cbPush.isChecked() ? "Y" : "N",
                    "N");

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {

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
