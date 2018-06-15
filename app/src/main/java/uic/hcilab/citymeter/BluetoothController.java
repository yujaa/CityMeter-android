package uic.hcilab.citymeter;

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
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

//Needs to be generalized: this code only works for the particular Raspberry Pi 3 we have
//Look into low energy bluetooth communication (LE BT)
public class BluetoothController {
    Activity mActivity;
    Bundle mBundle;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    final int REQUEST_ENABLE_BT = 7;

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
                Log.i("BT","Found device " + deviceName  + " address " + deviceMACAddress);
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
        Log.i("BT", "Looking Up devices..");
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
                    isPiPaired = true;
                }
                Log.i("BT", "paired devices " + device);
            }
        }
        //For discovering all other devices
        // If discovery is running, stop it
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

            ParcelUuid[] mpUUID = device.getUuids();
            //Log.i("BT", mpUUID.toString());
            String str = "00000003-0000-1000-8000-00805F9B34FB";
                    //mpUUID[6].getUuid().toString();//"00000000-0000-1000-8000-00805F9B34FB";

            UUID myUUID = UUID.fromString(str);

            Log.i("BT", myUUID + " " + device.getBluetoothClass() );
            BluetoothSocket mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
            BluetoothDevice remote = mBluetoothSocket.getRemoteDevice();
            mBluetoothAdapter.cancelDiscovery();
            try {
                mBluetoothSocket.connect();
                Log.i("BT", "is connected " + mBluetoothSocket.isConnected());
                byte []readLine = new byte[100];
                while (true) {
                    readLine = new byte[100];
                    mBluetoothSocket.getInputStream().read(readLine);
                    String msginfo = new String(readLine, "UTF-8");
                    Log.i("BT", "write x = " + msginfo);
                }

            } catch (IOException connectException) {
                Log.i("BT", " " + mBluetoothSocket.getRemoteDevice());
                Log.i("BT", connectException.getMessage());
            }
            Log.i("BT", "connected to pi " + remote + " " );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //To finalize
    public void destroyBTReciever() {
        mActivity.unregisterReceiver(mReceiver);
    }

}
