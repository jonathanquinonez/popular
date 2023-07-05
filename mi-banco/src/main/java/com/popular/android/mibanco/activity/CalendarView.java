/*
  Project: CIBP
  Company: Evertec
 */
package com.popular.android.mibanco.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.CalendarAdapter;
import com.popular.android.mibanco.adapter.MonthsListAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.KiuwanUtils;
import com.popular.android.mibanco.util.OnSwipeListener;
import com.popular.android.mibanco.util.SwipeDetector;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity that manages a calendar view
 */
public class CalendarView extends BaseSessionActivity {

    /**
     * Private vars
     */
    private CalendarAdapter adapter; // adapter

    /**
     * Current Date
     */
    private Calendar currentDate; // current date

    /**
     * Days views
     */
    private GridView gridView; // grid view

    /**
     * Selected user date
     */
    private Calendar selectedDate; // selected date

    /**
     * selected date to show in calendar
     */
    private Calendar startDate; // start date calendar

    /**
     * initial date to show in calendar
     */
    private Calendar mInitialDate; // initial date

    /**
     * calendar gesture events
     */
    private SwipeDetector swipeDetector; // event swipe listener

    /**
     * show calendar for real time payees
     */
    private Boolean rtNotification = false; // real time payee

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view, false);

        final String rtNotificationParam = "rtNotification"; // intent param
        final String initialDateParam = "initialDate"; // initial date intent param
        final String startTimeParam = "startTime"; // start date intent param
        final String realTimePayeeValue = "Y"; // value for real time payees

        if (getIntent().getLongExtra(startTimeParam, 0) != 0) {
            final Date date = new Date(getIntent().getLongExtra(startTimeParam, 0)); // selecte date o initial

            startDate = Calendar.getInstance();
            startDate.setTime(date);
        } else {
            startDate = Calendar.getInstance();
        }

        if (getIntent().getLongExtra(initialDateParam, 0) != 0) {
            final Date date = new Date(getIntent().getLongExtra(initialDateParam, 0)); // selected date

            mInitialDate = Calendar.getInstance();
            mInitialDate.setTime(date);
        } else {
            mInitialDate = Calendar.getInstance();
        }

        if (getIntent().hasExtra(rtNotificationParam)) {
            final String rtNotificationValue = getIntent().getStringExtra(rtNotificationParam); // rt notification intent param

            rtNotification = realTimePayeeValue.equals(rtNotificationValue);
        }

        if (savedInstanceState == null) {
            currentDate = startDate == null ? Calendar.getInstance() : (Calendar) startDate.clone();
            selectedDate = startDate == null ? KiuwanUtils.checkBeforeCast(Calendar.class,
                    startDate.clone()) : (Calendar) startDate.clone();
        } else {
            selectedDate = Calendar.getInstance();
            selectedDate.setTime(new Date(savedInstanceState.getLong("selectedDate")));
            currentDate = Calendar.getInstance();
            currentDate.setTime(new Date(savedInstanceState.getLong("currentDate")));
        }

        adapter = new CalendarAdapter(this, currentDate, mInitialDate);
        adapter.setSelectedDate(selectedDate);
        adapter.setRtNotification(rtNotification);

        gridView = findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        final TextView title = findViewById(R.id.title); // Title Calendar
        title.setText(getString(R.string.calendar_title).toUpperCase());

        setListeners();

        refreshCalendar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedDate != null) {
            outState.putLong("selectedDate", selectedDate.getTimeInMillis());
        }
        if (currentDate != null) {
            outState.putLong("currentDate", currentDate.getTimeInMillis());
        }
    }

    public void refreshCalendar() {
        adapter.refreshDays(currentDate);
        adapter.notifyDataSetChanged();

        final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.SPINNER_MONTH_YEAR_FORMAT);
        ((TextView) findViewById(R.id.date_text)).setText(sdf.format(currentDate.getTime()));

        gridView.cancelLongPress();
    }

    private void returnDate() {
        final Intent intent = getIntent();
        intent.putExtra("year", selectedDate.get(Calendar.YEAR));
        intent.putExtra("month", selectedDate.get(Calendar.MONTH));
        intent.putExtra("day", selectedDate.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("date", selectedDate.getTimeInMillis());

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    private void selectDate(final View v, final int position) {
        final TextView date = v.findViewById(R.id.date); // text view date description
        if (date instanceof TextView && !TextUtils.isEmpty(date.getText())) {
            final String stringDay = date.getText().toString();
            final Calendar pickedDate = (Calendar) startDate.clone();

            pickedDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
            pickedDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
            pickedDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(stringDay));

            if (adapter.getMonthOffset(position) == 1) {
                pickedDate.add(Calendar.MONTH, 1);
                setNextMonth();
            } else if (adapter.getMonthOffset(position) == -1) {
                pickedDate.add(Calendar.MONTH, -1);
                setPreviousMonth();
            }

            if (rtNotification && sameDateOrAfter(mInitialDate, pickedDate)) {
                selectedDate = pickedDate;
                adapter.setSelectedDate(selectedDate);
            } else if (sameDateOrAfter(mInitialDate, pickedDate)
                        && pickedDate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                        && pickedDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    selectedDate = pickedDate;
                    adapter.setSelectedDate(selectedDate);
            }
            refreshCalendar();
        }
    }

    private void setListeners() {
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
                selectDate(v, position);
            }
        });

        findViewById(R.id.buttonPositive).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                returnDate();
            }
        });

        findViewById(R.id.buttonNegative).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                finish();
            }
        });

        final LinearLayout datePicker = (LinearLayout) findViewById(R.id.date_picker);
        datePicker.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                final DialogHolo dialog = new DialogHolo(CalendarView.this);
                dialog.setTitleEnabled(false);
                final View view = dialog.setCustomContentView(R.layout.calendar_month_picker);
                final ListView monthsList = (ListView) view.findViewById(R.id.list_view_elements);
                monthsList.setAdapter(new MonthsListAdapter(CalendarView.this));
                monthsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
                        currentDate = Calendar.getInstance();
                        currentDate.set(Calendar.DAY_OF_MONTH, 1);
                        currentDate.add(Calendar.MONTH, position);
                        Utils.dismissDialog(dialog);
                        refreshCalendar();
                    }
                });
                dialog.setCancelable(true);
                Utils.showDialog(dialog, CalendarView.this);
            }
        });

        swipeDetector = new SwipeDetector(new OnSwipeListener() {

            @Override
            public void onTopToBottomSwipe() {
            }

            @Override
            public void onRightToLeftSwipe() {
                gridView.setOnLongClickListener(null);
                setNextMonth();
            }

            @Override
            public void onLeftToRightSwipe() {
                gridView.setOnLongClickListener(null);
                setPreviousMonth();

            }

            @Override
            public void onBottomToTopSwipe() {
            }
        });

        gridView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // disable GridView scrolling
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return swipeDetector.onTouchEvent(event);

            }
        });
    }

    private void setNextMonth() {
        currentDate.set(Calendar.DAY_OF_MONTH, 1);
        currentDate.add(Calendar.MONTH, 1);
        refreshCalendar();
    }

    private void setPreviousMonth() {
        if (validatePreviousMonth()) {
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            currentDate.add(Calendar.MONTH, -1);
            refreshCalendar();
        }
    }

    /**
     * validatePreviousMonth
     * @return boolean
     */
    private boolean validatePreviousMonth(){
        return currentDate.get(Calendar.YEAR) > startDate.get(Calendar.YEAR)
                || (currentDate.get(Calendar.YEAR) == startDate.get(Calendar.YEAR)
                && currentDate.get(Calendar.MONTH) > startDate.get(Calendar.MONTH));
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
    
    private boolean sameDateOrAfter(Calendar firstDate, Calendar secondDate) {
    	return (firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR) &&
    			firstDate.get(Calendar.DAY_OF_YEAR) == secondDate.get(Calendar.DAY_OF_YEAR)) ||
    			secondDate.after(firstDate);
    }
}
