package com.juanlitvin.aguila;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import com.tapadoo.alerter.Alerter;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    private Gpio gpioLED;
	public static Bus serviceBus = new Bus(ThreadEnforcer.MAIN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        FirebaseMessaging.getInstance().subscribeToTopic("news-debug");

        registerGPIO();
        RESTClient.init(this);
        Mirror.register(this);
        serviceBus.register(this);
    }

    private void registerGPIO() {
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            gpioLED = manager.openGpio("BCM26");
            gpioLED.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.w("MainActivity", "Unable to access GPIO", e);
        }
    }

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

        if (gpioLED != null) {
            try {
                gpioLED.close();
                gpioLED = null;
            } catch (IOException e) {
                Log.w("MainActivity", "Unable to close GPIO", e);
            }
        }

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
                JSONObject extras = new JSONObject(remoteMessage.getData().get("extras"));
                switch (extras.getString("code")) {
                    case "io.lights.on":
                        Toast.makeText(context, "Turning lights on", Toast.LENGTH_SHORT).show();
                        gpioLED.setValue(true);
                        break;
                    case "io.lights.off":
                        Toast.makeText(context, "Turning lights off", Toast.LENGTH_SHORT).show();
                        gpioLED.setValue(false);
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
