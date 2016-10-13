package com.safety.hss.safetyfirstdata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    Button mStart;
    Button mStop;
    Button mUpload;

    TextView mdesc,x,y,z,accuracy;

    private Sensor acc;
    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Linking my layout file - checking git push
        setContentView(R.layout.activity_main);
        mdesc = (TextView)findViewById(R.id.description);
        mStart = (Button)findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
        mUpload = (Button) findViewById(R.id.upload);
        mdesc.setText(R.string.description);

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        acc=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,acc,SensorManager.SENSOR_DELAY_NORMAL);

        x=(TextView)findViewById(R.id.xText);
        y=(TextView)findViewById(R.id.yText);
        z=(TextView)findViewById(R.id.zText);
        accuracy=(TextView)findViewById(R.id.accuracy);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        x.setText("X: "+sensorEvent.values[0]);
        y.setText("Y: "+sensorEvent.values[1]);
        z.setText("Z: "+sensorEvent.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        accuracy.setText("Accuracy: "+i);
    }
}
