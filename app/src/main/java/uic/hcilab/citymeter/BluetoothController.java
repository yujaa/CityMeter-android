package uic.hcilab.citymeter;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

//Needs to be generalized: this code only works for the particular Raspberry Pi 3 we have
//Look into low energy bluetooth communication (LE BT)
public class BluetoothController {
    Activity mActivity;
    Bundle mBundle;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    final int REQUEST_ENABLE_BT = 7;
    double longitude ;
    double latitude ;
    //Permissions handling
    String [] my_permissions = new String[2];
    /*my_permissions[0] = permission.ACCESS_COARSE_LOCATION;
    my_permissions[1] = permission.ACCESS_FINE_LOCATION;
    if (mActivity.checkSelfPermission(mActivity, permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, permissions, 7);
    }
    else{
        LocationManager lm = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude =  location.getLatitude();
        longitude = location.getLongitude();
    }*/



    // Create a BroadcastReceiver to handle bluetooth actions
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //For pairing
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int oldState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED && oldState == BluetoothDevice.BOND_BONDING) {
                    Log.i("BT", "device paired");
                } else if (state == BluetoothDevice.BOND_NONE && oldState == BluetoothDevice.BOND_BONDED){
                    Log.i("BT", "device unpaired");
                }
            }
            //For discovery
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i("BT", "discovery started");
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("BT", "discovery finished");
            }
            //On finding BT device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMACAddress = device.getAddress(); // MAC address
            }
        }
    };

    public BluetoothController(Activity activity, Bundle bundle ){
        mActivity = activity;
        mBundle = bundle;
    }

    //Function to check if BT is enabled and enables it if it is not enabled
    public void checkBTEnabled() {
        if (mBluetoothAdapter == null) {
            Log.i("BT", "Device is not supported by Bluetooth");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ActivityCompat.startActivityForResult(mActivity, enableBTIntent, REQUEST_ENABLE_BT, mBundle);
            }
        }
    }

    //Scan for devices
    public void lookupBTDevices() {
        boolean isPiPaired = false;

        // To broadcast when devices are discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(mReceiver, filter);

        // Set of the paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        //Check if there are any paired devices
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress()=="B8:27:EB:73:04:B1"){
                    isPiPaired = true;//Make sure the raspberry pi is paired, otherwise pair
                }
            }
        }
        //For discovering all other devices
        // If already discovering, stop
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Start discovery
        mBluetoothAdapter.startDiscovery();
        Log.i("BT", "started Discovery");

        if(!isPiPaired){
           pairDevice();
        }
    }

    //Pair a device
    public void pairDevice() {
        try {
            BluetoothDevice device= mBluetoothAdapter.getRemoteDevice("B8:27:EB:73:04:B1");
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> connectDevice(double longitude, double latitude) {
        checkBTEnabled();
        ArrayList<String> results = new ArrayList<String>();
        try {
            //This should be able to connect to any device not just ours
            BluetoothDevice device= mBluetoothAdapter.getRemoteDevice("B8:27:EB:73:04:B1");//create a device with the mac address of the Pi
            ParcelUuid[] mpUUID = device.getUuids();
            String uuid_str = "00000003-0000-1000-8000-00805F9B34FB";//The uuid for rfcomm, by bluetooth
            UUID myUUID = UUID.fromString(uuid_str);

            BluetoothSocket mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//socket to open connection
            BluetoothDevice remoteDevice = mBluetoothSocket.getRemoteDevice();//Getting the remote device
            mBluetoothAdapter.cancelDiscovery();//Make sure discovery is off for less battery consumption
            try {
                mBluetoothSocket.connect();
                Log.i("BT", "is connected " + mBluetoothSocket.isConnected());
                byte []readLine;
                if (mBluetoothSocket.isConnected()) {
                    while (true) {
                        readLine = new byte[100];
                        mBluetoothSocket.getInputStream().read(readLine);
                        String msgInfo = new String(readLine, "UTF-8");
                        Log.i("BT", "recieved = " + msgInfo);
                        int pm = pm_value(msgInfo);
                        String msg_timestamp = timestamp(msgInfo);
                        String result = "[{'latitude': " + latitude + ", 'pm2.5': " + pm + ", 'longitude': " + longitude + ", 'timestamp': '" + msg_timestamp + "'}]";
                        results.add(result);
                    }
                }
                else{
                    connectDevice(longitude,latitude);
                }
            } catch (IOException connectException) {
                Log.i("BT", connectException.getMessage());
                connectDevice(longitude,latitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
            connectDevice(longitude,latitude);
        }
        return results;
    }
    //To extract pm value from string
    private int pm_value (String line){
        int start_index = line.indexOf("pm2.5");
        int end_index1 = line.indexOf(',', start_index);
        int end_index2 = line.indexOf('}', start_index);
        int end_index;
        if (end_index1<end_index2)
            end_index = end_index1;
        else
            end_index=end_index2;
        String value_str  = line.substring(start_index+8, end_index);
        int value = Integer.valueOf(value_str);
        return value;
    }
    //to extract timestamp from string
    private String timestamp(String line){//[{'pm2.5': 10, 'timestamp': 'Jun 12 2018 17:51:51'}]
        int start_index = line.indexOf("timestamp");
        int end_index = line.indexOf('\'', start_index + 13);
        String value  = line.substring(start_index + 13, end_index);
        return value;
    }
    //To finalize
    public void destroyBTReciever() {
        mActivity.unregisterReceiver(mReceiver);
    }
}
