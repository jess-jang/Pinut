package com.mindlinksw.schoolmeals.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.SchoolChooseStep2Binding;
import com.mindlinksw.schoolmeals.databinding.SchoolChooseStep2ItemBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.model.SchoolSearchItem;
import com.mindlinksw.schoolmeals.model.SessionModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchoolChooseStep2Fragment extends Fragment implements Initialize {

    private String TAG = SchoolChooseStep2Fragment.class.getName();

    private SchoolChooseStep2Binding mBinding;
    private ViewModel mViewModel;
    private GenericRecyclerViewAdapter mAdapter;
    private ArrayList<GradeModel> mList;

    private ViewPager mViewPager;
    private int mMaxGrade = 0;

    private SchoolSearchItem mSchoolModel; // Step1 에서 넘어온 데이터
    private String mInsttId;
    private String mYearly;


    @SuppressLint("ValidFragment")
    public SchoolChooseStep2Fragment(ViewPager vpSchoolChoose) {
        mViewPager = vpSchoolChoose;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.school_choose_step2, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initLayout(view);
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

        // list
        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(getActivity(), R.layout.school_choose_step2_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                SchoolChooseStep2ItemBinding binding = (SchoolChooseStep2ItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((GradeModel) model);
                viewModel.mPosition.set(position);

                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvGrade.setAdapter(mAdapter);

    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initEventListener() {

    }

    public void onFocus() {
        if (mMaxGrade == 0) {
            Snackbar.make(mBinding.getRoot(), getString(R.string.school_need_step1), Snackbar.LENGTH_SHORT).show();
            mViewPager.setCurrentItem(0, true);
        }
    }

    public void setSchoolData(SchoolSearchItem model) {

        try {

            this.mMaxGrade = model.getMaxGrade();
            this.mSchoolModel = model;

            mList.clear();
            for (int i = 1; i <= mMaxGrade; i++) {
                mList.add(new GradeModel(i));
            }

            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<GradeModel> mModel = new ObservableField<>();
        public ObservableBoolean mIsSubmit = new ObservableBoolean();
        public ObservableInt mPosition = new ObservableInt();

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    new AlertDialog(getContext())
                            .setMessage(getString(R.string.school_finish_confirm))
                            .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {

                                }
                            })
                            .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                                @Override
                                public void onClick() {

                                }
                            })
                            .show();
                    break;

                case R.id.tv_submit:
                    mViewModel.setData();
                    break;
            }
        }

        public void onClick(View v, int position, GradeModel model) {
            switch (v.getId()) {
                case R.id.ll_grade:
                    // 선택된 값 색상 변경

                    boolean isSelf = false;
                    for (GradeModel item : mList) {

                        if (item.isChecked() && item.equals(model)) {
                            isSelf = true;
                        }

                        item.setChecked(false);
                    }

                    if (!isSelf) {
                        model.setChecked(true);
                        mYearly = String.valueOf(model.getGrade());

                        mViewModel.mIsSubmit.set(true);
                    } else {
                        mViewModel.mIsSubmit.set(false);
                    }

                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }

        public void setData() {


//            "rownum":"1",
//                    "insttId":19882,
//                    "insttNm":"가곡고등학교",
//                    "adres":"강원도 삼척시",
//                    "insttCode":"K100000350",
//                    "maxGrade":"3"

            final SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(getActivity(), SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqUpdateSchool(String.valueOf(mSchoolModel.getInsttCode()), mYearly);

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());
                                if ("success".equals(resData.getCode())) {

                                    mIsSubmit.set(true);
                                    Snackbar.make(mBinding.getRoot(), getString(R.string.school_success), Snackbar.LENGTH_SHORT).show();

                                    Intent intent = getActivity().getIntent();
                                    getActivity().setResult(Activity.RESULT_OK, intent);
                                    getActivity().finish();

                                    // 학교 코드 삽입
                                    SessionModel session = SessionSingleton.getInstance(getActivity()).select();
                                    session.setInsttCode(mSchoolModel.getInsttCode());
                                    session.setInsttNm(mSchoolModel.getInsttNm());

                                    SessionSingleton.getInstance(getActivity()).insert(session);

                                } else {
                                    mIsSubmit.set(false);
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
    }


    public class GradeModel {

        int grade;
        boolean isChecked;

        public GradeModel(int grade) {
            this.grade = grade;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }


}
