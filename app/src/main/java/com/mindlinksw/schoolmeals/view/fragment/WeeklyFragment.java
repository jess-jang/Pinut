package com.mindlinksw.schoolmeals.view.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.WeeklyAdapter;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.Style;
import com.mindlinksw.schoolmeals.databinding.WeeklyFragmentBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.WeeklyDisplayModel;
import com.mindlinksw.schoolmeals.model.WeeklyMealDateItem;
import com.mindlinksw.schoolmeals.model.WeeklyMealItem;
import com.mindlinksw.schoolmeals.model.WeeklyMealMenuItem;
import com.mindlinksw.schoolmeals.model.WeeklyMealModel;
import com.mindlinksw.schoolmeals.model.WeeklyMealTypeItem;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.TextFormatUtils;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mindlinksw.schoolmeals.view.activity.WeeklyActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 주간식단 메뉴
 */
public class WeeklyFragment extends Fragment implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private String TAG = WeeklyFragment.class.getName();

    private WeeklyFragmentBinding mBinding;
    public ViewModel mViewModel;

    private WeeklyAdapter mAdapter;
    public ArrayList<WeeklyDisplayModel> mList;
    private String mSearchDate;

    private int mPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.weekly_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initVariable();
        initLayout(view);
        initEventListener();

        getData();

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
    public void initVariable() {

        try {

            if (getArguments() != null) {
                // 다음주 구하기
                mPosition = getArguments().getInt(DataConst.POSITION);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initLayout(@Nullable View view) {

        try {

            mList = new ArrayList<>();

            mAdapter = new WeeklyAdapter(getActivity(), mList);
            mBinding.rvWeekly.setLayoutManager(new LinearLayoutManager(getActivity()));
            mBinding.rvWeekly.setAdapter(mAdapter);

            mBinding.refresh.setOnRefreshListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initEventListener() {

    }

    @Override
    public void onRefresh() {

        try {

            mList.clear();
            getData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 급식 데이터 불러오기
     */
    public void getData() {

        try {

            ArrayList<WeeklyDisplayModel> list = WeeklyActivity.mWeeklyMap.get(mPosition);
            if (!ObjectUtils.isEmpty(list)) {

                // 타이틀 세팅
                setTitle();

                // 보임 여부
                mViewModel.mExist.set(true);

                mList.addAll(list);
                mAdapter.notifyDataSetChanged();
                mBinding.refresh.setRefreshing(false);
                return;

            }

            // 학교 코드
            String insttCode = SessionSingleton.getInstance(getActivity()).getInsttCode();
            if (ObjectUtils.isEmpty(insttCode)) {
                return;
            }

            mBinding.refresh.setRefreshing(true);
            mViewModel.mExist.set(false);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, mPosition * 7);
            mSearchDate = Utils.getConvertDateFormat(calendar.getTimeInMillis(), "yyyy-MM-dd");
            Logger.e(TAG, "mPosition : " + mPosition + " / " + "mSearchDate : " + mSearchDate + " / " + "mInsttCode : " + insttCode);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(getActivity(), SchoolMealsService.class, HostConst.apiHost());
            Call<WeeklyMealModel> call = service.reqWeeklyMeal(
                    mSearchDate,
                    insttCode);

            RetrofitCall.enqueueWithRetry(call, new Callback<WeeklyMealModel>() {

                @Override
                public void onResponse(Call<WeeklyMealModel> call, Response<WeeklyMealModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            WeeklyMealModel resData = (WeeklyMealModel) response.body();
                            if (resData != null) {

                                Logger.e(TAG, resData.toString());

                                WeeklyMealItem result = resData.getResult();

                                // 타이틀 세팅
                                setTitle();

                                // 날짜
                                for (WeeklyMealDateItem item : result.getDateList()) {

                                    if (ObjectUtils.isEmpty(item.getBreakfast().getMenuList())
                                            && ObjectUtils.isEmpty(item.getLunch().getMenuList())
                                            && ObjectUtils.isEmpty(item.getDinner().getMenuList())) {
                                        // 아침점심저녁 데이터가 없을 경우 데이터를 넣지 않는다.
                                    } else {

                                        mViewModel.mExist.set(true);

                                        String weekDate = Utils.getConvertDateFormat(item.getDate(), "yyyy-MM-dd", "MM.dd(E)");
                                        mList.add(new WeeklyDisplayModel(Style.WEEKLY.TITLE, weekDate, ""));

                                        // 데이터 처리
                                        setMealData(item.getBreakfast());
                                        setMealData(item.getLunch());
                                        setMealData(item.getDinner());

                                    }
                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // static map 에 데이터 세팅
                    WeeklyActivity.mWeeklyMap.put(mPosition, mList);

                    mBinding.refresh.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<WeeklyMealModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 타이틀 세팅
     */
    public void setTitle() {

        try {
            // 타이틀
            mViewModel.mTitle.set(TextFormatUtils.getWeekText(mPosition + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 메뉴 세팅
     *
     * @param item
     */
    public void setMealData(WeeklyMealTypeItem item) {

        // 메뉴 세팅
        if (!ObjectUtils.isEmpty(item.getMenuList())) {

            StringBuilder menus = new StringBuilder();
            int index = 0;
            for (WeeklyMealMenuItem menu : item.getMenuList()) {

                if ("Y".equals(menu.getAlrgyYn())) {
                    String color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.enable_color) & 0x00ffffff);
                    menus.append("<span style='background-color:" + color + ";'>");
                    menus.append(menu.getMenuNm());
                    menus.append("</span>");
                } else {
                    menus.append(menu.getMenuNm());
                }

                menus.append(index == item.getMenuList().size() - 1 ? "" : ", ");


                index++;
            }

            mList.add(new WeeklyDisplayModel(Style.WEEKLY.MEAL, menus.toString(), item.getMealType()));
        }
    }


    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<String> mTitle = new ObservableField<>();
        public ObservableBoolean mExist = new ObservableBoolean();

    }
}


