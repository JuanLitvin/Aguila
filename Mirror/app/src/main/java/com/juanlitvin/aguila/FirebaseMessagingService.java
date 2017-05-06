package com.juanlitvin.aguila;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Bus;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public FirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null) {
            //there is data
            if (remoteMessage.getData().containsKey("action")) {
                //its an update
                switch (remoteMessage.getData().get("action").toString()) {
                    case "ownerRegistered":
                        postBus(RegisterOwnerActivity.getBus(), remoteMessage);
                        break;
                }
            } else {
                //its a notification
                postBus(MainActivity.getBus(), remoteMessage);
            }
        }

    }

    private void postBus(final Bus bus, final Object param) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //is in main thread
            bus.post(param);
        } else {
            //is in another thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    bus.post(param);
                }
            });
        }
    }

}
