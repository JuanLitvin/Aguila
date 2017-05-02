package com.juanlitvin.aguila;

import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class ClimaFragment extends MirrorModule {

    TextView lblTemp, lblSummary;

    @Override
    public void init() {
        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.RIGHT);

        lblTemp = new TextView(getActivity());
        lblTemp.setTextSize(50f);
        lblTemp.setTextColor(Color.WHITE);
        lblTemp.setTypeface(Typeface.SERIF, Typeface.BOLD);
        lblTemp.setIncludeFontPadding(false);

        lblSummary = new TextView(getActivity());
        lblSummary.setTextSize(50f);
        lblSummary.setTextColor(Color.WHITE);
        lblSummary.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 0, 0);
        lblSummary.setLayoutParams(params);

        layout.addView(lblTemp);
        layout.addView(lblSummary);

        return layout;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                updateTemp();
                //execute again when next 30 minutes happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(30).getMillis() - DateTime.now().getMillis());
            }
        });
    }

    private void updateTemp() {
        RESTClient.get("https://api.darksky.net/forecast/faa5b82ac733134b7bceee9ccfbfb0bb/-34.6076751,-58.4344687", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody, "UTF-8"));
                    JSONObject responseCurrently = response.getJSONObject("currently");
                    String responseCurrentlyTemp = Integer.toString(convertFahrenheitToCelcius(responseCurrently.getDouble("temperature")));
                    String responseCurrentlySummary = responseCurrently.getString("summary");

                    //update views
                    lblTemp.setText(responseCurrentlyTemp + "°");
                    lblSummary.setText(responseCurrentlySummary);
                } catch (Exception e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private int convertFahrenheitToCelcius(Double fahrenheit) {
        return (int) ((fahrenheit - 32) * 5 / 9);
    }

    private float convertCelciusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }
}
