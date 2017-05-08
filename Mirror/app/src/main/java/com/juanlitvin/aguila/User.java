package com.juanlitvin.aguila;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dalvik.system.DexClassLoader;

public class User {

    public static String userId, firstName, lastName, apiKey;

    public static void setUserId(String id) {
        userId = id;
    }

    public static void setFirstName(String fName) {
        firstName = fName;
    }

    public static void setLastName(String lName) {
        lastName = lName;
    }

    public static void setApiKey(String key) {
        apiKey = key;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getFullName() {
        return firstName + " " + lastName;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static List<Module> getModules(Context c) {
        List<Module> modules = new ArrayList<>();
        Bundle horaBundle = new Bundle();
        horaBundle.putString("timeZone", "GMT-03:00");
        Module module = new Module(new HoraFragment(), R.id.fragment2, horaBundle);
        modules.add(module);

        Bundle fechaBundle = new Bundle();
        fechaBundle.putString("timeZone", "GMT-03:00");
        fechaBundle.putString("format", "EEEE, MMM dd");
        module = new Module(new FechaFragment(), R.id.fragment3, fechaBundle);
        modules.add(module);

        module = new Module(new ClimaFragment(), R.id.fragment5, null);
        modules.add(module);

        module = new Module(new NoticiasFragment(), R.id.fragment6, null);
        modules.add(module);

        module = new Module(new GreetingFragment(), R.id.fragment1, null);
        modules.add(module);

        /*RhinoAndroidHelper rhinoAndroidHelper = new RhinoAndroidHelper(c);
        rhinoAndroidHelper.enterContext();
        try {
            rhinoAndroidHelper.loadClassJar(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NoticiasModule.jar"));
            Toast.makeText(c, "Did it", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return modules;
    }

    public static void load() {

    }

    public static void getModules(Context c, Callback handler) {
        //TODO: fetch from server with User.userId

        List<Module> modules = new ArrayList<>();
        Bundle horaBundle = new Bundle();
        horaBundle.putString("timeZone", "GMT-03:00");
        Module module = new Module(new HoraFragment(), R.id.fragment2, horaBundle);
        modules.add(module);

        Bundle fechaBundle = new Bundle();
        fechaBundle.putString("timeZone", "GMT-03:00");
        fechaBundle.putString("format", "EEEE, MMM dd");
        module = new Module(new FechaFragment(), R.id.fragment3, fechaBundle);
        modules.add(module);

        module = new Module(new ClimaFragment(), R.id.fragment5, null);
        modules.add(module);

        module = new Module(new NoticiasFragment(), R.id.fragment6, null);
        modules.add(module);

        module = new Module(new GreetingFragment(), R.id.fragment1, null);
        modules.add(module);

        /*RhinoAndroidHelper rhinoAndroidHelper = new RhinoAndroidHelper(c);
        rhinoAndroidHelper.enterContext();
        try {
            rhinoAndroidHelper.loadClassJar(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NoticiasModule.jar"));
            Toast.makeText(c, "Did it", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        handler.onCallback(modules);
    }

    public interface Callback {
        public void onCallback(Object result);
    }

}
