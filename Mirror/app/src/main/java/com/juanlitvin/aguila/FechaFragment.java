package com.juanlitvin.aguila;


import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class FechaFragment extends MirrorModule {

    TextView lblFecha;

    public FechaFragment() {
        // Required empty public constructor
    }

    @Override
    public void init() {
        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblFecha = new TextView(getActivity());
        lblFecha.setTextSize(40f);
        lblFecha.setTextColor(Color.parseColor("#DDDDDD"));
        lblFecha.setGravity(Gravity.RIGHT);
        lblFecha.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return lblFecha;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                DateTime now = DateTime.now();
                lblFecha.setText(getDayOfTheWeekString(now.getDayOfWeek()) + getMonthString(now.getMonthOfYear()) + getDayOfMonthString(now.getDayOfMonth()));
                //execute again when next day happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).plusDays(1).getMillis() - DateTime.now().getMillis());
            }
        });
    }

    private String getDateString() {
        Calendar calendar = Calendar.getInstance();
        return getDayOfTheWeekString(calendar.get(Calendar.DAY_OF_WEEK)) + getMonthString(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.DAY_OF_MONTH);
    }

    private String getDayOfTheWeekString(int day) {
        switch (day) {
            case 0:
                return "Sunday, ";
            case 1:
                return "Monday, ";
            case 2:
                return "Tuesday, ";
            case 3:
                return "Wednesday, ";
            case 4:
                return "Thursday, ";
            case 5:
                return "Friday, ";
            case 6:
                return "Saturday, ";
            default:
                return "";
        }
    }

    private String getMonthString(int month) {
        switch (month) {
            case 1:
                return "January ";
            case 2:
                return "February ";
            case 3:
                return "March ";
            case 4:
                return "April ";
            case 5:
                return "May ";
            case 6:
                return "June ";
            case 7:
                return "July ";
            case 8:
                return "August ";
            case 9:
                return "September ";
            case 10:
                return "October ";
            case 11:
                return "November ";
            case 12:
                return "December ";
            default:
                return "";
        }
    }

    private String getDayOfMonthString(int day) {
        switch (day) {
            case 1:
            case 21:
            case 31:
                return Integer.toString(day) + "st";
            case 2:
            case 22:
                return Integer.toString(day) + "nd";
            case 3:
            case 23:
                return Integer.toString(day) + "rd";
            default:
                return Integer.toString(day) + "th";
        }
    }

}
