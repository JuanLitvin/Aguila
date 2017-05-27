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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
                editSettings();
            }
        });
    }

    private void editSettings() {
        startActivity(new Intent(ConfigActivity.this, SettingsConfigActivity.class).putExtra("config",User.getConfig().toString()).putExtra("available-modules", User.getAvailableModules().toString()));
        overridePendingTransition(R.anim.fab_slide_in_from_right, R.anim.fab_slide_out_to_left);
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
            JSONObject modules = config.getJSONObject("modules");
            JSONArray availableModules = User.getAvailableModules();

            for (int i = 1; i <= 7 ; i++) { //loop 1-7
                String fragmentId = "fragment" + i;
                Button txtFragment = (Button) findViewById(getResources().getIdentifier(fragmentId, "id", getPackageName()));
                String text = "";

                int idModule = modules.has(fragmentId) ? modules.getInt(fragmentId) : -1;
                for (int j = 0; j < availableModules.length(); j++) {
                    JSONObject module = availableModules.getJSONObject(j);

                    if (idModule == module.getInt("id")) {
                        //this module belongs to current fragmentId
                        text = module.getString("name");
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
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}
