package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import uic.hcilab.citymeter.DB.SensingDBHelper;
import uic.hcilab.citymeter.voronoi.Line;

public class DetailCareTakerActivity extends AppCompatActivity {
    String cuid;
    boolean canLocation = false;
    boolean canActivities = false;
    boolean canCogTest = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_care_taker);
        cuid = getIntent().getStringExtra("CARETAKER_ID");
        canLocation = isActive(getIntent().getStringExtra("location"));
        canActivities = isActive(getIntent().getStringExtra("activities"));
        canCogTest = isActive(getIntent().getStringExtra("cogTest"));

        Toolbar couserToolbar = (Toolbar) findViewById(R.id.caretakerToolbar);
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
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                DetailCareTakerActivity.this.finish();
            }
        });

        LinearLayout location = (LinearLayout) findViewById(R.id.lastLocationLayout);
        if(canLocation){
            location.setVisibility(View.VISIBLE);
            SensingDBHelper sensingDBHelper = new SensingDBHelper(DetailCareTakerActivity.this);
            double longitude =  sensingDBHelper.getLatestLocation(cuid).get("longitude") ;
            double latitude =  sensingDBHelper.getLatestLocation(cuid).get("latitude") ;
        }
        else{
            location.setVisibility(View.GONE);
        }
        LinearLayout activities = (LinearLayout) findViewById(R.id.activityLayout);
        if(canActivities){
            activities.setVisibility(View.VISIBLE);
        }
        else{
            activities.setVisibility(View.GONE);
        }
        LinearLayout cogTest = (LinearLayout) findViewById(R.id.cognitiveTestLayout);
        if(canCogTest){
            cogTest.setVisibility(View.VISIBLE);
        }
        else{
            cogTest.setVisibility(View.GONE);
        }
    }
    Boolean isActive (String s){
        if(s.contains( "1.0")){
            Log.i("coco", s);
            return true;
        }
        else {
            return false;
        }
    }
}
