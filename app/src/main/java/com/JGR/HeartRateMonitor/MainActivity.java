package com.JGR.HeartRateMonitor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {


    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    SharedPreferences totalCount;

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    private static PowerManager.WakeLock wakeLock = null;

    @Override
    @SuppressLint("NewApi")

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_monitor, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "HeartRateMonitor:WakeLock");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);


        totalCount = getSharedPreferences("settings", MODE_PRIVATE);
        numSteps = totalCount.getInt("numSteps", 0);
        sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        Calendar cal = Calendar.getInstance();

        // Find new time
        int newYear = cal.get(Calendar.YEAR);
        int newMonth = cal.get(Calendar.MONTH) + 1;
        int newDay = cal.get(Calendar.DAY_OF_MONTH);

        System.out.println(newYear);
        System.out.println(newMonth);
        System.out.println(newDay);

        // Find old time

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        long milliTime = settings.getLong("time", 0);
        cal.setTimeInMillis(milliTime);

        int oldYear = cal.get(Calendar.YEAR);
        int oldMonth = cal.get(Calendar.MONTH) + 1;
        int oldDay = cal.get(Calendar.DAY_OF_MONTH);


        System.out.println(oldYear);
        System.out.println(oldMonth);
        System.out.println(oldDay);


        if (newYear != oldYear ||
                newMonth != oldMonth ||
                newDay != oldDay)
        {
            resetData();
        }

    }

    public void resetData()
    {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("numSteps", 0);
        editor.putInt("pushUps", 0);
        editor.apply();
    }

    @Override
    public void onStop ()
    {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("time", calendar.getTimeInMillis());

        System.out.println(calendar.getTimeInMillis());

        editor.apply();

        super.onStop();

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        System.out.println(numSteps);

        totalCount = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = totalCount.edit();
        editor.putInt("numSteps", numSteps);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
