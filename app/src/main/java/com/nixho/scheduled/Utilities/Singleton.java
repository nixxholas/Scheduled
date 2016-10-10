package com.nixho.scheduled.Utilities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.widget.CalendarView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by nixho on 22-Sep-16.
 *
 * Singleton Coding Pattern, http://www.vogella.com/tutorials/DesignPatternSingleton/article.html
 * 
 * However, since we're newer than Java 1.6, let's use Enumerators
 * http://stackoverflow.com/questions/8027701/singleton-using-enum
 *
 * Why Singleton is bad
 * http://programmers.stackexchange.com/questions/40373/so-singletons-are-bad-then-what
 *
 */

public enum Singleton {
    INSTANCE;

    public FirebaseApp firebase;
    public DatabaseReference rootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://scheduled-7f23b.firebaseio.com/"); // We'll have to utilize the new Firebase 3.0 APIs
    public CalendarView calendar;
    public FloatingActionButton FloatingButton;
    /* Create the Firebase ref that is used for all authentication with Firebase */
    //public Firebase firebase = new Firebase("https://scheduled-7f23b.firebaseio.com");
    // Data from the authenticated user
    //public AuthData mAuthData = firebase.getAuth();
    public FirebaseUser User;
    public FirebaseAuth mAuth; // Just like GoogleApiClient, but this is towards Firebase Authentication.
    // This is FirebaseAuth's Buddy Object. He is a listener that allows previously logged users
    // to "Auto" Authenticate upon the launch of the app with a method called onAuthStateChanged
    // in order to validate the returning user's token.
    public FirebaseAuth.AuthStateListener mAuthListener;
    public ProgressDialog progress; // Well, in case the user has shitty internet, this progress dialog can be shown to make him wait for a moment.
    public GoogleApiClient mGoogleApiClient; // This is your buddy to Google. He'll be helping you out to authenticate via Play Services.
}

