package com.juanlitvin.fenix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        RESTClient.init(this);

        assignViews();
        setListeners();

        loadModules();
    }

    private void assignViews() {

    }

    private void setListeners() {
        /*findViewById(R.id.btnAllowNotifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });*/
    }

    private void loadModules() {
        clearFragments();
        try {
            JSONObject config = User.getConfig();
            JSONArray modules = config.getJSONArray("modules");
            for (int i = 1; i <= 7 ; i++) { //loop 1-7
                Button txtFragment = (Button) findViewById(getResources().getIdentifier("fragment" + i, "id", getPackageName()));
                for (int j = 0; j < modules.length(); j++) {
                    JSONObject mod = modules.getJSONObject(j);
                    if (mod.getString("fragment-id").equals("fragment" + i)) {
                        txtFragment.setText(mod.getString("name"));
                        break;
                    } else {
                        txtFragment.setText("Empty");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFragments() {

    }

}
