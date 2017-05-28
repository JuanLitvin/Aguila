package com.juanlitvin.aguila;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dalvik.system.DexClassLoader;

public class User {

    public static String userId, name, apiKey;
    public static JSONObject config;

    public static void setUserId(String id) {
        userId = id;
    }

    public static void setName(String n) {
        name = n;
    }

    public static void setApiKey(String key) {
        apiKey = key;
    }

    public static void setConfig(JSONObject obj) {
        config = obj;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getName() {
        return name;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static JSONObject getConfig() {
        return config;
    }

}
