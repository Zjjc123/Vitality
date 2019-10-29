package com.JGR.HeartRateMonitor;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TargetHRActivity extends AppCompatActivity {

    Toolbar toolbar;

    private TextView tv_target;
    private View view;
    private int bgColor = Color.DKGRAY;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_targethr);

        view = this.getWindow().getDecorView();
        view.setBackgroundColor(bgColor);

        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle("Target Heart Rate");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_target = findViewById(R.id.tv_target_low);
        int[] HR = calcHR();

        if (HR == null){
            tv_target.setText("Please Set Profile");
            Toast.makeText(this, "Enter Your personal information in the profile tab",
                    Toast.LENGTH_SHORT).show();
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