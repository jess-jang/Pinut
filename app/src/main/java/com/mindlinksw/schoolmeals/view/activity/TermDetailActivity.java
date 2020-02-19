package com.mindlinksw.schoolmeals.view.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.Style;
import com.mindlinksw.schoolmeals.databinding.TermDetailActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.TermModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermDetailActivity extends AppCompatActivity implements Initialize {

    private final String TAG = TermDetailActivity.class.getName();

    public TermDetailActivityBinding mBinding;
    public ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.term_detail_activity);

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
            mViewModel.setType(intent.getIntExtra(DataConst.TYPE, -1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initEventListener() {

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<String> mTitle = new ObservableField();
        public ObservableField<String> mText = new ObservableField();

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
            }
        }

        // 타입설정
        public void setType(int type) {
            switch (type) {
                case Style.TERM_TYPE.TERM:
                    mTitle.set(getString(R.string.terms_term));
                    mViewModel.getData(Style.TERM_TYPE.TERM);
                    break;

                case Style.TERM_TYPE.PRIVATE:
                    mTitle.set(getString(R.string.terms_private));
                    mViewModel.getData(Style.TERM_TYPE.PRIVATE);
                    break;
            }
        }

        public void getData(final int type) {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(TermDetailActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<TermModel> call = service.reqTerm();

            RetrofitCall.enqueueWithRetry(call, new Callback<TermModel>() {

                @Override
                public void onResponse(Call<TermModel> call, Response<TermModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            TermModel resData = (TermModel) response.body();

                            if (resData != null) {

                                Logger.e(TAG, resData.toString());
                                switch (type) {
                                    case Style.TERM_TYPE.TERM:
                                        mText.set(resData.getAccessTerms());
                                        break;

                                    case Style.TERM_TYPE.PRIVATE:
                                        mText.set(resData.getPrsnlPolicy());
                                        break;
                                }

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
    }
}
