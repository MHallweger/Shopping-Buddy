package com.moco.marvin.shoppingbuddy.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.moco.marvin.shoppingbuddy.CreateNewMeetingFragment;
import com.moco.marvin.shoppingbuddy.Login.LogInActivity;
import com.moco.marvin.shoppingbuddy.MessageFragment;
import com.moco.marvin.shoppingbuddy.R;
import com.moco.marvin.shoppingbuddy.Utils.BottomNavigationViewHelper;
import com.moco.marvin.shoppingbuddy.Utils.SectionsPagerAdapter;
import com.moco.marvin.shoppingbuddy.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 2;

    private Context mContext = HomeActivity.this;


    private CreateNewMeetingFragment createNewMeetingFragment;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: starting");

        setupFirebaseAuth();


        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();

        /*bottomNavigationView = findViewById(R.id.bottomNavViewMain);

        // Fragments

        createNewMeetingFragment = new CreateNewMeetingFragment();
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.BottomNavViewItemOne:
                        replaceBottomNavBarFragment(createNewMeetingFragment);
                        return true;
                    case R.id.BottomNavViewItemTwo:
                        replaceBottomNavBarFragment(homeFragment);
                        return true;
                    case R.id.BottomNavViewItemThree:
                        replaceBottomNavBarFragment(messageFragment);
                        return true;
                        default:
                        return false;

                }
            }
        });*/

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Responsible for adding 3 tabs: Camera, Home, Messages
     */

    private void setupViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new com.moco.marvin.shoppingbuddy.Home.FilerFragment()); // 0
        sectionsPagerAdapter.addFragment(new com.moco.marvin.shoppingbuddy.Home.HomeFragment()); // 1
        sectionsPagerAdapter.addFragment(new SettingsFragment()); // 2
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_favorite);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dashboard);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_star);
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


   /* private void replaceBottomNavBarFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent_main, fragment);
        fragmentTransaction.commit();


    }*/


   /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(mContext, LogInActivity.class);
            startActivity(intent);
        }
    }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
