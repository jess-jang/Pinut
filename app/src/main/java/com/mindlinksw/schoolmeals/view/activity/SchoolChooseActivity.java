package com.mindlinksw.schoolmeals.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.GenericViewPagerAdapter;
import com.mindlinksw.schoolmeals.databinding.SchoolChooseActivityBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mindlinksw.schoolmeals.view.fragment.SchoolChooseStep1Fragment;
import com.mindlinksw.schoolmeals.view.fragment.SchoolChooseStep2Fragment;

import org.jetbrains.annotations.Nullable;

public class SchoolChooseActivity extends AppCompatActivity implements Initialize {

    private final String TAG = SchoolChooseActivity.class.getName();

    private SchoolChooseActivityBinding mBinding;
    private GenericViewPagerAdapter mAdapter;

    private SchoolChooseStep1Fragment mStep1Fragment;
    private SchoolChooseStep2Fragment mStep2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.school_choose_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();
    }

    @Override
    public void initDataBinding() {
        mBinding.setViewModel(new SchoolChooseViewModel());
    }

    @Override
    public void initLayout(@Nullable View view) {

        mAdapter = new GenericViewPagerAdapter(getSupportFragmentManager());
        mBinding.vpSchoolChoose.setOffscreenPageLimit(3);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // 학년 정보를 넘기기 위해 미리 세팅
                mStep2Fragment = new SchoolChooseStep2Fragment(mBinding.vpSchoolChoose);
                mStep1Fragment = new SchoolChooseStep1Fragment(mBinding.vpSchoolChoose, mStep2Fragment);

                mAdapter.addFragment(mStep1Fragment);
                if (SessionSingleton.getInstance(SchoolChooseActivity.this).isExist()) {
                    // 비회원 일경우 선택하지 않음
                    mAdapter.addFragment(mStep2Fragment);
                }
                mBinding.vpSchoolChoose.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initEventListener() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog(SchoolChooseActivity.this)
                    .setMessage(getString(R.string.school_finish_confirm))
                    .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick() {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick() {

                        }
                    })
                    .show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * View Model
     */
    public class SchoolChooseViewModel {

        public ObservableInt mCurrentPage = new ObservableInt(); // 현재 페이지


        public ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mCurrentPage.set(position);

                if (position != 0) {
                    Utils.setKeyboardShowHide(SchoolChooseActivity.this, mBinding.getRoot(), false);
                }

                if (mStep1Fragment != null && mStep2Fragment != null) {
                    if (position == 0) {
                        mStep1Fragment.onFocus();
                    } else if (position == 1) {
                        mStep2Fragment.onFocus();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }
}
