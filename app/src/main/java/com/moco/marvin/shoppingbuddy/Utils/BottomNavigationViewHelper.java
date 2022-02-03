package com.moco.marvin.shoppingbuddy.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.moco.marvin.shoppingbuddy.Chat.ChatActivityNew;
import com.moco.marvin.shoppingbuddy.CompletedMeetings.CompletedMeetings;
import com.moco.marvin.shoppingbuddy.CreateMeeting.CreateActivityNew;
import com.moco.marvin.shoppingbuddy.Home.HomeActivity;
import com.moco.marvin.shoppingbuddy.Profile.ProfilActivityNew;
import com.moco.marvin.shoppingbuddy.R;

/**
 * Created by User on 5/28/2017.
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.BottomNavViewItemChat:
                        Intent intent3 = new Intent(context, ChatActivityNew.class); // 3
                        context.startActivity(intent3);
                        break;
                    case R.id.BottomNavViewItemCreate:
                        Intent intent1 = new Intent(context, CreateActivityNew.class); // 1
                        context.startActivity(intent1);
                        break;
                    case R.id.BottomNavViewItemMeetings:
                        Intent intent2 = new Intent(context, HomeActivity.class); // 2
                        context.startActivity(intent2);
                        break;
                    case R.id.BottomNavViewItemDone:
                        Intent intent5 = new Intent(context, CompletedMeetings.class); // 0
                        context.startActivity(intent5);
                        break;
                    case R.id.BottomNavViewItemProfile:
                        Intent intent4 = new Intent(context, ProfilActivityNew.class); // 4
                        context.startActivity(intent4);
                        break;
                }



                return false;
            }
        });
    }
}
