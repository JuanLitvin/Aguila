package com.juanlitvin.aguila;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class LogUserInActivity extends AppCompatActivity {

    public static Bus serviceBus = new Bus(ThreadEnforcer.MAIN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_user_in);

        ((TextView)findViewById(R.id.lblCodigo)).setText(getIntent().getStringExtra("reg-code"));

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
    public void processUpdate(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("action").toString().equals("userLoggedIn")) {
            //user logged in, open mainactivity
            startActivity(new Intent(LogUserInActivity.this, MainActivity.class));
            finish();
        }
    }

}
