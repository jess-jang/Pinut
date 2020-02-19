package com.mindlinksw.schoolmeals.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.SearchMealActivityBinding;
import com.mindlinksw.schoolmeals.databinding.SearchMealRecentItemBinding;
import com.mindlinksw.schoolmeals.databinding.SearchMealResultItemBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.RecentModel;
import com.mindlinksw.schoolmeals.model.SearchMealItem;
import com.mindlinksw.schoolmeals.model.SearchMealMenu;
import com.mindlinksw.schoolmeals.model.SearchMealModel;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.utils.TextFormatUtils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMealActivity extends AppCompatActivity implements Initialize {

    private final String TAG = SearchMealActivity.class.getName();

    private SearchMealActivityBinding mBinding;
    private ViewModel mViewModel;

    private ArrayList<SearchMealItem> mSearchList;
    private ArrayList<RecentModel> mRecentList;

    private GenericRecyclerViewAdapter mSearchAdapter;
    private GenericRecyclerViewAdapter mRecentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.search_meal_activity);

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

        // list
        mSearchList = new ArrayList<>();
        mSearchAdapter = new GenericRecyclerViewAdapter(this, R.layout.search_meal_result_item, mSearchList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                SearchMealResultItemBinding binding = (SearchMealResultItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mSearchModel.set((SearchMealItem) model);
                viewModel.mSearchText.set(mViewModel.mSearchText.get());
                viewModel.getMealType();

                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvSearch.setAdapter(mSearchAdapter);

        // recent
        mRecentList = new ArrayList<>();
        mRecentAdapter = new GenericRecyclerViewAdapter(this, R.layout.search_meal_recent_item, mRecentList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                SearchMealRecentItemBinding binding = (SearchMealRecentItemBinding) dataBinding;

                ViewModel viewModel = new ViewModel();
                viewModel.mRecentModel.set((RecentModel) model);
                viewModel.mPosition.set(position);

                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvRecent.setAdapter(mRecentAdapter);
    }

    @Override
    public void initVariable() {

        // 인식 딜레이 초기화
        mViewModel.onRecognizeDelay();

        // 최근기록
        mViewModel.readRecent();

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
                                mViewModel.search(true, v.getText().toString().trim());

//                                // 키보드 내림
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Utils.setKeyboardShowHide(SearchMealActivity.this, mBinding.getRoot(), false);
//                                    }
//                                });
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

        public ObservableField<SearchMealItem> mSearchModel = new ObservableField<>();
        public ObservableField<RecentModel> mRecentModel = new ObservableField<>();

        public ObservableField<String> mMealType = new ObservableField<>();
        public ObservableField<String> mSearchText = new ObservableField<>();
        public ObservableBoolean mIsSearching = new ObservableBoolean();
        public ObservableBoolean mIsEmpty = new ObservableBoolean();
        public ObservableInt mPosition = new ObservableInt();

        // Delay Handler - 특정시간 입력 이후 서버 데이터 전송
        private Runnable mDelayRunnable;
        private Handler mDelayHandler;

        public void onClick(View v) {
            try {

                switch (v.getId()) {
                    case R.id.tv_finish:
                        finish();
                        break;

                    case R.id.iv_clear:
                        mBinding.etSearch.setText("");
                        break;

                    case R.id.tv_remove_all:
                        SharedPreferencesUtils.remove(SearchMealActivity.this, TAG);
                        mRecentList.clear();
                        mRecentAdapter.notifyDataSetChanged();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onClick(View v, int position) {
            try {
                switch (v.getId()) {
                    case R.id.iv_remove:
                        removeRecent(position);
                        mRecentList.remove(position);
                        mRecentAdapter.notifyItemRemoved(position);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecentAdapter.notifyDataSetChanged();
                            }
                        }, 200);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onClick(View v, String text) {
            try {
                switch (v.getId()) {
                    case R.id.tv_recent:
                        mBinding.etSearch.setText(text);
                        search(false, text);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                    search(false, mBinding.etSearch.getText().toString());
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

                mIsSearching.set(s.length() > 0);
                mSearchText.set(s.toString());

                onRemoveDelay();
                onRunDelay();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void getMealType() {
            String mealType = TextFormatUtils.getMealType(mSearchModel.get().getMealType());
            mMealType.set(mealType);
        }

        /**
         * 최근기록 불러옴
         */
        public void readRecent() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 최신 데이터 반영
                        mRecentList.clear();
                        String after = SharedPreferencesUtils.read(SearchMealActivity.this, TAG, null);
                        JSONArray jsonArray = new JSONArray(after);
                        JsonParser parser = new JsonParser();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String order = jsonArray.getString(i);
                            JsonObject object = (JsonObject) parser.parse(order);
                            mRecentList.add(new RecentModel(object.get("search").getAsString(), object.get("time").getAsLong()));
                        }

                        // 최신게 맨 위로 올라오게
                        Collections.reverse(mRecentList);

                        mRecentAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 최근기록 저장
         *
         * @param s
         */
        public void saveRecent(final String s) {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        // 저장할 데이터
                        RecentModel recent = new RecentModel(s, System.currentTimeMillis());
                        Gson gson = new Gson();
                        String json = gson.toJson(recent);
                        Logger.e(TAG, "저장할데이터 : " + json);

                        // 저장된 데이터 불러오기
                        String data = SharedPreferencesUtils.read(SearchMealActivity.this, TAG, null);
                        Logger.e(TAG, "기존데이터 : " + data);

                        JSONArray array;
                        if (data != null) {
                            array = new JSONArray(data);
                        } else {
                            array = new JSONArray();
                        }

                        // 데이터 저장
                        array.put(json);
                        SharedPreferencesUtils.save(SearchMealActivity.this, TAG, array.toString());

                        readRecent();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 최근기록 삭제
         */
        public void removeRecent(int position) {

            try {

                // 저장된 데이터 불러오기
                String data = SharedPreferencesUtils.read(SearchMealActivity.this, TAG, null);
                Logger.e(TAG, "기존데이터 : " + data);

                JSONArray array;
                if (data != null) {
                    array = new JSONArray(data);
                } else {
                    array = new JSONArray();
                }

                array.remove(position);

                // 데이터 저장
                SharedPreferencesUtils.save(SearchMealActivity.this, TAG, array.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param direct - 직접 엔터 쳐서 검색
         * @param s
         */
        public void search(final boolean direct, final String s) {

            // 딜레이 해제
            onRemoveDelay();

            if (s.length() < 1) {
                mSearchList.clear();
                mSearchAdapter.notifyDataSetChanged();
                return;
            }

            // 학교 코드
            String insttCode = SessionSingleton.getInstance(SearchMealActivity.this).getInsttCode();

            if (ObjectUtils.isEmpty(insttCode)) {
                return;
            }

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(SearchMealActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<SearchMealModel> call = service.reqSearchMeal(
                    s,
                    7,
                    insttCode);

            RetrofitCall.enqueueWithRetry(call, new Callback<SearchMealModel>() {

                @Override
                public void onResponse(Call<SearchMealModel> call, Response<SearchMealModel> response) {

                    try {

                        mSearchList.clear();

                        if (s.length() < 1) {
                            mSearchAdapter.notifyDataSetChanged();
                            return;
                        }

                        if (response.isSuccessful()) {
                            SearchMealModel resData = (SearchMealModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());

                                for (SearchMealItem item : resData.getList()) {
                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 0; i < item.getMenuList().size(); i++) {
                                        SearchMealMenu menu = item.getMenuList().get(i);
                                        builder.append(menu.getMenuNm());

                                        if (i < item.getMenuList().size() - 1) {
                                            builder.append(", ");
                                        }
                                    }

                                    item.setDisplayMenu(builder.toString());
                                }

                                mSearchList.addAll(resData.getList());

                                // 최근기록 저장
                                if (resData.getList().size() > 0 && direct) {
                                    saveRecent(s);
                                }
                            }
                        }
                        mIsEmpty.set(mSearchList.size() < 1);
                        mSearchAdapter.notifyDataSetChanged();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<SearchMealModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }
    }
}
