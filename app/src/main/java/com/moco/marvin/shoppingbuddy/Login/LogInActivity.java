package com.moco.marvin.shoppingbuddy.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.moco.marvin.shoppingbuddy.Home.HomeActivity;
import com.moco.marvin.shoppingbuddy.PasswordResetActivity;
import com.moco.marvin.shoppingbuddy.R;
import com.moco.marvin.shoppingbuddy.Utils.FirebaseMethods;
import com.tapadoo.alerter.Alerter;

public class LogInActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    TextView register;
    ProgressBar progressBar;
    private Button mBtnLogin;

    //CheckBox checkBox, checkBox2;

    FirebaseAuth mAuth; // create

    Context mContext;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private FirebaseMethods mFirebaseMethods;
    private Boolean saveLogin;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_new);

        emailField = findViewById(R.id.input_email);
        passwordField = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.loginRequestLoadingProgressbar);
        register = findViewById(R.id.link_signup);
        mBtnLogin = findViewById(R.id.btn_login);
        //checkBox = findViewById(R.id.checkBoxSaveDataID);
        //checkBox2 = findViewById(R.id.checkBoxAutoSigninID);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(LogInActivity.this);
        mContext = LogInActivity.this;

        /**
         * The user dont have an Account, go to register activity.
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SignUpActivity.class));
                finish();
            }
        });

        /**
         * Request Data, Log in the User.
         */
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recallData();
                mFirebaseMethods.LogInTheUser(emailField, passwordField, progressBar);
            }
        });
        //findViewById(R.id.pwResetID).setOnClickListener(this);

        //loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        //loginPrefsEditor = loginPreferences.edit();

        //saveLogin = loginPreferences.getBoolean("saveLogin", false);
        /*if (saveLogin == true) {
            mEmailField.setText(loginPreferences.getString("username", ""));
            mPasswordField.setText(loginPreferences.getString("password", ""));
            checkBox.setChecked(true);
        }*/


    }

    public void guestLogIn(View view) {
        Intent intentGuest = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentGuest);
    }

    private void recallData() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailField.getWindowToken(), 0);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        //if (checkBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", email);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        //} else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
       // }
        //if (checkBox2.isChecked()) {
           // autoSignIn();
        //}
    }

    private void autoSignIn() {
        startActivity(new Intent(LogInActivity.this, HomeActivity.class));
        LogInActivity.this.finish();
    }
}
