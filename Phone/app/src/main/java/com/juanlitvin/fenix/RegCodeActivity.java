package com.juanlitvin.fenix;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegCodeActivity extends AppCompatActivity {

    private boolean addMode = false;
    private Button btnAdd;
    private EditText txtRegCode, txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_code);

        if (getIntent().hasExtra("addMode")) {
            //is in addMode
            addMode = true;
        }

        assignViews();
        setListeners();

        if (addMode) {
            showNameField();
            changeEditTextText("Add Device");
            changeTitle("Add Device");
        }

    }

    private void changeTitle(String text) {
        ((TextView)findViewById(R.id.lblTitle)).setText(text);
    }

    private void assignViews() {
        txtRegCode = (EditText) findViewById(R.id.txtRegCode);
        txtName = (EditText) findViewById(R.id.txtMirrorName);
        btnAdd = (Button) findViewById(R.id.btnAdd);
    }

    private void setListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regCode = txtRegCode.getText().toString().trim();
                String name = txtName.getText().toString().trim();

                if (!areValuesValid(regCode, name)) return;

                //show before start loading and after value validity has been checked
                ProgressDialog progress = new ProgressDialog(RegCodeActivity.this);
                progress.setMessage("Loading...");
                progress.setCancelable(false);
                progress.show();

                if (addMode) {
                    registerDevice(regCode, name, progress);
                } else {
                    signInDevice(regCode, progress);
                }
            }
        });
    }

    private void registerDevice(String regCode, String name, final ProgressDialog progress) {
        User.registerMirror(regCode, name, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                progress.dismiss();
                finish();
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                progress.dismiss();
                Toast.makeText(RegCodeActivity.this, "No se puddo registrar el dispositivo.\nInténtelo nuevamnete.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInDevice(String regCode, final ProgressDialog progress) {
        User.loginMirror(regCode, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                progress.dismiss();
                finish();
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                progress.dismiss();
                Toast.makeText(RegCodeActivity.this, "No se pudo iniciar sesión en el dispositivo.\nInténtelo nuevamnete.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean areValuesValid(String regCode, String name) {
        boolean result = true;

        if (addMode) {
            if (name.isEmpty()) {
                setEditTextError(R.id.txtMirrorNameTIL, "You need to enter a name for the new mirror");
                result = false;
                txtName.requestFocus();
            } else {
                setEditTextError(R.id.txtMirrorNameTIL, null);
            }
        }

        if (regCode.isEmpty()) {
            setEditTextError(R.id.txtRegCodeTIL, "You need to enter a registration code");
            result = false;
            txtRegCode.requestFocus();
        } else if (!Pattern.matches("[a-zA-Z\\u00f1\\u00d1][a-zA-Z\\u00f1\\u00d1][a-zA-Z\\u00f1\\u00d1][a-zA-Z\\u00f1\\u00d1] [0-9][0-9][0-9][0-9]", regCode)) { //\u00f1\u00d1 = ñÑ
            if (Pattern.matches("[a-zA-Z\\u00f1\\u00d1][a-zA-Z\\u00f1\\u00d1][a-zA-Z\\u00f1\\u00d1][a-zA-Z\\u00f1\\u00d1][0-9][0-9][0-9][0-9]", regCode)) {
                //has no space, but matches
                setEditTextError(R.id.txtRegCodeTIL, "Your registration code is missing a space between letters and numbers");
                result = false;
                txtRegCode.requestFocus();
            } else {
                //no match at all
                setEditTextError(R.id.txtRegCodeTIL, "The format of your registration code is invalid");
                result = false;
                txtRegCode.requestFocus();
            }
        } else {
                setEditTextError(R.id.txtRegCodeTIL, null);
        }

        return result;
    }

    private void setEditTextError(int viewId, String error) {
        try {
            ((TextInputLayout)findViewById(viewId)).setError(error);
        } catch (Exception e) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showNameField() {
        findViewById(R.id.txtMirrorNameTIL).setVisibility(View.VISIBLE);
    }

    private void changeEditTextText(String text) {
        btnAdd.setText(text);
    }

}
