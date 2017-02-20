package com.example.naama.emoticonswatch;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Naama on 4/17/2016.
 */
public class ListenerService extends WearableListenerService {

    private static final String TAG = ListenerService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate ListenerService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy ListenerService");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.i(TAG, "onMessageReceived");
        if (messageEvent.getPath().equals("/message_path")) {
            Log.i(TAG, "onMessageReceived /message_path");
            final String message = new String(messageEvent.getData());

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}