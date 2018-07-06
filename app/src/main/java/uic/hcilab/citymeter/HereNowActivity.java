package uic.hcilab.citymeter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class HereNowActivity extends TabHost implements OnMapReadyCallback, ApiCallback, LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    float curLat = 41.751142f;
    float curLng = -87.71299f;
    private GoogleMap mMap;

    HashMap<String, HashMap<String, String>> nodesInfo = new HashMap<String,  HashMap<String, String>>();
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

                new AoTData(HereNowActivity.this).execute("info/nodes");

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
                pm25_thumb.setX(pm25_bar_width - (pm25_bar_loc+ pm25_bar_width*(pm25_value/pm25_range)-(pm25_thumb.getWidth()/2)));
                pm25_thumb_value.setX(pm25_thumb.getX()+pm25_thumb.getWidth());
                pm25_thumb_value.setText(Math.round(pm25_value)+"");

                //noise
                float here_noise_bar_loc;
                int here_noise_bar_width;
                float here_noise_range = 100f; //max - min //ToDo: Toy data
                float here_noise_value = soundNowData;              //ToDo: Toy data
                ImageView here_noise_bar = (ImageView) findViewById(R.id.here_noise_bar);
                ImageView here_noise_thumb = (ImageView) findViewById(R.id.here_noise_thumb);
                TextView here_noise_thumb_value = (TextView) findViewById(R.id.here_noise_value);
                here_noise_bar_width = here_noise_bar.getWidth();
                here_noise_bar_loc=here_noise_bar.getX();
                here_noise_thumb.setX(here_noise_bar_width - (here_noise_bar_loc+ here_noise_bar_width*((here_noise_value-35)/here_noise_range)-(here_noise_thumb.getWidth()/2)));
                here_noise_thumb_value.setX(here_noise_thumb.getX()+here_noise_thumb.getWidth());
                here_noise_thumb_value.setText(Math.round(here_noise_value)+"");

            }
        });
    }

    @Override
    public void onApiCallback(JSONObject jsonData, String urlApi) {
        //Log.i("my", String.valueOf(jsonData));
        //PM2.5
        if(urlApi.equals("info/nodes")) {
            getNodesLocationData(jsonData);
            new AoTData(HereNowActivity.this).execute("value/nodes");
        }
        else if(urlApi.equals("value/node/pm25/001e06113107"))
            getPmData(jsonData);

        else if(urlApi.equals("value/nodes")) {
            getSoundData(jsonData);
            float pm25_bar_loc;
            int pm25_bar_width;
            float pm25_range = 700f; //max - min //ToDo: Toy data
            float pm25_value = pmNowData;              //ToDo: Toy data
            ImageView pm25_bar = (ImageView) findViewById(R.id.here_pm25_bar);
            ImageView pm25_thumb = (ImageView) findViewById(R.id.here_pm25_thumb);
            TextView pm25_thumb_value = (TextView) findViewById(R.id.here_pm25_value);
            pm25_bar_width = pm25_bar.getWidth();
            pm25_bar_loc = pm25_bar.getX();
            pm25_thumb.setX(pm25_bar_width - (pm25_bar_loc + pm25_bar_width * (pm25_value / pm25_range) - (pm25_thumb.getWidth() / 2)));
            pm25_thumb_value.setX(pm25_thumb.getX() + pm25_thumb.getWidth());
            pm25_thumb_value.setText(Math.round(pm25_value) + "");

            //noise
            float here_noise_bar_loc;
            int here_noise_bar_width;
            float here_noise_range = 100f; //max - min //ToDo: Toy data
            float here_noise_value = soundNowData;              //ToDo: Toy data
            ImageView here_noise_bar = (ImageView) findViewById(R.id.here_noise_bar);
            ImageView here_noise_thumb = (ImageView) findViewById(R.id.here_noise_thumb);
            TextView here_noise_thumb_value = (TextView) findViewById(R.id.here_noise_value);
            here_noise_bar_width = here_noise_bar.getWidth();
            here_noise_bar_loc = here_noise_bar.getX();
            here_noise_thumb.setX(here_noise_bar_width - (here_noise_bar_loc + here_noise_bar_width * ((here_noise_value - 35) / here_noise_range) - (here_noise_thumb.getWidth() / 2)));
            here_noise_thumb_value.setX(here_noise_thumb.getX() + here_noise_thumb.getWidth());
            here_noise_thumb_value.setText(Math.round(here_noise_value) + "");

            getNodesValueData(jsonData);
            drawNodesMarker(nodesInfo);
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

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(curLat, curLng))
                .title((String) "")
                .alpha(10.0f)
                .flat(true)
        );
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
        LatLng curLoc = new LatLng(curLat, curLng);
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


    public void getNodesLocationData(JSONObject nodesLocation){
        //parse json
        for(Object k: nodesLocation.keySet()){
            //Log.i("my1", k.toString());
            if(!k.toString().equals("total")){
                HashMap<String, String> node = new HashMap<String, String>();
                node.put("lat",((JSONObject)nodesLocation.get(k.toString())).get("lat").toString());
                node.put("lon",((JSONObject)nodesLocation.get(k.toString())).get("lon").toString());
                node.put("addr",((JSONObject)nodesLocation.get(k.toString())).get("address").toString());
                nodesInfo.put(k.toString(), node);
            }
        }

    }

    public void getNodesValueData(JSONObject nodesValue)
    {
        //parse json
        for(Object k: nodesValue.keySet()){

            HashMap<String, String> node = new HashMap<String, String>();
            if(!k.toString().equals("total")&&!k.toString().equals("node")&&!k.toString().equals("")){
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("timestamp"))
                    if(!((JSONObject)nodesValue.get(k.toString())).get("timestamp").toString().equals(""))
                        node.put("timeStamp",((JSONObject)nodesValue.get(k.toString())).get("timestamp").toString());
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("sound"))
                    if(!((JSONObject)nodesValue.get(k.toString())).get("sound").toString().equals(""))
                        node.put("sound",((JSONObject)nodesValue.get(k.toString())).get("sound").toString());
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("no2"))
                    if(!((JSONObject)nodesValue.get(k.toString())).get("no2").toString().equals(""))
                        node.put("no2",((JSONObject)nodesValue.get(k.toString())).get("no2").toString());
//                if(((JSONObject)nodesValue.get(k.toString())).containsKey("pm2_5"))
//                        node.put("pm2_5",((JSONObject)nodesValue.get(k.toString())).get("pm2_5").toString());
                if(nodesInfo.get(k.toString())!=null)
                    nodesInfo.get(k.toString()).putAll(node);
            }
        }
    }

    public void drawNodesMarker( HashMap<String, HashMap<String, String>> nodesInfoParam)
    {
        Set set = nodesInfoParam.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            double sound;
            int circleColor = Color.argb(100, 100, 100, 100);;

            Map.Entry entry = (Map.Entry) iterator.next();
            double lat = Double.parseDouble((((HashMap) entry.getValue()).get("lat")).toString());
            double lon = Double.parseDouble((((HashMap) entry.getValue()).get("lon")).toString());

            if(((HashMap) entry.getValue()).containsKey("sound")) {
                sound = Double.parseDouble((((HashMap) entry.getValue()).get("sound")).toString());
                if (sound < 50) circleColor = Color.argb(100,0,228,0);
                else if (sound < 65) circleColor = Color.argb(100,255,255,0);
                else if (sound < 80) circleColor = Color.argb(100,255,126,0);
                else if (sound < 95) circleColor = Color.argb(100,255,0,0);
                else if (sound < 110) circleColor = Color.argb(100,143,63,151);
                else circleColor = Color.argb(100,126,0,35);
            }
//            else
//                continue;

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(lat, lon))
                    .radius(100) // In meters
                    .strokeWidth(0)
                    .fillColor(circleColor)
                    .clickable(true);

            // Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);


//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(lat, lon))
//                    .title((String) entry.getKey())
//                    .alpha(5.0f)
//                    .flat(true)
//                    );
        }
    }

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
