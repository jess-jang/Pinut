package com.mindlinksw.schoolmeals.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mindlinksw.schoolmeals.BuildConfig;
import com.mindlinksw.schoolmeals.consts.DataConst;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by N16326 on 2018. 11. 28..
 */

public class Utils {

    public static String TAG = Utils.class.getName();

    /**
     * Get Test
     */
    public static boolean isTest() {
        return "dev".equals(BuildConfig.FLAVOR);
    }

    /**
     * @description 네트워크체크
     */
    public static boolean isNetworkConnected(Context context) {
        // NetWork Connection Check
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean status3G = false;
        boolean statusWiFi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean statusWiMax = false;

        try {
            status3G = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        } catch (NullPointerException e) {
            status3G = false;
        }

        try {
            statusWiMax = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).isConnected();
        } catch (NullPointerException e) {
            statusWiMax = false;
        }

        return status3G | statusWiFi | statusWiMax;
    }

    /**
     * Get Hash Key
     *
     * @param activity
     */
    public static void getHashKey(Activity activity) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", "" + Base64.encodeToString(md.digest(), Base64.DEFAULT).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 앱 정보
     *
     * @return
     */
    public static String getAppInfo() {

        String result = "";

        try {

            JSONObject object = new JSONObject();
            object.put("os", "Android");
            object.put("isTest", isTest());
            object.put("id", BuildConfig.APPLICATION_ID);
            object.put("versionName", BuildConfig.VERSION_NAME);
            object.put("versionCode", BuildConfig.VERSION_CODE);
            result = object.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 버전 업데이트 유무
     *
     * @param releaseVersion
     * @return
     */
    public static boolean isVersionUpdated(String releaseVersion) {
        Logger.INSTANCE.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + releaseVersion);

//        int current = Integer.parseInt(BuildConfig.VERSION_NAME.replaceAll("\\.", ""));
//        int release = Integer.parseInt(releaseVersion.replaceAll("\\.", ""));
        boolean mupdate = false;
        String[] current = BuildConfig.VERSION_NAME.split("\\.");
        String[] release = releaseVersion.split("\\.");
        Logger.e("current", current[0] + " @@ " + current[1] + " @@ " + current[2]);
        Logger.e("release", release[0] + " @@ " + release[1] + " @@ " + release[2]);

        if(Integer.parseInt(current[0]) < Integer.parseInt(release[0])){
            Logger.e("Update", "current[0] < release[0]");
            mupdate = true;
        }else if(Integer.parseInt(current[0]) > Integer.parseInt(release[0])) {
            Logger.e("noUpdate", "current[0] > release[0]");
            mupdate = false;
        }else if(Integer.parseInt(current[0]) == Integer.parseInt(release[0])){
            Logger.e("noUpdate", "current[0] = release[0]");
            if(Integer.parseInt(current[1]) < Integer.parseInt(release[1])) {
                Logger.e("Update", "current[1] < release[1]");
                mupdate = true;
            }else if(Integer.parseInt(current[1]) > Integer.parseInt(release[1])) {
                Logger.e("noUpdate", "current[1] > release[1]");
                mupdate = false;
            }else if(Integer.parseInt(current[1]) == Integer.parseInt(release[1])) {
                Logger.e("noUpdate", "current[1] = release[1]");
                if(Integer.parseInt(current[2]) < Integer.parseInt(release[2])) {
                    Logger.e("Update", "current[2] < release[2]");
                    mupdate = true;
                }else {
                    Logger.e("noUpdate", "current[2] >= release[2]");
                    mupdate = false;
                }
            }
        }
        return mupdate;
    }

    /**
     * @author 장세진
     * @description 키보드 보임/숨김
     */
    public static void setKeyboardShowHide(final Activity activity, final View view, final boolean isShow) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (isShow) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 다른 형태의 포맷으로 날짜 타입 변경
     */
    public static String getConvertDateFormat(final long time, final String format) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
            return sdf.format(time);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 멀티라인 제한
     */
    public static String getMultiLineDisable(String content, String enter) {
        try {

            // 세줄이상 잘라버리기
            String block = content;
            String a = enter;
            for (int i = 0; i < 100; i++) {
                a += "\n";
                block = block.replaceAll(enter, "\n\n");
            }
            return block;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 다른 형태의 포맷으로 날짜 타입 변경
     */
    public static String getConvertDateFormat(final String date, final String originalFormat, final String convertFormat) {
        try {

            if (TextUtils.isEmpty(date)) {
                return date;
            }

            SimpleDateFormat sdf = new SimpleDateFormat(originalFormat, Locale.KOREA);
            Date d = sdf.parse(date);

            sdf = new SimpleDateFormat(convertFormat, Locale.KOREA);

            return sdf.format(d);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * Max Texture Size 구하기  > Open GL 이슈
     *
     * @return
     */
    public static int getMaxTextureSize(Context context) {

        try {

            int value = SharedPreferencesUtils.read(context, DataConst.MAXIMUM_TEXTURE_SIZE, -1);
            if (value > 0) {
                return value;
            }

            // Safe minimum default size
            final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

            // Get EGL Display
            EGL10 egl = (EGL10) EGLContext.getEGL();
            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            // Initialise
            int[] version = new int[2];
            egl.eglInitialize(display, version);

            // Query total number of configurations
            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            // Query actual list configurations
            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

            int[] textureSize = new int[1];
            int maximumTextureSize = 0;

            // Iterate through all the configurations to located the maximum texture size
            for (int i = 0; i < totalConfigurations[0]; i++) {
                // Only need to check for width since opengl textures are always squared
                egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

                // Keep track of the maximum texture size
                if (maximumTextureSize < textureSize[0])
                    maximumTextureSize = textureSize[0];
            }

            // Release
            egl.eglTerminate(display);

            // Return largest texture size found, or default
            value = Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
            SharedPreferencesUtils.save(context, DataConst.MAXIMUM_TEXTURE_SIZE, value);

            return value;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 클립보드 복사
     *
     * @param context
     * @param text
     */
    public static void setClipBoard(Context context, String text) {

        try {

            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", text);
            clipboardManager.setPrimaryClip(clipData);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 파일 확장자 가져오기
     *
     * @param fileStr 경로나 파일이름
     * @return
     */
    public static String getExtension(String fileStr) {
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }
}
