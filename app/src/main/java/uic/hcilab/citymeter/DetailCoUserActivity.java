package uic.hcilab.citymeter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import uic.hcilab.citymeter.DB.CoUserDBHelper;
import uic.hcilab.citymeter.DB.CousersDO;

//TODO: update user ID

public class DetailCoUserActivity extends TabHost {
    String cuid;
    CoUserDBHelper coUserDBHelper;
    CousersDO cousersDO;
    Boolean isTrue = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_co_user);
        cuid = getIntent().getStringExtra("COUSER_ID");
        Toolbar couserToolbar = (Toolbar) findViewById(R.id.editcotoolbar);
        setSupportActionBar(couserToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(cuid);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        couserToolbar.setNavigationIcon(R.drawable.ic_back);  //your icon
        couserToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
                DetailCoUserActivity.this.finish();
            }
        });
        coUserDBHelper = new CoUserDBHelper(this);
        coUserDBHelper.getCoUser("1"/*not co user ID*/, cuid);
        while (! coUserDBHelper.isDone){

        }
        coUserDBHelper.isDone = false;
        cousersDO = coUserDBHelper.coUsers.get(0);

        final Switch locationSwitch = (Switch) findViewById(R.id.locationSwitch);
        if (cousersDO.getCanSeeLocation() == 1.0){
            isTrue = true;
        }
        else{
            isTrue = false;
        }
        locationSwitch.setChecked(isTrue);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                double loc;
                if(cousersDO.getCanSeeLocation() == 1.0){
                    loc = 0.0;
                }
                else{
                    loc = 1.0;
                }
                cousersDO = coUserDBHelper.updateCoUser("1", cuid, loc,cousersDO.getCanSeeActivities(), cousersDO.getCanSeeCogTest());
            }
        });

        final Switch activitiesSwitch = (Switch) findViewById(R.id.activitiesSwitch);

        if (cousersDO.getCanSeeActivities() == 1.0){
            isTrue = true;
        }
        else{
            isTrue = false;
        }
        activitiesSwitch.setChecked(isTrue);
        activitiesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                double act;
                if(cousersDO.getCanSeeActivities() == 1.0){
                    act = 0.0;
                }
                else{
                    act = 1.0;
                }
                cousersDO = coUserDBHelper.updateCoUser("1", cuid, cousersDO.getCanSeeLocation(),act, cousersDO.getCanSeeCogTest());
            }
        });

        final Switch cognitiveDataSwitch = (Switch) findViewById(R.id.cogSwitch);

        if (cousersDO.getCanSeeCogTest() == 1.0){
            isTrue = true;
        }
        else{
            isTrue = false;
        }
        cognitiveDataSwitch.setChecked(isTrue);
        cognitiveDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                double cog;
                if(cousersDO.getCanSeeCogTest() == 1.0){
                    cog = 0.0;
                }
                else{
                    cog = 1.0;
                }
                cousersDO = coUserDBHelper.updateCoUser("1", cuid, cousersDO.getCanSeeLocation(),cousersDO.getCanSeeActivities(), cog);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.co_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Edit Name");
                alertDialog.setMessage("Enter Name");

                final EditText input = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(cuid);
                input.setSelection(input.getText().toString().length());
               alertDialog.setView(input);


                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                coUserDBHelper.deletecoUser(cousersDO);
                                while (!coUserDBHelper.isDone){
                                }
                                coUserDBHelper.isDone = false;
                                cousersDO.setCuid(input.getText().toString());
                                coUserDBHelper.createCoEntry("1", cousersDO.getCuid(), cousersDO.getCanSeeLocation(),
                                        cousersDO.getCanSeeActivities(),cousersDO.getCanSeeCogTest());
                                while (!coUserDBHelper.isDone){

                                }
                                coUserDBHelper.isDone = false;
                                getSupportActionBar().setTitle(cousersDO.getCuid());
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
                return true;
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                coUserDBHelper.deletecoUser(cousersDO);
                                while (!coUserDBHelper.isDone) {
                                }
                                coUserDBHelper.isDone = false;
                                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                                startActivity(intent);
                                DetailCoUserActivity.this.finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    @Override
    public int getContentViewId() {
        return R.layout.activity_detail_co_user;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }
}
