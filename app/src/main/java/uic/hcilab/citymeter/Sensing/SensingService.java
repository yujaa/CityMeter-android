package uic.hcilab.citymeter.Sensing;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import uic.hcilab.citymeter.DB.ExposureObject;
import uic.hcilab.citymeter.DB.SensingDBHelper;
import uic.hcilab.citymeter.Helpers.LogInHelper;
import uic.hcilab.citymeter.HomeActivity;

//Be careful with the variable when the available data is less than the buffer size

public class SensingService extends Service {
    public SensingController sensingController;
    ActivityManager activityManager;//To check if app is open
    ComponentName componentName;//To check if app is open
    SensingDBHelper sensingDBHelper;
    String id;
    public SensingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensingController = new SensingController();
        sensingDBHelper = HomeActivity.sensingDBHelper;
        id = LogInHelper.getCurrUser();
        dbThread.start();
        pmThread.start();
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
                    setLocation();
                    //Get noise levels readings
                    while (true) {
                        while (sensingController.noiseDetector.isRecording() && isOnline()) {
                            if (checkAppOpen()) {
                                ExposureObject dat = sensingController.noiseDetector.noiseLevel(sensingController.longitude, sensingController.latitude);
                                String timestamp_ = dat.timestamp;
                                final Double dbA_ = dat.reading;
                                Double longitude_ = dat.longitude;
                                Double latitude_ = dat.latitude;
                                Double indoor_ = dat.indoor;
                                if (dbA_ > 0) {
                                    sensingDBHelper.createExposureInst_dBA(id, timestamp_, dbA_, longitude_, latitude_, indoor_);

                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SensingService.this, "dB(A) = " + String.valueOf(dbA_), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                stopSelf();
                                onDestroy();
                                break;
                            }
                            Thread.sleep(5000);
                        }
                        if (!sensingController.noiseDetector.isRecording()) {
                            sensingController.noiseDetector.stop();
                            sensingController.noiseDetector.start();
                        }
                    }
                } catch (Exception e) {
                    Log.i("snsThrd", "dB Thread Error: " + e.toString());
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
            //Read BT
            while (sensingController.BTIsConnected() && isOnline()) {
                ExposureObject dat = sensingController.BTRead();
                String timestamp_ = dat.timestamp;
                final Double pm_ = dat.reading;
                Double longitude_ = dat.longitude;
                Double latitude_ = dat.latitude;
                Double indoor_ = dat.indoor;
                sensingDBHelper.createExposureInst_pm(id, timestamp_, pm_, longitude_, latitude_, indoor_);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SensingService.this, "PM 2.5 = " + String.valueOf(pm_), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (!sensingController.BTIsConnected()) {
                sensingController.BTDisable();
                sensingController.BTSetup();
                sensingController.BTConnect();
            }


        } catch (Exception e) {
            Log.i("snsThrd", "BT Handler Exception: " + e.toString());
        }
    }

    Thread pmThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    setLocation();
                    pmThread_helper();
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Log.i("snsThrd", "BT Thread Error: " + e.toString());
                }
            }
        }
    });

    private void setLocation(){
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        // Now get the Looper from the HandlerThread
        // NOTE: This call will block until the HandlerThread gets control and initializes its Looper
        Looper looper = handlerThread.getLooper();
        // Request location updates to be called back on the HandlerThread
        Criteria mFineCriteria = new Criteria();
        mFineCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mFineCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        mFineCriteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        mFineCriteria.setBearingRequired(true);
        String provider = sensingController.locationManager.getBestProvider(mFineCriteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        sensingController.locationManager.requestLocationUpdates(provider, 5000, 10, sensingController.locationListener, looper);

    }

    public boolean isOnline() {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
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