package com.nixho.scheduled;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * ActivityExtension Class
 *
 * Introduced to provide one solution
 * - Solve the need to apply global "settings" on all activities
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
