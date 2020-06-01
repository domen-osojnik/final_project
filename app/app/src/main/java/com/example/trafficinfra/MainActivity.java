package com.example.trafficinfra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.libreadings.RetrofitInterface;
import com.example.libreadings.SensorData;
import com.example.libreadings.SensorReading;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "LOGCATSTATUSMY";

    // Register new event every x seconds
    Handler handler;
    Runnable runnable;

    // Class for app-backend communication
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://final-project-backend-nodejs.herokuapp.com";

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
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private TextView consoleEvents;
    private TextView consoleActions;

    private float vibrateThreshold = 0;

    public Vibrator v;

    // Location properties
    private FusedLocationProviderClient fusedLocationClient;
    private int locationRequestCode = 1000;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double latitudeV;
    private double longitudeV;
    private float speedV;
    Location loc;

    // Camera properties
    public Camera cam;
    private CameraPreview mPreview;
    private FrameLayout mFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // auto generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // User Interface
        // Starting setup
        getPermission();
        initializeViews();


        // Initialize timer that will call save geo-loc every x seconds...
        handler = new Handler();

        // Initialize retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

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
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude.setText("latitude: " + String.valueOf(location.getLatitude()));
                    longitude.setText("longitude: " + String.valueOf(location.getLongitude()));
                    speed.setText("speed: " + String.valueOf(location.getSpeed()));
                    latitudeV = location.getLatitude();
                    longitudeV = location.getLongitude();
                    speedV = location.getSpeed();
                    loc = location;
                }
            }
        };
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.AccelerationX);
        gyroPosX = (TextView) findViewById(R.id.gyroPosX);
        gyroPosY = (TextView) findViewById(R.id.gyroPosY);
        gyroPosZ = (TextView) findViewById(R.id.gyroPosZ);
        buttonRecord = (Button) findViewById(R.id.buttonRecord);
        longitude = (TextView) this.findViewById(R.id.longtitude);
        latitude = (TextView) this.findViewById(R.id.latitude);
        speed = (TextView) this.findViewById(R.id.speed);
        mFrameLayout = (FrameLayout) findViewById(R.id.cameraPreview);
        consoleEvents = (TextView) findViewById(R.id.consoleEvents);
        consoleActions = (TextView) findViewById(R.id.consoleActions);
    }


    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        hideSystemUI();

        // Start saving new event every x seconds
        handler.postDelayed(runnable = new Runnable() {
        public void run() { handler.postDelayed(runnable, 10000); packData(0);  captureImage();} }, 10000);

        super.onResume();

        // Checking hardware, initializing camera
        if (!hasHardware()) {
            Toast.makeText(this, "Application requires a camera", Toast.LENGTH_LONG).show();
            finish();
        } else initializeCamera();

        // Start sensor measurements
        gyroPosInit = false;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        fusedLocationClient.removeLocationUpdates(locationCallback);

        this.log("Releasing camera");
        cam.release();
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
            // TODO: fix, prozi se samo od sebe
            /*
            if(deltaX > 10)packData(1);
            if(deltaY > 10)packData(1);
            if(deltaZ > 10)packData(1);

            if(deltaX > 20)packData(2);
            if(deltaY > 20)packData(2);
            if(deltaZ > 20)packData(2);
            */
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
                if (checkGyro4Bump(event.values[0], event.values[1], event.values[2], 3, 0.6f))
                    return;
                if (checkGyro4Bump(event.values[0], event.values[1], event.values[2], 2, 0.4f))
                    return;
                if (checkGyro4Bump(event.values[0], event.values[1], event.values[2], 1, 0.2f))
                    return;
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
        currentX.setText("Acc x: " + Float.toString(deltaX));

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
            packData(deg);
            return true;

        } else if (Math.abs(this._gyroPosY - Math.abs(val2)) > thresh) {
            //Log.d(TAG, "Gyroscope y-axis pos change detected");
            this._gyroPosY = Math.abs(val2);
            packData(deg);
            return true;

        } else if (Math.abs(this._gyroPosZ - Math.abs(val3)) > thresh) {
            //Log.d(TAG, "Gyroscope z-axis pos change detected");
            this._gyroPosZ = Math.abs(val3);
            packData(deg);
            return true;
        }

        // Gyroscope readings did not change enough, to register a bump,
        return false;
    }


    /**
     *  Toggle recording
     */
    public void toggleRecoredingState(View v) {
        if (isRecording) {
            this.log("Recording stopped");
            this.log("Sending to server");
            buttonRecord.setText("Start");
            sendToServer();

        } else {
            this.log("Recording started");
            buttonRecord.setText("Stop");
            // Reset readings class
            readings = new SensorReading();
        }

        isRecording = !isRecording;
    }


    /**
     *  Send data to server
     */
    public void sendToServer() {


        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.MIXED);
        for (int i = 0; i < readings.filesCount(); i++) {
            File file = new File(readings.getFilePath(i));
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        MultipartBody requestBody = builder.build();

        RetrofitInterface apiService = retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = apiService.postReading(readings, requestBody.parts());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MainActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error " + t.getMessage());
            }
        });
        /*

        Call<SensorReading> call = apiService.postReading(readings);
        call.enqueue(new Callback<SensorReading>() {
            @Override
            public void onResponse(Call<SensorReading> call, Response<SensorReading> response) {
                int statusCode = response.code();
                log("Status Code: " + statusCode);
            }

            @Override
            public void onFailure(Call<SensorReading> call, Throwable t) {
                log("Error: " + t.toString());
            }
        });
        */

    }


    /**
     *  Add to events list
     */
    public void packData(int shakeDegree) {
        if (isRecording) {
            if (readings.addData(new SensorData(latitudeV, longitudeV, speedV, shakeDegree))) {
                this.log("New event registered");
                consoleEvents.setText(readings.toString());
            }
        }
    }

    /**
     * Get all required permissions
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPermission() {

        int PERMISSION_ALL = 4;
        String[] PERMISSIONS = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    /**
     * Check hardware for camera usage
     */
    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    private Boolean hasHardware() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Check if this device has a camera
     */
    public android.hardware.Camera getCameraInstance() {
        // Get camera instance
        android.hardware.Camera cam = null;
        try {
            cam = android.hardware.Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.d(TAG, "Cannot open camera: camera not available!");
        }
        return cam;     // returns null if camera is unavailable
    }


    /**
     * Creating file for image storage on disc
     */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(getFilesDir(), "Images");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            String path = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(path);
            readings.addFile(path);
        } else {
            return null;
        }

        // Return new media file
        return mediaFile;
    }


    /**
     * Capture the image
     */
    public void captureImage() {
        if (!isRecording) return;
        this.log("Image captured");
        cam.takePicture(null, null, pictureCallback);
        initializeCamera();
    }

    /**
     * Callback function
     */
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, ">... Saving captured image");
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * Initialize camera
     */
    private void initializeCamera(){
        this.log("Initializing camera");
        this.cam = getCameraInstance();

        // Check permissions

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, cam);
            cam.enableShutterSound(false);

            // Set Preview
            mFrameLayout.addView(mPreview);
    }


    /**
     * hide system ui
     */
    private void hideSystemUI() {
        ((ConstraintLayout)findViewById(R.id.layoutMain)).setSystemUiVisibility(
         View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * log to logcat and UI
     */
    private void log(String msg) {
        Log.d(TAG, ">..." + msg);
        consoleActions.setText(String.format("%s\n>...%s", consoleActions.getText(), msg));
    }
}