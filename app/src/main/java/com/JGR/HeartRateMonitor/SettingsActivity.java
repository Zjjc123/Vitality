package com.JGR.HeartRateMonitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    String mTitle[] = {
            "Show Steps"
    };

    static Switch stepsSwitch;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listView = findViewById(R.id.settings_list);
        MyAdapter adapter = new MyAdapter(this, mTitle);
        listView.setAdapter(adapter);

        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle("Notification Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createNotificationChannel();

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

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.settings_row, parent, false);
            TextView myTitle = row.findViewById(R.id.settings_header);
            myTitle.setText(rTitle[position]);

            if (position == 0) {
                final SharedPreferences sharedPref = getSharedPreferences("notifications", Context.MODE_PRIVATE);
                stepsSwitch = row.findViewById(R.id.settings_switch);
                stepsSwitch.setChecked(sharedPref.getBoolean("stepsSwitch", false));
                if (stepsSwitch.isChecked())
                    displayStepsNotif();

                final SharedPreferences.Editor editor = sharedPref.edit();

                stepsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (stepsSwitch.getTag() != null) {
                            stepsSwitch.setTag(null);
                            return;
                        }

                        System.out.println("stepSwitch.isChecked(): " + stepsSwitch.isChecked());
                        if (!stepsSwitch.isChecked()) {
                            //stepsSwitch.setChecked(false);
                            editor.putBoolean("stepsSwitch", false);
                            clearStepsNotif();
                            System.out.println(false);
                        } else {
                            //stepsSwitch.setChecked(true);
                            editor.putBoolean("stepsSwitch", true);
                            displayStepsNotif();
                            System.out.println(true);

                        }
                        editor.apply();
                    }
                });
            }

            return row;
        }
    }

    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;
    private void displayStepsNotif(){
        System.out.println("displayStepsNotif");
        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int numSteps = settings.getInt("numSteps", 0);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(this, "stepsNotif");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(numSteps+" steps");
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(resultPendingIntent);

        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(001,builder.build());
    }
    private void clearStepsNotif(){
        System.out.println("clearStepsNotif");
        if (builder != null && notificationManagerCompat != null) {
            builder.setOngoing(false);

            notificationManagerCompat.cancel(001);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("stepsNotif", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
