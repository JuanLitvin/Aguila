package com.juanlitvin.aguila;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ai.api.model.AIError;
import ai.api.model.AIResponse;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    private Gpio gpioAssistantButton;

	public static Bus serviceBus = new Bus(ThreadEnforcer.MAIN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        FirebaseMessaging.getInstance().subscribeToTopic("news-debug");

        RESTClient.init(this);
        Mirror.register(this);
        serviceBus.register(this);
        initAssistant();
    }

    private void initAssistant() {
        //getPermission();
        startGPIO();
        Assistant.init(this, assistantResult);
    }

    private void startGPIO() {
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> portList = manager.getGpioList();
        if (portList.isEmpty()) {
            Log.w("MainActivity", "No GPIO port available on this device.");
            return;
        }

        try {
            gpioAssistantButton = manager.openGpio("BCM21");
            gpioAssistantButton.setDirection(Gpio.DIRECTION_IN);
            gpioAssistantButton.setActiveType(Gpio.ACTIVE_HIGH);
            gpioAssistantButton.setEdgeTriggerType(Gpio.EDGE_BOTH);
            gpioAssistantButton.registerGpioCallback(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    try {
                        if (gpio.getValue()) {
                            //HIGHT
                            Toast.makeText(MainActivity.this, "TOCASTE EL BOTON", Toast.LENGTH_SHORT).show();
                            Assistant.cancel(); //cancel if it was listening and start over;
                            Assistant.listen();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return super.onGpioEdge(gpio);
                }

                @Override
                public void onGpioError(Gpio gpio, int error) {
                    super.onGpioError(gpio, error);
                    Log.e("Gpio callback error", error + "");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error GPIO", e.getMessage(), e);
        }
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "No seas rata", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},5001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGPIO();
                } else {
                    Toast.makeText(this, "Could not start assistant. No audio permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private Assistant.AssistantResult assistantResult = new Assistant.AssistantResult() {
        @Override
        public void onResult(AIResponse result) {
            Toast.makeText(MainActivity.this, result.getResult().getAction(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(AIError error) {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            Log.e("App", error.getMessage());
        }
    };

    public static void loadUserModules(List<Module> modules) {
        showProgress(false);

        //clear modules
        clearFragments();

        for (Module module : modules) {
            try {
                if (module.hasExtras()) {
                    Mirror.addModule((MainActivity) context, module.getModule(), module.getFragmentId(), module.getExtras());
                } else {
                    Mirror.addModule((MainActivity) context, module.getModule(), module.getFragmentId());
                }
            } catch (Exception e) {
                Mirror.addModule((MainActivity) context, new ModuleErrorFragment(), module.getFragmentId());
            }
        }
    }

    public void updateModulesConfig(JSONObject config) {
        Mirror.updateConfig(config);
    }

    public void updateModulesConfig() {
        Mirror.updateConfig();
    }

    private static void clearFragments() {
        for (MirrorModule fragment : Mirror.moduleFragments) {
            ((MainActivity) context).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
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

        if (remoteMessage.getData().get("action").toString().equals("userChangedConfig")) {
            //owner was registered, update modules' config
            try {
                Mirror.updateConfig(new JSONObject(remoteMessage.getData().get("extras")).getJSONObject("config"));

            } catch (Exception e) {
                e.printStackTrace();
                Mirror.updateConfig();
            }
        }

        if (remoteMessage.getData().get("action").toString().equals("voiceCommand")) {
            try {
                switch (new JSONObject(remoteMessage.getData().get("extras")).getString("code")) {
                    case "io.lights.on":
                        Toast.makeText(context, "Turning lights on", Toast.LENGTH_SHORT).show();
                        break;
                    case "io.lights.off":
                        Toast.makeText(context, "Turning lights off", Toast.LENGTH_SHORT).show();
                        break;
                    case "order.uber":
                        Toast.makeText(context, "Ordering Uber", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, "There was a problem with the command you just sent", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MainActivity", "Error trying to get voice command extra", e);
            }
        }
    }

    private void assignControls() {

    }

    private void assignControlValues() {

    }

    private void setListeners() {

    }

}
