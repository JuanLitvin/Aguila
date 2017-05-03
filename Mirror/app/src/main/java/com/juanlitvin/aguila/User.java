package com.juanlitvin.aguila;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static List<Module> getModules() {
        List<Module> modules = new ArrayList<>();
        Bundle horaBundle = new Bundle();
        horaBundle.putString("timeZone", "GMT-03:00");
        Module module = new Module(new HoraFragment(), R.id.fragment2, horaBundle);
        modules.add(module);

        module = new Module(new FechaFragment(), R.id.fragment2, null);
        modules.add(module);

        module = new Module(new ClimaFragment(), R.id.fragment5, null);
        modules.add(module);

        module = new Module(new NoticiasFragment(), R.id.fragment6, null);
        modules.add(module);

        module = new Module(new GreetingFragment(), R.id.fragment1, null);
        modules.add(module);

        return modules;
    }
}
