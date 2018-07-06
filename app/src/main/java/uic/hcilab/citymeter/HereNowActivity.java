package uic.hcilab.citymeter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import org.json.simple.JSONObject;

public class HereNowActivity extends TabHost implements OnMapReadyCallback, ApiCallback, LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String curLat = "41.751142";
    String curLng = "-87.71299";
    private GoogleMap mMap;
    PlaceAutocompleteFragment placeAutoComplete;

    float pmNowData = 0;
    float soundNowData = 0;
    @Override
    public int getContentViewId() {
        return R.layout.activity_here_now;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_here_now);
        setSupportActionBar(myToolbar);

        initMap();

        new AoTData(HereNowActivity.this).execute("value/node/pm25/001e06113107");
        new AoTData(HereNowActivity.this).execute("value/nodes");

        //get View for change bar
        final View view = findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Removing layout listener to avoid multiple calls
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                ///Bar
                //PM2.5
                float pm25_bar_loc;
                int pm25_bar_width;
                float pm25_range = 650f; //max - min //ToDo: Toy data
                float pm25_value = pmNowData;              //ToDo: Toy data
                ImageView pm25_bar = (ImageView) findViewById(R.id.here_pm25_bar);
                ImageView pm25_thumb = (ImageView) findViewById(R.id.here_pm25_thumb);
                TextView pm25_thumb_value = (TextView) findViewById(R.id.here_pm25_value);
                pm25_bar_width = pm25_bar.getWidth();
                pm25_bar_loc=pm25_bar.getX();
                pm25_thumb.setX(pm25_bar_loc+ pm25_bar_width*(pm25_value/pm25_range)-(pm25_thumb.getWidth()/2));
                pm25_thumb_value.setX(pm25_thumb.getX()+pm25_thumb.getWidth());
                pm25_thumb_value.setText(pm25_value+"");

                //day noise
                float day_noise_bar_loc;
                int day_noise_bar_width;
                float day_noise_range = 100f; //max - min //ToDo: Toy data
                float day_noise_value = soundNowData;              //ToDo: Toy data
                ImageView day_noise_bar = (ImageView) findViewById(R.id.here_noise_bar);
                ImageView day_noise_thumb = (ImageView) findViewById(R.id.here_noise_thumb);
                TextView day_noise_thumb_value = (TextView) findViewById(R.id.here_noise_value);
                day_noise_bar_width = day_noise_bar.getWidth();
                day_noise_bar_loc=day_noise_bar.getX();
                day_noise_thumb.setX(day_noise_bar_loc+ day_noise_bar_width*(day_noise_value/day_noise_range)-(day_noise_thumb.getWidth()/2));
                day_noise_thumb_value.setX(day_noise_thumb.getX()+day_noise_thumb.getWidth());
                day_noise_thumb_value.setText(day_noise_value+"");

            }
        });
    }

    @Override
    public void onApiCallback(JSONObject jsonData, String urlApi) {
        //Log.i("my", String.valueOf(jsonData));
        if (urlApi.equals("value/node/pm25/001e06113107")) {
            getPmData(jsonData);
            ///Bar
            //PM2.5
            float pm25_bar_loc;
            int pm25_bar_width;
            float pm25_range = 650f; //max - min //ToDo: Toy data
            float pm25_value = pmNowData;              //ToDo: Toy data
            ImageView pm25_bar = (ImageView) findViewById(R.id.here_pm25_bar);
            ImageView pm25_thumb = (ImageView) findViewById(R.id.here_pm25_thumb);
            TextView pm25_thumb_value = (TextView) findViewById(R.id.here_pm25_value);
            pm25_bar_width = pm25_bar.getWidth();
            pm25_bar_loc=pm25_bar.getX();
            pm25_thumb.setX(pm25_bar_loc+ pm25_bar_width*(pm25_value/pm25_range)-(pm25_thumb.getWidth()/2));
            pm25_thumb_value.setX(pm25_thumb.getX()+pm25_thumb.getWidth());
            pm25_thumb_value.setText(pm25_value+"");
        }
        if (urlApi.equals("value/nodes")) {
            getSoundData(jsonData);
            ///Bar
            //day noise
            float day_noise_bar_loc;
            int day_noise_bar_width;
            float day_noise_range = 100f; //max - min //ToDo: Toy data
            float day_noise_value = soundNowData;              //ToDo: Toy data
            ImageView day_noise_bar = (ImageView) findViewById(R.id.here_noise_bar);
            ImageView day_noise_thumb = (ImageView) findViewById(R.id.here_noise_thumb);
            TextView day_noise_thumb_value = (TextView) findViewById(R.id.here_noise_value);
            day_noise_bar_width = day_noise_bar.getWidth();
            day_noise_bar_loc=day_noise_bar.getX();
            day_noise_thumb.setX(day_noise_bar_loc+ day_noise_bar_width*(day_noise_value/day_noise_range)-(day_noise_thumb.getWidth()/2));
            day_noise_thumb_value.setX(day_noise_thumb.getX()+day_noise_thumb.getWidth());
            day_noise_thumb_value.setText(day_noise_value+"");
        }
    }

    private void getPmData(JSONObject nodeValue)
    {
        if(nodeValue.containsKey("001e06113107"))
            pmNowData = Float.parseFloat(nodeValue.get("001e06113107").toString());
    }

    private void getSoundData(JSONObject nodesValue)
    {
        for(Object k: nodesValue.keySet()){
            if(k.toString().equals("001e0610bbff")) {
                soundNowData = Float.parseFloat(((JSONObject)nodesValue.get(k.toString())).get("sound").toString());
            }
        }
    }

    public void initMap() {
//        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_search_bar);
//        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//
//                Log.d("Maps", "Place selected: " + place.getName());
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.d("Maps", "An error occurred: " + status);
//            }
//        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showDefaultLocation();
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(10);
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            //mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        LatLng curLoc = new LatLng(Double.parseDouble(curLat), Double.parseDouble(curLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));
        Log.i("my","here");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else
                    showDefaultLocation();
                return;
            }
        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(10);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                }
            };

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
