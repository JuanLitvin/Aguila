package com.juanlitvin.aguila;


import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class NoticiasFragment extends MirrorModule {

    ListView listNoticias;

    @Override
    public void init() {
        startLoop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listNoticias = new ListView(getActivity());
        listNoticias.setDivider(null);
        listNoticias.setDividerHeight(0);
        listNoticias.setVerticalScrollBarEnabled(false);
        listNoticias.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return listNoticias;
    }

    private void startLoop() {
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            public void run(){
                updateNews();
                //execute again when next 30 minutes happens
                handler.postDelayed(this, DateTime.now().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(30).getMillis() - DateTime.now().getMillis());
            }
        });
    }

    private void updateNews() {
        RESTClient.get("http://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=0d920a4142d947e2bc88652b6fdd584c", null, new RESTClient.ResponseHandler() {
            @Override
            public void onSuccess(int code, String responseBody) {
                try {
                    JSONObject response = new JSONObject(responseBody);
                    JSONArray responseArticles = response.getJSONArray("articles");

                    List<String> news = new ArrayList<String>();
                    for (int i = 0; i < responseArticles.length(); i++) {
                        JSONObject responseArticlesCurrent = responseArticles.getJSONObject(i);
                        String responseArticlesCurrentTitle = responseArticlesCurrent.getString("title");
                        news.add(responseArticlesCurrentTitle);
                    }

                    listNoticias.setAdapter(new NoticiasAdapter(getActivity(), news));
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int code, String responseBody, Throwable error) {
                TextView textView = new TextView(getActivity());
                textView.setTextColor(Color.WHITE);
                textView.setText("Hubo un error al cargar las noticias");
                listNoticias.setEmptyView(textView);
            }
        });
    }
}
