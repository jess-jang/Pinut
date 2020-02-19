package com.mindlinksw.schoolmeals.utils;

import android.os.Build;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.transition.Slide;
import android.view.Gravity;

public class FragmentUtils {

    public interface Transition {
        int RIGHT_IN = 0;
        int BOTTOM_IN = 1;
    }

    /**
     * Add Fragment
     *
     * @param manager
     * @param container
     * @param fragment
     */
    public static void addFragment(FragmentManager manager, int container, Fragment fragment) {

        try {

            addFragment(manager, container, fragment, -1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Add Fragment for Animate
     *
     * @param manager
     * @param container
     * @param fragment
     * @param transition
     */
    public static void addFragment(FragmentManager manager, int container, Fragment fragment, int transition) {

        try {

            // 중복체크
            if (!ObjectUtils.isEmpty(getFragment(manager, fragment))) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (transition == Transition.RIGHT_IN) {
                    fragment.setEnterTransition(new Slide(Gravity.RIGHT));
                    fragment.setExitTransition(new Slide(Gravity.LEFT));
                } else if (transition == Transition.BOTTOM_IN) {
                    fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                    fragment.setExitTransition(new Slide(Gravity.TOP));
                }
            }

            manager.beginTransaction()
                    .add(container, fragment, fragment.getClass().getName())
                    .addToBackStack(fragment.getClass().getName())
                    .commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Add Fragment
     *
     * @param manager
     * @param container
     * @param fragment
     */
    public static void addFragmentNoBackStack(FragmentManager manager, int container, Fragment fragment) {

        try {

            // 중복체크
            if (!ObjectUtils.isEmpty(getFragment(manager, fragment))) {
                return;
            }

            manager.beginTransaction()
                    .add(container, fragment, fragment.getClass().getName())
                    .commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Replace Fragment
     *
     * @param manager
     * @param container
     * @param fragment
     */
    public static void replaceFragment(FragmentManager manager, int container, Fragment fragment) {

        try {

            replaceFragment(manager, container, fragment, Transition.RIGHT_IN);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Replace Fragment for Animate
     *
     * @param manager
     * @param container
     * @param fragment
     * @param transition
     */
    public static void replaceFragment(FragmentManager manager, int container, Fragment fragment, int transition) {

        try {

            // 중복체크
            if (!ObjectUtils.isEmpty(getFragment(manager, fragment))) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (transition == Transition.RIGHT_IN) {
                    fragment.setEnterTransition(new Slide(Gravity.RIGHT));
                    fragment.setExitTransition(new Slide(Gravity.LEFT));
                } else if (transition == Transition.BOTTOM_IN) {
                    fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                    fragment.setExitTransition(new Slide(Gravity.TOP));
                }
            }

            manager.beginTransaction()
                    .replace(container, fragment, fragment.getClass().getName())
                    .addToBackStack(fragment.getClass().getName())
                    .commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add Fragment
     *
     * @param manager
     * @param container
     * @param fragment
     */
    public static void replaceFragmentNoBackStack(FragmentManager manager, int container, Fragment fragment) {

        try {

            // 중복체크
            if (!ObjectUtils.isEmpty(getFragment(manager, fragment))) {
                return;
            }

            manager.beginTransaction()
                    .replace(container, fragment, fragment.getClass().getName())
                    .commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Remove Fragment
     *
     * @param manager
     */
    public static void removeFragment(FragmentManager manager) {
        try {
            manager.popBackStack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove All Fragment
     *
     * @param manager
     */
    public static void removeAllFragment(FragmentManager manager) {
        try {
            manager.popBackStackImmediate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description Get Tag
     */
    public static String getTag(Fragment fragment) {
        try {
            return fragment.getClass().getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @author 장세진
     * @description Get Fragment
     */
    public static Fragment getFragment(FragmentManager manager, Fragment fragment) {
        try {
            if (manager != null) {
                return manager.findFragmentByTag(fragment.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
