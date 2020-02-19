package com.mindlinksw.schoolmeals.view.fragment;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubePlayer;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.databinding.VideoFrameFragmentBinding;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.model.BoardData;
import com.mindlinksw.schoolmeals.utils.ImageLoadUtils;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils;
import com.mindlinksw.schoolmeals.view.youtubeview.listener.YouTubeEventListener;
import com.mindlinksw.schoolmeals.view.youtubeview.models.ImageLoader;
import com.mindlinksw.schoolmeals.view.youtubeview.models.YouTubePlayerType;


public class VideoFrameFragment extends Fragment implements Initialize {

    private static final String TAG = VideoFrameFragment.class.getName();

    private VideoFrameFragmentBinding mBinding;

    private YouTubePlayer mPlayer;
    private boolean mIsFullScreen = false;

    private BoardData mModel;
    private int mBoardId;
    private String mYoutubeMediaKey;
    private String mYoutubeMediaUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.video_frame_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initLayout(view);
        initVariable();
        initEventListener();

    }

    @Override
    public void onPause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
        super.onPause();
    }

    @Override
    public void initDataBinding() {
    }

    @Override
    public void initLayout(View view) {

    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initEventListener() {

    }

    /**
     * Full Screen 여부 반환
     *
     * @return
     */
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    /**
     * Full Screen 여부 반환
     *
     * @return
     */
    public void setFullScreen(boolean flag) {
        if (mPlayer != null) {
            mPlayer.setFullscreen(flag);
        }
    }

    public void initYoutube(final BoardData model) {

        if (ObjectUtils.isEmpty(model)) {
            return;
        }

        this.mModel = model;
        this.mBoardId = model.getBoardId();
        this.mYoutubeMediaKey = model.getYoutubeMediaKey();
        this.mYoutubeMediaUrl = model.getYoutubeMediaUrl();

        try {

            final String key = mYoutubeMediaKey + "_" + mBoardId;

            if (ObjectUtils.isEmpty(mBoardId)) {
                return;
            }

            // youtube
            mBinding.youtubePlayerView.initPlayer(
                    getString(R.string.google_key),
                    mYoutubeMediaKey,
                    mYoutubeMediaUrl,
                    YouTubePlayerType.STRICT_NATIVE,
                    this,
                    key,
                    imageLoader,
                    new YouTubeEventListener() {

                        @Override
                        public void setYoutubePlayer(YouTubePlayer player) {
                            mPlayer = player;
                        }

                        @Override
                        public void onReady() {
                            Logger.e(TAG, "onReady");
                        }

                        @Override
                        public void onPlay(int currentTime) {
                            Logger.e(TAG, "onPlay : " + currentTime);
                        }

                        @Override
                        public void onPause(int currentTime) {
                            Logger.e(TAG, "onPause : " + currentTime);
                            SharedPreferencesUtils.save(getActivity(), key, currentTime);
                        }

                        @Override
                        public void onStop(int currentTime, int totalDuration) {
                            Logger.e(TAG, "onStop : " + currentTime + " / " + totalDuration);
                            SharedPreferencesUtils.save(getActivity(), key, currentTime);
                        }

                        @Override
                        public void onBuffering(int currentTime, boolean isBuffering) {
                            Logger.e(TAG, "onBuffering : " + currentTime + " / " + isBuffering);
                        }

                        @Override
                        public void onSeekTo(int currentTime, int newPositionMillis) {
                            Logger.e(TAG, "onSeekTo : " + currentTime + " / " + newPositionMillis);
                        }

                        @Override
                        public void onInitializationFailure(String error) {
                            Logger.e(TAG, "onInitializationFailure : " + error);
                            Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNativeNotSupported() {
                            Logger.e(TAG, "onNativeNotSupported");
                        }

                        @Override
                        public void onCued() {
                            Logger.e(TAG, "onCued");
                        }

                        @Override
                        public void onFullscreen(boolean isFullScreen) {
                            Logger.e(TAG, "isFullScreen : " + isFullScreen);
                            mIsFullScreen = isFullScreen;
                        }
                    });

            mBinding.youtubePlayerView.handleBindPlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageLoader imageLoader = new ImageLoader() {
        @Override
        public void loadImage(@NonNull ImageView imageView, @NonNull String url, int height, int width) {
            ImageLoadUtils.setLoadImage(imageView, mModel.getThumbnailUrl(), width, height);
        }
    };
}