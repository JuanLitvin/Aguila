package com.juanlitvin.fenix;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_addDevice:
                showDialogAddDevice();
                return true;
            case R.id.menu_loginInDevice:
                showDialogSignInDevice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogAddDevice() {
        View view = getLayoutInflater().inflate(R.layout.alert_input_regcode, null);
        final EditText txtRegCode = (EditText) view.findViewById(R.id.txtRegCode);

        new AlertDialog.Builder(this)
                .setTitle("Add Device")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        //show before start loading
                        final ProgressDialog progress = new ProgressDialog(MainActivity.context);
                        progress.setMessage("Loading...");
                        progress.setCancelable(false);
                        progress.show();

                        User.registerMirror(txtRegCode.getText().toString(), new RESTClient.ResponseHandler() {
                            @Override
                            public void onSuccess(int code, String responseBody) {
                                progress.dismiss();
                            }

                            @Override
                            public void onFailure(int code, String responseBody, Throwable error) {
                                progress.dismiss();
                                Toast.makeText(MainActivity.this, "No se pudo registrar el dispositivo.\nInténtelo nuevamnete.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    private void showDialogSignInDevice() {
        View view = getLayoutInflater().inflate(R.layout.alert_input_regcode, null);
        final EditText txtRegCode = (EditText) view.findViewById(R.id.txtRegCode);

        new AlertDialog.Builder(this)
                .setTitle("Sign in - Device")
                .setView(view)
                .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        //show before start loading
                        final ProgressDialog progress = new ProgressDialog(MainActivity.context);
                        progress.setMessage("Loading...");
                        progress.setCancelable(false);
                        progress.show();

                        User.loginMirror(txtRegCode.getText().toString(), new RESTClient.ResponseHandler() {
                            @Override
                            public void onSuccess(int code, String responseBody) {
                                progress.dismiss();
                            }

                            @Override
                            public void onFailure(int code, String responseBody, Throwable error) {
                                progress.dismiss();
                                Toast.makeText(MainActivity.this, "No se pudo iniciar sesión en el dispositivo.\nInténtelo nuevamnete.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

}
