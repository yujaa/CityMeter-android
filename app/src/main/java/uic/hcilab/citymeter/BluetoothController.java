package uic.hcilab.citymeter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;


//Needs to be generalized: this code only works for the particular Raspberry Pi 3 we have
//Look into low energy bluetooth communication (LE BT)
public class BluetoothController {
    private Activity mActivity;
    private Bundle mBundle;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String result;
    private LocationManager locationManager;
    private InetAddress inetAddress;
    private Socket serverClientSocket;
    private OutputStream outputStream;
    private PrintWriter printWriter;

    private static final int SERVERPORT = 80;
    private static final String SERVER_IP = "ec2-34-229-219-45.compute-1.amazonaws.com";
    //Permissions handling
    /*String[] my_permissions = new String[2];
    my_permissions[0] = permission.ACCESS_COARSE_LOCATION;
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
    //Constructor
    BluetoothController(Activity activity, Bundle bundle) {
        mActivity = activity;
        mBundle = bundle;
        //location_setup();
    }

    //======================================================
    //==================Bluetooth handler===================
    //======================================================

    // Create a BroadcastReceiver to handle bluetooth actions
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //For pairing
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int oldState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED && oldState == BluetoothDevice.BOND_BONDING) {
                    Log.i("BT", "device paired");
                } else if (state == BluetoothDevice.BOND_NONE && oldState == BluetoothDevice.BOND_BONDED) {
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

    //Function to check if BT is enabled and enables it if it is not enabled
    public void checkBTEnabled() {
        if (mBluetoothAdapter == null) {
            Log.i("BT", "Device is not supported by Bluetooth");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                int REQUEST_ENABLE_BT = 7;
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
                if (device.getAddress().equals("B8:27:EB:73:04:B1")) {
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

        if (!isPiPaired) {
            pairDevice();
        }
    }

    //Pair a device
    private void pairDevice() {
        try {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("B8:27:EB:73:04:B1");
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectDevice() {
        checkBTEnabled();
        Thread thread = new Thread(new Runnable() {
            double longitude, latitude;
            NoiseDetector noiseDetector = new NoiseDetector();

            @Override
            public void run() {
                try {
                    //start recording
                    noiseDetector.noiseDetect();

                    //location setup
                    location_setup();//

                    //connect to server
                    inetAddress = InetAddress.getByName(SERVER_IP);
                    Log.i("BT", "address : " + inetAddress.getHostAddress() + "   1");
                    serverClientSocket = new Socket(inetAddress, SERVERPORT);
                    if (!serverClientSocket.isConnected()) {
                        Log.i("BT", "not connected to server 2");
                    } else {
                        Log.i("BT", "connected to server.. 2");


                        //connect to bluetooth
                        //This should be able to connect to any device not just ours
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("B8:27:EB:73:04:B1");//create a device with the mac address of the Pi
                        String uuid_str = "00000003-0000-1000-8000-00805F9B34FB";//The uuid for rfcomm, by bluetooth
                        UUID myUUID = UUID.fromString(uuid_str);
                        //Create Socket
                        BluetoothSocket mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//socket to open connection
                        mBluetoothAdapter.cancelDiscovery();//Make sure discovery is off for less battery consumption
                        try {
                            mBluetoothSocket.connect();
                            byte[] readLine;
                                while (true) {//needs to handle archive data for syncing
                                    readLine = new byte[100];
                                    if (mBluetoothSocket.isConnected()) {
                                        Log.i("BT", "Connected to bluetooth... 4");
                                    mBluetoothSocket.getInputStream().read(readLine);
                                    String msgInfo = new String(readLine, "UTF-8");
                                    Log.i("BT", "recieved = " + msgInfo + "  5");
                                    int pm = pm_value(msgInfo);
                                    String msg_timestamp = timestamp(msgInfo);
                                    result = "[{'latitude': " + latitude + ", 'pm2.5': " + pm + ", 'longitude': " + longitude + ", 'timestamp': '" + msg_timestamp + "'}]";
                                    outputStream = serverClientSocket.getOutputStream();
                                    printWriter = new PrintWriter(new BufferedWriter(
                                            new OutputStreamWriter(outputStream)),
                                            true);
                                    if (result.length() > 0) {
                                        SensorsDataHandler(result, printWriter);
                                    }
                                    } else {
                                        connectDevice();
                                    }
                                    String my_dB = noiseDetector.noiseLevel(longitude, latitude);
                                    if (my_dB.length() > 0) {
                                        SensorsDataHandler(my_dB, printWriter);
                                    }
                                    Log.i("BT", "data sent to server  6");
                                }
                        } catch (Exception e) {
                            Log.i("BT",e.toString() + "  e1");
                            e.printStackTrace();
                            connectDevice();
                        }
                    }
                } catch (IOException connectException) {
                    Log.i("BT", connectException.toString() + "  e2");
                    connectDevice();
                }
            }


            //Location handler
            private void location_setup() {
                locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
                try {
                    assert locationManager != null;
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    Log.i("BT", "location setup : " + longitude + " " + latitude);
                } catch (SecurityException exception) {
                    Log.i("BT", "location error");
                }
            }

            private final LocationListener locationListener = new LocationListener() {
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
        });

        thread.start();
    }

    //To extract pm value from string
    private int pm_value(String line) {
        int start_index = line.indexOf("pm2.5");
        int end_index1 = line.indexOf(',', start_index);
        int end_index2 = line.indexOf('}', start_index);
        int end_index = start_index + 8;
        if (end_index1 < end_index2 && end_index1 != -1) {
            end_index = end_index1;
        }
        else if (end_index2 < end_index1 && end_index2 != -1){
            end_index = end_index2;

        }
        Log.i("BT", "Line = " + line + "\nstart index = " + start_index + " end index = " + end_index  );
        String value_str = line.substring(start_index + 8, end_index);
        return Integer.valueOf(value_str);
    }

    //to extract timestamp from string
    private String timestamp(String line) {//[{'pm2.5': 10, 'timestamp': 'Jun 12 2018 17:51:51'}]
        int start_index = line.indexOf("timestamp");
        int end_index = line.indexOf('\'', start_index + 13);
        return line.substring(start_index + 13, end_index);
    }

    //To finalize
    private void destroyBTReciever() {
        mActivity.unregisterReceiver(mReceiver);
    }

    //======================================================
    //==================Server handler===================
    //======================================================

    private void SensorsDataHandler(String value, PrintWriter pw) {
        pw.println(value);
    }


}