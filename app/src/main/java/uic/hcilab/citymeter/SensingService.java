package uic.hcilab.citymeter;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

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
                            Double indoor_ = dat.indoor;
                            if(dbA_ != -1.0) {
                                sensingDBHelper.createExposureInst_dBA("1", timestamp_, dbA_, longitude_, latitude_, indoor_);
                            }
                        }
                        else {
                            stopSelf();
                            onDestroy();
                            break;
                        }
                        Thread.sleep(5000);
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
 private void pmThread_helper() {
     try {
     //not looping to try to connect again
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
             Double indoor_ = dat.indoor;
             sensingDBHelper.createExposureInst_pm("1", timestamp_, pm_, longitude_, latitude_, indoor_ );
         }
         if (!sensingController.BTIsConnected()) {
             sensingController.BTDisable();
             sensingController.BTSetup();
             sensingController.BTConnect();
         }
     }}
     catch (Exception e){
         Log.e("BT", "BT Handler Exception: " + e.toString());
     }
 }
    Thread pmThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    pmThread_helper();
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Log.i("BT", "BT Thread Error: " + e.toString());
                }
            }
        }
    });

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
        dbThread.interrupt();
        pmThread.interrupt();
        sensingController.destructor();
    }
}