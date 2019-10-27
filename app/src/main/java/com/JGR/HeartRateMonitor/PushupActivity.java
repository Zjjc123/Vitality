package com.JGR.HeartRateMonitor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

public class PushupActivity extends AppCompatActivity implements SensorEventListener {

    Toolbar toolbar;

    private TextView textView;
    private SensorManager sensorManager;
    private Sensor sensor;

    private View view;

    private TextView totalText;
    private TextView counter;

    private float downValue = 2.0f;

    private int count = 0;

    private int total;
    SharedPreferences totalCount;

    private boolean downState = false;

    private int downColor = Color.GREEN;
    private int upColor = Color.DKGRAY;

    public static SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup);

        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle("Push Up Counter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = getSharedPreferences("settings",0);

        counter = findViewById(R.id.counter);
        totalText = findViewById(R.id.total);


        view = this.getWindow().getDecorView();
        view.setBackgroundColor(upColor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        total = totalCount.getInt("pushUps", total);

        counter.setText(Integer.toString(count));
        totalText.setText("Daily: " + Integer.toString(total));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            if(event.values[0] < downValue){
                pushUpDown();
            } else {
                pushUpUp();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }


    public void pushUpDown(){
        if (!downState){
            downState = true;
            view.setBackgroundColor(downColor);

            count++;
            counter.setText(Integer.toString(count));

            total++;
            SharedPreferences.Editor editor = totalCount.edit();
            editor.putInt("pushUps", total);
            editor.apply();

            totalText.setText("Total " + Integer.toString(total));

        }
    }

    public void pushUpUp(){
        if (downState){
            downState = false;
            view.setBackgroundColor(upColor);
        }
    }

}
