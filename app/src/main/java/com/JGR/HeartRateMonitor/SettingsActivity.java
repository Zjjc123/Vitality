package com.JGR.HeartRateMonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    String mTitle[] = {
            "Pushups",
            "Steps",
            "Completions"
    };

    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_settings, null, false);
        listView = view.findViewById(R.id.settings_list);
        ((ViewGroup)listView.getParent()).removeView(listView);
        final Context c = this;

        MyAdapter adapter = new MyAdapter(c, mTitle);
        listView.setAdapter(adapter);

        super.onCreate(savedInstanceState);

        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(listView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];

        MyAdapter(Context c, String title[]) {
            super(c, R.layout.settings_row, R.id.settings_header, title);
            this.context = c;
            this.rTitle = title;
        }

        View row;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.settings_row, parent, false);
            TextView myTitle = row.findViewById(R.id.settings_header);

            myTitle.setText(rTitle[position]);

            return row;
        }
    }

}
