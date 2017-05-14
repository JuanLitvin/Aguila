package com.juanlitvin.fenix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    public static List<String> availableModuleNames = new ArrayList<>();
    public static List<Integer> availableModuleIds = new ArrayList<>();
    private static Map<String, Integer> newModules = new ArrayMap<>();


    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btnConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        RESTClient.init(this);

        assignViews();
        setListeners();

        loadModules();
    }

    private void assignViews() {
        btn1 = (Button) findViewById(R.id.fragment1);
        btn2 = (Button) findViewById(R.id.fragment2);
        btn3 = (Button) findViewById(R.id.fragment3);
        btn4 = (Button) findViewById(R.id.fragment4);
        btn5 = (Button) findViewById(R.id.fragment5);
        btn6 = (Button) findViewById(R.id.fragment6);
        btn7 = (Button) findViewById(R.id.fragment7);
        btnConfig = (Button) findViewById(R.id.btnConfig);
    }

    private void setListeners() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadConfig();
            }
        });
    }

    private void uploadConfig() {
        try {
            JSONObject config = User.getConfig();
            final JSONObject setting = config.getJSONObject("settings");

            Iterator<String> iterator = setting.keys();
            while(iterator.hasNext()) {
                final String key = (String)iterator.next();
                JSONObject moduleSettings = setting.getJSONObject(key);
                Iterator<String> moduleIterator = moduleSettings.keys();
                while (moduleIterator.hasNext()) {
                    final String moduleKey = (String) moduleIterator.next();
                    String value = moduleSettings.getString(moduleKey);

                    View view = getLayoutInflater().inflate(R.layout.alert_input_simple, null);
                    ((TextView)view.findViewById(R.id.text1)).setText(key + "\n" + moduleKey + ":");
                    final EditText txt = ((EditText)view.findViewById(R.id.text2));
                    txt.setHint("Value");
                    txt.setText(value);

                    new AlertDialog.Builder(this)
                            .setTitle("Settings")
                            .setView(view)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        if (!txt.getText().toString().isEmpty()) {
                                            setting.getJSONObject(key).put(moduleKey, txt.getText().toString());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
            }

            //finished editing settings
            //save settings
            config.put("settings", setting);

            //save to User
            User.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        final String IdFragment = (String) view.getTag();

        new AlertDialog.Builder(this)
                .setTitle("Select new module")
                .setItems(availableModuleNames.toArray(new String[availableModuleIds.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Button btnFragment = (Button) findViewById(getResources().getIdentifier(IdFragment.replace("Id", "fragment"), "id", getPackageName()));
                        btnFragment.setText(availableModuleNames.get(i));
                        try {
                            newModules.put(IdFragment, availableModuleIds.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }


    private void loadModules() {
        clearFragments();
        try {
            JSONObject config = User.getConfig();
            JSONArray modules = config.getJSONArray("modules");
            for (int i = 1; i <= 7 ; i++) { //loop 1-7
                Button txtFragment = (Button) findViewById(getResources().getIdentifier("fragment" + i, "id", getPackageName()));
                String text = "";
                for (int j = 0; j < modules.length(); j++) {
                    JSONObject mod = modules.getJSONObject(j);
                    if (mod.getString("fragment-id").equals("fragment" + i)) {
                        text = mod.getString("name");
                        break;
                    } else {
                        text = "Empty";
                    }
                }
                txtFragment.setText(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFragments() {

    }

    public static void saveAvailableModules(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject mod = array.getJSONObject(i);
                availableModuleIds.add(mod.getInt("id"));
                availableModuleNames.add(mod.getString("name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        availableModuleNames.add("Empty");
        availableModuleIds.add(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setMessage("Saving...");
                dialog.show();

                try {
                     JSONObject settings = User.getConfig().getJSONObject("settings");

                    User.sendConfigChange(settings.toString(), mapToJsonObjectString(newModules), new RESTClient.ResponseHandler() {
                        @Override
                        public void onSuccess(int code, String responseBody) {
                            dialog.dismiss();
                            finish();
                        }

                        @Override
                        public void onFailure(int code, String responseBody, Throwable error) {
                            dialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String mapToJsonObjectString(Map<String, Integer> map) throws JSONException {
        JSONObject response = new JSONObject();
        for (String key:map.keySet()) {
            if (!response.has(key))
            response.put(key, map.get(key));
        }
        return response.toString();
    }
}
