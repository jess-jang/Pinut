package com.mindlinksw.schoolmeals.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mindlinksw.schoolmeals.R;

public class AlertDialog extends Dialog implements View.OnClickListener {

    public interface OnDialogClickListener {
        public void onClick();
    }

    private Context mContext;
    private OnDialogClickListener mPositiveListener;
    private OnDialogClickListener mNegativeListener;

    private TextView mMessage;
    private TextView mPositive;
    private TextView mNegative;

    private String mMessageText;
    private String mPositiveText;
    private String mNegativeText;

    public AlertDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

//        mTitle = (TextView) findViewById(R.id.alert_title);
        mMessage = (TextView) findViewById(R.id.alert_message);
        mPositive = (TextView) findViewById(R.id.alert_positive);
        mNegative = (TextView) findViewById(R.id.alert_negative);

        int[] ids = {R.id.alert_positive, R.id.alert_negative};
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.alert_positive) {
            if (mPositiveListener != null) {
                mPositiveListener.onClick();
                dismiss();
            }
        } else if (i == R.id.alert_negative) {
            if (mNegativeListener != null) {
                mNegativeListener.onClick();
                dismiss();
            }
        }
    }


    public AlertDialog setMessage(String text) {
        mMessageText = text;
        return this;
    }

    public AlertDialog setPositiveButton(String text, final OnDialogClickListener listener) {
        mPositiveText = text;
        mPositiveListener = listener;
        return this;
    }

    public AlertDialog setNegativeButton(String text, final OnDialogClickListener listener) {
        mNegativeText = text;
        mNegativeListener = listener;
        return this;
    }

    public AlertDialog setAlertCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


    public void show() {
        super.show();

        try {

            setTextView(mMessage, mMessageText);
            setTextView(mPositive, mPositiveText);
            setTextView(mNegative, mNegativeText);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dismiss() {
        super.dismiss();
    }

    public void setTextView(TextView view, String text) {

        if (view != null && text != null) {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        } else {
            view.setVisibility(View.GONE);
            view.setText("");
        }

    }

}
