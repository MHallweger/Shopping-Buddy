package com.moco.marvin.shoppingbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.moco.marvin.shoppingbuddy.ProfilBearbeitenFragment.READ_PERMISSION_REQUEST_CODE;

public class CreateMeetingActivity extends AppCompatActivity implements View.OnClickListener {

    EditText modeField, personsField, whereField, genderField, whatField, kindField, whenField, descriptionField, locationField;
    Button newMeeting, addPhoto;  // testing, for now a button

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private Uri thumbnailURI;
    private String downloadThumbURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting_middle_end_part);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        modeField = findViewById(R.id.modusID);
        personsField = findViewById(R.id.personenanzahlID);
        whereField = findViewById(R.id.woID);
        genderField = findViewById(R.id.geschlechtID);
        whatField = findViewById(R.id.wasID);
        kindField = findViewById(R.id.artID);
        whenField = findViewById(R.id.wannID);
        descriptionField = findViewById(R.id.beschreibungID);
        locationField = findViewById(R.id.locationID);

        addPhoto = findViewById(R.id.addPhotoID);

        newMeeting = findViewById(R.id.buttonCreateNewMeeting);
        newMeeting.setOnClickListener(this);
        addPhoto.setOnClickListener(this);
    }

    private boolean validateInputs(String modus, String personen, String wo, String geschlecht, String was, String art, String wann, String beschreibung) {
        if (modus.isEmpty()) {
            modeField.setError("Modus wird benötigt");
            modeField.requestFocus();
            return true;
        }
        if (personen.isEmpty()) {
            personsField.setError("Personenanzahl wird benötigt");
            personsField.requestFocus();
            return true;
        }
        if (wo.isEmpty()) {
            whereField.setError("Wo willst du einkaufen?");
            whereField.requestFocus();
            return true;
        }
        if (geschlecht.isEmpty()) {
            genderField.setError("Geschlecht wird benötigt");
            genderField.requestFocus();
            return true;
        }
        if (was.isEmpty()) {
            whatField.setError("Was willst du einkaufen?");
            whatField.requestFocus();
            return true;
        }
        if (art.isEmpty()) {
            kindField.setError("Die Art wird benötigt");
            kindField.requestFocus();
            return true;
        }
        if (wann.isEmpty()) {
            whenField.setError("Wann willst du einkaufen?");
            whenField.requestFocus();
            return true;
        }
        if (beschreibung.isEmpty()) {
            descriptionField.setError("Die Beschreibung wird benötigt");
            descriptionField.requestFocus();
            return true;
        }
        return false; // All ok? return false -> negation later

    }

    public void createNewMeeting() {
        String uid = mAuth.getCurrentUser().getUid();
        String mode = modeField.getText().toString().trim();
        String persons = personsField.getText().toString().trim();
        String where = whereField.getText().toString().trim();
        String gender = genderField.getText().toString().trim();
        String what = whatField.getText().toString().trim();
        String kind = kindField.getText().toString().trim();
        String when = whenField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();
        String location = locationField.getText().toString().trim();

        String favoriteYesNo = "false";

        if (!validateInputs(mode, persons, where, gender, what, kind, when, description)) { //TODO überarbeiten

            Map<String, Object> newMeeting = new HashMap<>();
            newMeeting.put("mode", mode);
            newMeeting.put("persons", persons);
            newMeeting.put("where", where);
            newMeeting.put("location", location);
            newMeeting.put("gender", gender);
            newMeeting.put("what", what);
            newMeeting.put("kind", kind);
            newMeeting.put("when", when);
            newMeeting.put("description", description);
            newMeeting.put("uid", uid);
            newMeeting.put("imageUrl", downloadThumbURL);
            newMeeting.put("timestamp", FieldValue.serverTimestamp());


            db.collection("Meetings").add(newMeeting).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    showAlerterPositive("Erfolg!", "Dein Treffen wurde erfolgreich erstellt");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlerterNegative("Fehler!", "Deine Daten konnten nicht übermittelt werden");
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    thumbnailURI = result.getUri();

                    uploadImageToStorage();


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageToStorage() {
        // StorageReference profileImageReference = FirebaseStorage.getInstance().getReference("Profilepictures/" + System.currentTimeMillis() + ".jpg");
        // better with UID:
        String randomName = UUID.randomUUID().toString();
        StorageReference postImageReference = storageReference.child("MeetingPictures").child(randomName + ".jpg");

        if (thumbnailURI != null) {

            postImageReference.putFile(thumbnailURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadThumbURL = taskSnapshot.getDownloadUrl().toString();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlerterNegative("Upload fehlgeschlagen!", "Beim Upload ist wohl etwas schief gelaufen" + "Fehler: " + e.getMessage());
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Uri ist null", Toast.LENGTH_LONG).show();
        }
    }


    private void chooseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);


    }

    public void openImageChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                // dont have permission
                String[] permissionREquest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissionREquest, READ_PERMISSION_REQUEST_CODE);
            }
        } else {
            chooseImage();
        }
    }

    @Override // the result of galery permission question
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_PERMISSION_REQUEST_CODE) {
            // requested one permission -> [0]
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                showAlerterNegative("Achtung!", "Du kannst nur ein Foto hochladen wenn du die Berechtigung erteilst.");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCreateNewMeeting:
                createNewMeeting();
                //startActivity(new Intent(CreateMeetingActivity.this, HomeActivity.class));
                break;
            case R.id.addPhotoID:
                openImageChooser();
                break;
        }
    }

    public void showAlerterPositive(String title, String text) {
        Alerter.create(CreateMeetingActivity.this)
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
        Alerter.create(CreateMeetingActivity.this)
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
