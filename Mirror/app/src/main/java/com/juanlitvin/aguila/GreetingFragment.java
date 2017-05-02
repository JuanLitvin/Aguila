package com.juanlitvin.aguila;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Random;

public class GreetingFragment extends MirrorModule {

    TextView lblGreeting;

    @Override
    public void init() {
        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblGreeting = new TextView(getActivity());
        lblGreeting.setTextSize(30f);
        lblGreeting.setTextColor(Color.parseColor("#DDDDDD"));
        lblGreeting.setTypeface(Typeface.SERIF, Typeface.BOLD);
        lblGreeting.setGravity(Gravity.CENTER);

        return lblGreeting;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                lblGreeting.setText(getRandomGreeting());
                //execute again when next 30 seconds happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).plusSeconds(30).getMillis() - DateTime.now().getMillis());
            }
        });
    }

    private String getRandomGreeting() {
        switch (new Random().nextInt(5) + 1) {
            case 1:
                return "Welcome";
            case 2:
                return "Hello Sunshine!";
            case 3:
                return "Hi, Sexy!";
            case 4:
                return "Power up and have a good day";
            case 5:
                return "Well, hi there";
            default:
                return "Hello to you";
        }
    }
}
