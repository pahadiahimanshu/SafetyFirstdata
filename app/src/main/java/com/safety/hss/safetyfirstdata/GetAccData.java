package com.safety.hss.safetyfirstdata;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class GetAccData extends IntentService implements SensorEventListener {

    private static final String ACTION_COLLECT = "com.safety.hss.safetyfirstdata.action.COLLECT";
    private static final String ACTION_STOP = "com.safety.hss.safetyfirstdata.action.STOP";

    public GetAccData() {
        super("GetAccData");
    }
    public Sensor acc;
    public SensorManager sm;

    public static ArrayList<Float[]> data = new ArrayList<Float[]>();
    public static int currentAccuracy;

    public static boolean started=false, finished = false;
    private boolean conti = true;


    @Override
    protected void onHandleIntent(Intent intent) {
        while(conti)
        {
            Log.d("AccData","Continue boolean = "+conti);
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_COLLECT.equals(action) && conti == true) {
                    handleActionCollect();
                } else if (ACTION_STOP.equals(action)) {
                    conti = false;
                    handleActionStop();
                }
            }
            Log.v("AccData", "Sleeping 1 sec");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.v("AccData", "couldn't sleep");
                e.printStackTrace();
            }
            Log.v("AccData", "Woke up");
        }
    }

    private void handleActionCollect() {
        Log.d("AccData","Collect Called");
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        acc=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,acc,SensorManager.SENSOR_DELAY_NORMAL);
    }


    private void handleActionStop() {
        Log.d("AccData","Stop Called");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(started) {
            Log.d("AccData", "OnSensorChanged");
            Float[] temp = {sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], currentAccuracy * 1.0f};
            data.add(temp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(started) {
            currentAccuracy = i;
        }
    }
}
