package uic.hcilab.citymeter;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class SensingService extends Service {
    public SensingController sensingController;

    public SensingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensingController = new SensingController();
        dbThread.start();
        pmThread.start();
        serverThread.start();
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

                int db_counter = 0;

                //Enable noise detector
                sensingController.noiseDetector.start();

                //Get noise levels readings
                while (true) {

                    //Log.i("BT", "1 3");
                    while (sensingController.noiseDetector.isRecording()) {
                        if (db_counter <= 59) {
                            sensingController.dBs[db_counter] = sensingController.noiseDetector.noiseLevel(sensingController.longitude, sensingController.latitude);
                            db_counter = db_counter + 1;
                        } else {
                            db_counter = 0;
                            sensingController.dBs[db_counter] = sensingController.noiseDetector.noiseLevel(sensingController.longitude, sensingController.latitude);
                            db_counter = db_counter + 1;
                        }
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
                //sensingController.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                int pm_counter = 0;

                Log.i("BT", "setup starting");
                sensingController.BTSetup();
                Log.i("BT", "setup successful");
                //Connect BT
                if (!sensingController.BTIsConnected()) {
                    Log.i("BT", "connecting bt");
                    sensingController.BTConnect();
                    Log.i("BT", "bt connected");
                }

                while (true) {
                    //Read BT
                    while (sensingController.BTIsConnected()) {
                        if (pm_counter <= 59) {
                            sensingController.PMs[pm_counter] = sensingController.BTRead();
                            pm_counter = pm_counter + 1;
                        } else {
                            pm_counter = 0;
                            sensingController.PMs[pm_counter] = sensingController.BTRead();
                            pm_counter = pm_counter + 1;
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("BT", "BT Thread Error: " + e.toString());
            }
        }
    });
    Thread serverThread = new Thread(new Runnable() {
        public void run() {
            try {
               // sensingController.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                int pm_counter = 0;
                int db_counter = 0;

                while (true) {
                    sensingController.serverConnect();

                    while (sensingController.serverIsConnected()) {
                        if (pm_counter >= 59) {
                            sensingController.serverWrite(sensingController.PMs[pm_counter]);
                            Log.i("BT" ,sensingController.PMs[pm_counter]);
                            pm_counter = pm_counter + 1;
                        } else {
                            pm_counter = 0;
                            sensingController.serverWrite(sensingController.PMs[pm_counter]);
                            pm_counter = pm_counter + 1;
                        }
                        if (db_counter >= 59) {
                            sensingController.serverWrite(sensingController.dBs[db_counter]);
                            db_counter = db_counter + 1;
                        } else {
                            db_counter = 0;
                            sensingController.serverWrite(sensingController.dBs[db_counter]);
                            db_counter = db_counter + 1;
                        }
                    }
                    if (!sensingController.serverIsConnected()) {
                        sensingController.serverConnect();
                    }
                }
            } catch (Exception e) {
                Log.i("BT", "Server Thread Error: " + e.toString());
            }
        }
    });

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
        serverThread.interrupt();
    }
}