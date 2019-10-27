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

public class BMIActivity extends AppCompatActivity {

    Toolbar toolbar;

    private TextView tv_bmi;
    private TextView tv_health;
    private TextView tv_profile;

    private View view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        view = this.getWindow().getDecorView();

        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle("BMI Calculator");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_bmi = findViewById(R.id.tv_bmi);
        tv_health = findViewById(R.id.tv_health);

        tv_bmi.setText(String.format("%.1f", calcBMI()));

        if (calcBMI() == 0) {
            tv_bmi.setText("Please Set Profile");
            tv_bmi.setTextColor(Color.parseColor("#000000"));
            tv_bmi.setTextSize(40);
            tv_health.setText("");
            Toast.makeText(this, "Enter Your personal information in the profile tab",
                    Toast.LENGTH_SHORT).show();
        } else if (calcBMI() < 18.5) {
            tv_health.setText("Underweight");
            view.setBackgroundColor(Color.parseColor("#65d3db"));
        } else if (calcBMI() < 24.9) {
            tv_health.setText("Healthy");
            view.setBackgroundColor(Color.parseColor("#64de6e"));
        } else if (calcBMI() < 29.9) {
            tv_health.setText("Overweight");
            view.setBackgroundColor(Color.parseColor("#d99e4c"));
        } else if (calcBMI() < 34.9) {
            tv_health.setText("Obese");
            view.setBackgroundColor(Color.parseColor("#d94c4c"));
        } else {
            tv_health.setText("Extremely Obese");
            view.setBackgroundColor(Color.parseColor("#d47fcf"));
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

    public double calcBMI() {

        SharedPreferences sharedPref = getSharedPreferences("settings", MODE_PRIVATE);

        int weight_val = sharedPref.getInt("weight", 0);
        int height_val = sharedPref.getInt("height", 0);
        int age_val = sharedPref.getInt("age", 0);

        if (!(weight_val == 0) && !(height_val == 0) && !(age_val == 0)) {
            return (703.0*weight_val)/(height_val*height_val);
        } else {
            return 0;
        }
    }

}
