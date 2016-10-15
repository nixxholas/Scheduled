package com.nixho.scheduled.Objects;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.nixho.scheduled.InnerMainActivity;
import com.nixho.scheduled.R;

import java.util.Calendar;

/**
 * Adapted From
 * https://www.youtube.com/watch?v=eVsqDBvgd70
 *
 * Created by nixho on 12-Oct-16.
 */

public class CustomTimeDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    EditText DateInput;
    static int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private static String date;

    public CustomTimeDialog(View v) {
        DateInput = (EditText) v;
    }

    public static int getDayFinal() {
        return dayFinal;
    }

    public static void setDayFinal(int dayFinal) {
        CustomTimeDialog.dayFinal = dayFinal;
    }

    public static int getMonthFinal() {
        return monthFinal;
    }

    public static void setMonthFinal(int monthFinal) {
        CustomTimeDialog.monthFinal = monthFinal;
    }

    public static int getYearFinal() {
        return yearFinal;
    }

    public static void setYearFinal(int yearFinal) {
        CustomTimeDialog.yearFinal = yearFinal;
    }

    public static int getHourFinal() {
        return hourFinal;
    }

    public static void setHourFinal(int hourFinal) {
        CustomTimeDialog.hourFinal = hourFinal;
    }

    public static int getMinuteFinal() {
        return minuteFinal;
    }

    public static void setMinuteFinal(int minuteFinal) {
        CustomTimeDialog.minuteFinal = minuteFinal;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Instantiate the DatePickerDialog Object
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                this,
                year,
                month,
                day);

        // Enforce the user to only select dates later or equal to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        yearFinal = year;
        monthFinal = month;
        dayFinal = dayOfMonth;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        DateInput.setText(dayFinal + "/" + monthFinal + "/" + yearFinal + "\t" + hourFinal + ":" + minuteFinal);
    }

}
