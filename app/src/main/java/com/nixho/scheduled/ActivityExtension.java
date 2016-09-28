package com.nixho.scheduled;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityExtension extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix the orientation to portrait
        // http://stackoverflow.com/questions/7153078/disable-landscape-mode-for-a-whole-application
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
