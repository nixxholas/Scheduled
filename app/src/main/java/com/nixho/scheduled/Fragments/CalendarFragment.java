package com.nixho.scheduled.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nixho.scheduled.R;
import com.nixho.scheduled.Utilities.Singleton;

/**
 * Created by nixho on 28-Sep-16.
 */

public class CalendarFragment extends Fragment {
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

        // Setup the CalendarView
        Singleton.INSTANCE.calendar = (CalendarView) view.findViewById(R.id.MainCalendar);

        Singleton.INSTANCE.calendar.setLayoutParams(new RelativeLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        // https://www.youtube.com/watch?v=ZHLCfqN-60A
        Singleton.INSTANCE.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(view.getContext(), "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}