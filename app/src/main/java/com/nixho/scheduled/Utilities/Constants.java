package com.nixho.scheduled.Utilities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.CalendarView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by nixho on 22-Sep-16.
 */

public class Constants {
    public static CalendarView calendar;
    /* Create the Firebase ref that is used for all authentication with Firebase */
    public static Firebase firebase = new Firebase("https://scheduled-7f23b.firebaseio.com");
    /* Data from the authenticated user */
    //public static AuthData mAuthData = firebase.getAuth();
    public static FirebaseUser User;
    public static FirebaseAuth mAuth; // Just like GoogleApiClient, but this is towards Firebase Authentication.
    // This is FirebaseAuth's Buddy Object. He is a listener that allows previously logged users
    // to "Auto" Authenticate upon the launch of the app with a method called onAuthStateChanged
    // in order to validate the returning user's token.
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static Intent mainIntent;
    public static ProgressDialog progress; // Well, in case the user has shitty internet, this progress dialog can be shown to make him wait for a moment.
    public static GoogleApiClient mGoogleApiClient; // This is your buddy to Google. He'll be helping you out to authenticate via Play Services.
}

