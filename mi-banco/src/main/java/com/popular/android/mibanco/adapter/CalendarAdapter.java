package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.KiuwanUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Adapter for calendar list
 */
public class CalendarAdapter extends BaseAdapter {

    /**
     * DAYS_IN_WEEK
     */
    private final static int DAYS_IN_WEEK = 7;

    /**
     * FIRST_DAY_SUNDAY
     */
    private final static int FIRST_DAY_SUNDAY = 0;

    /**
     * context vars
     */
    private final Context context; // contents

    /**
     * inflater
     */
    private final LayoutInflater inflater; // inflater view

    /**
     * startDate
     */
    private final Calendar startDate; // start date

    /**
     * mInitialDate
     */
    private Calendar mInitialDate; // initial date

    /**
     * currentDate
     */
    private Calendar currentDate; // current calendar date

    /**
     * days
     */
    private final List<String> days; // days

    /**
     * items
     */
    private List<String> items; // items

    /**
     * monthOffset
     */
    private final List<Integer> monthOffset; // initial month

    /**
     * selectedDate
     */
    private Calendar selectedDate; // selected date

    /**
     * rtNotification
     */
    private Boolean rtNotification; // real time payee

    /**
     * CalendarAdapter constructor
     * @param context context value
     * @param startDate selectedinitial date
     * @param  initialDate inital date today
     */
    public CalendarAdapter(final Context context, final Calendar startDate, Calendar initialDate) {
        currentDate = startDate;
        mInitialDate = KiuwanUtils.checkBeforeCast(Calendar.class, initialDate.clone());
        selectedDate = KiuwanUtils.checkBeforeCast(Calendar.class, startDate.clone());
        this.startDate = KiuwanUtils.checkBeforeCast(Calendar.class, startDate.clone());
        this.context = context;
        currentDate.set(Calendar.DAY_OF_MONTH, 1);
        items = new ArrayList<>(0);
        days = new ArrayList<>(0);
        monthOffset = new ArrayList<>(0);
        rtNotification = false;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        refreshDays(currentDate);
    }

    /**
     * getCount
     * @return int
     */
    @Override
    public int getCount() {
        return days.size();
    }

    /**
     * getItem
     * @param position
     * @return Object
     */
    @Override
    public Object getItem(final int position) {
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    public int getMonthOffset(final int position) {
        return monthOffset.get(position);
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;

        myConvertView = inflater.inflate(R.layout.calendar_item, null);

        final TextView dayView = (TextView) myConvertView.findViewById(R.id.date);
        setDayItemLook(dayView, myConvertView, position);
        dayView.setText(days.get(position));

        String date = days.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (currentDate.get(Calendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        FontChanger.changeFonts(myConvertView);

        return myConvertView;
    }

    public void setDayItemLook(final TextView dayTextView, final View linearLayout, final int position) {
        dayTextView.setPressed(false);
        linearLayout.setPressed(false);

        dayTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
        linearLayout.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(days.get(position))) {
            linearLayout.setVisibility(View.INVISIBLE);
        } else {
            final Calendar actualDate = (Calendar) selectedDate.clone();

            actualDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
            actualDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
            actualDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(days.get(position)));

            linearLayout.setBackgroundResource(R.drawable.calendar_selector);

            if (actualDate.before(mInitialDate) && !sameDate(actualDate, mInitialDate)) {
                dayTextView.setTextColor(ContextCompat.getColor(context, R.color.grey_light));
            }
            else if (rtNotification) {
                if (monthOffset.get(position) != 0) {
                    dayTextView.setTextColor(ContextCompat.getColor(context, R.color.grey_light));
                }
                else if (sameDate(actualDate, selectedDate)) {
                    linearLayout.setBackgroundResource(R.drawable.calendar_item_selected);
                    dayTextView.setTextColor(Color.WHITE);
                } else if (sameDate(actualDate, startDate)) {
                    linearLayout.setBackgroundResource(R.drawable.calendar_item_current);
                }
            }
            else if (monthOffset.get(position) != 0
                    || actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dayTextView.setTextColor(ContextCompat.getColor(context, R.color.grey_light));
            }
            else if (sameDate(actualDate, selectedDate)) {
                linearLayout.setBackgroundResource(R.drawable.calendar_item_selected);
                dayTextView.setTextColor(Color.WHITE);
            }
            else if (sameDate(actualDate, startDate)) {
                linearLayout.setBackgroundResource(R.drawable.calendar_item_current);
            }
        }
    }

    /**
     * isWeekendDay
     * @param actualDate today
     * @return boolean
     */
    private boolean isWeekendDay(Calendar actualDate){
        return actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public void refreshDays(final Calendar date) {
        items.clear();
        days.clear();
        monthOffset.clear();

        currentDate = (Calendar) date.clone();

        final int firstDay = date.get(Calendar.DAY_OF_WEEK);
        final int numDays = date.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (firstDay > 1) {
            final Calendar previousMonth = (Calendar) date.clone();
            previousMonth.set(Calendar.DAY_OF_MONTH, 1);
            previousMonth.add(Calendar.DAY_OF_MONTH, -1);

            final int previousMonthLastDay = previousMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int j = 0; j < firstDay - FIRST_DAY_SUNDAY - 1; j++) {
                days.add("" + (previousMonthLastDay - (firstDay - FIRST_DAY_SUNDAY) + j + 2));
                monthOffset.add(-1);
            }
        }

        for (int i = 1; i <= numDays; ++i) {
            days.add("" + i);
            monthOffset.add(0);
        }

        if (days.size() % DAYS_IN_WEEK != 0) {
            for (int i = 1; i <= days.size() % DAYS_IN_WEEK; ++i) {
                days.add("" + i);
                monthOffset.add(1);
            }
        }
    }
    
    private boolean sameDate(Calendar firstDate, Calendar secondDate) {
    	return firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR) &&
    			firstDate.get(Calendar.DAY_OF_YEAR) == secondDate.get(Calendar.DAY_OF_YEAR);
    }

    public void setItems(final ArrayList<String> items) {
        this.items = items;
    }

    public void setSelectedDate(final Calendar aSelectedDate) {
        selectedDate = aSelectedDate;
    }

    public void setRtNotification(final Boolean boolRtNotification) {
        rtNotification = boolRtNotification;
    }
}
