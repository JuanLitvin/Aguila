package com.juanlitvin.aguila;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ModuleErrorFragment extends MirrorModule {

    @Override
    public void init() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView txt = new TextView(getActivity());
        txt.setText("Hubo un error al cargar el módulo");
        txt.setTextColor(Color.WHITE);
        txt.setTextSize(30f);
        txt.setGravity(Gravity.CENTER);

        return txt;
    }
}
