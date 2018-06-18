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

    private BluetoothController mBluetoothController;

    private View view;
    @Override
    public int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(myToolbar);
        //Create a bluetooth controller
        mBluetoothController = new BluetoothController(HomeActivity.this, savedInstanceState);
        //Permissions handling
        String[] permissions = new String[1];
        permissions[0] = Manifest.permission.RECORD_AUDIO;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 9);
        } else{
            new Thread(new Runnable() {
                public void run() {
                    try {
                        int db_counter = 0;
                        boolean db_flag = false;
                        int pm_counter = 0;
                        boolean pm_flag = false;

                        //Enable noise detector
                        mBluetoothController.noiseDetector.start();

                        //Get noise levels readings
                        while (mBluetoothController.noiseDetector.isRecording()){
                            if (db_counter <= 59) {
                                mBluetoothController.dBs[db_counter] = mBluetoothController.noiseDetector.noiseLevel(mBluetoothController.longitude, mBluetoothController.latitude);
                                db_counter = db_counter + 1;
                                db_flag = false;
                            }
                            else {
                                db_counter = 0;
                                mBluetoothController.dBs[db_counter] = mBluetoothController.noiseDetector.noiseLevel(mBluetoothController.longitude, mBluetoothController.latitude);
                                db_counter = db_counter + 1;
                                db_flag = true;
                            }
                        }

                        //Enable BT
                        if (!mBluetoothController.checkBTEnabled()) {
                            mBluetoothController.BTEnable();
                        }
                        mBluetoothController.BTSetup();
                        //Connect BT
                        if (!mBluetoothController.BTIsConnected())
                            mBluetoothController.BTConnect();

                        //Read BT
                        while (mBluetoothController.BTIsConnected()){
                            if (pm_counter <= 59) {
                                mBluetoothController.PMs[pm_counter] = mBluetoothController.BTRead();
                                pm_counter = pm_counter + 1;
                                pm_flag = false;
                            }
                            else {
                                pm_counter = 0;
                                mBluetoothController.PMs[pm_counter] = mBluetoothController.BTRead();
                                pm_counter = pm_counter + 1;
                                pm_flag = true;
                            }
                        }

                    } catch (Exception e) {
                        Log.i("BT", "Data Thread Error: " + e.toString());
                    }
                }
            }).start();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        int pm_counter = 0;
                        int db_counter = 0;

                        mBluetoothController.serverConnect();

                        while (mBluetoothController.serverIsConnected()) {
                            if (pm_counter >= 59) {
                                mBluetoothController.serverWrite(mBluetoothController.PMs[pm_counter]);
                                //mBluetoothController.PMs[pm_counter]= null;
                                pm_counter = pm_counter + 1;
                            }
                            else{
                                pm_counter = 0;
                                mBluetoothController.serverWrite(mBluetoothController.PMs[pm_counter]);
                                //mBluetoothController.PMs[pm_counter]= null;
                                pm_counter = pm_counter + 1;
                            }
                            if (db_counter >= 59) {
                                mBluetoothController.serverWrite(mBluetoothController.dBs[db_counter]);
                                //mBluetoothController.PMs[pm_counter]= null;
                                db_counter = db_counter + 1;
                            }
                            else{
                                db_counter = 0;
                                mBluetoothController.serverWrite(mBluetoothController.dBs[db_counter]);
                                //mBluetoothController.PMs[pm_counter]= null;
                                db_counter = db_counter + 1;
                            }
                        }
                    }
                    catch (Exception e) {
                        Log.i("BT", "Server Thread Error: " + e.toString());
                    }
                }
            }).start();
        }

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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int db_counter = 0;
                                boolean db_flag = false;
                                int pm_counter = 0;
                                boolean pm_flag = false;

                                //Enable noise detector
                                mBluetoothController.noiseDetector.start();

                                //Get noise levels readings
                                while (mBluetoothController.noiseDetector.isRecording()){
                                    if (db_counter <= 59) {
                                        mBluetoothController.dBs[db_counter] = mBluetoothController.noiseDetector.noiseLevel(mBluetoothController.longitude, mBluetoothController.latitude);
                                        db_counter = db_counter + 1;
                                        db_flag = false;
                                    }
                                    else {
                                        db_counter = 0;
                                        mBluetoothController.dBs[db_counter] = mBluetoothController.noiseDetector.noiseLevel(mBluetoothController.longitude, mBluetoothController.latitude);
                                        db_counter = db_counter + 1;
                                        db_flag = true;
                                    }
                                }

                                //Enable BT
                                if (!mBluetoothController.checkBTEnabled()) {
                                    mBluetoothController.BTEnable();
                                }
                                mBluetoothController.BTSetup();
                                //Connect BT
                                if (!mBluetoothController.BTIsConnected())
                                    mBluetoothController.BTConnect();

                                //Read BT
                                while (mBluetoothController.BTIsConnected()){
                                    if (pm_counter <= 59) {
                                        mBluetoothController.PMs[pm_counter] = mBluetoothController.BTRead();
                                        pm_counter = pm_counter + 1;
                                        pm_flag = false;
                                    }
                                    else {
                                        pm_counter = 0;
                                        mBluetoothController.PMs[pm_counter] = mBluetoothController.BTRead();
                                        pm_counter = pm_counter + 1;
                                        pm_flag = true;
                                    }
                                }

                            } catch (Exception e) {
                                Log.i("BT", "Data Thread Error: " + e.toString());
                            }
                        }
                    }).start();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int pm_counter = 0;
                                int db_counter = 0;

                                mBluetoothController.serverConnect();

                                while (mBluetoothController.serverIsConnected()) {
                                    if (pm_counter >= 59) {
                                        mBluetoothController.serverWrite(mBluetoothController.PMs[pm_counter]);
                                        //mBluetoothController.PMs[pm_counter]= null;
                                        pm_counter = pm_counter + 1;
                                    }
                                    else{
                                        pm_counter = 0;
                                        mBluetoothController.serverWrite(mBluetoothController.PMs[pm_counter]);
                                        //mBluetoothController.PMs[pm_counter]= null;
                                        pm_counter = pm_counter + 1;
                                    }
                                    if (db_counter >= 59) {
                                        mBluetoothController.serverWrite(mBluetoothController.dBs[db_counter]);
                                        //mBluetoothController.PMs[pm_counter]= null;
                                        db_counter = db_counter + 1;
                                    }
                                    else{
                                        db_counter = 0;
                                        mBluetoothController.serverWrite(mBluetoothController.dBs[db_counter]);
                                        //mBluetoothController.PMs[pm_counter]= null;
                                        db_counter = db_counter + 1;
                                    }
                                }
                            }
                            catch (Exception e) {
                                Log.i("BT", "Server Thread Error: " + e.toString());
                            }
                        }
                    }).start();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mBluetoothController.destroyBTController();
    }
}
