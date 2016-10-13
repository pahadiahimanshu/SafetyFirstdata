package com.safety.hss.safetyfirstdata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    Button mStart;
    Button mStop;
    Button mUpload;
    Spinner mVehicleSpinner;

    TextView mdesc,x,y,z,accuracy;

    private Sensor acc;
    private SensorManager sm;
    private ArrayList<Float[]> data = new ArrayList<Float[]>();
    int currentAccuracy;

    private boolean started=false, finished = false;

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

        // spinner added to select vehicle
        mVehicleSpinner = (Spinner)findViewById(R.id.vehicleSpinner);
        List<String> vehicle = new ArrayList<String>();
        vehicle.add("Car");
        vehicle.add("Motorbike");
        vehicle.add("Bus");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicle);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVehicleSpinner.setAdapter(dataAdapter);
        // data adapter set

        mVehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                // Showing selected spinner item
                Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                started=true;
                finished=false;
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                started=false;
                finished=true;
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finished) {
                    started = false;
                    finished = false;
                    //upload here

                    //clear
                    data.clear();
                }
            }
        });

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
        if(started) {
            x.setText("X: " + sensorEvent.values[0]);
            y.setText("Y: " + sensorEvent.values[1]);
            z.setText("Z: " + sensorEvent.values[2]);
            Float[] temp = {sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],currentAccuracy*1.0f};
            data.add(temp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(started) {
            accuracy.setText("Accuracy: " + i);
            currentAccuracy=i;
        }
    }
}
