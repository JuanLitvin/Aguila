package com.juanlitvin.fenix;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.zzi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity extends Activity {

    private static final int REQUEST_READ_CONTACTS = 0;

    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private EditText txtName;
    private Button btnSignIn, btnSignUp, btnSwitchSignIn, btnSwitchSignUp;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        RESTClient.init(this);

        initAuth();

        assignViews();
        setListeners();
    }

    private void initAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    User.loadFromFirebaseUser(user.getUid(), new User.LoginCallback() {
                        @Override
                        public void onComplete(int statusCode, JSONObject response) {
                            startActivity(new Intent(LoginActivity.context, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(int statusCode, Throwable error) {
                            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
    }

    private void assignViews() {
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        txtName = (EditText) findViewById(R.id.txtName);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSwitchSignIn = (Button) findViewById(R.id.btnSwitchSignIn);
        btnSwitchSignUp = (Button) findViewById(R.id.btnSwitchSignUp);
    }

    private void setListeners() {
        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        btnSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        btnSwitchSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignIn();
            }
        });
        btnSwitchSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignUp();
            }
        });
    }

    private void switchToSignUp() {
        clearFields();
        findViewById(R.id.signup).setVisibility(View.VISIBLE);
        findViewById(R.id.signin).setVisibility(View.GONE);
    }

    private void switchToSignIn() {
        clearFields();
        findViewById(R.id.signup).setVisibility(View.GONE);
        findViewById(R.id.signin).setVisibility(View.VISIBLE);
    }

    private void clearFields() {
        txtEmail.setText("");
        txtName.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    private void attemptLogin() {
        String email = txtEmail.getText().toString();
        String pass = txtPassword.getText().toString();

        //check if credentials are valid
        if (!areCredentialsValid(email, pass)) {
            //TODO: SHOW ERROR MESSAGE
            return;
        }

        //sing in
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "No se pudo iniciar sesion: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void attemptRegister() {
        final String name = txtName.getText().toString();
        final String email = txtEmail.getText().toString();
        String pass = txtPassword.getText().toString();
        String confirmPass = txtConfirmPassword.getText().toString();

        //check if credentials are valid
        if (!areCredentialsValid(email, pass, confirmPass, name)) {
            //TODO: SHOW ERROR MESSAGE
            return;
        }

        //save email and pass in user
        User.setEmail(email);
        User.setUserName(name);

        //register
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "No se pudo crear su cuenta: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean areCredentialsValid(String email, String password) {
        return isEmailValid(email) && isPasswordValid(password);
    }

    private boolean areCredentialsValid(String email, String password, String confirmPassword, String name) {
        return isEmailValid(email) && isPasswordValid(password);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}

