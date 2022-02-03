package com.moco.marvin.shoppingbuddy;


import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostMeetingID {

    // Extandable class get ID and send it to another place
    @Exclude
    public String meetingID;

    public <T extends PostMeetingID> T withId(@NonNull final String id) {
        this.meetingID = id;
        return (T) this;
    }
}
