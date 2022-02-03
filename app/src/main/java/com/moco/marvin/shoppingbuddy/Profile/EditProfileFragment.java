package com.moco.marvin.shoppingbuddy.Profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moco.marvin.shoppingbuddy.R;
import com.moco.marvin.shoppingbuddy.Utils.UniversalImageLoader;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Marvin.H on 13.06.18.
 */

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private static final int CHOOSE_IMAGE = 101;
    public static final int READ_PERMISSION_REQUEST_CODE = 65535; // requestCode in onRequestPermissionsResult need 16 bits, range is from 0 to 65535.
    private boolean permissionIsGranted = false;

    CircleImageView profilePicture;
    TextView emailVerfication, changeProfilePicture;
    Uri uriProfilePicture;
    String profileImageUrl;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    ImageView done;

    FirebaseFirestore firebaseFirestore;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile_new, container, false);

        changeProfilePicture = view.findViewById(R.id.changeProfilePhoto);
        profilePicture = view.findViewById(R.id.profile_photo);
        done = view.findViewById(R.id.saveChanges);

        mAuth = FirebaseAuth.getInstance();

        loadUserInformation();

        storageReference = FirebaseStorage.getInstance().getReference();

        //loadUserInformation();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToStorage();
            }
        });

        return view;
    }

    /*@Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            // finish doesnt work here:
            getActivity().getSupportFragmentManager().popBackStack();

            startActivity(new Intent(getActivity().getApplicationContext(), LogInActivity.class));
        }


    }*/


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
                    //uploadImageToStorage();

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
                            showAlerterPositive("Erfolg!", "Dein Profil wurde erfolgreich aktualisiert");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showAlerterPositive("Fehler!", "Dein Profil konnte nicht aktualisiert werden");
                        }
                    });
        }
    }

    /**
     * This Method takes the current User Photo Url and saves it into the Profile Image View.  ::: TODO : atm the photo url is null
     */
    public void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser(); // final for onclick innerclass

       setProfileImage();
                /*Glide.with(getActivity())
                        .load(user.getPhotoUrl().toString())
                        .into(profilePicture);*/



            /*if (user.isEmailVerified()) {
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
            }*/
    }

    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: setting profile image.");
        String imgURL = "weknowyourdreams.com/images/dog/dog-03.jpg";
        UniversalImageLoader.setImage(imgURL, profilePicture, null, "http://");
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
}

