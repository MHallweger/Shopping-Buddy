package com.moco.marvin.shoppingbuddy;

import android.app.Application;

import com.firebase.client.Firebase;

public class ShoppingBuddy extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);

    }
}
