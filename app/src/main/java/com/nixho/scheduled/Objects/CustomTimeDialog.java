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

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),
                this,
                year, month, day);
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
