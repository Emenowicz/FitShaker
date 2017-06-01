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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.calTV)
    TextView calTV;
    @BindView(R.id.timerTV)
    TextView timerTV;
    @BindView(R.id.start_button)
    Button startButton;


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private CaloriesCounter calCounter;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private static final int SHAKE_SLOP_TIME_MS = 300;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 2000;
    long shakeTimeStamp;
    int shakesCounter = 0;
    long startTime = 0;
    long time = 0;
    int weight = 70;
    boolean stopped = false;
    float cals;

    //Timer setup
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            time = millis;
            int seconds = (int) (millis / 1000);
            millis = (int) ((millis % 1000) / 10);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTV.setText(String.format(getString(R.string.time_format), minutes, seconds, millis));
            cals = calCounter.countCals(time, weight);
            calTV.setText(String.format(getString(R.string.cal_format), cals));
            timerHandler.postDelayed(this, 10);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        calCounter = new CaloriesCounter();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.saveAction:
                Toast.makeText(this, "Save selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.showSettingsAction:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;

    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        timerHandler.removeCallbacks(timerRunnable);
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

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                Log.d("Speed", String.valueOf(speed));
                final long now = System.currentTimeMillis();
                if (shakeTimeStamp + SHAKE_SLOP_TIME_MS > now) {
                    Log.d("Shakestamp>now", "true");
                    return;
                }
                Log.d("Shakestamp>now", "false");

                if (shakeTimeStamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    timerHandler.removeCallbacks(timerRunnable);
                    sensorManager.unregisterListener(this);
                    startButton.setClickable(true);
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
    public void startCounting(View view) {
        shakesCounter = 0;
        shakeTimeStamp = System.currentTimeMillis();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        startButton.setClickable(false);

    }

    @OnClick(R.id.stop_button)
    public void stopCounting(View view) {
        if (!stopped) {
            sensorManager.unregisterListener(this);
            timerHandler.removeCallbacks(timerRunnable);
            startButton.setClickable(true);
            stopped = true;
        }
        else {
            timerTV.setText(getString(R.string.default_timer));
            calTV.setText(getString(R.string.default_cal));
            stopped = false;
        }
    }

    private void onShake() {
        shakesCounter++;
    }
}
