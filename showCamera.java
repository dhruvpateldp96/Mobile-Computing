package com.example.camerag14;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.hardware.Camera;

import java.io.IOException;


public class showCamera extends SurfaceView implements SurfaceHolder.Callback
{
    Camera camera;
    private boolean          isPreviewRunning = false;

    public showCamera(Context context, Camera camera) {
        super(context);
        this.camera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.e(getClass().getSimpleName(), "surfaceCreated");
        //camera = Camera.open();
        //camera.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
        //Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO = camera.setFocusMode();
        Camera.Parameters parameters = camera.getParameters();

        //SR
        parameters.setPreviewFrameRate(20);
        parameters.setPreviewSize(176,144);
        camera.setParameters(parameters);
        try
        {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //

        float[] distances = new float[3];
        parameters.getFocusDistances(distances);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.e(getClass().getSimpleName(), "surfaceChanged");
        if (isPreviewRunning)
        {
            camera.stopPreview();
        }
        Camera.Parameters p = camera.getParameters();
        //String focusMode = null;
        //focusMode = findSettableValue(p.getSupportedFocusModes(),Camera.Parameters.FOCUS_MODE_MACRO,"edof");
        p.setPreviewSize(w, h);
        camera.setParameters(p);
        try
        {
            camera.setPreviewDisplay(holder);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
        isPreviewRunning = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.e(getClass().getSimpleName(), "surfaceDestroyed");
        camera.stopPreview();
        isPreviewRunning = false;
        camera.release();
    }


}
