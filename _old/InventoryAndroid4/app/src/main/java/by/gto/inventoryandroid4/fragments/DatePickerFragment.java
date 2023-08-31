package by.gto.inventoryandroid4.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by timo on 10.01.2017.
 */

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private Date initDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;

        if (initDate != null) {
            year = initDate.getYear() + 1900;
            month = initDate.getMonth();
            day = initDate.getDate();
        } else {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

    public DatePickerFragment() {
    }

    public DatePickerDialog.OnDateSetListener getListener() {
        return listener;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }
}