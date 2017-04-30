package com.juanlitvin.aguila;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    Context context;
    TextView widgetHora, widgetFecha, widgetClimaTemp, widgetClimaSummary;
    LinearLayout widgetClima;
    ListView widgetNoticias;
	
	public static Bus serviceBus = new Bus(ThreadEnforcer.MAIN);
	
	private int notificationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        FirebaseMessaging.getInstance().subscribeToTopic("news-debug");

        RESTClient.init(this);

        assignControls();
        assignControlValues();
        setListeners();
    }

    public static Bus getBus() {
        return serviceBus;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            serviceBus.register(this);
        } catch (Exception e) {}
    }

    @Override
    public void onDestroy() {
        serviceBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void processNotification(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().containsKey("add")) {
            Alerter.create(this)
                    .setTitle(remoteMessage.getData().get("title"))
                    .setText(remoteMessage.getData().get("body"))
                    .setDuration(10000)
                    .setBackgroundColor(android.R.color.holo_orange_dark)
                    .setOnHideListener(new OnHideAlertListener() {
                        @Override
                        public void onHide() {
                            addNotificationCount(1);
                        }
                    })
                    .show();
        } else if (remoteMessage.getData().containsKey("remove")) {
            addNotificationCount(-1);
        }
    }

    private void addNotificationCount(int i) {
        notificationCount += i;
        ((TextView) findViewById(R.id.widgetNotificationsCount)).setText(Integer.toString(notificationCount));
    }

    private void assignControls() {
        widgetHora  = (TextView) findViewById(R.id.widgetHora);
        widgetFecha = (TextView) findViewById(R.id.widgetFecha);
        widgetClima = (LinearLayout) findViewById(R.id.widgetClima);
        widgetClimaTemp = (TextView) findViewById(R.id.widgetClimaTemp);
        widgetClimaSummary = (TextView) findViewById(R.id.widgetClimaSummary);
        widgetNoticias = (ListView) findViewById(R.id.widgetNoticias);
    }

    private void assignControlValues() {
        //----------lblHora----------
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT-03:00"));
                if (Build.VERSION.SDK_INT >= 24) widgetHora.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));
                else widgetHora.setText(String.format("%02d" , c.get(Calendar.HOUR_OF_DAY), Locale.US) + ":" + String.format("%02d" , c.get(Calendar.MINUTE), Locale.US));
                //execute again when next minute happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(1).getMillis() - DateTime.now().getMillis());
            }
        });

        //----------lblHora----------

        widgetFecha.setText(getDateString());

        getTemp();

        getNews();
    }

    private void setListeners() {

    }

    private void getTemp() {
        RESTClient.get("https://api.darksky.net/forecast/faa5b82ac733134b7bceee9ccfbfb0bb/-34.6076751,-58.4344687", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody, "UTF-8"));
                    JSONObject responseCurrently = response.getJSONObject("currently");
                    String responseCurrentlyTemp = Integer.toString(convertFahrenheitToCelcius(responseCurrently.getDouble("temperature")));
                    String responseCurrentlySummary = responseCurrently.getString("summary");
                    widgetClimaTemp.setText(responseCurrentlyTemp + "°");
                    widgetClimaSummary.setText(responseCurrentlySummary);
                } catch (Exception e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getNews() {
        RESTClient.get("http://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=0d920a4142d947e2bc88652b6fdd584c", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody, "UTF-8"));
                    JSONArray responseArticles = response.getJSONArray("articles");

                    List<HashMap<String,String>> news = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < responseArticles.length(); i++) {
                        JSONObject responseArticlesCurrent = responseArticles.getJSONObject(i);
                        String responseArticlesCurrentTitle = responseArticlesCurrent.getString("title");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("text", responseArticlesCurrentTitle);
                        news.add(map);
                    }

                    widgetNoticias.setAdapter(new SimpleAdapter(getApplicationContext(),  news, R.layout.news_list, new String[] {"text"}, new int[] {android.R.id.text1}));
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

    private String getDateString() {
        Calendar calendar = Calendar.getInstance();
        return getDayOfTheWeekString(calendar.get(Calendar.DAY_OF_WEEK)) + getMonthString(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.DAY_OF_MONTH);
    }

    private String getDayOfTheWeekString(int day) {
        switch (day) {
            case 1:
                return "Sunday, ";
            case 2:
                return "Monday, ";
            case 3:
                return "Tuesday, ";
            case 4:
                return "Wednesday, ";
            case 5:
                return "Thursday, ";
            case 6:
                return "Friday, ";
            case 7:
                return "Saturday, ";
            default:
                return "";
        }
    }

    private String getMonthString(int month) {
        switch (month + 1) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }
}
