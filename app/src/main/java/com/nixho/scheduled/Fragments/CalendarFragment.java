package com.nixho.scheduled.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.nixho.scheduled.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nixho on 28-Sep-16.
 */

public class CalendarFragment extends Fragment {
    @BindView(R.id.MainCalendar) CalendarView calendar;
    
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

        View view = inflater.inflate(R.layout.content_inner_main, container, false);

        // ButterKnife once again
        ButterKnife.bind(this, view);

        // Setup the CalendarView
        //calendar = (CalendarView) view.findViewById(R.id.MainCalendar);

        calendar.setLayoutParams(new CoordinatorLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT,
                                CoordinatorLayout.LayoutParams.MATCH_PARENT));

        // https://www.youtube.com/watch?v=ZHLCfqN-60A
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(view.getContext(), "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public static void createNewCalendarEvent(View v) {
        // Inflate -> View
        View newView = LayoutInflater.from(v.getContext()).inflate(R.layout.content_inner_tasks_createalert, null); // Well, indians told me to null..

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        builder//.setMessage("Create a new task") Well, this overlaps with the resources from newView
                .setView(newView);

        AlertDialog alert = builder.create();
        alert.show();
    }
}