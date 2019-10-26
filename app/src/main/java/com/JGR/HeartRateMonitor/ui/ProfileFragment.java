package com.JGR.HeartRateMonitor.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.JGR.HeartRateMonitor.R;

public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        input1 = root.findViewById(R.id.in_weight);
        input2 = root.findViewById(R.id.in_height);
        input3 = root.findViewById(R.id.in_age);

        Button bt_calculate = root.findViewById(R.id.bt_calculate);

        tv_result = root.findViewById(R.id.tv_result);

        bt_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCalculations();
            }
        });

        return root;
    }


    private EditText input1;
    private EditText input2;
    private EditText input3;

    SharedPreferences sharedPref;

    private TextView tv_result;

    private void makeCalculations() {
        // I'm assuming you're getting numbers.
        double weight = Double.valueOf(input1.getText().toString());
        double height = Double.valueOf(input2.getText().toString());
        double age = Double.valueOf(input3.getText().toString());

        // int restHR = sharedPref.getInt("rest_hr", 80);

        double bmi = (703*weight)/(height*height);
        double hrMax = 207-(0.7*age);
        // Do your calculation here.
        // I'm assuming you have inserted the result on a variable called 'result'. Like: double result

        tv_result.setText("BMI: " + bmi);
    }

    // The rest of your Activity and methods.

}