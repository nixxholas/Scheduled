package com.nixho.scheduled.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.nixho.scheduled.R;
import com.nixho.scheduled.Utilities.Singleton;

/**
 * Created by nixho on 28-Sep-16.
 *
 * Remember that a fragment always needs to be part of an activity.
 *
 * In the event when R does not regenerate:
 * http://stackoverflow.com/questions/2757107/developing-for-android-in-eclipse-r-java-not-regenerating
 *
 * Communicating a Fragment with an Activity via Interfaces
 * https://www.youtube.com/watch?v=MHHXxWbSaho
 */

public class TasksFragment extends Fragment{

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

        View view = inflater.inflate(R.layout.content_inner_tasks, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tasks_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**
         * We are using the URL here instead of the Firebase Object
         *
         * Firebase ref = new Firebase("https://scheduled-7f23b.firebaseio.com/Zaki");
         *
         * DatabaseReference is the updated Object for Firebase 3.0
         */
        DatabaseReference databaseReference = Singleton.INSTANCE.getDatabase("Zaki");

        FirebaseRecyclerAdapter<String, MessageViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, MessageViewHolder>(
                        String.class,
                        android.R.layout.two_line_list_item,
                        MessageViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(MessageViewHolder viewHolder, String model, int position) {
                        viewHolder.mText.setText(model);

                        viewHolder.mText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                };

        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Allows us to Initialize a TextView Dynamically
     *
     * Only use this when you wanna learn RecyclerView
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        public MessageViewHolder(View v) {
            super(v);
            mText = (TextView) v.findViewById(android.R.id.text1);

            // You can also set an OnClickListener here so that you
            // can listen on a specific resource/element.
        }
    }
}
