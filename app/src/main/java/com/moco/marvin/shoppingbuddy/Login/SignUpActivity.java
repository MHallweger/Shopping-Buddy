package com.moco.marvin.shoppingbuddy.Login;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moco.marvin.shoppingbuddy.DatePickerFragment;
import com.moco.marvin.shoppingbuddy.Home.HomeActivity;
import com.moco.marvin.shoppingbuddy.R;
import com.moco.marvin.shoppingbuddy.Utils.FirebaseMethods;
import com.moco.marvin.shoppingbuddy.Utils.MyAlerter;
import com.moco.marvin.shoppingbuddy.Utils.ValidateInputs;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Views
    EditText mEmailField, mPasswordField, mPasswordField2, mNameField;
    ProgressBar mProgressBar;
    String currentDataString;
    TextView dateTextView, mAlreadAccount;
    Button mButtonRegister;


    // Context + Firebase References
    private Context mContext;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private MyAlerter mMyAlerter;
    private ValidateInputs mValidateInputs;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // get References, initialize
        mEmailField = findViewById(R.id.input_email);
        mPasswordField = findViewById(R.id.input_password);
        mPasswordField2 = findViewById(R.id.input_password_repeat);
        mProgressBar = findViewById(R.id.progressBar);
        mNameField = findViewById(R.id.input_username);
        mButtonRegister = findViewById(R.id.btn_register);
        mAlreadAccount = findViewById(R.id.youHaveAnAccountTextView);
        dateTextView = findViewById(R.id.birthday);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mContext = SignUpActivity.this;
        mMyAlerter = new MyAlerter(mContext);
        mValidateInputs = new ValidateInputs(mContext);
        mFirebaseMethods = new FirebaseMethods(SignUpActivity.this);


        /**
         * Register the User with email Address and Password.
         */
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseMethods.registerNewUser(mNameField, mEmailField, mPasswordField, mPasswordField2, mProgressBar);
            }
        });

        /**
         * Go back to Login Screen.
         */
        mAlreadAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Start the date picker. Load Choosed date into TextView.
         */
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // get Date-String out of Calendar, auto format it (timezone)
        currentDataString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        // Set Date-String
        dateTextView.setText(currentDataString);
    }


}
