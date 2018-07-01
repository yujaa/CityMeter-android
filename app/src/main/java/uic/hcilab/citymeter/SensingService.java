package uic.hcilab.citymeter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

import java.util.Dictionary;



//Be careful with the variable when the available data is less than the buffer size

public class SensingService extends Service {
    public SensingController sensingController;
    ActivityManager activityManager;//To check if app is open
    ComponentName componentName;//To check if app is open
    SensingDBHelper sensingDBHelper;

    public SensingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensingController = new SensingController();
        sensingDBHelper = HomeActivity.sensingDBHelper;
        dbThread.start();
        pmThread.start();
        //serverThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    Thread dbThread = new Thread(new Runnable() {

        public void run() {
            try {
                sensingController.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                sensingController.location_setup();

                //Enable noise detector
                sensingController.noiseDetector.start();
                //Get noise levels readings
                while (true) {
                    while (sensingController.noiseDetector.isRecording()) {
                        if (checkAppOpen()) {
                            ExposureObject dat = sensingController.noiseDetector.noiseLevel(sensingController.longitude, sensingController.latitude);
                            String timestamp_ = dat.timestamp;
                            Double dbA_ = dat.reading;
                            Double longitude_ = dat.longitude;
                            Double latitude_ = dat.latitude;
                            sensingDBHelper.createExposureInst_dBA("1", timestamp_,dbA_,longitude_,latitude_);
                        }
                        else {
                            stopSelf();
                            onDestroy();
                            break;
                        }
                        Log.i("nina", "Waiting..");
                        //wait(50);
                        Thread.sleep(5000);
                        Log.i("nina", "After Wait..");
                    }
                    if (!sensingController.noiseDetector.isRecording()) {

                        sensingController.noiseDetector.start();
                    }
                }
            } catch (Exception e) {
                Log.i("BT", "dB Thread Error: " + e.toString());
            }
        }
    });

    Thread pmThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                sensingController.BTSetup();
                //Connect BT
                if (!sensingController.BTIsConnected()) {
                    sensingController.BTConnect();
                }

                while (true) {
                    //Read BT
                    while (sensingController.BTIsConnected()) {
                        ExposureObject dat =sensingController.BTRead();

                        String timestamp_ = dat.timestamp;
                        Double pm_ = dat.reading;
                        Double longitude_ = dat.longitude;
                        Double latitude_ = dat.latitude;
                        sensingDBHelper.createExposureInst_pm("1", timestamp_, pm_, longitude_, latitude_ );
                    }
                    if (!sensingController.BTIsConnected()) {
                        sensingController.BTDisable();
                        sensingController.BTSetup();
                        sensingController.BTConnect();
                    }
                }
            } catch (Exception e) {
                Log.i("BT", "BT Thread Error: " + e.toString());
            }
        }
    });
    /*Thread serverThread = new Thread(new Runnable() {
        public void run() {
            try {
                // sensingController.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                int pm_counter = 0;
                int db_counter = 0;

                while (true) {
                    sensingController.serverConnect();

                    while (sensingController.serverIsConnected()) {
                        String entry = sensingDBHelper.readNext();
                        sensingController.serverWrite(entry);
                        sensingDBHelper.updateDBRow(entry);
                        sensingDBHelper.deleteDBRow(entry);
                        Log.i("BT", "Write to server successful");
                    }
                    if (!sensingController.serverIsConnected()) {
                        sensingController.serverConnect();
                    }
                }
            } catch (Exception e) {
                Log.i("BT", "Server Thread Error: " + e.toString());
            }
        }
    });*/

    //Function to check if the app is open and active or not
    private boolean checkAppOpen(){
        activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        componentName = activityManager.getRunningTasks(1).get(0).topActivity;
        boolean result = false;
        if (componentName.flattenToShortString().indexOf("uic.hcilab.citymeter/.") != -1)
        {
            result = true;
        }
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //sensingDBHelper.close();
        dbThread.interrupt();
        pmThread.interrupt();
        //serverThread.interrupt();
        sensingController.destructor();
    }
}