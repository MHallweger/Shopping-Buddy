package com.moco.marvin.shoppingbuddy.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moco.marvin.shoppingbuddy.Home.HomeActivity;
import com.moco.marvin.shoppingbuddy.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marvin.H on 15.06.18.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db; // Firestore

    private String userID;
    private String currentUser;
    private Context mContext;

    private MyAlerter mMyAlerter;
    private ValidateInputs mValidateInputs;




    public FirebaseMethods(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        mContext = activity;

        mValidateInputs = new ValidateInputs(mContext);
        mMyAlerter = new MyAlerter(mContext);

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void LogInTheUser(EditText emailField, EditText passwordField, final ProgressBar progressBar) {

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Email wird benötigt");
            emailField.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Bitte eine korrekte email eintragen!");
            emailField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Passwort wird benötigt");
            passwordField.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordField.setError("Das Passwort sollte mindestens 6 Zeichen lang sein!");
            passwordField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear the activitys (for not coming back to login Activity)
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish(); // dont come back to login Activity
                } else {
                    mMyAlerter.showAlerterNegative("Fehler!", "Die angegebenen Daten stimmen nicht überein");
                }
            }
        });
    }

    public void registerNewUser(final EditText mNameField, final EditText mEmailField, EditText mPasswordField, EditText mPasswordField2, final ProgressBar mProgressBar) {

        String fullName = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        String password2 = mPasswordField2.getText().toString().trim();
        // Register the User with email and password on Auth Site

        if (!mValidateInputs.validateInputs(fullName,email,password,password2,mNameField,mEmailField,mPasswordField,mPasswordField2)) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        currentUser.sendEmailVerification();
                        mProgressBar.setVisibility(View.GONE);
                        saveData(mNameField, mEmailField, mProgressBar);
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        mMyAlerter.showAlerterNegative("Fehler!", "Diese email besteht bereits");
                        mProgressBar.setVisibility(View.GONE);
                    } else {
                        mMyAlerter.showAlerterNegative("Fehler!", "Ein Unbekannter Fehler ist aufgetreten");
                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(View.GONE);
                    }

                }
            });
        }
    }

    public void saveData(EditText mNameField, EditText mEmailField, final ProgressBar mProgressBar) {


        String name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String image = null;

        //userID = mAuth.getCurrentUser().getUid();

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", name);
        newUser.put("email", email);
        newUser.put("image", image);

        db = FirebaseFirestore.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("User").document(currentUser).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mMyAlerter.showAlerterPositive("Erfolg!","Bitte bestätige dein Konto, indem du auf den Link in der mail drückst, die wir dir gesendt haben");
                mProgressBar.setVisibility(View.GONE);

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4100);

                            Intent intent = new Intent(mContext, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear the activitys (for not coming back to login Activity)
                            mContext.startActivity(intent);
                            ((Activity)mContext).finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mMyAlerter.showAlerterNegative("Fehler!", "Deine Daten konnten nicht übermittelt werden");
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.GONE);

            }
        });
    }

}
