package uic.hcilab.citymeter.Sensing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import uic.hcilab.citymeter.DB.ExposureObject;

//Needs to be generalized: this code only works for the particular Raspberry Pi 3 we have
//Look into low energy bluetooth communication (LE BT)
public class SensingController {
    //======================================================
    //Variables
    private BluetoothAdapter mBluetoothAdapter ;
    private BluetoothDevice bluetoothDevice;
    private UUID myUUID;
    private BluetoothSocket mBluetoothSocket;
    private byte[] readLine;

    double longitude;
    double latitude;

    public LocationManager locationManager;
    public NoiseDetector noiseDetector;

    //======================================================
    //Constructor
    SensingController() {
        noiseDetector = new NoiseDetector();
    }

    //Destructor
    public void destructor(){
        try {
            noiseDetector.stop();
        }catch(Exception e){
            Log.e("snsCtrl", "stop error");
        }
    }

    //======================================================
    //==================Bluetooth handler===================
    //======================================================



    //To finalize
    public void BTDisable() {
        try {
            mBluetoothAdapter.disable();
           // mActivity.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e("snsCtrl", "unregister error");
        }
    }

    public void BTSetup() throws IOException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = mBluetoothAdapter.getRemoteDevice("B8:27:EB:73:04:B1");//create a device with the mac address of the Pi
        String uuid_str = "00000003-0000-1000-8000-00805F9B34FB";//The uuid for rfcomm, by bluetooth
        myUUID = UUID.fromString(uuid_str);
        mBluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);//socket to open connection
    }

    public void BTConnect() throws IOException {
        mBluetoothAdapter.cancelDiscovery();//Make sure discovery is off for less battery consumption
        mBluetoothSocket.connect();
        BTSendTime();
    }

    public Boolean BTIsConnected(){
        return mBluetoothSocket.isConnected();
    }

    public ExposureObject BTRead() throws IOException {
        readLine = new byte[80];
        mBluetoothSocket.getInputStream().read(readLine);
        String msgInfo = new String(readLine, "UTF-8");
        if(msgInfo.indexOf('[') == 0 && msgInfo.indexOf('{') == 1) {
            Double pm = pm_value(msgInfo);
            Double indoor_ = get_indoor(msgInfo);
            ExposureObject result = new ExposureObject(timestamp(msgInfo), pm, longitude, latitude, indoor_);
            return result;
        }
        return null;
    }

    public void BTSendTime(){
        try{
        SimpleDateFormat s = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        mBluetoothSocket.getOutputStream().write(timestamp.getBytes());
        }
        catch (Exception e){
            Log.i("snsCtrl", "error sending time");
        }

    }

    // Create a BroadcastReceiver to handle bluetooth actions
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //For pairing
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int oldState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED && oldState == BluetoothDevice.BOND_BONDING) {
                    Log.i("snsCtrl", "device paired");
                } else if (state == BluetoothDevice.BOND_NONE && oldState == BluetoothDevice.BOND_BONDED) {
                    Log.i("snsCtrl", "device unpaired");
                }
            }
            //For discovery
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i("snsCtrl", "discovery started");
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("snsCtrl", "discovery finished");
            }
            //On finding BT device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMACAddress = device.getAddress(); // MAC address
            }
        }
    };

    //======================================================
    //==================Location handler====================
    //======================================================

    //Location setup
    public void location_setup() {
        try {
            assert locationManager != null;
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }catch (SecurityException exception){
            Log.i("snsCtrl", "location security error: " + exception.toString());

        }
        catch (Exception exception) {
            Log.i("snsCtrl", "location error " + exception.toString());
        }

    }

    public final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
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

    //======================================================
    //==================Formatting output===================
    //======================================================
    //To extract pm value from string
    private Double pm_value(String line) {
        int start_index = line.indexOf("pm2.5");
        int end_index1 = line.indexOf(',', start_index);
        int end_index2 = line.indexOf('}', start_index);
        int end_index = start_index + 8;
        if(end_index1 == -1){
            end_index =end_index2;
        }
        else if (end_index2 == -1){
            end_index = end_index1;
        }
        else if (end_index1 < end_index2 ) {
            end_index = end_index1;
        }
        else if (end_index2 < end_index1 ){
            end_index = end_index2;

        }
        String value_str = line.substring(start_index + 7, end_index);
        return Double.valueOf(value_str);
    }

    //to extract timestamp from string
    private String timestamp(String line) {//[{'pm2.5': 10, 'timestamp': 'Jun 12 2018 17:51:51'}]
        int start_index = line.indexOf("timestamp");
        int end_index = line.indexOf(',', start_index + 13);
        int end_index1 = line.indexOf(',', start_index);
        int end_index2 = line.indexOf('}', start_index);
        if(end_index1 == -1){
            end_index =end_index2;
        }
        else if (end_index2 == -1){
            end_index = end_index1;
        }
        else if (end_index1 < end_index2 ) {
            end_index = end_index1;
        }
        else if (end_index2 < end_index1 ){
            end_index = end_index2;

        }
        return line.substring(start_index + 12, end_index);
    }

    //to extract indoor flag from string
    private Double get_indoor(String line){
        int start_index = line.indexOf("indoor");
        int end_index1 = line.indexOf(',', start_index);
        int end_index2 = line.indexOf('}', start_index);
        int end_index = start_index + 9;
        if(end_index1 == -1){
            end_index =end_index2;
        }
        else if (end_index2 == -1){
            end_index = end_index1;
        }
        else if (end_index1 < end_index2 ) {
            end_index = end_index1;
        }
        else if (end_index2 < end_index1 ){
            end_index = end_index2;

        }
        String value_str = line.substring(start_index + 8, end_index);
        return Double.valueOf(value_str);
    }

    //======================================================
    //======================================================
    //======================================================
}