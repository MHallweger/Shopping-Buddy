package com.moco.marvin.shoppingbuddy;

import com.google.firebase.firestore.FieldValue;

public class Meeting {

    private String modus;
    private String personen;
    private String wo;
    private String location;
    private String geschlecht;
    private String was;
    private String art;
    private String wann;
    private String beschreibung;
    private String uid;
    private String imageUri;

    public Meeting() {
    }

    public Meeting(String modus, String personen, String wo, String location, String geschlecht, String was, String art, String wann, String beschreibung, String uid, String imageUri) {
        this.modus = modus;
        this.personen = personen;
        this.wo = wo;
        this.geschlecht = geschlecht;
        this.was = was;
        this.art = art;
        this.wann = wann;
        this.beschreibung = beschreibung;
        this.uid = uid;
        this.imageUri = imageUri;
    }

    public String getMode() {
        return modus;
    }

    public String getPersons() {
        return personen;
    }

    public String getWhere() {
        return wo;
    }

    public String getGender() {
        return geschlecht;
    }

    public String getWhat() {
        return was;
    }

    public String getKind() {
        return art;
    }

    public String getWhen() {
        return wann;
    }

    public String getDescription() {
        return beschreibung;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getLocation() {
        return location;
    }


}
