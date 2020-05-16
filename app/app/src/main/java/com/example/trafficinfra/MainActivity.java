package com.example.trafficinfra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.CollationElementIterator;
import java.util.Locale;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public final static String TAG = "STATUS";
    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Gyroscope properties
    private Sensor gyro;
    private float[] rotationMatrix;
    Boolean gyroPosInit;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    // Gyroscope sensor values
    private float _gyroPosX;
    private float _gyroPosY;
    private float _gyroPosZ;

    // Views - UI
    private TextView currentX;
    private TextView gyroPosX;
    private TextView gyroPosY;
    private TextView gyroPosZ;

    private float vibrateThreshold = 0;

    public Vibrator v;

    private FusedLocationProviderClient fusedLocationClient;
    TextView latitude;
    TextView longitude;
    private int locationRequestCode = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // auto generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        // Initialize sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Initialize gyro
        this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.rotationMatrix = new float[16];

        // Initialize gyro values
        _gyroPosX = _gyroPosY = _gyroPosZ = 0;
        gyroPosInit = false;

        // Check if device has gyroscope
        if (this.gyro == null) {
            Log.d(TAG, ">... Application requires a gyroscope to run");
            finish();
        }

        //  Register listener for gyroscope
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize accelerometer
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            Log.d(TAG, "Pospeškometer ne obstaja na tej napravi!");
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        longitude = (TextView) this.findViewById(R.id.longitude);
        latitude = (TextView) this.findViewById(R.id.latitude);

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            // reuqest for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);


        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            latitude.setText("latitude: "+ String.valueOf(location.getLatitude()));
                            longitude.setText("longitude: "+ String.valueOf(location.getLongitude()));
                            Log.v(TAG, String.valueOf(location.getSpeed()));
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });
        }
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.AccelerationX);
        gyroPosX = (TextView) findViewById(R.id.gyroPosX);
        gyroPosY = (TextView) findViewById(R.id.gyroPosY);
        gyroPosZ = (TextView) findViewById(R.id.gyroPosZ);
    }


    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

        gyroPosInit = false;
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Triggered event
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // clean current values
            displayCleanValues();
            // display the current x,y,z accelerometer values
            displayCurrentValues();

            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);

            // if the change is below 2, it is just plain noise
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if ((deltaZ > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
                v.vibrate(50);
            }
        }
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Event was triggered by gyroscope
            // Help: www.youtube.com/watch?v=8Veyw4e1MX0
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // 0 - X, 1 - Y, 2 - z
            // Gyroscope was not not initialized,
            // save initial sensor values, set isInnit to true
            if (!this.gyroPosInit) {
                _gyroPosX = Math.abs(event.values[0]);
                _gyroPosY = Math.abs(event.values[1]);
                _gyroPosZ = Math.abs(event.values[2]);

                this.gyroPosInit = true;
            }
            // Gyroscope already initialized,
            // calculate difference of prev pos and curr pos
            else {

                // Difference in pos is greater than 0.2f, device rotation changed!
                if (Math.abs(_gyroPosX - Math.abs(event.values[0])) > 0.2f) {
                    //Log.d(TAG, "Gyroscope z-axis pos change detected");
                    _gyroPosX = Math.abs(event.values[0]);
                }
                else if  (Math.abs(_gyroPosY - Math.abs(event.values[1])) > 0.2f) {
                    //Log.d(TAG, "Gyroscope y-axis pos change detected");
                    _gyroPosY = Math.abs(event.values[1]);
                }
                else if  (Math.abs(_gyroPosZ - Math.abs(event.values[2])) > 0.2f) {
                    //Log.d(TAG, "Gyroscope z-axis pos change detected");
                    _gyroPosZ = Math.abs(event.values[2]);
                }
            }
        }
    }

    public void displayCleanValues() {
        // accelerometer
        currentX.setText("0.0");

        // gyroscope
        gyroPosX.setText("0");
        gyroPosY.setText("0");
        gyroPosZ.setText("0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        gyroPosX.setText("X : " + _gyroPosX);
        gyroPosY.setText("Y : " + _gyroPosY);
        gyroPosZ.setText("Z : " + _gyroPosZ);
    }
}
