package mietzekatze.climbingtracker.dataHandling;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import mietzekatze.climbingtracker.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener  {

    private DatePickerFragmentListener datePickerListener;
    public interface DatePickerFragmentListener {
        public void onDateSet(Calendar calendar);
    }

    public DatePickerFragmentListener getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(DatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(Calendar calendarEntry) {
        if(this.datePickerListener != null) {
            this.datePickerListener.onDateSet(calendarEntry);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i("DATEPICKER","Created");
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public static DatePickerFragment newInstance(DatePickerFragmentListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setDatePickerListener(listener);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendarEntry = Calendar.getInstance();
        calendarEntry.set(year, month, day);
        notifyDatePickerListener(calendarEntry);
    }
    /*
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView editText = (TextView) getActivity().findViewById(R.id.date_slot);
        editText.setText(day +"/"+month+"/"+year);
        dismiss();
    }*/

}

