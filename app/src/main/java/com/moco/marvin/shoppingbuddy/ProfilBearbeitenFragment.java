package com.moco.marvin.shoppingbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moco.marvin.shoppingbuddy.Login.LogInActivity;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfilBearbeitenFragment extends Fragment implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;
    public static final int READ_PERMISSION_REQUEST_CODE = 65535; // requestCode in onRequestPermissionsResult need 16 bits, range is from 0 to 65535.
    private boolean permissionIsGranted = false;

    CircleImageView profilePicture;
    TextView emailVerfication;
    Button pictureButton, logOut;
    Uri uriProfilePicture;
    String profileImageUrl;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    ProgressBar progressBar;

    FirebaseFirestore firebaseFirestore;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profil_bearbeiten_fragment, container, false);

        profilePicture = view.findViewById(R.id.profilePicID);

        pictureButton = view.findViewById(R.id.ändernID);

        emailVerfication = view.findViewById(R.id.emailVerficationID);

        progressBar = view.findViewById(R.id.progressBarIDProfile);

        logOut = view.findViewById(R.id.abmeldenID);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loadUserInformation();

        pictureButton.setOnClickListener(this);
        logOut.setOnClickListener(this);

        return view;

    }

    public void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser(); // final for onclick innerclass


        // new User
        if (user.getPhotoUrl() != null) {
            String photoUrl = user.getPhotoUrl().toString();
        }

        // userURL -> userProfilePicture into profilePictureImageView
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(profilePicture);
                profilePicture.setBackgroundResource(0);
            }

            if (user.isEmailVerified()) {
                emailVerfication.setText("Email"); // instead of (Email (Nicht Verifiziert))
            } else {
                emailVerfication.setText("Email nicht verifiziert (Klicke hier um sie zu bestätigen)");
                emailVerfication.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showAlerterPositive("Versendet!", "Schau in deinem email Postfach nach und bestätige dein Konto");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showAlerterNegative("Email Versandt fehlgeschlagen!", "Die Verifizierungs-email konnte nicht versandt werden");
                                //Toast.makeText(getActivity().getApplicationContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            // finish doesnt work here:
            getActivity().getSupportFragmentManager().popBackStack();

            startActivity(new Intent(getActivity().getApplicationContext(), LogInActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    uriProfilePicture = result.getUri();

                    System.out.println(uriProfilePicture);

                    // set Image
                    profilePicture.setImageURI(uriProfilePicture);


                    // upload
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
        progressBar.setVisibility(View.VISIBLE);
        // StorageReference profileImageReference = FirebaseStorage.getInstance().getReference("Profilepictures/" + System.currentTimeMillis() + ".jpg");
        // better with UID:
        String userID = mAuth.getCurrentUser().getUid();
        StorageReference profileImageReference = storageReference.child("ProfilePictures").child(userID + ".jpg");


        if (uriProfilePicture != null) {

            profileImageReference.putFile(uriProfilePicture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();


                    saveNewProfilePicture();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlerterNegative("Upload fehlgeschlagen!", "Beim Upload ist wohl etwas schief gelaufen" + "Fehler: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Uri ist null", Toast.LENGTH_LONG).show();
        }
    }

    public void saveNewProfilePicture() {
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null && profileImageUrl != null) {

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("image", profileImageUrl); // search the field with the name "image", put the new profile picture in it

            firebaseFirestore
                    .collection("User")
                    .document(mAuth.getCurrentUser().getUid())
                    .update(userMap) // start operation
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Success", "test");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failure ", "test2");
                        }
                    });
        }
    }

    public void showAlerterPositive(String title, String text) {
        Alerter.create(getActivity())
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
        Alerter.create(getActivity())
                .setTitle(title)
                .setText(text)
                .setIcon(R.drawable.ic_close_black_24dp)
                .setBackgroundColorRes(R.color.red)
                .setDuration(3500)
                .enableSwipeToDismiss()
                .enableProgress(true)
                .setProgressColorRes(R.color.colorPrimary)
                .show();
    }


    public void openImageChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

    private void chooseImage() {

        CropImage.activity()
                .start(getContext(), this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ändernID:
                //checkPermissionsForImage();
                openImageChooser();
                break;
            case R.id.abmeldenID:
                FirebaseAuth.getInstance().signOut();
                // finish doesnt work here:
                getActivity().getSupportFragmentManager().popBackStack();
                startActivity(new Intent(getActivity().getApplicationContext(), LogInActivity.class));
                break;
        }
    }
}
