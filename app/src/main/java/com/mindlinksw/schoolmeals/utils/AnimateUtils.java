package com.mindlinksw.schoolmeals.utils;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimateUtils {

    public static Handler mHandler;

    public static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    /**
     * 알파 애니메이션
     *
     * @param view
     * @param duration
     * @param start
     * @param end
     */
    public static void setAlpha(final View view, long duration, float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setAlpha((float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * * 알파 애니메이션 - 딜레이
     *
     * @param view
     * @param duration
     * @param start
     * @param end
     * @param delay
     */
    public static void setAlpha(final View view, final long duration, final float start, final float end, int delay) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAlpha(view, duration, start, end);
            }
        }, delay);
    }

    /**
     * TranslationY
     *
     * @param view
     * @param duration
     * @param start
     * @param end
     */
    public static void setTranslationY(final View view, final long duration, final float start, final float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * TranslationY - 딜레이
     *
     * @param view
     * @param duration
     * @param start
     * @param end
     */
    public static void setTranslationY(final View view, final long duration, final float start, final float end, int delay) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTranslationY(view, duration, start, end);
            }
        }, delay);
    }
}
