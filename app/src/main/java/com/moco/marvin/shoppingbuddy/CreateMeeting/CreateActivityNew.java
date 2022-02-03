package com.moco.marvin.shoppingbuddy.CreateMeeting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.moco.marvin.shoppingbuddy.R;
import com.moco.marvin.shoppingbuddy.Utils.BottomNavigationViewHelper;

/**
 * Created by Marvin.H on 10.06.18.
 */

public class CreateActivityNew extends AppCompatActivity{
    private static final String TAG = "CreateActivityNew";
    private static final int ACTIVITY_NUM = 1;


    Context mContext = CreateActivityNew.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Log.d(TAG, "onCreate: started");

        setupBottomNavigationView();
    }

    /**
     * Setting up Bottom Navigation View
     */

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBarrr);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
