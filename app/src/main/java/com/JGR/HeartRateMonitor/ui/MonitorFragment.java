package com.JGR.HeartRateMonitor.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.JGR.HeartRateMonitor.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MonitorFragment extends Fragment {

    private final String TAG = "HeartRateMonitor";
    private final AtomicBoolean processing = new AtomicBoolean(false);

    private  SurfaceView preview = null;
    private  SurfaceHolder previewHolder = null;
    private  Camera camera = null;
    private  TextView heartRateText = null;
    private  TextView statusText = null;
    private  ImageView heartImg = null;
    private  SharedPreferences sharedPref;

    private  int averageIndex = 0;
    private  final int averageArraySize = 4;
    private  final int[] averageArray = new int[averageArraySize];

    public enum TYPE {
        GREEN, RED
    }

    private static MonitorFragment.TYPE currentType = MonitorFragment.TYPE.GREEN;

    public static MonitorFragment.TYPE getCurrent() {
        return currentType;
    }

    private  int beatsIndex = 0;
    private  final int beatsArraySize = 3;
    private  final int[] beatsArray = new int[beatsArraySize];
    private  double beats = 0;
    private  long startTime = 0;


    private  boolean fingerOn = false;
    private  int updateTime = 3;
    private  boolean initialScan = false;


    private  LineChart mChart;
    private  int chartIndex = 0;

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NewApi")

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_monitor, container, false);

        preview = root.findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        heartRateText = root.findViewById(R.id.heartRateText);
        statusText = root.findViewById(R.id.statusText);
        heartImg = root.findViewById(R.id.image);

        mChart = root.findViewById(R.id.chart);
        LineData data = new LineData();
        mChart.setData(data);

        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        return root;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        //preview.setVisibility(View.VISIBLE);

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;

        //preview.setVisibility(View.GONE);
    }

    private  void ResetData()
    {
        for (int i = 0; i < averageArray.length; i ++)
        {
            averageArray[i] = 0;
        }
        System.out.println();

        for (int i = 0; i < beatsArray.length; i ++)
        {
            beatsArray[i] = 0;
        }
        startTime = System.currentTimeMillis();
        beatsIndex = 0;
        averageIndex = 0;
    }

    private  Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            // Checking if camera has been initialized
            try {
                cam.getParameters();

            } catch (Exception e){
                return;
            }
            if (data == null)
                throw new NullPointerException();

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            if (size == null)
                throw new NullPointerException();

            if (!processing.compareAndSet(false, true))
                return;

            int width = size.width;
            int height = size.height;

            int imgAvg = com.JGR.HeartRateMonitor.ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            //System.out.println("imgAvg=" + imgAvg);

            // If red average is less than 200 --> assume finger is not on
            if (imgAvg < 200) {
                fingerOn = false;
                initialScan = false;

                statusText.setText("Place finger on camera!");
                mChart.setVisibility(View.INVISIBLE);
                heartRateText.setText("0");
                heartImg.setImageResource(R.drawable.heart_icon_off);

                // If finger is not on Camera --> reset previous data
                ResetData();
                processing.set(false);
                return;
            } else {
                fingerOn = true;

                // If finger is just placed on --> set text to 'Scanning...'
                if (!initialScan) {
                    statusText.setText("Scanning...");
                    mChart.setVisibility(View.INVISIBLE);
                }

                int averageArrayAvg = 0;
                int averageArrayCnt = 0;

                // Rolling average --> every number above 200 is counted and averaged to determine the average
                for (int i = 0; i < averageArray.length; i++) {
                    if (averageArray[i] > 200) {
                        averageArrayAvg += averageArray[i];
                        averageArrayCnt++;
                    }
                }
                int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;


                LineData chartData = mChart.getData();

                if (chartData != null) {

                    ILineDataSet set = chartData.getDataSetByIndex(0);
                    // set.addEntry(...); // can be called as well

                    if (set == null) {
                        set = createSet();
                        chartData.addDataSet(set);
                    }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
                    chartData.addEntry(new Entry(set.getEntryCount(), rollingAverage), 0);
                    chartData.notifyDataChanged();

                    // let the chart know it's data has changed
                    mChart.notifyDataSetChanged();

                    // limit the number of visible entries
                    mChart.setVisibleXRangeMaximum(50);
                    // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                    // move to the latest entry
                    mChart.moveViewToX(chartData.getEntryCount());

                }


                // If the new image is less than the rolling average --> BEAT
                MonitorFragment.TYPE newType = currentType;
                if (imgAvg < rollingAverage) {
                    newType = MonitorFragment.TYPE.RED;
                    if (newType != currentType) {
                        beats++;
                        // Log.d(TAG, "BEAT!! beats="+beats);
                    }
                    // If not then did not beat
                } else if (imgAvg > rollingAverage) {
                    newType = MonitorFragment.TYPE.GREEN;
                }


                // If average index is at 4 --> start from 0
                if (averageIndex == averageArraySize)
                    averageIndex = 0;

                averageArray[averageIndex] = imgAvg;
                averageIndex++;

                // Transitioned from one state to another to the same
                if (newType != currentType) {
                    currentType = newType;
                }

                long endTime = System.currentTimeMillis();
                double totalTimeInSecs = (endTime - startTime) / 1000d;
                // Every update
                if (totalTimeInSecs >= updateTime) {
                    if (!initialScan)
                        initialScan = true;
                    double bps = (beats / totalTimeInSecs);

                    // Calculate beats per minute
                    int dpm = (int) (bps * 60d);

                    // If bpm is less than 30 or greater than 180 skip
                    if (dpm < 30 || dpm > 180) {
                        startTime = System.currentTimeMillis();
                        beats = 0;
                        processing.set(false);
                        return;
                    }

                    // Log.d(TAG,
                    // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);


                    // if = 3, reset
                    if (beatsIndex == beatsArraySize)
                        beatsIndex = 0;

                    // Stores past calculated bpm
                    beatsArray[beatsIndex] = dpm;
                    beatsIndex++;

                    int beatsArrayAvg = 0;
                    int beatsArrayCnt = 0;

                    for (int i = 0; i < beatsArray.length; i++) {
                        if (beatsArray[i] > 0) {
                            beatsArrayAvg += beatsArray[i];
                            beatsArrayCnt++;
                        }
                    }
                    int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                    // Calculate the bpm of past 3 bpm

                    heartRateText.setText(String.valueOf(beatsAvg));
                    showTarget();
                    mChart.setVisibility(View.VISIBLE);
                    startTime = System.currentTimeMillis();
                    beats = 0;
                }
        }


            if (MonitorFragment.getCurrent() == MonitorFragment.TYPE.GREEN)
            {
                heartImg.setImageResource(R.drawable.heart_icon_off);
            }
            else
                {
                heartImg.setImageResource(R.drawable.heart_icon_on);
            }


            camera.setParameters(parameters);
            processing.set(false);
        }
    };

    private  SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (camera == null){
                camera = Camera.open();
                camera.setDisplayOrientation(90);

                startTime = System.currentTimeMillis();
            }
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                System.out.println("Exception in setPreviewDisplay() " + t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private  Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    private  LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(5f);
        set.setColor(Color.BLACK);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private  void showTarget() {
        sharedPref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int hr_low = sharedPref.getInt("lowHR", 0);
        int hr_high = sharedPref.getInt("highHR", 0);
        int age_val = sharedPref.getInt("age", 0);

        if (!(hr_low == 0) && !(hr_high == 0)) {
            statusText.setText("Target Heart Rate During Exercise\n" + hr_low + " - " + hr_high);
        } else if(!(age_val == 0)){
            hr_low = (220 - age_val - 50);
            hr_high = (220 - age_val - 20);
            statusText.setText("Target Heart Rate During Exercise\n" + hr_low + " - " + hr_high);
        } else {
            statusText.setText("Scanning...");
        }

    }

}
