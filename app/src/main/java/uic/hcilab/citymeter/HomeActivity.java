package uic.hcilab.citymeter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uic.hcilab.citymeter.NoiseDetector;

public class HomeActivity extends TabHost {

    private BluetoothController mBluetoothController;

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
