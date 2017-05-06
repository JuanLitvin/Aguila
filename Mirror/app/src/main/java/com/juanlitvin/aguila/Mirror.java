package com.juanlitvin.aguila;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Map;

public class Mirror {

    private static boolean isOwnerRegistered = false;

    public static String idDevice;
    public static String regCode;
    public static boolean enabled;

    private Mirror() {}

    public static void register(final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.pref_string), Context.MODE_PRIVATE);
        if (preferences.contains("device-id") && !preferences.getString("device-id", "").isEmpty()) {
            //device is registered, add a boot in server
            RequestParams params = new RequestParams();
            params.put("device-id", preferences.getString("device-id", ""));

            Map<String, String> headers = new ArrayMap<>();
            headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");

            RESTClient.post("https://juanlitvin.com/api/aguila/v1/index.php/mirror/login", params, headers, new RESTClient.ResponseHandler() {
                @Override
                public void onSuccess(int code, String responseBody) {
                    try {
                        JSONObject response = new JSONObject(responseBody);

                        setMirrorConfig(response);

                        if (response.has("register-required")) {
                            if (response.getBoolean("register-required")) {
                                registerNewMirror(context);
                                return;
                            }
                        }

                        isOwnerRegistered = response.getBoolean("isOwnerRegistered");
                        if (isOwnerRegistered) {
                            //registered, setup user's config
                            MainActivity.downloadModules();
                        } else {
                            //not registered, register device
                            if (preferences.contains("reg-code") && !preferences.getString("reg-code", "").isEmpty()) {
                                //has regcode. Show it
                                context.startActivity(new Intent(context, RegisterOwnerActivity.class).putExtra("reg-code", preferences.getString("reg-code", "")));
                                ((Activity)context).finish();
                            } else {
                                //no reg-code. call register
                                registerNewMirror(context);
                            }
                        }


                        if (!isOwnerRegistered) {
                            context.startActivity(new Intent(context, RegisterOwnerActivity.class).putExtra("reg-code", response.getString("mirror-reg-code")));
                            ((Activity)context).finish();
                        }
                    } catch (Exception e) {}
                }

                @Override
                public void onFailure(int code, String responseBody, Throwable error) {
                    context.startActivity(new Intent(context, ErrorActivity.class).putExtra("error", "Hubo un error al registrar su espejo.\nCompruebe su conexión y reinicie el dispositivo."));
                    ((Activity)context).finish();
                }
            });
        } else {
            //not registered. Register new mirror
            registerNewMirror(context);
        }
    }

    private static void setMirrorConfig(JSONObject response) {
        try {
            idDevice = response.getString("device-id");
            regCode = response.getString("mirror-reg-code");
            enabled = response.getBoolean("enabled");

            Mirror.setFCMToken(MainActivity.context, FirebaseInstanceId.getInstance().getToken());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void registerNewMirror(final Context context) {
        isOwnerRegistered = false;

        String macaddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        RequestParams params = new RequestParams();
        params.put("macaddr", macaddress);

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");

        RESTClient.post("https://juanlitvin.com/api/aguila/v1/index.php/mirror/register", params, headers, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                try {
                    JSONObject response = new JSONObject(responseBody);
                    String regCode = response.getString("mirror-reg-code");
                    String idDevice = response.getString("device-id");

                    SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.pref_string), Context.MODE_PRIVATE).edit();
                    editor.putString("reg-code", regCode);
                    editor.putString("device-id", idDevice);
                    editor.apply();

                    context.startActivity(new Intent(context, RegisterOwnerActivity.class).putExtra("reg-code", regCode));
                    ((Activity)context).finish();
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                context.startActivity(new Intent(context, ErrorActivity.class).putExtra("error", "Hubo un error al registrar su espejo.\nCompruebe su conexión y reinicie el dispositivo."));
                ((Activity)context).finish();
            }
        });
    }

    public static void addModule(MainActivity context, MirrorModule module, int fragmentId) {
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentId, module);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        module.init();
    }

    public static void addModule(MainActivity context, MirrorModule module, int fragmentId, Bundle extras) {
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentId, module);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        module.setArguments(extras);
        module.init();
    }

    public static void setFCMToken(Context context, String token) {
        RequestParams params = new RequestParams();
        params.put("device-id", idDevice);
        params.put("fcm-token", token);

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");

        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/mirror/config/set/fcm", params, headers, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                int code2 = code;
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
