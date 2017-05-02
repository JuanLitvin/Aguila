package com.juanlitvin.fenix;

import android.annotation.TargetApi;
import android.app.Service;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.loopj.android.http.RequestParams;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationListener extends NotificationListenerService {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        RequestParams params = new RequestParams();
        params.add("title", notification.getNotification().extras.get("android.title").toString());
        params.add("body", notification.getNotification().extras.get("android.text").toString());

        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/notification/add", params, null);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {

    }

}
