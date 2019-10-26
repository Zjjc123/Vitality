package com.JGR.HeartRateMonitor.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        weight = root.findViewById(R.id.in_weight);
        height = root.findViewById(R.id.in_height);
        age = root.findViewById(R.id.in_age);

        disp_weight = root.findViewById(R.id.disp_weight);
        disp_height = root.findViewById(R.id.disp_height);
        disp_age = root.findViewById(R.id.disp_age);

        showValues();

        Button bt_calculate = root.findViewById(R.id.bt_calculate);

        tv_result = root.findViewById(R.id.tv_result);

        bt_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeValues();
            }
        });


        return root;
    }


    private EditText weight;
    private EditText height;
    private EditText age;

    SharedPreferences sharedPref;

    private TextView tv_result;
    private TextView disp_weight;
    private TextView disp_height;
    private TextView disp_age;


    private void storeValues() {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (!(weight.getText().toString().matches(""))) {
            int user_weight = Integer.valueOf(weight.getText().toString());
            editor.putInt("weight", user_weight);
        }

        if (!(height.getText().toString().matches(""))) {
            int user_height = Integer.valueOf(height.getText().toString());
            editor.putInt("height", user_height);
        }

        if (!(age.getText().toString().matches(""))) {
            int user_age = Integer.valueOf(age.getText().toString());
            editor.putInt("age", user_age);
        }

        editor.apply();

        // int restHR = sharedPref.getInt("rest_hr", 80);

//        double bmi = (703*user_weight)/(user_height*user_height);
//        double hrMax = 207-(0.7*user_age);
        // Do your calculation here.
        // I'm assuming you have inserted the result on a variable called 'result'. Like: double result

        tv_result.setText("Values Stored");

        showValues();


//        weight.setText(user_weight);
//        height.setText(user_height);
//        age.setText(user_age);

    }

    private void showValues() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int weight = sharedPref.getInt("weight", 0);
        int height = sharedPref.getInt("height", 0);
        int age = sharedPref.getInt("age", 0);



        disp_weight.setText(checkDefault(weight));
        disp_height.setText(checkDefault(height));
        disp_age.setText(checkDefault(age));

    }

    private String checkDefault(int key) {
        if (key == 0) {
            return "Please Enter Your Value Below";
        } else {
            return Integer.toString(key);
        }
    }

    // The rest of your Activity and methods.

}