package com.nixho.scheduled;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nixho.scheduled.Authentication.InternalLayer;
import com.nixho.scheduled.Utilities.Singleton;

public class InnerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView profilePicture;
    int backCount = 0;
    InternalLayer IL = new InternalLayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent(); // Coming from MainActivity,
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Setup the CalendarView
        Singleton.INSTANCE.calendar = (CalendarView) findViewById(R.id.MainCalendar);

        // https://www.youtube.com/watch?v=ZHLCfqN-60A
        Singleton.INSTANCE.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    Toast.makeText(view.getContext(), "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
            }
        });

        setSupportActionBar(toolbar);

        FloatingActionButton FloatingButton = (FloatingActionButton) findViewById(R.id.InnerActivityFAButton);
        FloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load the current user
        GoogleSignInAccount currUser = (GoogleSignInAccount) getIntent().getSerializableExtra("GoogleAccount");

        // We'll have to initialize the data
        //profilePicture = (ImageView) navigationView.findViewById(R.id.imageView);

        // http://stackoverflow.com/questions/37466644/set-an-imageview-from-photo-url
        //Picasso.with(this).load(currUser.getPhotoUrl()).into(profilePicture);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // http://stackoverflow.com/questions/2592037/is-there-a-default-back-keyon-device-listener-in-android
            //super.onBackPressed(); // Goes back to the parent activity

            // Just send the app to a background task
            // http://stackoverflow.com/questions/17719634/how-to-exit-an-android-app-using-code
            moveTaskToBack(true);

            // This will bloody shut the app down
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inner_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out)  {
            IL.logout();
            Intent mainIntent = new Intent(InnerMainActivity.this, MainActivity.class);

            // Intent flag to prevent the activity to be "back"able
            //http://stackoverflow.com/questions/5000787/how-to-finish-current-activity-in-android
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(mainIntent);
            Toast.makeText(this, "Leggo", Toast.LENGTH_SHORT).show();

            // Reason why finish is used instead
            // http://stackoverflow.com/questions/7117690/whats-the-difference-between-finish-and-finishactivityint-requestcode-in-and
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
