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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.JGR.HeartRateMonitor.R;

public class ProfileFragment extends Fragment {

    private EditText weight;
    private EditText height;
    private EditText age;
    private EditText name;

    SharedPreferences sharedPref;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        weight = root.findViewById(R.id.et_weight);
        height = root.findViewById(R.id.et_height);
        age = root.findViewById(R.id.et_age);
        name = root.findViewById(R.id.et_name);

//        disp_weight = root.findViewById(R.id.disp_weight);
//        disp_height = root.findViewById(R.id.disp_height);
//        disp_age = root.findViewById(R.id.disp_age);

        showValues();

        Button bt_update = root.findViewById(R.id.btn_update);

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(root.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                storeValues();
            }
        });

        return root;
    }

    private void storeValues() {

        sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
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

        if (!(name.getText().toString().matches(""))) {
            String user_name = name.getText().toString();
            editor.putString("name", user_name);
        }

        editor.apply();

        // int restHR = sharedPref.getInt("rest_hr", 80);

//        double bmi = (703*user_weight)/(user_height*user_height);
//        double hrMax = 207-(0.7*user_age);
        // Do your calculation here.
        // I'm assuming you have inserted the result on a variable called 'result'. Like: double result

        showValues();


//        weight.setText(user_weight);
//        height.setText(user_height);
//        age.setText(user_age);

    }

    private void showValues() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int weight_val = sharedPref.getInt("weight", 0);
        int height_val = sharedPref.getInt("height", 0);
        int age_val = sharedPref.getInt("age", 0);
        String name_val = sharedPref.getString("name", "");

        weight.setText(checkDefault(weight_val));
        height.setText(checkDefault(height_val));
        age.setText(checkDefault(age_val));
        name.setText(name_val);

//        disp_weight.setText(checkDefault(weight));
//        disp_height.setText(checkDefault(height));
//        disp_age.setText(checkDefault(age));

    }

    private String checkDefault(int key) {
        if (key == 0) {
            return "";
        } else {
            return Integer.toString(key);
        }
    }

    // The rest of your Activity and methods.

}