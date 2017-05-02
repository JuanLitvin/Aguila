package com.juanlitvin.fenix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.kairos.Kairos;
import com.kairos.KairosListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RESTClient.init(this);

        Button btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        findViewById(R.id.btnAllowNotifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
    }

    KairosListener kairosListener = new KairosListener() {

        @Override
        public void onSuccess(String response) {
            // your code here!
            Log.d("KAIROS DEMO", response);
        }

        @Override
        public void onFail(String response) {
            // your code here!
            Log.d("KAIROS DEMO", response);
        }
    };
}
