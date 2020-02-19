package com.mindlinksw.schoolmeals.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class TextFormatUtils {

    /**
     * 아침, 점심, 저녁 구하기
     *
     * @param value
     * @return
     */
    public static String getMealType(String value) {
        try {
            if ("B".equals(value)) {
                return "아침";
            } else if ("L".equals(value)) {
                return "점심";
            } else if ("D".equals(value)) {
                return "저녁";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 아침, 점심, 저녁 코드 구하기
     *
     * @return
     */
    public static String getMealTypeTime() {
        try {
            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour >= 0 && hour < 9) {
                return "B";
            } else if (hour >= 13 && hour < 23) {
                return "D";
            } else {
                return "L";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "L";
    }

    /**
     * 첫째주 ..
     */
    public static String getWeekText(int weekNum) {

        String result = "";

        try {

            switch (weekNum) {
                case 1:
                    result = "이번주";
                    break;

                case 2:
                    result = "다음주";
                    break;

                default:
                    result = (weekNum - 1) + "주 뒤";
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Youtube URL Validator/Matcher
     */
    public static boolean isYoutubeUrl(String url) {

        boolean result = false;

        try {

            if (ObjectUtils.isEmpty(url)) {
                return false;
            }

            if (Pattern.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+", url)) {
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * File Name
     *
     * @return
     */
    public static String getFileName() {
        try {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "pinut" + "_" + timeStamp + "_" + System.currentTimeMillis();
            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "pinut";

    }
}
