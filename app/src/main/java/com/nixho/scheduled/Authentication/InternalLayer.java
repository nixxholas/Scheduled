package com.nixho.scheduled.Authentication;

import android.util.Log;

import com.google.android.gms.plus.Plus;
import com.nixho.scheduled.Utilities.Singleton;

import static android.content.ContentValues.TAG;

/**
 * Created by nixho on 23-Sep-16.
 */

public class InternalLayer {
    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    public void logout() {
        Log.e(TAG,"Running Logout()");
        if (Singleton.INSTANCE.mAuth != null) {
            /* logout of Firebase */
            Singleton.INSTANCE.mAuth.signOut();
            //firebase.unauth();
            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Google+ after logging out of Firebase. */
            //if (mAuthData.getProvider().equals("google")) {
                /* Logout from Google+ */
                if (Singleton.INSTANCE.mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(Singleton.INSTANCE.mGoogleApiClient);
                    Singleton.INSTANCE.mGoogleApiClient.disconnect();
                }
            //}

            /* Update authenticated user and show login buttons */
            Singleton.INSTANCE.User = null;
            //setAuthenticatedUser(null);
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     *
     * Deprecated and useless
     */
    /*private void setAuthenticatedUser(FirebaseAuth authData) {
        if (authData != null) {
            *//* show a provider specific status text *//*
            String name = null;
            if (authData.getProvider().equals("google")) {
                name = (String) authData.getProviderData().get("displayName");
            } else if (authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }
            if (name != null) {
                //mLoggedInStatusTextView.setText("Logged in as " + name + " (" + authData.getProvider() + ")");
            }
        }
        mAuth = authData;
        Log.e(TAG,"Successfully Nullified mAuthData");
    }*/

}
