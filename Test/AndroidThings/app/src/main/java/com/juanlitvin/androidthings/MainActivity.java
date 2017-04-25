package com.juanlitvin.androidthings;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    //Gpio myGpio;
    //boolean isTimeShown = false;
    TextView lblHora, lblFecha, txtWidgetClimaTemp, txtWidgetClimaSummary;
    LinearLayout widgetClima;
    ListView widgetNoticias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RESTClient.init(this);

        assignControls();
        assignControlValues();
        setListeners();

        /*PeripheralManagerService pms = new PeripheralManagerService();
        try {
            myGpio = pms.openGpio("5");
            myGpio.setDirection(Gpio.DIRECTION_IN);
            myGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            myGpio.registerGpioCallback(GPIO_Callback);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
        Log.d(TAG, "Available GPIO: " + pms.getGpioList());*/
    }

    private void assignControls() {
        lblHora  = (TextView) findViewById(R.id.widgetHora);
        lblFecha = (TextView) findViewById(R.id.widgetFecha);
        widgetClima = (LinearLayout) findViewById(R.id.widgetClima);
        txtWidgetClimaTemp = (TextView) findViewById(R.id.widgetClimaTemp);
        txtWidgetClimaSummary = (TextView) findViewById(R.id.widgetClimaSummary);
        widgetNoticias = (ListView) findViewById(R.id.widgetNoticias);
    }

    private void assignControlValues() {
        //----------lblHora----------
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                lblHora.setText(new SimpleDateFormat("hh:mm").format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 1000);
        //----------lblHora----------

        lblFecha.setText(getDateString());

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
                    txtWidgetClimaTemp.setText(responseCurrentlyTemp + "°");
                    txtWidgetClimaSummary.setText(responseCurrentlySummary);
                } catch (Exception e) {}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getNews() {
        RESTClient.get("http://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=0d920a4142d947e2bc88652b6fdd584c", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody, "UTF-8"));
                    JSONArray responseArticles = response.getJSONArray("articles");

                    List<String> news = new ArrayList<String>();
                    for (int i = 0; i < responseArticles.length(); i++) {
                        JSONObject responseArticlesCurrent = responseArticles.getJSONObject(i);
                        String responseArticlesCurrentTitle = responseArticlesCurrent.getString("title");
                        news.add(responseArticlesCurrentTitle);
                    }

                    widgetNoticias.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.news_list, news));
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("HELLO", "HI");
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

    private void fetchWeather() {
        //https://api.darksky.net/forecast/8f76284c737039a256a88804e8edd56d/37.8267,-122.4233
        //https://github.com/zh-wang/YWeatherGetter4a
    }

    /*private GpioCallback GPIO_Callback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            showTime(!isTimeShown);
            return true;
        }
    };

    @Override
    public void onDestroy() {
        if (myGpio != null) {
            myGpio.unregisterGpioCallback(GPIO_Callback);
            try {
                myGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }

        super.onDestroy();
    }

    private void showTime(boolean show) {
        if (show) {
            isTimeShown = true;
            lblHora.setVisibility(View.VISIBLE);
        } else {
            isTimeShown = false;
            lblHora.setVisibility(View.GONE);
        }
    }*/

}
