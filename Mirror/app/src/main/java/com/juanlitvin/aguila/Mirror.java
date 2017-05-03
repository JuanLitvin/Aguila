package com.juanlitvin.aguila;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONObject;

public class Mirror {

    private static boolean isOwnerRegistered = false;

    private Mirror() {}

    public static void register(final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.pref_string), Context.MODE_PRIVATE);
        if (preferences.contains("device-id")) {
            //device is registered, add a boot in server
            RESTClient.post("https://juanlitvin.com/api/aguila/v1/index.php/login/" + preferences.getString("device-id", ""), null, new RESTClient.ResponseHandler() {
                @Override
                public void onSuccess(int code, String responseBody) {
                    try {
                        JSONObject response = new JSONObject(responseBody);
                        isOwnerRegistered = response.getBoolean("isOwnerRegistered");

                        if (!isOwnerRegistered) {
                            context.startActivity(new Intent(context, RegisterOwnerActivity.class).putExtra("reg-code", response.getString("mirror-reg-code")));
                            ((Activity)context).finish();
                        }
                    } catch (Exception e) {}
                }

                @Override
                public void onFailure(int code, String responseBody, Throwable error) {

                }
            });
        } else {
            //not registered. Register new mirror
            isOwnerRegistered = false;
            RESTClient.post("https://juanlitvin.com/api/aguila/v1/index.php/register", null, new RESTClient.ResponseHandler() {
                @Override
                public void onSuccess(int code, String responseBody) {
                    try {
                        JSONObject response = new JSONObject(responseBody);
                        String device_id = response.getString("device_id");

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("device-id", device_id);
                        editor.apply();
                    } catch (Exception e) {}
                }

                @Override
                public void onFailure(int code, String responseBody, Throwable error) {
                    context.startActivity(new Intent(context, ErrorActivity.class).putExtra("error", "Hubo un error al registrar su espejo.\nCompruebe su conexión y reinicie el dispositivo."));
                }
            });
        }
    }

    public static void addModule(MainActivity context, MirrorModule module, int fragmentId) {
        module.init();
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentId, module);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static void addModule(MainActivity context, MirrorModule module, int fragmentId, Bundle extras) {
        module.setArguments(extras);
        module.init();
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentId, module);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}
