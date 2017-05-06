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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
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
    public static Context context;
	
	public static Bus serviceBus = new Bus(ThreadEnforcer.MAIN);
	
	private int notificationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        FirebaseMessaging.getInstance().subscribeToTopic("news-debug");

        RESTClient.init(this);

        Mirror.register(this);
    }

    public static void downloadModules() {
        User.getModules(context, new User.Callback() {
            @Override
            public void onCallback(Object result) {
                showProgress(false);
                for (Module module : (List<Module>) result) {
                    if (module.hasExtras()) {
                        Mirror.addModule((MainActivity)context, module.getModule(), module.getFragmentId(), module.getExtras());
                    } else {
                        Mirror.addModule((MainActivity)context, module.getModule(), module.getFragmentId());
                    }
                }
            }
        });
    }

    public static void downloadModules(List<Module> modules) {
        showProgress(false);
        for (Module module : modules) {
            if (module.hasExtras()) {
                Mirror.addModule((MainActivity)context, module.getModule(), module.getFragmentId(), module.getExtras());
            } else {
                Mirror.addModule((MainActivity)context, module.getModule(), module.getFragmentId());
            }
        }
    }

    private static void showProgress(boolean show) {
        ((MainActivity)context).findViewById(R.id.progressLoading).setVisibility(show ? View.VISIBLE : View.GONE);
        ((MainActivity)context).findViewById(R.id.module_container).setVisibility(show ? View.GONE : View.VISIBLE);
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
                    .show();
        }
    }

    private void assignControls() {

    }

    private void assignControlValues() {

    }

    private void setListeners() {

    }

}
