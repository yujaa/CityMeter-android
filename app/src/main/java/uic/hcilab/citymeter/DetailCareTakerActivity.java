package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import uic.hcilab.citymeter.DB.CognitiveTestDBHelper;
import uic.hcilab.citymeter.DB.CousersDO;
import uic.hcilab.citymeter.DB.SensingDBHelper;
import uic.hcilab.citymeter.Helpers.CaretakersRecyclerViewAdapter;
import uic.hcilab.citymeter.Helpers.CognitiveTestRecyclerViewAdapter;
import uic.hcilab.citymeter.Helpers.LogInHelper;
import uic.hcilab.citymeter.voronoi.Line;

public class DetailCareTakerActivity extends AppCompatActivity implements OnMapReadyCallback, CognitiveTestRecyclerViewAdapter.ItemClickListener {
    String cuid;
    boolean canLocation = false;
    boolean canActivities = false;
    boolean canCogTest = false;
    GoogleMap googleMap;
    double longitude;
    double latitude;
    CognitiveTestRecyclerViewAdapter adapter;
    List<com.amazonaws.models.nosql.CognitiveTestDO> tests;
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
                sensingDBHelper.connect();
                longitude = sensingDBHelper.getLatestLocation(cuid).get("longitude");
                latitude = sensingDBHelper.getLatestLocation(cuid).get("latitude");
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.lastLocationMapView);
                mapFragment.getMapAsync(DetailCareTakerActivity.this);
            }
            catch (Exception e){
                Log.i("careTkr", "Error : " + e.toString());
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
            CognitiveTestDBHelper cognitiveTestDBHelper = new CognitiveTestDBHelper(DetailCareTakerActivity.this);
            cognitiveTestDBHelper.getAllTests(cuid);

           tests = cognitiveTestDBHelper.tests;

            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.cogTestRecyclerCaretaker);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CognitiveTestRecyclerViewAdapter(this, tests);
            adapter.setClickListener(DetailCareTakerActivity.this);
            recyclerView.setAdapter(adapter);
        }
        else{
            cogTest.setVisibility(View.GONE);
        }
    }
    Boolean isActive (String s){
        if(s.contains( "1.0")){
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

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), TestResultActivity.class);
        intent.putExtra("User", cuid);
        intent.putExtra("Time", adapter.getItem(position));
        intent.putExtra("memoryProblem", tests.get(position).getMemoryProblem());
        intent.putExtra("blood", tests.get(position).getBlood());
        intent.putExtra("balance", tests.get(position).getBalance());
        intent.putExtra("balanceCause", tests.get(position).getBalanceCause());
        intent.putExtra("stroke", tests.get(position).getMajorStroke());
        intent.putExtra("sad", tests.get(position).getSadDepressed());
        intent.putExtra("personality", tests.get(position).getPersonality());
        intent.putExtra("difficulty", tests.get(position).getDifficultyActivities());
        intent.putExtra("today", tests.get(position).getTodayDate());
        intent.putExtra("rhino", tests.get(position).getNamePictureRhino());
        intent.putExtra("harp", tests.get(position).getNamePictureHarp());
        intent.putExtra("tulip", tests.get(position).getTulip());
        intent.putExtra("quarters", tests.get(position).getQuarters());
        intent.putExtra("change", tests.get(position).getGroceriesChange());
        intent.putExtra("copy", tests.get(position).getCopyPictureFile());
        intent.putExtra("clock", tests.get(position).getDrawClockFile());
        intent.putExtra("countries", tests.get(position).getCountries12());
        intent.putExtra("circle", tests.get(position).getCircleLinesFile());
        intent.putExtra("tri", tests.get(position).getTrianglesFile());
        intent.putExtra("done", tests.get(position).getDone());
        startActivity(intent);

    }
}
