package uic.hcilab.citymeter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class SensorsDataHandler {
    Activity mActivity;
    double longitude;
    double latitude;
    BluetoothController bluetoothController;
    NoiseDetector noiseDetector;
    LocationManager locationManager;

    public SensorsDataHandler(Activity activity){
        mActivity = activity;
    }

    public void location_setup() {
        locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);

        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.i("BT", longitude + " " + latitude);
        } catch (SecurityException exception) {
            Log.i("DH", "location error");
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.i("BT", longitude + " " + latitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



}
