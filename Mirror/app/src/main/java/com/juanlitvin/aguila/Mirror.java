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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Mirror {

    private static boolean isOwnerRegistered = false;

    public static String idDevice;
    public static String regCode;
    public static boolean enabled;
    private static SharedPreferences preferences;

    public static List<MirrorModule> moduleFragments = new ArrayList<>();

    private Mirror() {}

    public static void register(final Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.pref_string), Context.MODE_PRIVATE);
        if (preferences.contains("device-id") && !preferences.getString("device-id", "").isEmpty()) {
            //device is registered, login and register boot in server
            loadMirror(context, preferences.getString("device-id", ""));

        } else {
            //not registered. Register new mirror
            registerNewMirror(context);
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

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("reg-code", regCode);
                    editor.putString("device-id", idDevice);
                    editor.apply();

                    context.startActivity(new Intent(context, RegisterOwnerActivity.class).putExtra("reg-code", regCode));
                    ((Activity)context).finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                context.startActivity(new Intent(context, ErrorActivity.class).putExtra("error", "Hubo un error al registrar su espejo.\nCompruebe su conexión y reinicie el dispositivo."));
                ((Activity)context).finish();
            }
        });
    }

    private static void loadMirror(final Context context, String idDevice) {
        RequestParams params = new RequestParams();
        params.put("device-id", idDevice);

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");

        RESTClient.post("https://juanlitvin.com/api/aguila/v1/index.php/mirror/login", params, headers, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                try {
                    JSONObject response = new JSONObject(responseBody);

                    setMirrorConfig(response);

                    if (isRegistrationRequired(response)) {
                        registerNewMirror(context);
                        return;
                    }

                    /*
                    *   Mirror is registered.
                    *   Check if owner is registered
                    *   If yes, log in last user (if non-existant, prompt to login)
                    *   If no, prompt reg-code
                    */
                    isOwnerRegistered = response.getBoolean("isOwnerRegistered");
                    if (isOwnerRegistered) {
                        //registered, set up logged user's config
                        loadUser(response.getJSONObject("user"));
                    } else {
                        //not registered, register device
                        tryRegister();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                context.startActivity(new Intent(context, ErrorActivity.class).putExtra("error", "Hubo un error al iniciar su espejo.\nCompruebe su conexión y reinicie el dispositivo."));
                ((Activity)context).finish();
            }
        });
    }

    private static void loadUser(JSONObject user) {
        try {
            if (user.has("id") && user.getString("id").length() > 0) {
                //there is a logged user
                User.setUserId(user.getString("id"));
                User.setFirstName(user.getString("fname"));
                User.setLastName(user.getString("lname"));
                User.setApiKey(user.getString("apikey"));

                updateConfig(user.getJSONObject("config"));
            } else {
                //no logged user. Prompt login
                MainActivity.context.startActivity(new Intent(MainActivity.context, LogUserInActivity.class).putExtra("reg-code", preferences.getString("reg-code", "")));
                ((MainActivity)MainActivity.context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.context.startActivity(new Intent(MainActivity.context, ErrorActivity.class).putExtra("error", "Hubo un error al iniciar su sesión.\nCompruebe su conexión y reinicie el dispositivo."));
            ((MainActivity)MainActivity.context).finish();
        }
    }

    public static void updateConfig(JSONObject config) {
        try {
            MainActivity.loadUserModules(jsonToModuleArray(config.getJSONArray("modules")));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.context.startActivity(new Intent(MainActivity.context, ErrorActivity.class).putExtra("error", "Hubo un error al iniciar su sesión.\nCompruebe su conexión y reinicie el dispositivo."));
            ((MainActivity)MainActivity.context).finish();
        }
    }

    public static void updateConfig() {
        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");
        headers.put("Auth", User.apiKey);

        RESTClient.get("https://juanlitvin.com/api/aguila/v1/index.php/user/config", null, headers, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                try {
                    JSONObject config = new JSONObject(responseBody);

                    //update config
                    MainActivity.loadUserModules(jsonToModuleArray(config.getJSONArray("modules")));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.context.startActivity(new Intent(MainActivity.context, ErrorActivity.class).putExtra("error", "Hubo un error al actualizar su configuración.\nCompruebe su conexión y reinicie el dispositivo."));
                    ((MainActivity)MainActivity.context).finish();
                }
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                MainActivity.context.startActivity(new Intent(MainActivity.context, ErrorActivity.class).putExtra("error", "Hubo un error al actualizar su configuración.\nCompruebe su conexión y reinicie el dispositivo."));
                ((MainActivity)MainActivity.context).finish();
            }
        });

    }

    private static List<Module> jsonToModuleArray(JSONArray modulesArray) {
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i < modulesArray.length(); i++) {
            try {
                JSONObject m = modulesArray.getJSONObject(i);
                Module module = new Module(getModuleFragmentByPackage(m.getString("package")), getModuleFragmentIdByStringId(m.getString("fragment-id")), m.get("extras") == null ? null : jsonObjectToBundle(m.getJSONObject("extras")));
                modules.add(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return modules;
    }

    private static void tryRegister() {
        if (preferences.contains("reg-code") && !preferences.getString("reg-code", "").isEmpty()) {
            //has regcode. Show it
            MainActivity.context.startActivity(new Intent(MainActivity.context, RegisterOwnerActivity.class).putExtra("reg-code", preferences.getString("reg-code", "")));
            ((MainActivity)MainActivity.context).finish();
        } else {
            //no reg-code. call register
            registerNewMirror(MainActivity.context);
        }
    }

    private static MirrorModule getModuleFragmentByPackage(String modulePackage) {
        final String basePackage = "com.juanlitvin.aguila.modules";
        switch (modulePackage) {
            case basePackage + ".hora":
                return new HoraFragment();
            case basePackage + ".fecha":
                return new FechaFragment();
            case basePackage + ".clima":
                return new ClimaFragment();
            case basePackage + ".noticias":
                return new NoticiasFragment();
            case basePackage + ".greeting":
                return new GreetingFragment();
            default:
                return new ModuleErrorFragment();
        }
    }

    private static int getModuleFragmentIdByStringId(String fragmentId) {
        switch (fragmentId) {
            case "fragment1":
                return R.id.fragment1;
            case "fragment2":
                return R.id.fragment2;
            case "fragment3":
                return R.id.fragment3;
            case "fragment4":
                return R.id.fragment4;
            case "fragment5":
                return R.id.fragment5;
            case "fragment6":
                return R.id.fragment6;
            case "fragment7":
                return R.id.fragment7;
            default:
                return R.id.fragment1;
        }
    }

    private static Bundle jsonObjectToBundle(JSONObject extras) {
        Bundle result = new Bundle();

        Iterator<String> keysIterator = extras.keys();
        while (keysIterator.hasNext()) {
            String key = (String) keysIterator.next();
            try {
                String value = extras.getString(key);
                result.putString(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
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

    public static void addModule(MainActivity context, MirrorModule module, int fragmentId) {
        //save fragment for later removal
        moduleFragments.add(module);

        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.replace(fragmentId, module);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        module.init();
    }

    public static void addModule(MainActivity context, MirrorModule module, int fragmentId, Bundle extras) {
        //save fragment for later removal
        moduleFragments.add(module);

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

    public static boolean isRegistrationRequired(JSONObject response) {
        try {
            if (response.has("register-required")) {
                return response.getBoolean("register-required");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.context.startActivity(new Intent(MainActivity.context, ErrorActivity.class).putExtra("error", "Hubo un error al iniciar su espejo.\nCompruebe su conexión y reinicie el dispositivo."));
            ((MainActivity)MainActivity.context).finish();
        }
        return false;
    }
}
