package com.moco.marvin.shoppingbuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterForMeetings extends RecyclerView.Adapter<AdapterForMeetings.ViewHolder> {

    public List<PostMeeting> meetingList;

    public Context context;

    private static final String[] months = {"Januar", "Februar", "März", "April", "Mai",
            "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public String currentUserId;
    public int numberOfPersons;
    public int count;

    public AdapterForMeetings(List<PostMeeting> meetingList) {
        this.meetingList = meetingList;
    }

    // Methods for Adapter
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_meeting, parent, false);
        context = parent.getContext();


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override // recycle new unused view holder
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String idOfClickedMeeting = meetingList.get(position).meetingID; // PostMeeting -> PostMeetingID -> Adapter


        currentUserId = firebaseAuth.getCurrentUser().getUid();

        String descriptionData = meetingList.get(position).getBeschreibung(); // from PostMeeting
        holder.setdescriptionText(descriptionData);


        final String imageUri = meetingList.get(position).getImageUri();
        holder.setImage(imageUri);


        String userID = meetingList.get(position).getUid();
        firebaseFirestore.collection("User").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String firstGivenName = task.getResult().getString("vorname");
                    String surname = task.getResult().getString("nachname");


                    String profileImageUri = task.getResult().getString("image");

                    holder.setData(firstGivenName, surname, profileImageUri);
                } else {
                    // Error
                }
            }
        });

        firebaseFirestore.collection("Meetings").document(idOfClickedMeeting).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        if (!TextUtils.isEmpty(task.getResult().getString("persons"))) {
                            numberOfPersons = Integer.parseInt(task.getResult().getString("persons"));
                            holder.setPersons(numberOfPersons);
                        }
                    }


                } else {
                    // Error
                }
            }
        });

        // get Time
        // convert the saved firebase timestamp into a long value
        long milliseconds = meetingList.get(position).getTimestamp().getTime();
        String postTime = getDateStringFromMilliseconds(milliseconds, Calendar.getInstance().getTime());
        holder.setTime(postTime);


        // Get location and kind
        String location = meetingList.get(position).getLocation();
        String kind = meetingList.get(position).getKind();
        String where = meetingList.get(position).getWo();
        holder.updateKindAndLocation(kind, location, where);

        // Get Request Count

        firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {


                    count = queryDocumentSnapshots.size();

                    holder.updatePersons(count);


                } else {

                }
            }
        });

        // get Requests
        firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Requests").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    // the user pressed the requestButton

                    holder.requestMeetingButton.setImageDrawable(context.getResources().getDrawable(R.drawable.cerclebackgroundblue));


                } else {
                    // not pressed the request Button

                    holder.requestMeetingButton.setImageDrawable(context.getResources().getDrawable(R.drawable.cerclebackgroundturcis));

                }
            }
        });

        // Likes
        holder.requestMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Requests").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (!task.getResult().exists()) {

                            // just create the requeststamp if !exists
                            Map<String, Object> requestMap = new HashMap<>();
                            requestMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Requests").document(currentUserId).set(requestMap);
                            // inside of Meetings search for the post which is clicked, create a new section named Requests, create a new document which has the id of the requesting user as a name. Put the map in there.
                            Log.d("RequestButton Clicked", "Request saved");
                        } else {

                            // if the requeststamp exists, delete it
                            Log.d("RequestButton Clicked", "Request deleted");

                            firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Requests").document(currentUserId).delete();
                        }

                    }
                });
            }
        });

        // favorites
        holder.saveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Favorites").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (!task.getResult().exists()) {

                            // just create the requeststamp if !exists
                            Map<String, Object> favoriteMap = new HashMap<>();
                            favoriteMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Favorites").document(currentUserId).set(favoriteMap);
                            // inside of Meetings search for the post which is clicked, create a new section named Requests, create a new document which has the id of the requesting user as a name. Put the map in there.
                            Log.d("RequestButton Clicked", "Favorite saved");
                        } else {

                            // if the requeststamp exists, delete it
                            Log.d("RequestButton Clicked", "Favorite deleted");

                            firebaseFirestore.collection("Meetings/" + idOfClickedMeeting + "/Favorites").document(currentUserId).delete();
                        }

                    }
                });
            }
        });
    }

    static public String getDateStringFromMilliseconds(long milliseconds, Date currentDate) {

        // Sekunden
        long difference = (long) (currentDate.getTime() - milliseconds) / 1000;

        if (difference < 3600) {
            long minutesAgo = (long) difference / 60;

            if (minutesAgo < 1) {
                return "Gerade eben";

            } else if (minutesAgo == 1) {
                return "Vor einer Minute";

            } else {
                return ("Vor " + String.valueOf(minutesAgo) + " Minuten");
            }
        } else if (difference < 7200) {
            return "Vor einer Stunde";
        } else if (difference < 86400) {
            return ("Vor " + String.valueOf((int) difference / 3600) + " Stunden");
        } else {
            Date d = new Date(milliseconds);
            return d.getDate() + ". " + months[d.getMonth()];
        }
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descriptionView;
        private ImageView imageView;

        private TextView postDate;
        private TextView postUsername;
        private TextView kindIV;
        private TextView whereIV;

        CircleImageView userProfilePicture;

        private ImageView requestMeetingButton;
        private ImageView saveMeeting;

        private ImageView personOne, personTwo, personThree, personFour, personFive;


        // Konstruktor for ViewHolder
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            // get Reference
            requestMeetingButton = mView.findViewById(R.id.singleMeetingButtonTwo);
            saveMeeting = mView.findViewById(R.id.singleMeetingButtonThree);
        }

        public void setdescriptionText(String description) {
            descriptionView = mView.findViewById(R.id.singleMeetingDescription); // description of a single post
            descriptionView.setText(description);
        }

        public void setImage(String uri) {
            imageView = mView.findViewById(R.id.singleMeetingPicture); // image of a single post
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.rectangle);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(uri).into(imageView);
        }

        public void setTime(String date) {

            postDate = mView.findViewById(R.id.singleMeetingDate);
            postDate.setText(date);

        }

        public void setData(String firstGivenName, String surname, String image) {
            userProfilePicture = mView.findViewById(R.id.singeMeetingProfilePicture);
            postUsername = mView.findViewById(R.id.singleMeetingUsername);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.placeholder_profile_picture);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(userProfilePicture);
            postUsername.setText(firstGivenName + " " + surname);
        }


        public void updatePersons(int count) {
            personOne = mView.findViewById(R.id.singleMeetingPersonOne);
            personTwo = mView.findViewById(R.id.singleMeetingPersonTwo);
            personThree = mView.findViewById(R.id.singleMeetingPersonThree);
            personFour = mView.findViewById(R.id.singleMeetingPersonFour);
            personFive = mView.findViewById(R.id.singleMeetingPersonFive);

            ImageView persons[] = {personOne, personTwo, personThree, personFour, personFive};

            for (int i = 0; i < count; i++) {
                persons[i].setImageResource(R.drawable.ic_person_black_24dp);
            }
        }

        public void setPersons(int personCount) {

            personOne = mView.findViewById(R.id.singleMeetingPersonOne);
            personTwo = mView.findViewById(R.id.singleMeetingPersonTwo);
            personThree = mView.findViewById(R.id.singleMeetingPersonThree);
            personFour = mView.findViewById(R.id.singleMeetingPersonFour);
            personFive = mView.findViewById(R.id.singleMeetingPersonFive);

            ImageView persons[] = {personOne, personTwo, personThree, personFour, personFive};

            //int personCountInt = Integer.parseInt(personCount);

            if (personCount != 0) {
                for (int i = 0; i < personCount; i++) {
                    persons[i].setVisibility(View.VISIBLE);
                }
            }

        }

        public void updateKindAndLocation(String kind, String location, String where) {
            kindIV = mView.findViewById(R.id.singleMeetingKind);
            whereIV = mView.findViewById(R.id.singleMeetingLocation);

            kindIV.setText(kind);
            whereIV.setText(where + ", " + location);
        }
    }
}
