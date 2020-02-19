package com.mindlinksw.schoolmeals.utils;

import androidx.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.view.custom.DividerDecoration;
import com.mindlinksw.security.AES256Util;
import com.pixplicity.htmlcompat.HtmlCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import be.tim.rijckaert.snaprecyclerview.GravitySnapHelper;


public class BindingAdapterUtils {

    public static String TAG = BindingAdapterUtils.class.getName();

    /**
     * 특정 텍스트 컬러 변경
     *
     * @param view
     * @param search      - 엑센트를 부여할 텍스트
     * @param text        - 원본 텍스트
     * @param accentColor - 컬러
     */
    @BindingAdapter({"accentSearch", "accentText", "accentColor"})
    public static void setAccentTextColor(final TextView view, final String search, final String text, final int accentColor) {
        try {


            int start = 0;
            int end = 0;

            if (search != null && text != null) {
                start = text.indexOf(search);
                end = start + search.length();

//                        Logger.e(TAG, "start : " + start);
//                        Logger.e(TAG, "end : " + end);

                if (start > -1) {
                    SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                    ssb.setSpan(new ForegroundColorSpan(accentColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.setText(ssb);
                } else {
                    view.setText(text);
                }
            } else {
                view.setText(search);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 특정 폰트 변경 (굵게)
     *
     * @param view
     * @param search
     * @param text
     */
    @BindingAdapter({"accentSearch", "accentText"})
    public static void setAccentTextFont(final TextView view, final String search, final String text) {
        try {

            int start = 0;
            int end = 0;

            if (search != null && text != null) {
                start = text.indexOf(search);
                end = start + search.length();
                Logger.e(TAG, "start : " + start);
                Logger.e(TAG, "end : " + end);

                if (start > -1) {

                    SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                    ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    view.setText(ssb);

                } else {
                    view.setText(text);
                }
            } else {
                view.setText(search);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 배경 색상 변경
     */
    @BindingAdapter({"backgroundColor"})
    public static void setAccentTextColor(final View view, final int color) {
        try {

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setBackgroundColor(color);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 배경 색상 String 변경
     */
    @BindingAdapter({"backgroundColor"})
    public static void setBackgroundColor(final View view, final String color) {
        try {

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setBackgroundColor(Color.parseColor(color));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 다른 형태의 포맷으로 날짜 타입 변경
     *
     * @param view
     * @param date
     * @param originalFormat
     * @param convertFormat
     */
    @BindingAdapter({"date", "originalFormat", "convertFormat"})
    public static void setConvertDateFormat(final TextView view, final String date, final String originalFormat, final String convertFormat) {
        try {

            view.post(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (TextUtils.isEmpty(date)) {
                            return;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat(originalFormat, Locale.KOREA);
                        Date d = sdf.parse(date);

                        sdf = new SimpleDateFormat(convertFormat, Locale.KOREA);
                        String result = sdf.format(d);

                        view.setText(result);

                    } catch (Exception e) {
                        e.printStackTrace();

                        view.setText(date);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 다른 형태의 포맷으로 날짜 타입 변경
     *
     * @param view
     * @param date
     * @param convertFormat
     */
    @BindingAdapter({"date", "convertFormat"})
    public static void setConvertDateFormat(final TextView view, final long date, final String convertFormat) {
        try {

            view.post(new Runnable() {
                @Override
                public void run() {
                    try {

                        SimpleDateFormat sdf = new SimpleDateFormat(convertFormat, Locale.KOREA);
                        view.setText(sdf.format(date));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 새로고침 컬러 변경
     *
     * @param view
     * @param color
     */
    @BindingAdapter({"refreshColor"})
    public static void setColorSchemeResources(final SwipeRefreshLayout view, final int color) {
        try {
            view.setColorSchemeColors(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 이미지뷰 이미지 세팅
     *
     * @param view
     * @param resId
     */
    @BindingAdapter({"backgroundResource"})
    public static void setColorSchemeResources(final ImageView view, final int resId) {
        try {
            view.setBackgroundResource(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * html format
     *
     * @param view
     * @param html
     */
    @BindingAdapter({"htmlFormat"})
    public static void setHtmlFormat(final TextView view, final String html) {
        try {
            Spanned fromHtml = HtmlCompat.fromHtml(view.getContext(), html, 0);
            view.setText(fromHtml);
        } catch (Exception e) {
            e.printStackTrace();
            view.setText(html);
        }
    }

    /**
     * aesDecode
     *
     * @param view
     * @param encode
     */
    @BindingAdapter({"aesDecode"})
    public static void setAesDecode(final TextView view, final String encode) {
        try {

            view.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (encode != null) {
                            view.setText(AES256Util.decode(view.getContext().getString(R.string.aes256_key), encode));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        view.setText(null);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * aesDecode
     *
     * @param view
     * @param isItemAnimator
     */
    @BindingAdapter({"isItemAnimator"})
    public static void setAesDecode(final RecyclerView view, final boolean isItemAnimator) {
        try {

            if (isItemAnimator) {
                view.setItemAnimator(new DefaultItemAnimator());
            } else {
                view.setItemAnimator(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * aesDecode
     *
     * @param view
     * @param type
     */
    @BindingAdapter({"snapHelper"})
    public static void setSnapHelper(final RecyclerView view, String type) {
        try {

            if ("start".equals(type)) {
                SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                snapHelper.attachToRecyclerView(view);
            } else if ("center".equals(type)) {
                SnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
                snapHelper.attachToRecyclerView(view);
            } else if ("center".equals(type)) {
                SnapHelper snapHelper = new GravitySnapHelper(Gravity.RIGHT);
                snapHelper.attachToRecyclerView(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * aesDecode
     *
     * @param view
     * @param divider
     */
    @BindingAdapter({"divider"})
    public static void setDivider(final RecyclerView view, Drawable divider) {
        try {
            view.addItemDecoration(new DividerDecoration(divider));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 아침, 점심, 저녁 구하기
     *
     * @param value
     * @return
     */
    @BindingAdapter({"mealType"})
    public static void getMealType(TextView view, String value) {
        try {
            if ("B".equals(value)) {
                view.setText("아침");
            } else if ("L".equals(value)) {
                view.setText("점심");
            } else if ("D".equals(value)) {
                view.setText("저녁");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 아침, 점심, 저녁 구하기
     *
     * @param value
     * @return
     */
    @BindingAdapter({"enterRemove"})
    public static void getEnterRemove(TextView view, String text) {
        try {
            if (!ObjectUtils.isEmpty(text)) {
                String s = text.replaceAll("\n", " ");
                view.setText(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
