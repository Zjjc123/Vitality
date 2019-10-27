package com.JGR.HeartRateMonitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.JGR.HeartRateMonitor.BMIActivity;
import com.JGR.HeartRateMonitor.MainActivity;
import com.JGR.HeartRateMonitor.PushupActivity;
import com.JGR.HeartRateMonitor.R;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    ListView listView;
    String mTitle[] = {
            "Pushup Counter",
            "BMI Calculator",
            "Test",
            "Test",
            "Test",
            "Test",
            "Test"
    };
    String mDescription[] = {
            "Place your phone directly below your chest and perform pushups, your phone will count and display the pushups.",
            "Calculates your Body Mass Index (BMI) using the values in your profile",
            "Test",
            "Test",
            "Test",
            "Test",
            "Test"
    };
    int images[] = {
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_notifications_black_24dp
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
            if (position ==  0) {
                //Toast.makeText(c, "Test Description", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), PushupActivity.class));
            } else if (position == 1) {
                startActivity(new Intent(getActivity(), BMIActivity.class));
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

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.category_row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);

            return row;
        }
    }

}

