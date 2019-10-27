package com.JGR.HeartRateMonitor.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
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

public class MonitorFragment extends Fragment {

    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static TextView text = null;
    private static ImageView heartImg = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public enum TYPE {
        GREEN, RED
    }

    private static MonitorFragment.TYPE currentType = MonitorFragment.TYPE.GREEN;

    public static MonitorFragment.TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;


    private static boolean fingerOn = false;
    private static int updateTime = 3;
    private static boolean initialScan = false;

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

        text = root.findViewById(R.id.text);
        heartImg = root.findViewById(R.id.image);

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        //System.out.println("onResume");

        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        //System.out.println("onPause");
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private static void ResetData()
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

    private static Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
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

                text.setText("Place finger on camera!");
                heartImg.setImageResource(R.drawable.heart_icon_off);

                // If finger is not on Camera --> reset previous data
                ResetData();
                processing.set(false);
                return;
            } else {
                fingerOn = true;

                // If finger is just placed on --> set text to 'Scanning...'
                if (!initialScan) {
                    text.setText("Scanning...");
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

                    text.setText(String.valueOf(beatsAvg));
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

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

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

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
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
}
