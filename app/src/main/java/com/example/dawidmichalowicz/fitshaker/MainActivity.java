package com.example.dawidmichalowicz.fitshaker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.calTV)
    TextView calCounter;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    long shakeTimeStamp;
    int shakesCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        shakesCounter = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
                if (speed > SHAKE_THRESHOLD) {
                    onShake();
                }
                last_x = x;
                last_y = y;
                last_z = z;
                shakeTimeStamp = now;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void onShake() {
        shakesCounter++;
        Log.d("ShakesCounter", "Incremented");
        System.out.println(shakesCounter);
        calCounter.setText(String.valueOf(shakesCounter));

    }
}
