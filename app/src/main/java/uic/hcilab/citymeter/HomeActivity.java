package uic.hcilab.citymeter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

import uic.hcilab.citymeter.DB.SensingDBHelper;
import uic.hcilab.citymeter.Sensing.SensingService;

public class HomeActivity extends TabHost {

    private BluetoothAdapter mBluetoothAdapter;
    private  Boolean permissions_granted = false;
    public static SensingDBHelper sensingDBHelper;

    private View view;
    @Override
    public int getContentViewId() {
        return R.layout.activity_home;
    }
    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.baseline_location_on_black_18dp);  //your icon
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x =  myToolbar.getNavigationIcon().toString();
                toggleLocation();
            }
        });
        //For no keyboard showing on starting the activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sensingDBHelper = new SensingDBHelper(this);
        //Permissions handling
        String[] permissions = new String[3]; //Increase size to add more permissions
        permissions[0] = Manifest.permission.RECORD_AUDIO;
        permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;//Add more permissions
        permissions[2] = Manifest.permission.ACCESS_FINE_LOCATION;//Add more permissions
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) //Check new permission here
        {
            Log.i("BT" , "no permissions");
            ActivityCompat.requestPermissions(this, permissions, 9);
        }
        //BT adapter initialization
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!checkBTEnabled()){
            BTEnable(savedInstanceState);
        }
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("nina", "AWSMobileClient is instantiated and connected to AWS");
            }
        }).execute();

        Button xposure_btn = (Button)findViewById(R.id.home_xposure_btn);
        final Intent xp_intent = new Intent(this, XposureActivity.class);
        xposure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(xp_intent);
            }
        });

        Button hereNow_btn = (Button)findViewById(R.id.home_hereNow_btn);
        final Intent hn_intent = new Intent(this, HereNowActivity.class);
        hereNow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(hn_intent);
            }
        });

        Button step_btn = (Button)findViewById(R.id.home_step_btn);
        final Intent st_intent = new Intent(this, StepsActivity.class);
        step_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(st_intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show thea app settings UI...
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 9: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissions_granted = true;
                }
            }
        }
    }

    public void toggleLocation(){

    }
    //Function to check if BT is enabled only
    public Boolean checkBTEnabled() {//change name
        if (mBluetoothAdapter == null) {
            Log.i("BT", "Device is not supported by Bluetooth");
            return false;
        } else if (mBluetoothAdapter.isEnabled() && permissions_granted) {
                Intent svcIntent = new Intent(this, SensingService.class);
                startService(svcIntent);
            return true;
        }
        return false;
    }
    //Enables bluetooth
    public void BTEnable(Bundle mBundle){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 7;
            ActivityCompat.startActivityForResult(this, enableBTIntent, REQUEST_ENABLE_BT, mBundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 7:
                Log.i("BT", "enable bt");
                if (mBluetoothAdapter.isEnabled()) {
                    startService(new Intent(this, SensingService.class));
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mBluetoothController.destroyBTController();
    }
}