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

    private NoiseDetector noiseDetector = new NoiseDetector();
    private ProgressBar noiseBar;
    private BluetoothController mBluetoothController;
    private SensorsDataHandler sensorsDataHandler;
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
                    noiseDetector.noiseDetect();
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
        mBluetoothController.checkBTEnabled();
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
                            noiseDetector.noiseDetect();
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
        noiseDetector.stop();
    }
}
