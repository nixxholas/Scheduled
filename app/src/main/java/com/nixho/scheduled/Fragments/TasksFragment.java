package com.nixho.scheduled.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.nixho.scheduled.InnerMainActivity;
import com.nixho.scheduled.Objects.Tasks;
import com.nixho.scheduled.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;

import static com.nixho.scheduled.MainActivity.User;
import static com.nixho.scheduled.MainActivity.rootRef;

/**
 * Created by nixho on 28-Sep-16.
 * <p>
 * Remember that a fragment always needs to be part of an activity.
 * <p>
 * In the event when R does not regenerate:
 * http://stackoverflow.com/questions/2757107/developing-for-android-in-eclipse-r-java-not-regenerating
 * <p>
 * Communicating a Fragment with an Activity via Interfaces (Didn't use this)
 * https://www.youtube.com/watch?v=MHHXxWbSaho
 * <p>
 * Communicating with Fragments
 * https://www.youtube.com/watch?v=dHEQ-xeFxUM
 * <p>
 * Reading up on Firebase Realtime Database & Offline Database
 * https://www.youtube.com/watch?v=cYinms8LurA
 */

public class TasksFragment extends Fragment {
    private static String TAG = "TASKSFRAGMENT: ";

    /**
     * We are using the URL here instead of the Firebase Object
     * <p>
     * Firebase ref = new Firebase("https://scheduled-7f23b.firebaseio.com/Zaki");
     * <p>
     * DatabaseReference is the updated Object for Firebase 3.0
     */
    static final DatabaseReference tasksRef = rootRef.child(User.getUid()).child("Tasks"); // Setup the Database Reference

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * In order to prevent fragment stacking, we'll need to remove any trace
         *
         * http://stackoverflow.com/questions/18309815/fragments-displayed-over-each-other
         */
        if (container != null) {
            container.removeAllViews();
        }

        // Setup the views
        final View view = inflater.inflate(R.layout.content_inner_tasks, container, false);

        // Setup the RecyclerView, a place to show all the tasks
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tasks_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**
         * PLEASE READ ALL THE COMMENTS BEFORE REUSING THE CODE BELOW.
         */
        final FirebaseRecyclerAdapter<Tasks, TaskViewHolder> adapter =
                new FirebaseRecyclerAdapter<Tasks, TaskViewHolder>(
                        Tasks.class, // We need to inform the Adapter what kind of datatype we're taking in.
                        // in this case, we're taking in a Tasks object.
                        R.layout.taskrow, // Inform the adapter to utilize the taskrow layout for any
                        // form of view that we're going to inject into
                        TaskViewHolder.class, // We need to invoke a view injector, which is this class
                        // that will help us to inject the Tasks object into the taskrow layout.
                        tasksRef // Basically the DatabaseReference we're taking the object from.
                ) {

                    /**
                     * Populating the RecyclerView..
                     *
                     * @param viewHolder
                     *
                     *
                     * @param task
                     *
                     *
                     * @param position
                     * With the use of position, we can obtain the key of from the FirebaseDatabase
                     *
                     * http://stackoverflow.com/questions/37568703/how-to-get-keys-and-values-using-firebaselistadapter
                     */
                    @Override
                    protected void populateViewHolder(TaskViewHolder viewHolder, Tasks task, int position) {
                        // Get the key of the Tasks object
                        //String currentKey = getRef(position).push().getKey();
                        //final String currentKey = getRef(position).toString(); // This returns the object URL from Firebase
                        final String currentKey = getRef(position).getKey();
                        Log.d(TAG, currentKey.toString());
                        Log.d(TAG, "Image: " + task.getImageUrl());

                        // Basically we need to attach the task to the viewHolder so that
                        // the cards can instantiate their view properly
                        viewHolder.setTaskName(task.getTaskName());
                        viewHolder.setTaskDesc(task.getTaskDescription());
                        viewHolder.setTaskImage(task.getImageUrl());

                        final Intent updateView = new Intent(getActivity(), UpdateTaskActivity.class);

                        // Implement Serializable on the Tasks object,
                        // Push the object directly via updateView.putExtra
                        // That way we can have everything we need in the object.

                        //updateView.putExtra("TaskName", task.getTaskName());
                        //updateView.putExtra("TaskDesc", task.getTaskDescription());
                        updateView.putExtra("TaskObject", task);
                        updateView.putExtra("Key", currentKey);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 * How to provide a foundation to animate cards
                                 *
                                 * http://stackoverflow.com/questions/27300441/how-do-i-expand-cardviews-to-show-more-detail-like-google-keep-cards
                                 */
                                //Toast.makeText(getActivity(), currentKey, Toast.LENGTH_LONG).show(); // Test Line to Showcase the Key.

                                ActivityOptionsCompat options =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                                v,   // The view which starts the transition
                                                getString(R.string.transition_taskcard)    // The transitionName of the view we’re transitioning to
                                        );

                                ActivityCompat.startActivity(getActivity(), updateView, options.toBundle());
                            }
                        });
                    }
                };

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * This removes the CardView from the RecyclerView, allowing us to create a delete request
             *
             * http://stackoverflow.com/questions/27293960/swipe-to-dismiss-for-recyclerview/30601554#30601554
             *
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // First find the position of the card
                // https://www.learn2crack.com/2016/02/custom-swipe-recyclerview.html
                int position = viewHolder.getAdapterPosition();

                // Connect to the database, identify which card was swiped via the RecyclerViewAdapter
                DatabaseReference currRef = adapter.getRef(position);

                // Get it out of the DB
                currRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    /**
     * Allows us to Initialize a TextView Dynamically
     * <p>
     * Only use this when you wanna learn RecyclerView
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;

        public TaskViewHolder(View v) {
            super(v);

            mView = v;

            // You can also set an OnClickListener here so that you
            // can listen on a specific resource/element.
        }

        public void setTaskName(String taskName) {
            TextView taskNameView = (TextView) mView.findViewById(R.id.taskrow_TaskName);
            taskNameView.setText(taskName);
        }

        public void setTaskDesc(String taskDesc) {
            TextView taskDescView = (TextView) mView.findViewById(R.id.taskrow_TaskDesc);
            taskDescView.setText(taskDesc);
        }

        public void setTaskImage(String imageURI) {
            String TAG = "TASKSFRAGMENT/setTaskImage: ";
            ImageView taskImageView = (ImageView) mView.findViewById(R.id.taskrow_TaskImage);

            try {
                //URI url = URI.create(imageURI);
                //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                Picasso.with(mView.getContext())
                        .load(imageURI)
                        .resize(taskImageView.getMeasuredWidth(), taskImageView.getMaxHeight())
                        .centerInside()
                        .into(taskImageView);


                // http://stackoverflow.com/questions/17356312/converting-of-uri-to-string
                // taskImageView.setImageURI(Uri.parse(imageURI));
                // taskImageView.setImageBitmap(bmp);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onClick(View v) {

            // We'll need to create the new view here.
            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Call some material design APIs here
                /*ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(TasksFragment.class,
                                v,   // The view which starts the transition
                                ""    // The transitionName of the view we’re transitioning to
                        );
                ActivityCompat.startActivity(activity, intent, options.toBundle());*/

            } else {
                // Implement this feature without material design
            }

        }
    }

}
