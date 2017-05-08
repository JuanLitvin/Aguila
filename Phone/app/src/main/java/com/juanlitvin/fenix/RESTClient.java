package com.juanlitvin.fenix;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

class RESTClient {
    private static AsyncHttpClient client;
    private static Context context;

    public interface ResponseHandler {
        void onSuccess(int code, String responseBody);
        void onFailure(int code, String responseBody, Throwable error);
    }

    public static void init(Context c) {
        context = c;

        client = new AsyncHttpClient();
        client.setEnableRedirects(false);
        client.setTimeout(30);
    }

    public static void get(String url, RequestParams params, final ResponseHandler handler) {
        init(context);
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    handler.onSuccess(statusCode, new String(responseBody, "UTF-8"));
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    handler.onFailure(statusCode, new String(responseBody, "UTF-8"), error);
                } catch (Exception e) {}
            }
        });
    }

    public static void get(String url, RequestParams params,  Map<String, String> headers, final ResponseHandler handler) {
        init(context);
        for (String key : headers.keySet()) {
            client.addHeader(key, headers.get(key));
        }
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    handler.onSuccess(statusCode, new String(responseBody, "UTF-8"));
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    handler.onFailure(statusCode, new String(responseBody, "UTF-8"), error);
                } catch (Exception e) {}
            }
        });
    }

    public static void post(String url, RequestParams params, final ResponseHandler handler) {
        init(context);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    handler.onSuccess(statusCode, new String(responseBody, "UTF-8"));
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    handler.onFailure(statusCode, new String(responseBody, "UTF-8"), error);
                } catch (Exception e) {}
            }
        });
    }

    public static void post(String url, RequestParams params, Map<String, String> headers, final ResponseHandler handler) {
        init(context);
        for (String key : headers.keySet()) {
            client.addHeader(key, headers.get(key));
        }
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    handler.onSuccess(statusCode, new String(responseBody, "UTF-8"));
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    handler.onFailure(statusCode, new String(responseBody, "UTF-8"), error);
                } catch (Exception e) {}
            }
        });
    }
}
