package com.moutamid.bluetoothapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public static final String IS_LOGGED_IN = "isloggedin";
    public static final String USER_NAME = "username";
    public static final String ALL_USERS = "all_users";
    public static final String CHATS = "chats";
    public static final String ALL_MESSAGES = "all_messages";

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("BluetoothApp");
        db.keepSynced(true);
        return db;
    }
}
