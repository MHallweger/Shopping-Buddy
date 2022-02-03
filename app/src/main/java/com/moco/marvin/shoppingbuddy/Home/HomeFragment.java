package com.moco.marvin.shoppingbuddy.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.moco.marvin.shoppingbuddy.AdapterForMeetings;
import com.moco.marvin.shoppingbuddy.PostMeeting;
import com.moco.marvin.shoppingbuddy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marvin.H on 10.06.18.
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Toolbar toolbar;

    private RecyclerView listViewMeetings;
    private List<PostMeeting> meetingList;
    private AdapterForMeetings adapterForMeetings;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private ActionBarDrawerToggle drawerToggle;
    private DocumentSnapshot lastVisible; // For nextQuery

    private Boolean isFirstPageFirstLoad = true; // this boolean is for later purpose. The Problem is that If some other device add a new post, the new post will be on the top
    // the firstQuery Method will be started with the new post and the last 2 items: e.g: we have 1,2,3,4,5; 6 will be added, so we will have 6,1,2.. -> this boolean will help to fix this issue
    // on default its true because the first loaded data is the right data


    FirebaseAuth firebaseAuth;

    private Dialog customDialog;
    private ImageView search;
    private ImageView closeDialog;
    private Button accept;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_new, container, false);

        meetingList = new ArrayList<>(); // List for meetings

        listViewMeetings = view.findViewById(R.id.list_view_meetings);

        adapterForMeetings = new AdapterForMeetings(meetingList);

        listViewMeetings.setLayoutManager(new LinearLayoutManager(getActivity()));
        listViewMeetings.setAdapter(adapterForMeetings);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) { // case: logout

            listViewMeetings.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        nextQuery();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Meetings").orderBy("timestamp", Query.Direction.DESCENDING).limit(3); // down, newer ones first

            /*firebaseFirestore.collection("Meetings")*/
            //this: Listener will start if the activity is started, if stopped, the listener will stop too
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot DocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (firebaseAuth.getCurrentUser() != null) { // case: logout
                        if (isFirstPageFirstLoad) {
                            // just trigger this line if the first page is not loaded (first time)
                            lastVisible = DocumentSnapshots.getDocuments().get(DocumentSnapshots.size() - 1); // the last in Query

                        }
                        for (DocumentChange documentChange : DocumentSnapshots.getDocumentChanges()) {

                            if (documentChange.getType() == DocumentChange.Type.ADDED) { // if added: TODO deleting

                                String meetingID = documentChange.getDocument().getId();

                                PostMeeting postMeeting = documentChange.getDocument().toObject(PostMeeting.class).withId(meetingID); // Send ID to with extendable class to model class PostMeeting, this can be recieved in AdapterForMeetings

                                if (isFirstPageFirstLoad) { // first time

                                    meetingList.add(postMeeting);

                                } else { // second time, new posts will be on the top

                                    meetingList.add(0, postMeeting);
                                }

                                adapterForMeetings.notifyDataSetChanged();
                            }
                        }

                        isFirstPageFirstLoad = false; // here the first page is loaded
                    }
                }
            });
        }
        return view;

    }


    public void nextQuery() {

        Query nextQuery = firebaseFirestore.collection("Meetings")
                .orderBy("timestamp", Query.Direction.DESCENDING) // down, newer ones first
                .startAfter(lastVisible) // load data after this object
                .limit(3); // load just 3

        /*firebaseFirestore.collection("Meetings")*/
        //this: Listener will start if the activity is started, if stopped, the listener will stop too
        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot DocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (!DocumentSnapshots.isEmpty()) {
                        lastVisible = DocumentSnapshots.getDocuments().get(DocumentSnapshots.size() - 1); // the last in Query

                        for (DocumentChange documentChange : DocumentSnapshots.getDocumentChanges()) {

                            if (documentChange.getType() == DocumentChange.Type.ADDED) { // if added: TODO deleting

                                String meetingID = documentChange.getDocument().getId();

                                PostMeeting postMeeting = documentChange.getDocument().toObject(PostMeeting.class).withId(meetingID);
                                meetingList.add(postMeeting);

                                adapterForMeetings.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }


}

