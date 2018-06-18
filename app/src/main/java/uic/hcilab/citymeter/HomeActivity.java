package uic.hcilab.citymeter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uic.hcilab.citymeter.NoiseDetector;

public class HomeActivity extends TabHost {

    //private NoiseDetector noiseDetector = new NoiseDetector();
    private ProgressBar noiseBar;
    private BluetoothController mBluetoothController;
    private View view;
    //private SensorsDataHandler sensorsDataHandler;
    @Override
    public int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To be removed: just for preview\
        noiseBar = (ProgressBar) findViewById(R.id.noiseBar);
        //End of to be removed
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(myToolbar);
        //sensorsDataHandler = new SensorsDataHandler(this, savedInstanceState);
        //Permissions handling
        String [] permissions = new String[1];
        permissions[0] = Manifest.permission.RECORD_AUDIO;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 9);
        }

        else{
            Thread thread = new Thread(new Runnable() {//To run the noise detector in the background
                @Override
                public void run() {
                    //noiseDetector.noiseDetect();
                    while (true) {
                        /*double dBval = noiseDetector.noiseLevel();
                        Log.i("Recorder", (int)dBval + "");
                        if (dBval > 0 && dBval <= 5000) {
                            noiseBar.setProgress((int) dBval);
                        }*/
                    }
                }
            });
            thread.start();
        }
        //Create a bluetooth controller
        mBluetoothController= new BluetoothController(this, savedInstanceState);
        mBluetoothController.connectDevice();

        //get View for change bar
        final View view = findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                ///Bar
                //PM2.5
                float pm25_bar_loc;
                int pm25_bar_width;
                float pm25_range = 100f; //max - min //ToDo: Toy data
                float pm25_value = 60f;              //ToDo: Toy data
                ImageView pm25_bar = (ImageView) findViewById(R.id.pm25_bar);
                ImageView pm25_thumb = (ImageView) findViewById(R.id.pm25_thumb);
                TextView pm25_thumb_value = (TextView) findViewById(R.id.pm25_value);
                pm25_bar_width = pm25_bar.getWidth();
                pm25_bar_loc=pm25_bar.getX();
                pm25_thumb.setX(pm25_bar_loc+ pm25_bar_width*(pm25_value/pm25_range)-(pm25_thumb.getWidth()/2));
                pm25_thumb_value.setX(pm25_thumb.getX()+pm25_thumb.getWidth());
                pm25_thumb_value.setText(pm25_value+"");

                //PM2.5
                float noise_bar_loc;
                int noise_bar_width;
                float noise_range = 100f; //max - min //ToDo: Toy data
                float noise_value = 20f;              //ToDo: Toy data
                ImageView noise_bar = (ImageView) findViewById(R.id.noise_bar);
                ImageView noise_thumb = (ImageView) findViewById(R.id.noise_thumb);
                TextView noise_thumb_value = (TextView) findViewById(R.id.noise_value);
                noise_bar_width = noise_bar.getWidth();
                noise_bar_loc=noise_bar.getX();
                noise_thumb.setX(noise_bar_loc+ noise_bar_width*(noise_value/noise_range)-(noise_thumb.getWidth()/2));
                noise_thumb_value.setX(noise_thumb.getX()+noise_thumb.getWidth());
                noise_thumb_value.setText(noise_value+"");
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 9: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Thread thread = new Thread(new Runnable() {//To run the noise detector in the background
                        @Override
                        public void run() {
                            //noiseDetector.noiseDetect();
                            while (true) {
                                /*double dBval = noiseDetector.noiseLevel();
                                Log.i("Recorder", (int)dBval + "");
                                if (dBval > 0 && dBval <= 5000) {
                                    noiseBar.setProgress((int) dBval);
                                }*/
                            }
                        }
                    });
                    thread.start();
                }
            }
            return;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //noiseDetector.stop();
    }
}
