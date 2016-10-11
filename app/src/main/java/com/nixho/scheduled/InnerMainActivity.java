package com.nixho.scheduled;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.plus.Plus;
import com.nixho.scheduled.Fragments.CalendarFragment;
import com.nixho.scheduled.Fragments.TasksFragment;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static com.nixho.scheduled.Fragments.TasksFragment.createTaskView;
import static com.nixho.scheduled.MainActivity.User;
import static com.nixho.scheduled.MainActivity.mAuth;
import static com.nixho.scheduled.MainActivity.mGoogleApiClient;
import static com.nixho.scheduled.MainActivity.progress;

public class InnerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Before you attempt to public static any view from your layouts,
     * read this first.
     *
     * http://stackoverflow.com/questions/11908039/android-static-fields-and-memory-leaks
     *
     * Doing public static CalendarView calendar for example is bad, and will result in
     * memory leaks.
     */
    ImageView profilePicture; // Placeholder to store the user's profile picture

    /**
     * The use of ButterKnife
     *
     * ButterKnife allows us to tidy up code for "linking" our xml objects
     * with our code. So as you scroll through my code, you'll notice that
     * the onCreate method wouldn't be so long due to ButterKnife.
     */
    @BindView(R.id.InnerActivityFAButton) FloatingActionButton FloatingButton;
    @BindView(R.id.MainCalendar) CalendarView calendar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_main);
        progress.dismiss(); // Stop loading lmao
        ButterKnife.bind(this); // Bind ButterKnife to this activity so that it can be used.

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Setup the CalendarView
        calendar = (CalendarView) findViewById(R.id.MainCalendar);

        // https://www.youtube.com/watch?v=ZHLCfqN-60A
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(view.getContext(), "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
            }
        });

        setSupportActionBar(toolbar);

        // Initialize the Universal Floating Button
        //FloatingButton = (FloatingActionButton) findViewById(R.id.InnerActivityFAButton);

        /**
         * Handling Fragment OnClicks via the Floating Action Button like a boss
         *
         * http://stackoverflow.com/questions/6750069/get-the-current-fragment-object
         */
        FloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTitle = getTitle().toString(); // Get the Title from the action bar.

                switch (currentTitle) {
                    case "Scheduled":
                        FloatingButton.hide();
                        break;
                    case "Calendar":
                        FloatingButton.hide();
                        break;
                    case "Tasks":
                        if (!FloatingButton.isShown()) {
                            FloatingButton.show();
                        }

                        createTaskView(view); // Invokes the createTaskView method via the TasksFragment class
                        break;
                    default:
                        break;
                }
            }
        });

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        // http://stackoverflow.com/questions/35639454/method-setdrawerlistener-is-deprecated
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        /**
         * FragmentManager
         *
         * The FragmentManager obejct helps us to manage all of the Fragments that we have
         * instantiated, helps us replace the current fragment over another without having to
         * run lots of lines of code for that.
         */
        FragmentManager manager = getSupportFragmentManager(); // Initialize the FragmentManager

        switch (id) {
            case R.id.nav_calendar:
                CalendarFragment calendarFragment = new CalendarFragment(); // Create an object from the fragment
                setTitle(R.string.title_activity_inner_main);

                // The set of code below is probably broken somewhere..
                // NullPointerException somewhere...

                /**
                 * .replace
                 *
                 * When applying .replace to the different sources of fragments, we'll be able to
                 * replace the existing fragment with another one, without even having the need
                 * to close/remove the existing fragment. FragmentManager helps us to achieve this without
                 * the need of doing anything about it.
                 */
                manager.beginTransaction().replace(R.id.mainContent, calendarFragment, calendarFragment.getTag())
                        //.setTransitionStyle()
                        .commit();
                break;
            case R.id.nav_tasks:
                TasksFragment tasksFragment = new TasksFragment(); // Create an object from the fragment
                setTitle(R.string.title_activity_inner_tasks);

                manager.beginTransaction().replace(R.id.mainContent, tasksFragment, tasksFragment.getTag()).commit();
                break;
            case R.id.sign_out:
                logout();
                Intent mainIntent = new Intent(InnerMainActivity.this, MainActivity.class);

                // Intent flag to prevent the activity to be "back"able
                //http://stackoverflow.com/questions/5000787/how-to-finish-current-activity-in-android
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(mainIntent);

                // Reason why finish is used instead
                // http://stackoverflow.com/questions/7117690/whats-the-difference-between-finish-and-finishactivityint-requestcode-in-and
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    public void logout() {
        Log.e(TAG, "Running Logout()");

        if (mAuth != null) {
            /* logout of Firebase */
            mAuth.signOut();
            //firebase.unauth(); // Deprecated code from FireBase 2.X

            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Google+ after logging out of Firebase. */
            /* Logout from Google+ */
            //if (mAuthData.getProvider().equals("google")) { // Deprecated Code
            if (mGoogleApiClient.isConnected()) {
                //Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
            //}

            /* Update authenticated user and show login buttons */
            User = null;
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
