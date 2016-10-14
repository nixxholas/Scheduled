package com.nixho.scheduled.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.nearby.messages.internal.Update;
import com.nixho.scheduled.Objects.Tasks;
import com.nixho.scheduled.R;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nixho.scheduled.Fragments.TasksFragment.tasksRef;
import static com.nixho.scheduled.MainActivity.User;

public class UpdateTaskActivity extends AppCompatActivity {
    Uri uploadedViewUri;
    Tasks updatedTask;
    String taskImageUrl;
    String taskDeadline;
    String TAG = "UPDATETASK";
    String currentKey;
    DateTime taskDate;

    @BindView(R.id.taskrow_detailed_TaskName) EditText EditTaskName;
    @BindView(R.id.taskrow_detailed_TaskDesc) EditText EditTaskDesc;
    @BindView(R.id.taskrow_detailed_TaskDate) EditText EditTaskDate;
    @BindView(R.id.taskrow_detailed_TaskImage) ImageView EditTaskImage;
    @BindView(R.id.taskrow_submitBtn) Button EditTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskrow_detailed);
        ButterKnife.bind(this);

        Log.d(TAG, "Initializing UpdateTaskActivity");

        // Retrieve the Task Object from the Intent
        Tasks task = (Tasks) getIntent().getSerializableExtra("TaskObject");

        // Set the URL of the image for updating
        taskImageUrl = task.getImageUrl();

        taskDeadline = task.getTaskDeadline();

        // Initialize the current layout's elements into objects first
        //EditText EditTaskName = (EditText) findViewById(R.id.taskrow_detailed_TaskName);
        //EditText EditTaskDesc = (EditText) findViewById(R.id.taskrow_detailed_TaskDesc);

        // Retrieve the whole intent that previously invoked this method.
        Bundle bundle = getIntent().getExtras(); // Retrieves all of the extra data from the previous intent.

        // Set the key first.
        currentKey = bundle.getString("Key");

        // set an exit transition
        getWindow().setReenterTransition(new Explode());

        // Getting the data from the previous layout
        // http://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
        if (task.getTaskName() != null) {
            EditTaskName.setText(task.getTaskName());
            Log.d(TAG, "Loaded EditTaskName");
        }

        if (task.getTaskDescription() != null) {
            EditTaskDesc.setText(task.getTaskDescription());
            Log.d(TAG, "Loaded EditTaskDesc");
    }

        if (task.getImageUrl() != null) {
            uploadedViewUri = Uri.parse(task.getImageUrl());
            Log.d(TAG, "Loading EditTaskImage");

            // Quick way to create a thread for Picasso is via Handler instead of Thread
            // https://github.com/square/picasso/issues/547
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(UpdateTaskActivity.this)
                            .load(uploadedViewUri)
                            .resize(EditTaskImage.getMeasuredWidth(), EditTaskImage.getMaxHeight())
                            .centerInside()
                            .into(EditTaskImage);
                }
            });

            Log.d(TAG, "Loaded EditTaskImage");
        }

        if (task.getTaskDeadline() != null) {
            taskDate = new DateTime(task.getTaskDeadline(), DateTimeZone.UTC);

            EditTaskDate.setText(taskDate.toLocalDateTime().toString());
            Log.d(TAG, "Loaded EditTaskDeadline");
        }

        Log.d(TAG, "Initialized UpdateTaskActivity");
    }

    @OnClick(R.id.taskrow_submitBtn)
    public void submit() {
        //String UID, String userName, String taskName, String taskDescription, String taskDeadline, String imageUrl
        updatedTask = new Tasks(User.getUid(), User.getDisplayName(), EditTaskName.getText().toString(), EditTaskDesc.getText().toString(), taskDeadline, taskImageUrl);

        Log.d(TAG, "Updating Task: " + updatedTask.getUniqueId());

        // Let's make sure we're really updated the task
        // by referencing to the key
        // https://www.youtube.com/watch?v=ci7NBiBVk5I
        tasksRef.child(currentKey).setValue(updatedTask);
    }
}
