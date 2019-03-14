package io.strider.ai.bubblelevel.sensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
//    private TextView                textThetaX;
//    private TextView                textThetaY;
    private TextView                userMessage;
    private ToneGenerator           toneGenerator;
    private Canvas                  canvasY;
    private Canvas                  canvasX;
    private Rect                    rectangleY;
    private Rect                    rectangleX;
    private Paint                   paintRectangle;
    private Bitmap                  bitmapY;
    private Bitmap                  bitmapX;
    private ImageView               mImageViewY;
    private ImageView               mImageViewX;
    private Paint                   paintLine;


    private Boolean enablePhoto;
    private Boolean tonePlayed;
    private double thetaX;
    private double thetaY;


    public BubbleLevel(SensorManager sensorManager, Sensor sensor, Context ctx) {

        this.sensorManager = sensorManager;
        this.sensor = sensor;

        mImageViewY = (ImageView) ((Activity) ctx).findViewById(R.id.iv);
        mImageViewX = (ImageView) ((Activity) ctx).findViewById(R.id.ix);

        userMessage = (TextView) ((Activity) ctx).findViewById(R.id.user_message);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        bitmapY = Bitmap.createBitmap(
                50, // Width
                200, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        bitmapX = Bitmap.createBitmap(
                200, // Width
                50, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        canvasY = new Canvas(bitmapY);
        canvasY.drawColor(Color.LTGRAY);

        canvasX = new Canvas(bitmapX);
        canvasY.drawColor(Color.LTGRAY);

        rectangleY = new Rect(0,0, canvasY.getWidth(), canvasY.getHeight());
        rectangleX = new Rect(0,0, canvasX.getWidth(), canvasX.getHeight());

        paintRectangle = new Paint();
        paintRectangle.setStyle(Paint.Style.FILL);
        paintRectangle.setColor(Color.YELLOW);
        paintRectangle.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setColor(Color.BLACK);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(2f);

        enablePhoto = false;
        tonePlayed = false;
        thetaX = 0d;
        thetaY = 0d;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double gx = sensorEvent.values[0] > GRAVITY ? GRAVITY : sensorEvent.values[0];
        double gy = sensorEvent.values[1] > GRAVITY ? GRAVITY : sensorEvent.values[1];

        gx = gx < -GRAVITY ? -GRAVITY : gx;
        gy = gy < -GRAVITY ? -GRAVITY : gy;

        thetaX = Math.toDegrees(Math.asin(gy/GRAVITY));
        thetaY = Math.toDegrees(Math.asin(gx/GRAVITY));

        canvasY.drawRect(rectangleY, paintRectangle);
        canvasX.drawRect(rectangleX, paintRectangle);

        // Draw Thresholds
        canvasY.drawLine(0, getLineLocation(30d), canvasY.getWidth(), getLineLocation(30d), paintLine);
        canvasY.drawLine(0, getLineLocation(-30d), canvasY.getWidth(), getLineLocation(-30d), paintLine);

        canvasX.drawLine(getLineLocation(30d), 0, getLineLocation(30d), canvasX.getHeight(), paintLine);
        canvasX.drawLine(getLineLocation(-30d), 0, getLineLocation(-30d), canvasX.getHeight(), paintLine);

        canvasY.drawLine(0, getLineLocation(thetaY), canvasY.getWidth(), getLineLocation(thetaY), paintLine);
        canvasX.drawLine(getLineLocation(thetaX), 0, getLineLocation(thetaX), canvasX.getHeight(), paintLine);


        mImageViewY.setImageBitmap(bitmapY);
        mImageViewX.setImageBitmap(bitmapX);

        if (thetaX >= MIN_DEGREE && thetaX <= MAX_DEGREE && thetaY >= MIN_DEGREE && thetaY <= MAX_DEGREE) {
            enablePhoto = true;
            userMessage.setBackgroundColor(Color.GREEN);
            userMessage.setText(R.string.photo_authorized);
            if (!tonePlayed) {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                tonePlayed = true;
            }


            Log.i(TAG, "Camera enabled");
        } else {
            enablePhoto = false;
            tonePlayed = false;
            userMessage.setBackgroundColor(Color.RED);

            if (thetaY > 0) {
                userMessage.setText(R.string.phone_up);
            } else {
                userMessage.setText(R.string.phone_down);
            }
        }

    }

    private int getLineLocation(double angle){
        Double value =  ( - angle + 90d) * 1.111d;
        return value.intValue();
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
