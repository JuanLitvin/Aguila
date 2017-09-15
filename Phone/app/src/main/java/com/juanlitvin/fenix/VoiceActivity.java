package com.juanlitvin.fenix;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import ai.api.model.AIError;
import ai.api.model.AIResponse;

public class VoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        if (!getIntent().hasExtra("id-device")) {
            //doesnt have device id. close
            setResult(RESULT_CANCELED);
            finish();
        }

        getPermission();
        Assistant.init(this, assistantResult);

        setResult(RESULT_CANCELED);

        //set button
        ImageButton btnStartListening = (ImageButton) findViewById(R.id.btnStartListening);
        btnStartListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });
    }

    private void startListening() {
        if (checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") == PackageManager.PERMISSION_GRANTED) {
            Assistant.cancel(); //cancel if it was listening and start over;
            Assistant.listen();
        }
        getPermission();
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "We require microphone perimssions to enable the voice assistant. Please, enable them and try again.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO},5001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Could not start assistant. No audio permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private Assistant.AssistantResult assistantResult = new Assistant.AssistantResult() {
        @Override
        public void onResult(AIResponse result) {
            Toast.makeText(VoiceActivity.this, result.getResult().getAction(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, new Intent().putExtra("id-device", getIntent().getStringExtra("id-device")).putExtra("code", result.getResult().getAction()));
            finish();
        }

        @Override
        public void onError(AIError error) {
            Toast.makeText(VoiceActivity.this, "Error", Toast.LENGTH_SHORT).show();
            Log.e("App", error.getMessage());
        }
    };


}
