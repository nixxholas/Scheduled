package com.nixho.scheduled.Fragments;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.widget.EditText;

import com.nixho.scheduled.ActivityExtension;
import com.nixho.scheduled.R;

public class UpdateTaskActivity extends ActivityExtension {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskrow_detailed);

        // Initialize the current layout's elements into objects first
        EditText EditTaskName = (EditText) findViewById(R.id.taskrow_detailed_TaskName);
        EditText EditTaskDesc = (EditText) findViewById(R.id.taskrow_detailed_TaskDesc);

        // Retrieve the whole intent that previously invoked this method.
        Bundle bundle = getIntent().getExtras(); // Retrieves all of the extra data from the previous intent.

        // set an exit transition
        getWindow().setExitTransition(new Explode());

        // Getting the data from the previous layout
        // http://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
        if (bundle.getString("TaskName") != null) {
            EditTaskName.setText(bundle.getString("TaskName"));
        }

        if (bundle.getString("TaskDesc") != null) {
            EditTaskDesc.setText(bundle.getString("TaskDesc"));
        }
    }
}
