package com.juanlitvin.aguila;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

class RESTClient {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static Context context;

    public static void init(Context c) {
        context = c;

        client.setEnableRedirects(false);
        client.setTimeout(30);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }
}
