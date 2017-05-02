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
            if (Looper.myLooper() == Looper.getMainLooper()) {
                //is in main thread
                MainActivity.getBus().post(remoteMessage);
            } else {
                //is in another thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getBus().post(remoteMessage);
                    }
                });
            }
        }

    }

}