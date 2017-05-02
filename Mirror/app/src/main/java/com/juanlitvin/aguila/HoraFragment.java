package com.juanlitvin.aguila;


import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

    public HoraFragment() {

    }

    @Override
    public void init() {
        Bundle extras = getArguments();
        timeZone = TimeZone.getTimeZone(extras.getString("timeZone"));

        startLoop();
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT-03:00"));
                if (Build.VERSION.SDK_INT >= 24) lblHora.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));
                else lblHora.setText(String.format("%02d" , c.get(Calendar.HOUR_OF_DAY), Locale.US) + ":" + String.format("%02d" , c.get(Calendar.MINUTE), Locale.US));
                //execute again when next minute happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(1).getMillis() - DateTime.now().getMillis());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblHora = new TextView(getActivity());
        lblHora.setTextSize(30f);
        lblHora.setTextColor(Color.WHITE);
        lblHora.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return lblHora;
    }

}
