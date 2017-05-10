package com.juanlitvin.fenix;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class User {

    private static String idUser;
    private static String userName;
    private static String email;
    private static String apiKey;
    private static JSONObject config;
    private static JSONArray devices;

    public interface LoginCallback {
        void onComplete(int statusCode, JSONObject response);
        void onError(int statusCode, Throwable error);
    }

    public static void loadFromFirebaseUser(String uid, final LoginCallback callback) {
        setIdUser(uid);

        //create user on server and get apikey
        RequestParams params = new RequestParams();
        params.put("id", getIdUser());
        params.put("name", getUserName());
        params.put("email", getEmail());
        params.put("fcm-token", FirebaseInstanceId.getInstance().getToken());

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");

        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/user/phone/login", params, headers, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String responseBody) {
                try {
                    JSONObject response = new JSONObject(responseBody);

                    setApiKey(response.getString("api-key"));
                    setConfig(response.getJSONObject("config"));
                    setDevices(response.getJSONArray("devices"));
                    ConfigActivity.saveAvailableModules(response.getJSONArray("available-modules"));

                    callback.onComplete(statusCode, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(statusCode, e);
                }
            }

            @Override
            public void onFailure(int statusCode, String responseBody, Throwable error) {
                callback.onError(statusCode, error);
            }
        });
    }

    public static void sendConfigChange(String settings, String fragments, final RESTClient.ResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("config", settings);
        params.put("fragments", fragments);

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");
        headers.put("Auth", User.getApiKey());

        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/user/config/edit", params, headers, handler);
    }

    public static void registerMirror(String regCode, RESTClient.ResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("reg-code", regCode);
        params.put("name", "New Mirror");

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");
        headers.put("Auth", User.getApiKey());

        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/user/mirror/register", params, headers, handler);
    }

    public static void loginMirror(String regCode, RESTClient.ResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("reg-code", regCode);

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");
        headers.put("Auth", User.getApiKey());

        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/user/mirror/login", params, headers, handler);
    }

    public static String getIdUser() {
        return idUser;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static JSONObject getConfig() {
        return config;
    }

    public static JSONArray getDevices() {
        return devices;
    }

    public static void setIdUser(String str) {
        idUser = str;
    }

    public static void setUserName(String str) {
        userName = str;
    }

    public static void setEmail(String str) {
        email = str;
    }

    public static void setApiKey(String str) {
        apiKey = str;
    }

    public static void setConfig(JSONObject jsonObject) {
        config = jsonObject;
    }

    public static void setDevices(JSONArray jsonArray) {
        devices = jsonArray;
    }

}
