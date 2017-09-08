package com.juanlitvin.aguila;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Random;

public class GreetingFragment extends MirrorModule {

    TextView lblGreeting;
    String[] greetings = {
            "Welcome",
            "Have a nice day",
            "Hello Sunshine!",
            "Hi, Sexy!",
            "Hi there",
            "Keep it up",
            "Good day!"
    };

    @Override
    public void init() {
        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lblGreeting = new TextView(getActivity());
        lblGreeting.setTextSize(30f);
        lblGreeting.setTextColor(Color.BLACK);
        lblGreeting.setBackgroundColor(Color.WHITE);
        lblGreeting.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        lblGreeting.setIncludeFontPadding(false);
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
        return greetings[new Random().nextInt(greetings.length)];
    }
}
