package com.safety.hss.safetyfirstdata;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Struct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    Button mStart;
    Button mStop;
    Button mUpload;
    Spinner spinner;

    TextView mdesc,x,y,z,accuracy;

    private Sensor acc;
    private SensorManager sm;


    private String vehicleName;
    View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Linking my layout file - checking git push
        setContentView(R.layout.activity_main);
        mdesc = (TextView)findViewById(R.id.description);
        mStart = (Button)findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
        mUpload = (Button) findViewById(R.id.upload);
        spinner = (Spinner) findViewById(R.id.spinner) ;
        mdesc.setText(R.string.description);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.vehicles));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                vehicleName=arg0.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GetAccData.started && !GetAccData.finished) {
                    mainView = view;
                    GetAccData.started = true;
                    GetAccData.finished = false;
                    Intent intent = new Intent(MainActivity.this, GetAccData.class);
                    intent.setAction("com.safety.hss.safetyfirstdata.action.COLLECT");
                    startService(intent);
                    Snackbar.make(view, "Accelerometer Log Started.", Snackbar.LENGTH_LONG).show();
                }else{
                    if(!GetAccData.finished)
                        Snackbar.make(view, "Already Started.", Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(view, "Upload Previous Data Before Logging Again.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GetAccData.started) {
                    GetAccData.started = false;
                    GetAccData.finished = true;
                    Intent intent = new Intent(MainActivity.this, GetAccData.class);
                    intent.setAction("com.safety.hss.safetyfirstdata.action.STOP");
                    startService(intent);
                    Snackbar.make(view, "Accelerometer Log Stopped.", Snackbar.LENGTH_LONG).show();
                }else{
                    if(GetAccData.finished)
                        Snackbar.make(view, "Already Stopped", Snackbar.LENGTH_LONG).show();
                    else if(!GetAccData.finished)
                        Snackbar.make(view, "You need to start the log first.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GetAccData.finished) {
                    GetAccData.started = false;
                    GetAccData.finished = false;
                    //upload here
                    Snackbar.make(view,"Uploading Accelerometer Log.",Snackbar.LENGTH_LONG).show();
                    String filename = vehicleName.replace(" ","") + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    try {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            File file = getExternalFilesDir("SafetyFirstData");
                            File file2=new File(file,filename);
                            FileOutputStream out = new FileOutputStream(file2);
                            for(int i=0;i<GetAccData.data.size();i++) {
                                out.write(((Float[])GetAccData.data.get(i))[0].toString().getBytes());
                                out.write(",".getBytes());
                                out.write(((Float[])GetAccData.data.get(i))[1].toString().getBytes());
                                out.write(",".getBytes());
                                out.write(((Float[])GetAccData.data.get(i))[2].toString().getBytes());
                                out.write(",".getBytes());
                                out.write(((Float[])GetAccData.data.get(i))[3].toString().getBytes());
                                out.write("\n".getBytes());
                            }
                            out.close();

                            RequestParams params = new RequestParams();
                            try {
                                params.put("myfile", file2);
                            } catch(FileNotFoundException e) {}

                            AsyncHttpClient client = new AsyncHttpClient();
                            AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                    Log.d("post",Integer.toString(responseBody.length));
                                    if(responseBody.length==39)
                                        Snackbar.make(mainView,"Upload Successful",Snackbar.LENGTH_LONG).show();
                                    else if(responseBody.length==4)
                                        Snackbar.make(mainView,"Upload Failed. File size too big.",Snackbar.LENGTH_LONG).show();
                                    else
                                        Snackbar.make(mainView,"Upload Error",Snackbar.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                    Log.e("post",error.toString());
                                    Snackbar.make(mainView,"Upload Failed",Snackbar.LENGTH_LONG).show();
                                }
                            };
                            client.post("http://safetyfirst.pe.hu/upload.php", params, responseHandler);
                        }
                    } catch (Exception e) {
                        Log.d("External Public","error in saving");
                    }

                    //clear
                    GetAccData.data.clear();
                }else{
                    Snackbar.make(view, "Cannot Upload, No Data Found.", Snackbar.LENGTH_LONG).show();
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
        if(GetAccData.started) {
            x.setText("X: " + sensorEvent.values[0]);
            y.setText("Y: " + sensorEvent.values[1]);
            z.setText("Z: " + sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(GetAccData.started) {
            accuracy.setText("Accuracy: " + i);
        }
    }
}
