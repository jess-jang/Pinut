package com.mindlinksw.schoolmeals.viewholder;

import android.app.Activity;
import android.content.Intent;

import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.databinding.DateMealItemBinding;
import com.mindlinksw.schoolmeals.databinding.DateMealMenuBinding;
import com.mindlinksw.schoolmeals.databinding.MainItemMealsBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.MainMealItem;
import com.mindlinksw.schoolmeals.model.MainMealModel;
import com.mindlinksw.schoolmeals.model.WeeklyMealMenuItem;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.utils.TextFormatUtils;
import com.mindlinksw.schoolmeals.view.activity.SchoolChooseActivity;
import com.mindlinksw.schoolmeals.view.activity.WeeklyActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메인 상단 급식 데이터
 */
public class DateMealsViewHolder extends RecyclerView.ViewHolder implements Initialize {

    private final String TAG = DateMealsViewHolder.class.getName();

    private Activity mActivity;
    private MainItemMealsBinding mBinding;
    private View mView;
    private ViewModel mViewModel;

    public ArrayList<MainMealItem> mList;
    private GenericRecyclerViewAdapter mAdapter;
    public static boolean isRefresh = true; // 초기값 true
    private Gson mGson = new Gson();

    private int mMaxCount = 5;

    public DateMealsViewHolder(Activity activity, MainItemMealsBinding binding) {
        super(binding.getRoot());
        this.mActivity = activity;
        this.mBinding = binding;
        this.mView = mBinding.getRoot();

        initDataBinding();
        initVariable();
        initEventListener();

    }

    @Override
    public void initDataBinding() {

        try {

            mViewModel = new ViewModel();
            mBinding.setViewModel(mViewModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initLayout(@Nullable View view) {

        try {

            mList = getDateMeals();
            mAdapter = new GenericRecyclerViewAdapter(mActivity, R.layout.date_meal_item, mList) {

                @Override
                public void onBindData(int position, Object model, Object dataBinding) {

                    try {

                        DateMealItemBinding binding = (DateMealItemBinding) dataBinding;

                        ViewModel viewModel = new ViewModel();
                        viewModel.mModel = ((MainMealItem) model);
                        viewModel.mPosition = position;
                        viewModel.setMenuCount();
                        viewModel.getMealType();

                        viewModel.initLayout(binding, (MainMealItem) model);

                        binding.setViewModel(viewModel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            mBinding.rvMeals.setAdapter(mAdapter);
            mAdapter.notifyItemRangeChanged(0, mList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initEventListener() {

    }

    public void onBindView() {

        try {

            mViewModel.setExistSchool();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 급식 데이터 불러오기 API
     */
    public void getData() {

        final String insttCode = SessionSingleton.getInstance(mActivity).getInsttCode();
        if (ObjectUtils.isEmpty(insttCode)) {
            return;
        }

        // 새로고침이 아닐 경우
        if (!isRefresh) {
            return;
        }

        isRefresh = false;
        mViewModel.mProgress.set(true);

        // 학교 선택 여부 확인 후 데이터 출력
        initLayout(null);

        SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(mActivity, SchoolMealsService.class, HostConst.apiHost());
        Call<MainMealModel> call = service.reqMainMealList(insttCode);

        RetrofitCall.enqueueWithRetry(call, new Callback<MainMealModel>() {

            @Override
            public void onResponse(Call<MainMealModel> call, Response<MainMealModel> response) {

                try {

                    if (response.isSuccessful()) {

                        mList.clear();

                        MainMealModel resData = (MainMealModel) response.body();

                        if (resData != null) {

                            Logger.e(TAG, resData.toString());
                            mList.addAll(resData.getList());

                            // 마지막 아이템 일부러 하나 추가함
                            MainMealItem item = new MainMealItem(true);
                            mList.add(item);

                            mAdapter.notifyItemRangeChanged(0, mList.size());

                            setDateMeals(mList);

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mViewModel.mProgress.set(false);
            }

            @Override
            public void onFailure(Call<MainMealModel> call, Throwable t) {
                Logger.e(TAG, t.getMessage());
            }
        });
    }

    /**
     * 급식 데이터 불러옴
     */
    private ArrayList<MainMealItem> getDateMeals() {

        ArrayList<MainMealItem> list = new ArrayList<>();

        try {

            String json = SharedPreferencesUtils.read(mActivity, DataConst.DATE_MEALS, null);

            // 급식 데이터가 없을 경우 return
            if (ObjectUtils.isEmpty(json)) {
                return list;
            }

            list = mGson.fromJson(json, new TypeToken<List<MainMealItem>>() {
            }.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    /**
     * 급식 데이터 저장
     *
     * @param list
     */
    private void setDateMeals(ArrayList<MainMealItem> list) {

        try {

            // json 으로 변환
            String json = mGson.toJson(list);
            Logger.e(TAG, json);

            SharedPreferencesUtils.save(mActivity, DataConst.DATE_MEALS, json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class ViewModel {

        public ObservableBoolean mIsExistSchool = new ObservableBoolean();
        public ObservableBoolean mProgress = new ObservableBoolean();

        public MainMealItem mModel;
        public WeeklyMealMenuItem mMenuModel;
        public int mPosition;
        public int mMenuCount;
        public int mMealIcon;
        public String mMealType;

        public void onClick(View v) {

            Intent intent;

            switch (v.getId()) {
                case R.id.v_choose_school:
                    intent = new Intent(mActivity, SchoolChooseActivity.class);
                    mActivity.startActivityForResult(intent, RequestConst.REFRESH);
                    break;

                case R.id.v_meal:
                    intent = new Intent(mActivity, WeeklyActivity.class);
                    mActivity.startActivity(intent);
                    break;
            }
        }

        // 학교 선택 유무
        public void setExistSchool() {

            mIsExistSchool.set(false);

            if (SessionSingleton.getInstance(mActivity).isExist()) {
                if (SessionSingleton.getInstance(mActivity).select().insttCode != null) {
                    mIsExistSchool.set(true);
                }
            } else {
                if (SharedPreferencesUtils.read(mActivity, DataConst.NONMEMBER_SCHOOL_CODE, null) != null) {
                    mIsExistSchool.set(true);
                }
            }

            if (mIsExistSchool.get()) {
                // 오늘날짜 기준으로 데이터 가져옴
                getData();
            }

        }

        // 메뉴 카운트
        public void setMenuCount() {

            int result = 0;

            try {

                int count = mModel.getMenuCnt();

                // 고정 7개가 넘는 거는 +1 처리
                if (count > mMaxCount) {
                    result = count - mMaxCount;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            mMenuCount = result;
        }

        public void getMealType() {

            try {

                mMealType = TextFormatUtils.getMealType(mModel.getMealType());

                if ("B".equals(mModel.getMealType())) {
                    mMealIcon = R.drawable.ico_date_breakfast;
                } else if ("L".equals(mModel.getMealType())) {
                    mMealIcon = R.drawable.ico_date_lunch;
                } else if ("D".equals(mModel.getMealType())) {
                    mMealIcon = R.drawable.ico_date_dinner;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /***
         * 내부 리스트
         * @param binding
         * @param model
         */
        public void initLayout(DateMealItemBinding binding, MainMealItem model) {

            ArrayList<WeeklyMealMenuItem> list = model.getMenuList();
            if (ObjectUtils.isEmpty(list)) {
                return;
            }

            // 최대 mMaxCount개 허용
            int max = mMaxCount;
            int size = list.size();
            if (size > max) {
                for (int i = size - 1; max <= i; i--) {
                    list.remove(i);
                }
            }

            GenericRecyclerViewAdapter adapter = new GenericRecyclerViewAdapter(mActivity, R.layout.date_meal_menu, list) {

                @Override
                public void onBindData(int position, Object model, Object dataBinding) {

                    DateMealMenuBinding binding = (DateMealMenuBinding) dataBinding;

                    ViewModel viewModel = new ViewModel();
                    viewModel.mMenuModel = (WeeklyMealMenuItem) model;
                    viewModel.mPosition = position;
                    binding.setViewModel(viewModel);

                }
            };

            RecyclerView rvMenu = (RecyclerView) binding.getRoot().findViewById(R.id.rv_menu);
            rvMenu.setAdapter(adapter);
        }
    }
}
