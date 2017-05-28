package com.juanlitvin.fenix;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    private ListView listDevices;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabAddDevice, fabSignInDevice, fabSettings;

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
        fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        fabAddDevice = (FloatingActionButton) findViewById(R.id.fab_addDevice);
        fabSignInDevice = (FloatingActionButton) findViewById(R.id.fab_signInDevice);
        fabSettings = (FloatingActionButton) findViewById(R.id.fab_settings);
    }

    private void setListeners() {
        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddDevice();
                fabMenu.close(true);
            }
        });
        // <------- fab_addDevice ------->

        fabSignInDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSignInDevice();
                fabMenu.close(true);
            }
        });
        // <------- fab_signInDevice ------->

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                fabMenu.close(false);
            }
        });
        // <------- fab_settings ------->
    }

    private void loadDevices() {
        try {
            List<Device> devices = User.getDevices();
            listDevices.setAdapter(new DeviceListAdapter(this, devices));
        } catch (Exception e) {}
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
                                Toast.makeText(MainActivity.this, "No se puddo registrar el dispositivo.\nInténtelo nuevamnete.", Toast.LENGTH_SHORT).show();
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
