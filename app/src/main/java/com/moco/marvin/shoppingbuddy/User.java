package com.moco.marvin.shoppingbuddy;

public class User {

    private String vorname, nachname, geburtsdatum, email, image;

    public User() {
        // for firestore
    }

    public User(String vorname, String nachname, String geburtsdatum, String email, String image) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsdatum = geburtsdatum;
        this.email = email;
        this.image = image;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() { return image; }
}
