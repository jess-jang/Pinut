package com.mindlinksw.schoolmeals.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;

import com.mindlinksw.schoolmeals.R;


/**
 * Created by N16326 on 2018. 6. 14..
 */

public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.progress_dialog);
        initDialog();
    }

    private void initDialog() {

        try {

            setCancelable(false);

            setCanceledOnTouchOutside(false);

            // 투명하게 세팅
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}