package com.JGR.HeartRateMonitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.InstanceCountViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.JGR.HeartRateMonitor.BMIActivity;
import com.JGR.HeartRateMonitor.PushupActivity;
import com.JGR.HeartRateMonitor.R;
import com.JGR.HeartRateMonitor.SitUpActivity;
import com.JGR.HeartRateMonitor.StepsActivity;
import com.JGR.HeartRateMonitor.TargetHRActivity;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DashboardFragment extends Fragment {

    ListView listView;
    String mTitle[] = {
            "Basics",
            "BMI Calculator",
            "Target Heart Rate",
            "Counters",
            "Pushup Counter",
            "Step Counter",
            "Sit Up Counter"
    };
    String mDescription[] = {
            "",
            "Place your phone directly below your chest and perform pushups, your phone will count and display the number of pushups.",
            "Calculates your Body Mass Index (BMI) using the values in your profile.",
            "",
            "What your heart rate should be in order for your exercise to achieve maximum effectiveness.",
            "Counts your steps as you go throughout the day.",
            "Counts the number of sit ups you do. Please hold the screen face up flat on your chest"
    };
    int images[] = {
            0,
            R.drawable.ic_bmi,
            R.drawable.ic_target,
            0,
            R.drawable.ic_pushups,
            R.drawable.ic_steps,
            0
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        listView = view.findViewById(R.id.text_dashboard);
        ((ViewGroup)listView.getParent()).removeView(listView);
        final Context c = getContext();

        DashboardFragment.MyAdapter adapter = new DashboardFragment.MyAdapter(c, mTitle, mDescription, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  4) {
                    startActivity(new Intent(getActivity(), PushupActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(getActivity(), BMIActivity.class));
                } else if (position == 2) {
                    startActivity(new Intent(getActivity(), TargetHRActivity.class));
                } else if (position == 5) {
                    startActivity(new Intent(getActivity(), StepsActivity.class));
                } else if (position == 6)
                {
                    startActivity(new Intent(getActivity(), SitUpActivity.class));
                }
            }
        });

        return listView;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs[];

        MyAdapter(Context c, String title[], String description[], int imgs[]) {
            super(c, R.layout.category_row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
        }

        View row;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (position == 0 || position == 3){
                row = layoutInflater.inflate(R.layout.section_header, parent, false);
                TextView myTitle = row.findViewById(R.id.textView3);

                myTitle.setText(rTitle[position]);
            }
            else {
                row = layoutInflater.inflate(R.layout.category_row, parent, false);
                ImageView images = row.findViewById(R.id.image);
                TextView myTitle = row.findViewById(R.id.textView1);
                TextView myDescription = row.findViewById(R.id.textView2);

                images.setImageResource(rImgs[position]);
                myTitle.setText(rTitle[position]);
                myDescription.setText(rDescription[position]);
            }

            return row;
        }
    }

}

