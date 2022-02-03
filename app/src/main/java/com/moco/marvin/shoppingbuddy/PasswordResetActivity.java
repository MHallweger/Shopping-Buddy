package com.moco.marvin.shoppingbuddy;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.tapadoo.alerter.Alerter;

public class PasswordResetActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mail1, mail2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mail1 = findViewById(R.id.mailForResetID);
        mail2 = findViewById(R.id.mailForResetAgainID);

        findViewById(R.id.resetButtonID).setOnClickListener(this);

    }

    private void sendPasswordResetMail() {

        String mailString1 = mail1.getText().toString().trim();
        String mailString2 = mail2.getText().toString().trim();

        if (mailString1.isEmpty()) {
            mail1.setError("Email wird benötigt");
            mail1.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mailString1).matches()) {
            mail1.setError("Bitte eine korrekte email eintragen!");
            mail1.requestFocus();
            return;
        }
        if (mailString2.isEmpty()) {
            mail2.setError("Email wird benötigt");
            mail2.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mailString2).matches()) {
            mail2.setError("Bitte eine korrekte email eintragen!");
            mail2.requestFocus();
            return;
        }

        if (!mailString1.equals(mailString2)) {
            mail1.setError("Keine Übereinstimmung!");
            mail2.setError("Keine Übereinstimmung!");
            mail1.requestFocus();
            mail2.requestFocus();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(mailString1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showAlerterPositive("Erfolg!", "Klicke auf den Link in der email die wir dir gesendet haben, um dein Passwort zurückzusetzen");
                        } else {
                            showAlerterNegative("Fehler!", "Diese email scheint nicht zu existieren");
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetButtonID:
                sendPasswordResetMail();
                break;
        }
    }

    public void showAlerterPositive(String title, String text) {
        Alerter.create(PasswordResetActivity.this)
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
        Alerter.create(PasswordResetActivity.this)
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
