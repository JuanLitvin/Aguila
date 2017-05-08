package com.juanlitvin.fenix;

import android.support.v4.util.ArrayMap;

import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Map;

public class User {

    private static String idUser;
    private static String userName;
    private static String email;
    private static String apiKey;

    public interface LoginCallback {
        void onComplete(int statusCode, JSONObject response);
        void onError(int statusCode, Throwable error);
    }

    public static void loadFromFirebaseUser(FirebaseUser user, final LoginCallback callback) {
        setIdUser(user.getUid());
        setUserName(user.getDisplayName());
        setEmail(user.getEmail());

        //create user on server and get apikey
        RequestParams params = new RequestParams();
        params.put("id", getIdUser());
        params.put("name", getUserName());
        params.put("email", getEmail());

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");

        RESTClient.get("https://juanlitvin.com/api/aguila/v1/index.php/user/phone/login", params, headers, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String responseBody) {
                try {
                    JSONObject response = new JSONObject(responseBody);

                    setApiKey(response.getString("api-key"));

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

}
