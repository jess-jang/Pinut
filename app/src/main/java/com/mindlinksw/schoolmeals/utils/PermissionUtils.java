package com.mindlinksw.schoolmeals.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;

public class PermissionUtils {

    /**
     * @return 다수 권한 체크 여부
     * @author 장세진
     * @description 다수 권한 체크 여부, 한 권한이라도 false 일 경우 권한 체크 거부로 인식
     */
    public static boolean isCheckPermission(@NonNull int[] grantResults) {

        boolean result = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } else {
            result = true;
        }

        return result;
    }

}
