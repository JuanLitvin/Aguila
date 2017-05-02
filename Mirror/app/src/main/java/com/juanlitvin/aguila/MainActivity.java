package com.juanlitvin.aguila;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
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
    TextView widgetFecha, widgetClimaTemp, widgetClimaSummary;
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

        //assignControls();
        //assignControlValues();
        //setListeners();

        addModules();
    }

    private void addModules() {
        MirrorModule hora = new HoraFragment();
        Bundle bundle = new Bundle();
        bundle.putString("timeZone", "GMT-03:00");
        hora.setArguments(bundle);
        hora.init();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment2, hora);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        MirrorModule fecha = new FechaFragment();
        fecha.init();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment3, fecha);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        MirrorModule clima = new ClimaFragment();
        clima.init();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment5, clima);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        MirrorModule noticias = new NoticiasFragment();
        noticias.init();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment6, noticias);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
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
                    /*.setOnHideListener(new OnHideAlertListener() {
                        @Override
                        public void onHide() {
                            addNotificationCount(1);
                        }
                    })*/           //do not add notifications, could be conflic regarding how apps display their notifications.
                    .show();
        } else if (remoteMessage.getData().containsKey("remove")) {
            //addNotificationCount(-1); //do not remove either.
        }
    }

    private void addNotificationCount(int i) {
        notificationCount += i;
        ((TextView) findViewById(R.id.widgetNotificationsCount)).setText(Integer.toString(notificationCount));
    }

    private void assignControls() {

    }

    private void assignControlValues() {

    }

    private void setListeners() {

    }

}
