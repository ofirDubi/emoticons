package com.example.naama.emoticonswatch.Activities;

import android.app.Notification;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.naama.emoticonswatch.Fragments.MainFragment;
import com.example.naama.emoticonswatch.GetSensorData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.zzj;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.security.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import panamana.emoticonswatch.R;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    static GetSensorData getSensorData;
    private GoogleApiClient googleClient;
    private int count = 0;
    private String message = "none";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout linearLayout;
    long lastSentTime;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMainActivity);

        getSensorData = (GetSensorData) new GetSensorData(this);

        getSensorData.run();
        getSensorData.setUpdateListener(new GetSensorData.OnUpdateListener() {
            @Override
            public void onUpdate(final GetSensorData.UIObject uiObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        ImageView happy = new ImageView(getApplicationContext());
                        happy.setImageResource(R.mipmap.happy);

                        ImageView stressed = new ImageView(getApplicationContext());
                        stressed.setImageResource(R.mipmap.stressed);

                        ImageView concentrated = new ImageView(getApplicationContext());
                        concentrated.setImageResource(R.mipmap.concentrated);
                        if(uiObject.getemotion().equals("happy")){
                            if (linearLayout.getChildAt(1) != null){
                                linearLayout.removeViewAt(1);
                            }
                            linearLayout.addView(happy);
                            message = "happy";
                        }
                        if(uiObject.getemotion().equals("emotional")){
                            if (linearLayout.getChildAt(1) != null){
                                linearLayout.removeViewAt(1);
                            }
                            linearLayout.addView(stressed);
                            message = "stressed";
                        }
                        if(uiObject.getemotion().equals("concentration")){
                            if (linearLayout.getChildAt(1) != null){
                                linearLayout.removeViewAt(1);
                            }
                            linearLayout.addView(concentrated);
                            message = "concentrated";
                        }
                        if (googleClient.isConnected()) {
                            int timeInMillis;
//                            if ()
                            lastSentTime = Calendar.getInstance().getTimeInMillis();
                            SendToDataLayerThread sendToDataLayerThread = new SendToDataLayerThread("/message_path", message + " " + lastSentTime);
                            sendToDataLayerThread.setPriority(SendToDataLayerThread.MAX_PRIORITY);
                            sendToDataLayerThread.start();
                            Log.i(TAG, "sent, is connected" + message);
//                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

                        } else {
                            googleClient.connect();
                        }
                    }
                });
            }
        });
        getSensorData.run();
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMain);
        ((MainFragment) mainFragment).setListener(new MainFragment.MainFragmentListener() {
            @Override
            public void stop() {
                System.exit(0);
                getSensorData.stop();
            }

            @Override
            public void start() {
                getSensorData.run();
            }
        });
        count++;

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final EditText editTextSensorName = (EditText) findViewById(R.id.editTextSensorName);
        editTextSensorName.setText(sharedPreferences.getString("sensor", "0006664e5c10"));
        editor.putString("sensor", editTextSensorName.getText().toString());
        editor.commit();

        Button buttonStop = (Button) findViewById(R.id.buttonExit);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button stop = (Button) view;
                editor.putString("sensor", editTextSensorName.getText().toString());
                editor.commit();
                getSensorData.stop();
                System.exit(0);
            }
        });



    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Send a message when the data layer connection is successful.
    @Override
    public void onConnected(Bundle connectionHint) {
        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread("/message_path", message + " " + Calendar.getInstance()).start();
        Log.i(TAG, "sent, onConnected" + message);
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
