package com.nixho.scheduled.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.nixho.scheduled.Objects.Tasks;
import com.nixho.scheduled.R;

import static com.nixho.scheduled.MainActivity.User;
import static com.nixho.scheduled.MainActivity.rootRef;

/**
 * Created by nixho on 28-Sep-16.
 *
 * Remember that a fragment always needs to be part of an activity.
 *
 * In the event when R does not regenerate:
 * http://stackoverflow.com/questions/2757107/developing-for-android-in-eclipse-r-java-not-regenerating
 *
 * Communicating a Fragment with an Activity via Interfaces (Didn't use this)
 * https://www.youtube.com/watch?v=MHHXxWbSaho
 *
 * Communicating with Fragments
 * https://www.youtube.com/watch?v=dHEQ-xeFxUM
 *
 * Reading up on Firebase Realtime Database & Offline Database
 * https://www.youtube.com/watch?v=cYinms8LurA
 */

public class TasksFragment extends Fragment {
    private static String TAG = "TASKSFRAGMENT: ";

    /**
     * We are using the URL here instead of the Firebase Object
     *
     * Firebase ref = new Firebase("https://scheduled-7f23b.firebaseio.com/Zaki");
     *
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

                        // Basically we need to attach the task to the viewHolder so that
                        // the cards can instantiate their view properly
                        viewHolder.setTaskName(task.getTaskName());
                        viewHolder.setTaskDesc(task.getTaskDescription());

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
     *
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

    public static void createTaskView(View v) {
        // Inflate -> View
        final View newView = LayoutInflater.from(v.getContext()).inflate(R.layout.content_inner_tasks_createalert, null); // Well, indians told me to null..

        // Initialize the elements after the view has been initialized
        final EditText taskName = (EditText) newView.findViewById(R.id.createalert_TaskName);
        final EditText taskDesc = (EditText) newView.findViewById(R.id.createalert_TaskDesc);
        final EditText taskDate = (EditText) newView.findViewById(R.id.createalert_TaskDate);
        //Button createTaskBtn = (Button) newView.findViewById(R.id.createTask_btnCreate);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        builder//.setMessage("Create a new task") Well, this overlaps with the resources from newView
                .setCancelable(false)
                .setView(newView);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //String uid = User.getUid();
                String Username = User.getDisplayName();
                // String name = "User " + uid.substring(0, 6);
                String taskname = taskName.getText().toString();
                String taskdesc = taskDesc.getText().toString();
                String taskCal = taskDate.getText().toString();

                Tasks task = new Tasks(Username, taskname, taskdesc, taskCal);

                String key = tasksRef.push().getKey();

                task.setUniqueId(key);

                // Users
                // -> User's Unique ID
                // --> Tasks
                // ---> Task1..
                tasksRef.push().setValue(task);
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();

        alert.show();
    }

}
