package com.thiennm77.diceroller;

import android.app.Activity;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends Activity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private Matrix matrix = new Matrix();


    int[] diceId = { R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
            R.drawable.dice4, R.drawable.dice5, R.drawable.dice6 };

    private ImageView diceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        diceImage = (ImageView) findViewById(R.id.dice);
        diceImage.setImageMatrix(matrix);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(double force) {
                handleShakeEvent(force);
            }
        });
    }

    private void handleShakeEvent(double force) {
        new RollDice().execute(force);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private class RollDice extends AsyncTask<Double, Integer, Void> {

        private Random random = new Random();

        @Override
        protected Void doInBackground(Double... doubles) {
            int times = (int) Math.floor(doubles[0]) * 3;
            int interval =  ShakeDetector.SHAKE_SLOP_TIME_MS / times;

            for (int i = 0; i < times; i++) {
                try {
                    publishProgress(random.nextInt(6), random.nextInt(360) - 180);
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            diceImage.setImageResource(diceId[values[0]]);
            matrix.postRotate(values[1], diceImage.getDrawable().getBounds().width()/2,
                    diceImage.getDrawable().getBounds().height()/2);
            diceImage.setImageMatrix(matrix);
        }
    }

}
