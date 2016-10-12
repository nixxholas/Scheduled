package com.nixho.scheduled.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.nixho.scheduled.Objects.Tasks;
import com.nixho.scheduled.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nixho.scheduled.Fragments.TasksFragment.tasksRef;
import static com.nixho.scheduled.MainActivity.User;

public class UpdateTaskActivity extends AppCompatActivity {
    Tasks updatedTask;
    String TAG = "UPDATETASK";
    String currentKey;

    @BindView(R.id.taskrow_detailed_TaskName) EditText EditTaskName;
    @BindView(R.id.taskrow_detailed_TaskDesc) EditText EditTaskDesc;
    @BindView(R.id.taskrow_submitBtn) Button EditTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskrow_detailed);
        ButterKnife.bind(this);

        Log.d(TAG, "Initializing UpdateTaskActivity");

        // Retrieve the Task Object from the Intent
        Tasks task = (Tasks) getIntent().getSerializableExtra("TaskObject");

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

        Log.d(TAG, "Initialized UpdateTaskActivity");

    }

    @OnClick(R.id.taskrow_submitBtn)
    public void submit() {
        //String userId, String userName, String taskName, String taskDescription, String taskDeadline
        updatedTask = new Tasks(User.getUid(), User.getDisplayName(), EditTaskName.getText().toString(), EditTaskDesc.getText().toString(), null);

        Log.d(TAG, "Updating Task: " + updatedTask.getUniqueId());

        // Let's make sure we're really updated the task
        // by referencing to the key
        // https://www.youtube.com/watch?v=ci7NBiBVk5I
        tasksRef.child(currentKey).setValue(updatedTask);
    }
}
