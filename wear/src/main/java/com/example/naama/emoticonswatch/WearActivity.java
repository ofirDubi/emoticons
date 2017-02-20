package com.example.naama.emoticonswatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class WearActivity extends Activity {

    private static final String TAG = WearActivity.class.getSimpleName();
    private TextView mTextView;
    private ImageView mImageView;
    private MessageReceiver messageReceiver;
    private int count = 0;
    private String text = "none";
    private int imageNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mImageView = (ImageView) stub.findViewById(R.id.image);
                mImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            count++;
            count = count % 4;
            String message = intent.getStringExtra("message");
            // Display message in UI
            ImageView happy = new ImageView(getApplicationContext());
            happy.setImageResource(R.mipmap.happy);

            ImageView stressed = new ImageView(getApplicationContext());
            stressed.setImageResource(R.mipmap.stressed);

            ImageView concentrated = new ImageView(getApplicationContext());
            concentrated.setImageResource(R.mipmap.concentrated);
            if(message.contains("happy")){
                message = "happy";
                mImageView.setImageResource(R.mipmap.happy);
            }
            if(message.contains("stressed")){
                message = "stressed";
                mImageView.setImageResource(R.mipmap.stressed);
            }
            if(message.contains("concentrated")){
                message = "concentrated";
                mImageView.setImageResource(R.mipmap.concentrated);
            }
            mTextView.setText(message);
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        Log.i(TAG, "onDestroy WearActivity");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }
}
