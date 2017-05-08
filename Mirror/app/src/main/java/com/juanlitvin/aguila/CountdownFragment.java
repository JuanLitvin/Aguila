package com.juanlitvin.aguila;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.TimeZone;

public class CountdownFragment extends MirrorModule {

    TextView lblResultado;
    long millis;
    TimeZone timeZone;
    String until;

    @Override
    public void init() {
        Bundle extras = getArguments();
        millis = Long.parseLong(extras.getString("millis"));
        until = extras.getString("until");
        timeZone = TimeZone.getTimeZone(extras.getString("timeZone"));

        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblResultado = new TextView(getActivity());
        lblResultado.setTextSize(30f);
        lblResultado.setTextColor(Color.parseColor("#DDDDDD"));
        lblResultado.setGravity(Gravity.RIGHT);
        lblResultado.setIncludeFontPadding(false);
        lblResultado.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return lblResultado;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                TimeZone.setDefault(timeZone);
                DateTime start = new DateTime(DateTimeZone.forTimeZone(timeZone));
                DateTime end = new DateTime(millis, DateTimeZone.forTimeZone(timeZone));
                lblResultado.setText(Days.daysBetween(start, end).getDays() + " days until " + until);
                //execute again when next day happens
                handler.postDelayed(this, DateTime.now(DateTimeZone.forTimeZone(timeZone)).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).plusDays(1).getMillis() - DateTime.now().getMillis());
            }
        });
    }

}
