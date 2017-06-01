package com.example.dawidmichalowicz.fitshaker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.calTV)
    TextView calCounter;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private static final int SHAKE_SLOP_TIME_MS = 300;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    long shakeTimeStamp;
    int shakesCounter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y  +z - last_x - last_y - last_z) / diffTime * 10000;
                Log.d("Speed",String.valueOf(speed));
                final long now = System.currentTimeMillis();
                if(shakeTimeStamp + SHAKE_SLOP_TIME_MS > now){
                    Log.d("Shakestamp>now", "true");
                    return;
                }
                Log.d("Shakestamp>now", "false");

                if(shakeTimeStamp + SHAKE_COUNT_RESET_TIME_MS < now){
                    shakesCounter = 0;
                    Log.d("shakesCounter", "set 0");
                    calCounter.setText(String.valueOf(shakesCounter));
                }

                if (speed > SHAKE_THRESHOLD) {
                    onShake();
                    last_x = x;
                    last_y = y;
                    last_z = z;
                    shakeTimeStamp = now;
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @OnClick(R.id.start_button)
    public void startCounting(View view){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @OnClick(R.id.stop_button)
    public void stopCounting(View view){
        sensorManager.unregisterListener(this);
    }

    private void onShake() {
        shakesCounter++;
        Log.d("ShakesCounter", "Incremented");
        calCounter.setText(String.valueOf(shakesCounter));

    }
}
