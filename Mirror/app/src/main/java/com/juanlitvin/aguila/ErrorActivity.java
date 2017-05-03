package com.juanlitvin.aguila;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        ((TextView)findViewById(R.id.lblError)).setText(getIntent().hasExtra("error") ? getIntent().getStringExtra("error") : "Hubo un error inesperado. Reinicie el dispositivo.");

    }
}
