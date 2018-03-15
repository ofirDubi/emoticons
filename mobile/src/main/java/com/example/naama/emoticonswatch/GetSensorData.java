package com.example.naama.emoticonswatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import panamana.emoticonswatch.R;

import okio.Buffer;

/**
 * Created by naama on 12/3/15.
 */
public class GetSensorData extends AppCompatActivity {

    private Context context;
    WebSocketCall call;
    FileWriter writer;
    private OnUpdateListener listener;
    private SharedPreferences sharedPreferences;
    double e2;
    double c1;
    double c2;
    double c3;
    double e1;
    double h1;
    double h2;
    double e3;
    private String TAG = GetSensorData.class.getSimpleName();


    public void stop() {
//        try {
            // TODO
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.call.cancel();
    }


    public interface OnUpdateListener {
        public void onUpdate(UIObject obj);
    }

    public void setUpdateListener(OnUpdateListener listener) {
        this.listener = listener;
    }


    public GetSensorData(Context context) {
        this.context = context;
    }

    public void run() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //change the device name here
        String deviceName = sharedPreferences.getString("sensor", "00A3B4816862");

        String url = "ws://api.neurosteer.com:8080/v1/features/"+ deviceName + "/pull";
        Log.d("Debug", "* Openning connection. URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        call = WebSocketCall.create(new OkHttpClient(), request);
        Log.d("Debug", "****received message*****");
        call.enqueue(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("Debug", "* Connection opened");

            }

            @Override
            public void onFailure(IOException e, Response response) {
                Log.d("Error", "* Connection failed to open. Exception: " + e.getMessage());

            }

            @Override
            public void onMessage(ResponseBody responseBody) throws IOException {


                JSONObject jsonObject = null;
                JSONObject featuresJsonObject = null;
                float backgroundTrackVolume = 0;
                float track1Volume = 0;
                float track2Volume = 0;
                float track3Volume = 0;

                try {
                    jsonObject = new JSONObject(responseBody.source().readUtf8());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Debug", "* Message received. responseBody - " + jsonObject.toString());

                try {
                    if ((((JSONObject) jsonObject.get("features")).get("q0")).getClass().getName().equals("java.lang.Double")) { // or other feature
                        double q0 = (double)((JSONObject) jsonObject.get("features")).get("q0");
                    } else { //what the hell is q0 and features
                    }
                    if((((JSONObject) jsonObject.get("features")).get("e2")).getClass().getName().equals("java.lang.Double")) {
                         e2 = (double) ((JSONObject) jsonObject.get("features")).get("e2");
                    }
                    else{

                    }
                    if (((JSONObject) jsonObject.get("features")).get("c1").getClass().getName().equals("java.lang.Double")) { // or other feature
                         c1 = (double) ((JSONObject) jsonObject.get("features")).get("c1");
                    } else {
                    }
                    if (((JSONObject) jsonObject.get("features")).get("c2").getClass().getName().equals("java.lang.Double")) {
                         c2 = (double) ((JSONObject) jsonObject.get("features")).get("c2");
                    } else{

                    }
                    if (((JSONObject) jsonObject.get("features")).get("c3").getClass().getName().equals("java.lang.Double")) {
                         c3 = (double) ((JSONObject) jsonObject.get("features")).get("c3");
                    } else{

                    }
                    if (((JSONObject) jsonObject.get("features")).get("h1").getClass().getName().equals("java.lang.Double")) {
                         h1 = (double) ((JSONObject) jsonObject.get("features")).get("h1");
                    } else{

                    }
                    if (((JSONObject) jsonObject.get("features")).get("h2").getClass().getName().equals("java.lang.Double")) {
                         h2 = (double) ((JSONObject) jsonObject.get("features")).get("h2");
                    } else{
                    }
                    if (((JSONObject) jsonObject.get("features")).get("e1").getClass().getName().equals("java.lang.Double")) {
                         e1 = (double) ((JSONObject) jsonObject.get("features")).get("e1");
                    } else{
                    }
                    if (((JSONObject) jsonObject.get("features")).get("e2").getClass().getName().equals("java.lang.Double")) {
                         e2 = (double) ((JSONObject) jsonObject.get("features")).get("e2");
                    } else{
                    }
                    if (((JSONObject) jsonObject.get("features")).get("e3").getClass().getName().equals("java.lang.Double")) {
                         e3 = (double) ((JSONObject) jsonObject.get("features")).get("e3");
                    } else{
                    }
                    String repData = representdata(e1, e2, e3, c1,c2, c3, h2, h1);
                    UIObject uiObject = new UIObject(repData);
                    listener.onUpdate(uiObject);


                }

                catch (JSONException e) {
                    e.printStackTrace();
                }
                responseBody.close();
            }

            @Override
            public void onPong(Buffer payload) {
            }

            @Override
            public void onClose(int code, String reason) {
            }
        });
    }
    public static class UIObject {
        private String emoticon;

        public UIObject( String emoticon ) {
            this.emoticon= emoticon;
        }
        public String getemotion(){
            return emoticon;
        }
    }

    public String representdata(double e1,double e2, double e3,double  c1, double c2, double c3,double  h2,double h1){

        double re1, re2, re3, rc1, rc2, rc3, rh1, rh2;

        // thresholds
        double t1e1 = -0.8;
        double t2e1 = 0.8;
        double t1e2 = -0.2;
        double t2e2 = 0.9;
        double t1e3 = -0.5;
        double t2e3 = 0.95;

        double t1c1 = -0.2;
        double t2c1 = 0.9;
        double t1c2 = -0.5;
        double t2c2 = 0.5;
        double t1c3 = -0.1;
        double t2c3 = 0.9;

        double t1h1 = -0.3;
        double t2h1 = 0.6;
   //     double t1h2 = -0.5;
    //    double t2h2 = -0.8;

        // relative features values
        re1 = Math.abs(e1-t1e1)/((Math.abs(t1e1)+Math.abs(t2e1)));
        re2 = Math.abs(e2-t1e2)/((Math.abs(t1e2)+Math.abs(t2e2)));
        re3 = Math.abs(e3-t1e3)/((Math.abs(t1e3)+Math.abs(t2e3)));

        rc1 = Math.abs(c1-t1c1)/((Math.abs(t1c1)+Math.abs(t2c1)));
        rc2 = Math.abs(c2-t1c2)/((Math.abs(t1c2)+Math.abs(t2c2)));
        rc3 = Math.abs(c3-t1c3)/((Math.abs(t1c3)+Math.abs(t2c3)));

        rh1 = Math.abs(h1-t1h1)/((Math.abs(t1h1)+Math.abs(t2h1)));
     //   rh2 = Math.abs(h2-t1h2)/((Math.abs(t1h2)+Math.abs(t2h2)));

        double biggestE = Math.max(re1,Math.max(re2,re3));
       // double biggestE = re2;

        double biggestC = Math.max(rc1,Math.max(rc2,rc3));
        //double biggestC = rc1;
      //  double biggestH = Math.max(rh2,rh1);
        double biggestH = rh1;

        double biggestValue = Math.max(biggestC,Math.max(biggestE,biggestH));
        Log.d("Debug", "biggestH : " + biggestH + ", biggestC : " + biggestC + ", biggestE: " + biggestE);
        if (biggestE == biggestValue) {
            Log.d("Debug", "feeling emotional");
            return "emotional";
        }
        if(biggestH == biggestValue){
            Log.d("Debug", "feeling happy");
            return "happy";

        }
        if(biggestC == biggestValue){
            Log.d("Debug", "feeling concentration");
            return "concentration";
        }

        Log.i(TAG, "return value");
        return "hey";
    }
}
