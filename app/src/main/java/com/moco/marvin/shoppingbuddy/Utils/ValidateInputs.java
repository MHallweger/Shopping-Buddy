package com.moco.marvin.shoppingbuddy.Utils;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Marvin.H on 15.06.18.
 */

public class ValidateInputs {

    Context mContext;

    public ValidateInputs(Context context) {
        mContext = context;
    }

    public boolean validateInputs(String name, String email, String passwort, String passwort2, TextView nameField, EditText emailField, EditText passwordField, EditText passwordField2) {
        if (name.isEmpty()) {
            nameField.setError("Vorname wird benötigt");
            nameField.requestFocus();
            return true;
        }
        if (email.isEmpty()) {
            emailField.setError("Email wird benötigt");
            emailField.requestFocus();
            return true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Bitte eine korrekte email eintragen!");
            emailField.requestFocus();
            return true;
        }
        if (passwort.isEmpty()) {
            passwordField.setError("Passwort wird benötigt");
            passwordField.requestFocus();
            return true;
        }
        if (passwort.length() < 6) {
            passwordField.setError("Das Passwort sollte mindestens 6 Zeichen lang sein!");
            passwordField.requestFocus();
            return true;
        }
        if (passwort2.isEmpty()) {
            passwordField2.setError("Passwort wird benötigt");
            passwordField2.requestFocus();
            return true;
        }
        if (passwort2.length() < 6) {
            passwordField2.setError("Das Passwort sollte mindestens 6 Zeichen lang sein!");
            passwordField2.requestFocus();
            return true;
        }
        if (!passwort.equals(passwort2)) {
            passwordField.setError("Keine Übereinstimmung");
            passwordField2.setError("Keine Übereinstimmung");
            passwordField.requestFocus();
            passwordField2.requestFocus();
            return true;
        }
        return false; // All ok? return false -> negation later

    }
}
