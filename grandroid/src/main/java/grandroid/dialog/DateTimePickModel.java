/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;

/**
 *
 * @author Rovers
 */
public abstract class DateTimePickModel implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /**
     *
     */
    protected int mode;
    /**
     *
     */
    protected Calendar cal;
    /**
     *
     */
    public static final int DateMode = 0;
    /**
     *
     */
    public static final int TimeMode = 1;
    public Activity activity;
    protected boolean picked;

    /**
     *
     * @param mode
     */
    public DateTimePickModel(int mode) {
        this.mode = mode;
        cal = Calendar.getInstance();
    }

    /**
     *
     * @param mode
     * @param cal
     */
    public DateTimePickModel(int mode, Calendar cal) {
        this.mode = mode;
        this.cal = cal;
    }

    /**
     *
     * @param picker
     * @param year
     * @param month
     * @param date
     */
    public synchronized void onDateSet(DatePicker picker, int year, int month, int date) {
        if (!picked) {
            picked = true;
            cal.set(year, month, date);
            onPicked(DateMode, cal);
            if (activity != null) {
                activity.removeDialog(DateMode);
            }
        }
    }

    /**
     *
     * @param picker
     * @param hour
     * @param minute
     */
    public synchronized void onTimeSet(TimePicker picker, int hour, int minute) {
        if (!picked) {
            picked = true;
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            onPicked(TimeMode, cal);
            if (activity != null) {
                activity.removeDialog(TimeMode);
            }
        }
    }

    /**
     *
     * @return
     */
    public int getMode() {
        return mode;
    }

    /**
     *
     * @param context
     * @return
     */
    public Dialog createDialog(Activity face) {
        this.activity = face;
        picked = false;
        switch (mode) {
            case DateMode:
                return new DatePickerDialog(face, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            case TimeMode:
                return new TimePickerDialog(face, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        }
        return null;
    }

    /**
     *
     * @param mode
     * @param calendar
     */
    public abstract void onPicked(int mode, Calendar calendar);
}
