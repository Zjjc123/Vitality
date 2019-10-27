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
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

public class TargetHRActivity extends AppCompatActivity {

    Toolbar toolbar;

    private TextView tv_target;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_targethr);

        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle("Target Heart Rate");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_target = findViewById(R.id.tv_target);
        int[] HR = calcHR();

        if (HR == null){
            tv_target.setText("Please Set Profile");
        } else {
            tv_target.setText(HR[0] + " - " + HR[1] + " BPM");
        }

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
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    public int[] calcHR() {

        SharedPreferences sharedPref = getSharedPreferences("settings", MODE_PRIVATE);

        int age_val = sharedPref.getInt("age", 0);

        int[] HR= new int[2];
        HR[0] = (220 - age_val - 50);
        HR[1] = (220 - age_val - 20);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lowHR", HR[0]);
        editor.putInt("highHR", HR[1]);
        editor.apply();

        if (!(age_val == 0)) {
            return HR;
        } else {
            return null;
        }
    }

}