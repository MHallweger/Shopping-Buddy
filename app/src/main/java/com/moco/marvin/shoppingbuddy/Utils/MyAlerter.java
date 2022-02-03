package com.moco.marvin.shoppingbuddy.Utils;

import android.app.Activity;
import android.content.Context;

import com.moco.marvin.shoppingbuddy.Login.SignUpActivity;
import com.moco.marvin.shoppingbuddy.R;

/**
 * Created by Marvin.H on 15.06.18.
 */

public class MyAlerter {

    public Context mContext;


    public MyAlerter(Context context) {
        mContext = context;
    }

    public void showAlerterPositive(String title, String text) {
        com.tapadoo.alerter.Alerter.create((Activity) mContext)
                .setTitle(title)
                .setText(text)
                .setIcon(R.drawable.ic_check_black_24dp)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(3500)
                .enableSwipeToDismiss()
                .enableProgress(true)
                .setProgressColorRes(R.color.white)
                .show();
    }

    public void showAlerterNegative(String title, String text) {
        com.tapadoo.alerter.Alerter.create((Activity) mContext)
                .setTitle(title)
                .setText(text)
                .setIcon(R.drawable.ic_close_black_24dp)
                .setBackgroundColorRes(R.color.red)
                .setDuration(3500)
                .enableSwipeToDismiss()
                .enableProgress(true)
                .setProgressColorRes(R.color.white)
                .show();
    }
}
