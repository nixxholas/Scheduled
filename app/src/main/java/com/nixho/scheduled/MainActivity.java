package com.nixho.scheduled;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nixho.scheduled.Authentication.InternalLayer;
import com.nixho.scheduled.Authentication.LoginActivity;
import com.nixho.scheduled.Utilities.Constants;

import java.io.Serializable;

import static com.google.firebase.auth.GoogleAuthProvider.*;
import static com.nixho.scheduled.Utilities.Constants.User;
import static com.nixho.scheduled.Utilities.Constants.firebase;
import static com.nixho.scheduled.Utilities.Constants.mAuth;
import static com.nixho.scheduled.Utilities.Constants.mAuthListener;
import static com.nixho.scheduled.Utilities.Constants.mGoogleApiClient;
import static com.nixho.scheduled.Utilities.Constants.progress;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, Serializable {
    /**
     * All the variables/objects instantiated here is implicitly made for this class only.
     * <p>
     * Should you require re-using these objects or variables, I would suggest you to create a unified
     * class that stores such variables or objects to prevent spaghettification.
     */
    private InternalLayer AuthHelper; // Let's call it AuthHelper, to help us
    SignInButton googleSignInButton; // The Google Sign In Button
    Button defaultSignInButton; // The usual sign in button
    Intent innerIntent; // We'll be using this intent to bring the user to an activity after successful authentication.
    Intent defaultSignInIntent; // This will be used to bring the user to the default sign in activity.
    private static final String TAG = "SignInActivity"; // Just a name.
    private static GoogleSignInAccount currUser; // Current User to come in IF he's from Google
    /**
     * RC_SIGN in is the request code you will assign for starting the new activity. this can
     * be any number. When the user is done with the subsequent activity and returns, the system
     * calls your activity's onActivityResult() method.
     * <p>
     * More information can be found here: https://developer.android.com/training/basics/intents/result.html?hl=es
     */
    private static final int RC_SIGN_IN = 9001;
    //public GoogleApiClient mGoogleApiClient; // This is your buddy to Google. He'll be helping you out to authenticate via Play Services.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        // We need to point to what the innerIntent is going to launch.
        innerIntent = new Intent(this, InnerMainActivity.class);

        // GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Request ID Token Fix by Tomer Gu via https://groups.google.com/forum/#!msg/firebase-talk/T904DMYBuSY/YVre9S4oAQAJ
                .requestEmail()
                .build();

        // Progress Dialog for Persistent Authentication
        // http://stackoverflow.com/questions/12841803/best-way-to-show-a-loading-spinner
        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        progress.setMessage("Performing cleanup!");
        progress.show();

        // Firebase Auth Initialization
        firebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                } else {
                    // user is not logged in
                }
            }
        });

        // Google Authentication Initialization
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* Fragment Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API)
                .build();
        mGoogleApiClient.connect();

        // Firebase Authentication Initialization
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            /**
             * @param firebaseAuth
             * This method gets invoked in the UI thread on changes in the authentication state:

                - Right after the listener has been registered
                - When a user is signed in
                - When the current user is signed out
                - When the current user changes
                - When there is a change in the current user's token
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                User = firebaseAuth.getCurrentUser();
                if (User != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //innerIntent.putExtra("GoogleAccount", currUser); // Because we're pushing the currUser into the intent, we don't need it to be public anymore.

                    startActivity(innerIntent);
                    progress.hide();
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                    progress.hide();

                    // Implement a redirector when a user logs out
                    // http://stackoverflow.com/questions/37533745/firebase-authentication-auth-state-changed
                    String className = this.getClass().getName();
                    if (!(className == "LoginActivity") || !(className == "MainActivity")) {
                        //Intent intent = new Intent(MainActivity.class, MainActivity.class);
                        // intent.putExtra("EXTRA_SESSION_ID", sessionId);

                        //finish();
                    }
                }
                // ...
            }
        };

        googleSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(this);
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        googleSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        googleSignInButton.setScopes(gso.getScopeArray());

        defaultSignInButton = (Button) findViewById(R.id.signInButton);
        defaultSignInButton.setOnClickListener(this);
        defaultSignInButton.setVisibility(View.GONE); // Don't let the user signout before signing in
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignInButton:
                signIn();
                break;
            case R.id.signInButton:
                customSignIn();
                break;
        }
    }

    public void customSignIn() {
        Intent customSignInIntent = new Intent(this, LoginActivity.class);
        startActivity(customSignInIntent);
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSIgnInApi.getSignInIntent(..);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);

            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        currUser = acct;
        innerIntent = new Intent(this, InnerMainActivity.class);

        AuthCredential credential = getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());

                        }

                        // We'll be launching the InnerMainActivity here
                        innerIntent.putExtra("GoogleAccount", currUser); // Because we're pushing the currUser into the intent, we don't need it to be public anymore.

                        startActivity(innerIntent);
                    }
                });
    }

    /**
     * Deprecated Sign-in Handler
     * v1
     * <p>
     * This old handler basically handles the google authentication directly and was a
     * foundation for me to utilize with to learn more about the basis of authentication
     *
     * @param connectionResult
     */
/*    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            //Signed in successfully, show authenticated UI.
            currUser = result.getSignInAccount();

            //statusTextView.setText("Hello " + acct.getDisplayName());

            // We'll be launching the InnerMainActivity here
            // https://developer.android.com/training/basics/firstapp/starting-activity.html
            Intent intent = new Intent(this, InnerMainActivity.class);

            // This intent flag will clear all the history of activities. This means that
            // the user won't be able to spam login again.
            // http://stackoverflow.com/questions/3473168/clear-the-entire-history-stack-and-start-a-new-activity-on-android
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Doesn't work
            intent.putExtra("GoogleAccount", currUser); // Because we're pushing the currUser into the intent, we don't need it to be public anymore.

            startActivity(intent);
            finish();
        } else {
            //Log the error out
            Log.d(TAG, "handleSignInError: " + result.getStatus());
        }
    }*/
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                //statusTextView.setText("Signed Out");
            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //mGoogleApiClient.connect();
        //AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}