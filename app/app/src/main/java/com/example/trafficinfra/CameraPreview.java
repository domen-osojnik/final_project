package com.example.trafficinfra;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "LOGMYAPPSTATUS";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera cam) {
        super(context);

        mCamera = cam;

        mHolder = getHolder();
        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void setFocus(String mParameter) {
        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setFocusMode(mParameter);
        mCamera.setParameters(mParameters);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // ...
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null){
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){
            return;
        }

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}

