package com.juanlitvin.aguila;


import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HoraFragment extends MirrorModule {

    private TimeZone timeZone;
    TextView lblHora;

    @Override
    public void init() {
        Bundle extras = getArguments();
        timeZone = TimeZone.getTimeZone(extras.getString("timeZone"));

        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblHora = new TextView(getActivity());
        lblHora.setTextSize(60f);
        lblHora.setTextColor(Color.WHITE);
        lblHora.setTypeface(null, Typeface.BOLD);
        lblHora.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return lblHora;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                TimeZone.setDefault(timeZone);
                Calendar c = Calendar.getInstance(timeZone);
                if (Build.VERSION.SDK_INT >= 24) lblHora.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));
                //execute again when next minute happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(1).getMillis() - DateTime.now().getMillis());
            }
        });
    }

}
