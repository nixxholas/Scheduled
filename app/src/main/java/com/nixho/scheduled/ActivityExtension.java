package com.nixho.scheduled;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * ActivityExtension Class
 *
 * Introduced to provide one solution
 * - Solve the need to apply global "settings" on all activities
 *
 * Why use FragmentActivity over Activity?
 * http://stackoverflow.com/questions/33932997/error-incompatible-types-mainactivity-cannot-be-converted-to-fragmentactivity
 */
public class ActivityExtension extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix the orientation to portrait
        // http://stackoverflow.com/questions/7153078/disable-landscape-mode-for-a-whole-application
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
