package com.mindlinksw.schoolmeals.view.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.ad.AdlibConstants;
import com.mindlinksw.schoolmeals.adapter.GenericViewPagerAdapter;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.databinding.WeeklyActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.WeeklyDisplayModel;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.view.fragment.WeeklyFragment;
import com.mocoplex.adlib.AdlibAdViewContainer;
import com.mocoplex.adlib.AdlibManager;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 주간 식단
 */
public class WeeklyActivity extends BaseActivity implements Initialize {

    private final String TAG = WeeklyActivity.class.getName();
    private final int MAX_DATE = 3;

    private WeeklyActivityBinding mBinding;
    private GenericViewPagerAdapter mAdapter;
    private ViewModel mViewModel;

    public static HashMap<Integer, ArrayList<WeeklyDisplayModel>> mWeeklyMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.weekly_activity);

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
    public void initVariable() {

        try {

            // 타이틀 정보
            mViewModel.setTitle();

            if (mWeeklyMap == null) {
                mWeeklyMap = new HashMap<>();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initLayout(@Nullable View view) {

        try {

            mAdapter = new GenericViewPagerAdapter(getSupportFragmentManager());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < MAX_DATE; i++) {

                        Bundle bundle = new Bundle();
                        bundle.putInt(DataConst.POSITION, i);

                        WeeklyFragment fragment = new WeeklyFragment();
                        fragment.setArguments(bundle);

                        mAdapter.addFragment(fragment);

                    }

                    mBinding.vpWeekly.setAdapter(mAdapter);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initEventListener() {
        recordAnalytics("weeklyActivity","weeklyActivityView","activity");
    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableInt mCurrentPage = new ObservableInt(); // 현재 페이지
        public ObservableField<String> mTitle = new ObservableField<>(); // 타이틀

        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_back:
                    finish();
                    break;

                case R.id.iv_before:
                    if (mBinding.vpWeekly.getCurrentItem() > 0) {
                        mBinding.vpWeekly.setCurrentItem(mBinding.vpWeekly.getCurrentItem() - 1, true);
                    }
                    break;

                case R.id.iv_next:
                    if (mBinding.vpWeekly.getCurrentItem() < mAdapter.getCount() - 1) {
                        mBinding.vpWeekly.setCurrentItem(mBinding.vpWeekly.getCurrentItem() + 1, true);
                    }
                    break;

                case R.id.iv_search:
                    // 식단 검색
                    Intent intent = new Intent(WeeklyActivity.this, SearchMealActivity.class);
                    startActivity(intent);
                    break;
            }
        }

        /**
         * 타이틀 세팅
         */
        public void setTitle() {
            try {
                mTitle.set(SessionSingleton.getInstance(WeeklyActivity.this).getInsttNm());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                try {

                    mCurrentPage.set(position);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }
}
