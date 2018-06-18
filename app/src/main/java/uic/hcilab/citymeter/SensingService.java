package uic.hcilab.citymeter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileDescriptor;

public class SensingService extends Service {
    public SensingController sensingController;


    public SensingService() {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensingController = new SensingController();
        dbThread.start();
        pmThread.start();
        serverThread.start();

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbThread.interrupt();
        pmThread.interrupt();
        serverThread.interrupt();
    }

    Thread dbThread = new Thread(new Runnable() {

        public void run() {
            try {

                sensingController.locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                int db_counter = 0;
                boolean db_flag = false;
                //Log.i("BT", "1 1");
                //Enable noise detector
                sensingController.noiseDetector.start();

                //Log.i("BT", "1 2");
                //Get noise levels readings
                while(true) {

                    //Log.i("BT", "1 3");
                    while (sensingController.noiseDetector.isRecording()) {
                        if (db_counter <= 59) {
                            sensingController.dBs[db_counter] = sensingController.noiseDetector.noiseLevel(sensingController.longitude, sensingController.latitude);
                            db_counter = db_counter + 1;
                            db_flag = false;
                        } else {
                            db_counter = 0;
                            sensingController.dBs[db_counter] = sensingController.noiseDetector.noiseLevel(sensingController.longitude, sensingController.latitude);
                            db_counter = db_counter + 1;
                            db_flag = true;
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

                sensingController.locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                int pm_counter = 0;
                boolean pm_flag = false;

                Log.i("BT", "2 1");
                sensingController.BTSetup();
                //Connect BT
                if (!sensingController.BTIsConnected())
                    sensingController.BTConnect();

                Log.i("BT", "2 2");

                while(true) {

                    Log.i("BT", "2 3");
                    //Read BT
                    while (sensingController.BTIsConnected()) {
                        if (pm_counter <= 59) {
                            sensingController.PMs[pm_counter] = sensingController.BTRead();
                            pm_counter = pm_counter + 1;
                            pm_flag = false;
                        } else {
                            pm_counter = 0;
                            sensingController.PMs[pm_counter] = sensingController.BTRead();
                            pm_counter = pm_counter + 1;
                            pm_flag = true;
                        }
                    }
                }
            }catch (Exception e){
                Log.i("BT", "BT Thread Error: " + e.toString());
            }
        }
    });
    Thread serverThread = new Thread(new Runnable() {
        public void run() {
            try {

                sensingController.locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                int pm_counter = 0;
                int db_counter = 0;

                Log.i("BT", "3 1");
                sensingController.serverConnect();

                Log.i("BT", "3 2");
                while (true) {

                    Log.i("BT", "3 3");
                    while (sensingController.serverIsConnected()) {
                        if (pm_counter >= 59) {
                            sensingController.serverWrite(sensingController.PMs[pm_counter]);
                            //mBluetoothController.PMs[pm_counter]= null;
                            pm_counter = pm_counter + 1;
                        } else {
                            pm_counter = 0;
                            sensingController.serverWrite(sensingController.PMs[pm_counter]);
                            //mBluetoothController.PMs[pm_counter]= null;
                            pm_counter = pm_counter + 1;
                        }
                        if (db_counter >= 59) {
                            sensingController.serverWrite(sensingController.dBs[db_counter]);
                            //mBluetoothController.PMs[pm_counter]= null;
                            db_counter = db_counter + 1;
                        } else {
                            db_counter = 0;
                            sensingController.serverWrite(sensingController.dBs[db_counter]);
                            //mBluetoothController.PMs[pm_counter]= null;
                            db_counter = db_counter + 1;
                        }
                    }
                    if(!sensingController.serverIsConnected()){
                        sensingController.serverConnect();
                    }
                }
            }
            catch (Exception e) {
                Log.i("BT", "Server Thread Error: " + e.toString());
            }
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
