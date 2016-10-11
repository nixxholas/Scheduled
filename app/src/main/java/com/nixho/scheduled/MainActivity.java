package com.nixho.scheduled;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.auth.GoogleAuthProvider.getCredential;

/**
 *  ButterKnife
 *  https://github.com/JakeWharton/butterknife
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, Serializable {
    /**
     * All the variables/objects instantiated here is implicitly made for this class only.
     *
     * Should you require re-using these objects or variables, I would suggest you to create a unified
     * class that stores such variables or objects to prevent spaghettification.
     */
    SignInButton googleSignInButton; // The Google Sign In Button
    Intent innerIntent; // We'll be using this intent to bring the user to an activity after successful authentication.
    private static final String TAG = "SignInActivity"; // Just a name.
    private GoogleSignInAccount currUser; // Current User to come in IF he's from Google

    //public static FirebaseApp mFirebaseApp;
    public static DatabaseReference rootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://scheduled-7f23b.firebaseio.com/"); // We'll have to utilize the new Firebase 3.0 APIs

    /* Create the Firebase ref that is used for all authentication with Firebase */
    //public Firebase firebase = new Firebase("https://scheduled-7f23b.firebaseio.com"); // Deprecated Code from Firebase 2.0

    // Data from the authenticated user
    //public AuthData mAuthData = firebase.getAuth(); // Deprecated Code from Firebase 2.0

    /**
     * We'll have to store all of the user's data here.
     *
     * Having FirebaseUser instead of GoogleSignInAccount has 2 main advantages:
     *
     * - You don't have to instantiate a new User object specific to that service
     * when you use it. (i.e. You need to use GoogleSignInAccount object when the user is a Google
     * Account. You just need to use FirebaseUser for all the various types.)
     *
     * - Once you add in a new OAuth service, you just need to provide the relevant user
     * information, instantiate the FirebaseUser object, and use it for every single class when
     * you need to access the user's information. That way you can code permanent methods to
     * retrieve the user's data without compromising the power to scale with more OAuth Services.
     */
    public static FirebaseUser User;

    public static FirebaseAuth mAuth; // Just like GoogleApiClient, but this is towards Firebase Authentication.

    // This is FirebaseAuth's Buddy Object. He is a listener that allows previously logged users
    // to "Auto" Authenticate upon the launch of the app with a method called onAuthStateChanged
    // in order to validate the returning user's token.
    public FirebaseAuth.AuthStateListener mAuthListener;

    // Instantiate a basic progress dialog while performing authentication.
    public static ProgressDialog progress; // Well, in case the user has shitty internet access, this progress dialog can be shown to make him wait for a moment.


    /**
     * RC_SIGN in is the request code you will assign for starting the new activity. this can
     * be any number. When the user is done with the subsequent activity and returns, the system
     * calls your activity's onActivityResult() method.
     *
     * More information can be found here: https://developer.android.com/training/basics/intents/result.html?hl=es
     */
    private static final int RC_SIGN_IN = 9001;
    public static GoogleApiClient mGoogleApiClient; // This is your buddy to Google. He'll be helping you out to authenticate via Play Services.

    // Initialize some of the Widgets
    @BindView(R.id.regularSignUpButton) Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Fix the orientation to portrait
        // http://stackoverflow.com/questions/7153078/disable-landscape-mode-for-a-whole-application
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
         * FirebaseApp.InitializeApp
         *
         * initializeApp(Context, FirebaseOptions) initializes the default app instance.
         * This method should be invoked from Application. This is also necessary if it
         * is used outside of the application's main process.
         */
        //Firebase.setAndroidContext(this); Deprecated, Firebase 2 Code
        FirebaseApp.initializeApp(this); // New, Firebase 3 Code

        // We need to point to what the innerIntent is going to launch.
        innerIntent = new Intent(this, InnerMainActivity.class);

        // GoogleSignInOptions
        // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions
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

        // Firebase Authentication Initialization
        mAuth = FirebaseAuth.getInstance();

        /**
         * Useless code, need to understand what I was doing previously again..
         */
        // Firebase Auth Initialization
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth != null) {
                    // user is logged in
                } else {
                    // user is not logged in
                }
            }

            /*@Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                } else {
                    // user is not logged in
                }
            }*/
        });

        // Google Authentication Initialization
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* Fragment Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)

                /**
                 * Registers a listener to receive connection events from this GoogleApiClient.
                 * Applications should balance calls to this method with calls to
                 * unregisterConnectionCallbacks(ConnectionCallbacks) to avoid leaking resources.

                 If the specified listener is already registered to receive connection events,
                 this method will not add a duplicate entry for the same listener.

                 Note that the order of messages received here may not be stable, so
                 clients should not rely on the order that multiple listeners receive events in.
                 */
                .addConnectionCallbacks(this)

                /**
                 * https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.OnConnectionFailedListener
                 *
                 * A ConnectionResult that can be used for resolving the error, and
                 * deciding what sort of error occurred. To resolve the error, the
                 * resolution must be started from an activity with a non-negative requestCode
                 * passed to startResolutionForResult(Activity, int). Applications should
                 * implement onActivityResult in their Activity to call connect() again
                 * if the user has resolved the issue (resultCode is RESULT_OK).
                 */
                .addOnConnectionFailedListener(this) // Add a fail listener should connection fail,
                // so that we can throw the user something and loop back without any app crashes.

                // https://developers.google.com/android/guides/api-client
                .addApi(AppIndex.API)

                // Build!
                .build();

        mGoogleApiClient.connect(); // Connect to Google..

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
                    //progress.hide();
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                    progress.hide();

                    // Implement a redirector when a user logs out
                    // http://stackoverflow.com/questions/37533745/firebase-authentication-auth-state-changed
                    String className = this.getClass().getName();
                    if (!(className == "MainActivity")) {
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

        /*defaultSignInButton = (Button) findViewById(R.id.signInButton);
        defaultSignInButton.setOnClickListener(this);
        defaultSignInButton.setVisibility(View.GONE); // Don't let the user signout before signing in*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignInButton:
                progress.setTitle("Loading...");
                progress.setMessage("Authenticating!");
                signIn();
                break;
        }
    }

    @OnClick(R.id.regularSignUpButton)
    public void signUp() {
        // Launch the intent for the register activity
        Intent signUpIntent = new Intent(this, RegisterMain.class);

        startActivity(signUpIntent);
    }

    public void signIn() {
        progress.show();
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
                        progress.hide();
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