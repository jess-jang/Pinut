package com.mindlinksw.schoolmeals.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.AllergyActivityBinding;
import com.mindlinksw.schoolmeals.databinding.AllergyItemBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.AllergyItem;
import com.mindlinksw.schoolmeals.model.AllergyModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.viewholder.DateMealsViewHolder;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllergyActivity extends AppCompatActivity implements Initialize {

    private String TAG = AllergyActivity.class.getName();

    private AllergyActivityBinding mBinding;
    private ViewModel mViewModel;
    private GenericRecyclerViewAdapter mAdapter;
    private ArrayList<AllergyItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.allergy_activity);

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

        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(AllergyActivity.this, R.layout.allergy_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                AllergyItemBinding binding = (AllergyItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((AllergyItem) model);
                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvAllergy.setAdapter(mAdapter);
    }

    @Override
    public void initVariable() {

        mViewModel.search();

    }

    @Override
    public void initEventListener() {

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<AllergyItem> mModel = new ObservableField<>();
        public ObservableBoolean mIsSubmit = new ObservableBoolean();

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.ll_submit:
                    DateMealsViewHolder.isRefresh = true;
                    setData();
                    break;
            }

        }

        public void onClick(View v, AllergyItem model) {

            switch (v.getId()) {
                case R.id.tv_allergy:
                    // 선택된 값 색상 변경
                    if (model.isChecked()) {
                        model.setChecked(false);
                    } else {
                        model.setChecked(true);
                    }
                    mAdapter.notifyDataSetChanged();
                    mViewModel.mIsSubmit.set(true);
                    break;
            }
        }

        // 알레르기 항목 조회
        public void search() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(AllergyActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<AllergyModel> call = service.reqAllergy();

            RetrofitCall.enqueueWithRetry(call, new Callback<AllergyModel>() {

                @Override
                public void onResponse(Call<AllergyModel> call, Response<AllergyModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            AllergyModel resData = (AllergyModel) response.body();

                            if (resData != null) {

                                mList.clear();
                                mList.addAll(resData.getList());

                                // 체크된 항목 세팅
                                int index = 0;
                                for (AllergyItem item : mList) {
                                    if ("Y".equals(item.getTargetAt())) {
                                        item.setChecked(true);
                                    } else {
                                        item.setChecked(false);
                                    }
                                    mList.set(index, item);
                                    index++;
                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<AllergyModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        // 알레르기 항목 세팅
        public void setData() {

            try {

                // 데이터 가공
                JsonArray array = new JsonArray();
                for (AllergyItem item : mList) {
                    if (item.isChecked()) {
                        JsonObject code = new JsonObject();
                        code.addProperty("code", String.valueOf(item.getCode()));
                        array.add(code);
                    }
                }

                JsonObject object = new JsonObject();
                object.add("list", array);
                Logger.e(TAG, object.toString());

                SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(AllergyActivity.this, SchoolMealsService.class, HostConst.apiHost());
                Call<ResponseModel> call = service.reqAllergyInsert(object.toString());

                RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        try {

                            if (response.isSuccessful()) {
                                ResponseModel resData = (ResponseModel) response.body();

                                if (resData != null) {
                                    if ("success".equals(resData.getCode())) {

                                        Snackbar.make(mBinding.getRoot(), getString(R.string.allergy_submit_success), Snackbar.LENGTH_SHORT).show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, Snackbar.LENGTH_SHORT + 300);

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}


