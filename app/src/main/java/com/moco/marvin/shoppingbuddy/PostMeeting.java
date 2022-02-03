package com.moco.marvin.shoppingbuddy;

import java.util.Date;

public class PostMeeting extends PostMeetingID {

    String uid;
    String mode;
    String persons;
    String where;
    String location;
    String gender;
    String what;
    String kind;
    String when;
    String description;
    String imageUrl;

    public Date timestamp;


    public PostMeeting() {
    }

    public PostMeeting(String uid, String mode, String persons, String where, String location, String gender, String what, String kind, String when, String description, String imageUrl, Date timestamp) {
        this.uid = uid;
        this.mode = mode;
        this.persons = persons;
        this.where = where;
        this.gender = gender;
        this.what = what;
        this.kind = kind;
        this.when = when;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getModus() {
        return mode;
    }

    public void setModus(String modus) {
        this.mode = modus;
    }

    public String getPersonen() {
        return persons;
    }

    public void setPersonen(String personen) {
        this.persons = personen;
    }

    public String getWo() {
        return where;
    }

    public void setWo(String wo) {
        this.where = wo;
    }

    public String getGeschlecht() {
        return gender;
    }

    public void setGeschlecht(String geschlecht) {
        this.gender = geschlecht;
    }

    public String getWas() {
        return what;
    }

    public void setWas(String was) {
        this.what = was;
    }

    public String getKind() {
        return kind;
    }

    public void setArt(String art) {
        this.kind = art;
    }

    public String getWann() {
        return when;
    }

    public void setWann(String wann) {
        this.when = wann;
    }

    public String getBeschreibung() {
        return description;
    }

    public void setBeschreibung(String beschreibung) {
        this.description = beschreibung;
    }

    public String getImageUri() {
        return imageUrl;
    }

    public void setImageUri(String imageUri) {
        this.imageUrl = imageUri;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
