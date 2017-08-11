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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_VOICE = 1000;
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
                startActivity(new Intent(MainActivity.this, RegCodeActivity.class).putExtra("addMode", 1));
                fabMenu.close(true);
            }
        });
        // <------- fab_addDevice ------->

        fabSignInDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegCodeActivity.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_VOICE) {
            if (resultCode == RESULT_OK) {
                String idDevice = data.getStringExtra("id-device");
                String code = data.getStringExtra("code");
                User.sendVoiceCode(idDevice, code);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_signOut:
                FirebaseAuth.getInstance().signOut();
                User.clear();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra("loggedOut", 1));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
