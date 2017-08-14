package com.juanlitvin.aguila;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class NoticiasAdapter extends ArrayAdapter<String> {

    private Context c;
    private List<String> list;

    public NoticiasAdapter(Context context, List<String> news) {
        super(context, 0, news);
        this.c = context;
        this.list = news;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(c);
        layout.setPadding(0, dpToPx(16), 0, dpToPx(16));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView img = new ImageView(c);
        img.setImageDrawable(c.getDrawable(R.drawable.ic_news));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(50), dpToPx(50));
        params.setMargins(0, dpToPx(10), 0, 0);
        img.setLayoutParams(params);
        layout.addView(img);

        TextView txt = new TextView(c);
        txt.setText(Html.fromHtml(list.get(position)));
        txt.setTextColor(Color.parseColor("#DDDDDD"));
        txt.setTextSize(15f);
        txt.setPadding(dpToPx(30), 0, 0, 0);
        txt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        layout.addView(txt);

        return layout;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * (getContext().getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
