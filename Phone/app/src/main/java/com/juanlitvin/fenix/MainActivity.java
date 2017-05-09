package com.juanlitvin.fenix;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    private ListView listDevices;
    private Button btnConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        assignViews();
        setListeners();

        loadDevices();
    }

    private void assignViews() {
        listDevices = (ListView) findViewById(R.id.listDevices);
        btnConfig = (Button) findViewById(R.id.btnConfig);
    }

    private void setListeners() {
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
            }
        });
    }

    private void loadDevices() {
        try {
            List<Map<String, String>> devices = new ArrayList<>();
            JSONArray dev = User.getDevices();
            for (int i = 0; i < dev.length(); i++) {
                JSONObject obj = dev.getJSONObject(i);
                Map<String, String> map = new ArrayMap<>();
                map.put("text1", obj.getString("name"));
                map.put("text2", obj.getString("owner"));
                devices.add(map);
            }
            listDevices.setAdapter(new SimpleAdapter(this, devices, R.layout.devices_list, new String[] {"text1", "text2"}, new int[] {R.id.text1, R.id.text2}));
        } catch (Exception e) {}
    }

}
