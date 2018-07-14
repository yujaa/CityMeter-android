package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uic.hcilab.citymeter.DB.SensingDBHelper;
import uic.hcilab.citymeter.Helpers.CaretakersRecyclerViewAdapter;
import uic.hcilab.citymeter.voronoi.Line;

public class DetailCareTakerActivity extends AppCompatActivity implements OnMapReadyCallback {
    String cuid;
    boolean canLocation = false;
    boolean canActivities = false;
    boolean canCogTest = false;
    GoogleMap googleMap;
    double longitude;
    double latitude;
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
            try {
                location.setVisibility(View.VISIBLE);
                SensingDBHelper sensingDBHelper = new SensingDBHelper(DetailCareTakerActivity.this);
                longitude = sensingDBHelper.getLatestLocation(cuid).get("longitude");
                latitude = sensingDBHelper.getLatestLocation(cuid).get("latitude");
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.lastLocationMapView);
                mapFragment.getMapAsync(DetailCareTakerActivity.this);
            }
            catch (Exception e){
                Toast.makeText(DetailCareTakerActivity.this, "Failed to get last known location", Toast.LENGTH_SHORT).show();
                Log.i("lastLoc", "Error : " + e.toString());
            }
        }
        else{
            location.setVisibility(View.GONE);
        }
        LinearLayout activities = (LinearLayout) findViewById(R.id.activityLayout);
        if(canActivities){
            activities.setVisibility(View.VISIBLE);
            Button xposure = (Button) findViewById(R.id.activitiesCaretakerButton);
            xposure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), CaretakersXposureActivity.class);
                    intent.putExtra("CARETAKER_ID", cuid);
                    startActivity(intent);
                }
            });
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

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMinZoomPreference(10);

    }
}
