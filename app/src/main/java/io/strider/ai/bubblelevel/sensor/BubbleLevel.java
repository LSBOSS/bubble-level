package io.strider.ai.bubblelevel.sensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import io.strider.ai.bubblelevel.R;

/**
 * Created by Victor on 13/03/2019.
 */

public class BubbleLevel implements SensorEventListener{

    static private final String TAG = "BubbleLevel";
    static private final double GRAVITY = 9.81d;
    static private final double MIN_DEGREE = -30d;
    static private final double MAX_DEGREE = 30d;

    private Sensor                  sensor;
    private SensorManager           sensorManager;
    private TextView                textThetaX;
    private TextView                textThetaY;

    private Boolean enablePhoto;
    private double thetaX;
    private double thetaY;

    private double[] gravity = {0,0,0};


    public BubbleLevel(SensorManager sensorManager, Sensor sensor, Context ctx) {

        this.sensorManager = sensorManager;
        this.sensor = sensor;

        textThetaX =  (TextView) ((Activity) ctx).findViewById(R.id.thetaX);
        textThetaY =  (TextView) ((Activity) ctx).findViewById(R.id.thetaY);

        Log.i(TAG, "Initializing bubble level service.");

        enablePhoto = false;
        thetaX = 0d;
        thetaY = 0d;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        final double alpha = 0.9;

//        gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];

        double gx = sensorEvent.values[0] > GRAVITY ? GRAVITY : sensorEvent.values[0];
        double gy = sensorEvent.values[1] > GRAVITY ? GRAVITY : sensorEvent.values[1];
//        double gx = gravity[0] > GRAVITY ? GRAVITY : gravity[0];
//        double gy = gravity[1] > GRAVITY ? GRAVITY : gravity[1];

        gx = gx < -GRAVITY ? -GRAVITY : gx;
        gy = gy < -GRAVITY ? -GRAVITY : gy;

        thetaX = Math.toDegrees(Math.asin(gy/GRAVITY));
        thetaY = Math.toDegrees(Math.asin(gx/GRAVITY));

        textThetaX.setText(String.format("X = %f degrees",thetaX));
        textThetaY.setText(String.format("Y = %f degrees",thetaY));

//        Log.i(TAG, String.format("[ SENSOR ] gx = %f  gy = %f  /  theta_x = %f degrees   theta_y = %f degrees", gx, gy, thetaX, thetaY));


        if (thetaX >= MIN_DEGREE && thetaX <= MAX_DEGREE && thetaY >= MIN_DEGREE && thetaY <= MAX_DEGREE) {
            enablePhoto = true;
            textThetaX.setTextColor(Color.GREEN);
            textThetaY.setTextColor(Color.GREEN);

            Log.i(TAG, "Camera enabled");
        } else {
            enablePhoto = false;
            textThetaX.setTextColor(Color.RED);
            textThetaY.setTextColor(Color.RED);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public Boolean isCameraEnabled() {
        return enablePhoto;
    }

    public double[] getPhoneAngles() {
        double [] angles = {thetaX, thetaY};

        return angles;
    }

}