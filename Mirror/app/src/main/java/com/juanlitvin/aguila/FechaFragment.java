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

import java.util.Calendar;
import java.util.TimeZone;


public class FechaFragment extends MirrorModule {

    TextView lblFecha;
    String format = "EEEE, MMM dd";
    TimeZone timeZone = TimeZone.getTimeZone("GMT");

    @Override
    public void init() {
        Bundle extras = getArguments();
        format = extras.getString("format");
        timeZone = TimeZone.getTimeZone(extras.getString("timeZone"));

        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblFecha = new TextView(getActivity());
        lblFecha.setTextSize(40f);
        lblFecha.setTextColor(Color.parseColor("#DDDDDD"));
        lblFecha.setGravity(Gravity.RIGHT);
        lblFecha.setIncludeFontPadding(false);
        lblFecha.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return lblFecha;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                TimeZone.setDefault(timeZone);
                Calendar c = Calendar.getInstance(timeZone);
                if (Build.VERSION.SDK_INT >= 24) lblFecha.setText(new SimpleDateFormat(format).format(c.getTime()));
                //execute again when next day happens
                handler.postDelayed(this, DateTime.now(DateTimeZone.forTimeZone(timeZone)).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).plusDays(1).getMillis() - DateTime.now().getMillis());
            }
        });
    }

}
