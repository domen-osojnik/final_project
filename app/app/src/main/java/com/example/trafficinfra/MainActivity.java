package com.example.trafficinfra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.libreadings.SensorData;
import com.example.libreadings.SensorReading;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public final static String TAG = "STATUS";

    // Recording state:
    // if true then save sensor readings
    // else do not
    private Boolean isRecording;
    private Button buttonRecord;

    // Sensor readings containter
    private SensorReading readings;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Accelerometer properties
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float lastX, lastY, lastZ;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    // Gyroscope properties
    private Sensor gyro;
    private float[] rotationMatrix;
    Boolean gyroPosInit;

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

    Location loc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // auto generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        // Do not save sensor states until record button is pressed
        isRecording = false;

        // Initialize sensor readings container
        readings = new SensorReading();

        // Initialize sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //region Ustvarjanje žiroskopa
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
        //endregion

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        longitude = (TextView) this.findViewById(R.id.longitude);
        latitude = (TextView) this.findViewById(R.id.latitude);

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);


        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            loc = location;

                            latitude.setText("latitude: " + String.valueOf(location.getLatitude()));
                            longitude.setText("longitude: " + String.valueOf(location.getLongitude()));
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
        buttonRecord = (Button) findViewById(R.id.buttonRecord);
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

            //Add to data list if a larger shake is detected
            if(deltaX > 10)packData(1);
            if(deltaY > 10)packData(1);
            if(deltaZ > 10)packData(1);

            if(deltaX > 20)packData(2);
            if(deltaY > 20)packData(2);
            if(deltaZ > 20)packData(2);

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Event was triggered by gyroscope
            // Help: www.youtube.com/watch?v=8Veyw4e1MX0
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // 0 - X, 1 - Y, 2 - Z
            // Gyroscope was not initialized,
            // save initial sensor values, set state initialized to true
            if (!this.gyroPosInit) {
                _gyroPosX = Math.abs(event.values[0]);
                _gyroPosY = Math.abs(event.values[1]);
                _gyroPosZ = Math.abs(event.values[2]);

                this.gyroPosInit = true;
            }
            // Gyroscope already initialized,
            // calculate difference of prev pos and curr pos
            else {
                // degree 1 = small bump
                // degree 2 = medium bump
                // degree 3 = large bump

                // If the difference in pos is greater than -degree-, then device rotation changed!
                // If so, calculate new values and save readings (degree, date, geo-loc)
                if (checkGyro4Bump(event.values[0], event.values[1], event.values[2], 3, 0.6f)) return;
                if (checkGyro4Bump(event.values[0], event.values[1], event.values[2], 2, 0.4f)) return;
                if (checkGyro4Bump(event.values[0], event.values[1], event.values[2], 1, 0.2f)) return;
            }
        }
    }

    public void displayCleanValues() {
        // accelerometer
        currentX.setText("0.0");

        // Gyro values
        gyroPosX.setText("0");
        gyroPosY.setText("0");
        gyroPosZ.setText("0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));

        // Gyro values
        gyroPosX.setText("X : " + _gyroPosX + " rad/s");
        gyroPosY.setText("Y : " + _gyroPosY + " rad/s");
        gyroPosZ.setText("Z : " + _gyroPosZ + " rad/s");
    }


    // Print saved sensor readings
    public void printReadings(View v) {
        Log.d(TAG, readings.toString());
    }

    // Check if gyroscope sensor readings
    // changed enough to register a bump in the road
    public boolean checkGyro4Bump(float val1, float val2, float val3, int deg, float thresh) {
        if (Math.abs(this._gyroPosX - Math.abs(val1)) > thresh) {
            //Log.d(TAG, "Gyroscope x-axis pos change detected");
            this._gyroPosX = Math.abs(val1);
            if(isRecording) this.readings.readGyro(loc.getLatitude(), loc.getLongitude(), deg);
            return true;

        } else if (Math.abs(this._gyroPosY - Math.abs(val2)) > thresh) {
            //Log.d(TAG, "Gyroscope y-axis pos change detected");
            this._gyroPosY = Math.abs(val2);
            if(isRecording) this.readings.readGyro(loc.getLatitude(), loc.getLongitude(), deg);
            return true;

        } else if (Math.abs(this._gyroPosZ - Math.abs(val3)) > thresh) {
            //Log.d(TAG, "Gyroscope z-axis pos change detected");
            this._gyroPosZ = Math.abs(val3);
            if(isRecording) this.readings.readGyro(loc.getLatitude(), loc.getLongitude(), deg);
            return true;
        }

        // Gyroscope readings did not change enough, to register a bump,
        return false;
    }

    // Stop/Start recording sensor readings
    public void toggleRecoredingState(View v) {
        if (isRecording) {
            buttonRecord.setText("Start");
        }
        else {
            buttonRecord.setText("Stop");
        }

        isRecording = !isRecording;
    }

    private void packData(int shakeDegree){
        readings.addData(new SensorData(0.00, 0.00, 0, shakeDegree));
    }
}




