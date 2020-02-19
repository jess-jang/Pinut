package com.mindlinksw.schoolmeals.view.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.databinding.WebActivityBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.interfaces.WebViewListener;

import org.jetbrains.annotations.Nullable;

public class WebActivity extends AppCompatActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = WebActivity.class.getName();

    private WebActivityBinding mBinding;
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.web_activity);

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

    }

    @Override
    public void onRefresh() {
        mBinding.webView.reload();
    }

    @Override
    public void initEventListener() {

        mBinding.webView.setOnPageStatusListener(new WebViewListener.OnPageStatusListener() {
            @Override
            public void onPageStarted(String url) {
                mBinding.refresh.setRefreshing(true);
            }

            @Override
            public void onPageFinished(String url) {
                mBinding.refresh.setRefreshing(false);
            }
        });

        mBinding.webView.setOnProgressChangedListener(new WebViewListener.OnProgressChangedListener() {

            @Override
            public void onProgressChanged(int progress) {
                if (progress == 100) {
                    mBinding.refresh.setRefreshing(false);
                }
            }
        });
    }

    /**
     * View Model
     */
    public class ViewModel {

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
            }
        }
    }

}
