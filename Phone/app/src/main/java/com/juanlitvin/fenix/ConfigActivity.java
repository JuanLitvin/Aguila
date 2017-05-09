package com.juanlitvin.fenix;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    public static List<String> availableModuleNames;
    public static List<Integer> availableModuleIds;


    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;

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
        btn1 = (Button) findViewById(R.id.fragment1);
        btn2 = (Button) findViewById(R.id.fragment2);
        btn3 = (Button) findViewById(R.id.fragment3);
        btn4 = (Button) findViewById(R.id.fragment4);
        btn5 = (Button) findViewById(R.id.fragment5);
        btn6 = (Button) findViewById(R.id.fragment6);
        btn7 = (Button) findViewById(R.id.fragment7);
    }

    private void setListeners() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String IdFragment = (String) view.getTag();

        new AlertDialog.Builder(this)
                .setTitle("Change Module")
                .setItems(availableModuleNames.toArray(new String[availableModuleIds.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            JSONObject settings = new JSONObject();
                            JSONArray userModules = User.getConfig().getJSONArray("modules");
                            for (int j = 0; j < userModules.length(); j++) {
                                JSONObject mod = userModules.getJSONObject(j);
                                settings.put(mod.getString("package"), mod.getJSONObject("extras"));
                            }

                            JSONObject fragments = new JSONObject();
                            fragments.put(IdFragment, availableModuleIds.get(i));

                            User.sendConfigChange(settings.toString(), fragments.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                        text = "";
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
    }
}
