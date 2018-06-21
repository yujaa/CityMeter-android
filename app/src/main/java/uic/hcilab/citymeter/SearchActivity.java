package uic.hcilab.citymeter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import voronoi.Pnt;
import voronoi.VoronoiLayer;

public class SearchActivity extends TabHost implements OnMapReadyCallback, ApiCallback{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private JSONObject nodesLocation = null;
    HashMap<String, HashMap<String, String>> nodesInfo = new HashMap<String,  HashMap<String, String>>();

    @Override
    public int getContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(myToolbar);

        initMap();

        new AoTData(SearchActivity.this).execute("info/nodes");

    }


    public void initMap() {
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
        mMap.setMinZoomPreference(11);
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
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        LatLng uic = new LatLng(41.8693, -87.6475);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uic));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(15);
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
    public void onApiCallback(JSONObject jsonData, String urlApi){
        //Log.i("my", String.valueOf(jsonData));
        if(urlApi.equals("info/nodes")) {
            getNodesLocationData(jsonData);
            new AoTData(SearchActivity.this).execute("value/nodes");
        }
        else if(urlApi.equals("value/nodes")){
            getNodesValueData(jsonData);
            drawNodesMarker(nodesInfo);
            drawNodesVoronoi(nodesInfo);
        }
    }


    public void getNodesLocationData(JSONObject nodesLocation){
        //parse json
        for(Object k: nodesLocation.keySet()){
            //Log.i("my", k.toString());
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
            //Log.i("my", k.toString());
            HashMap<String, String> node = new HashMap<String, String>();
            if(!k.toString().equals("total")&&!k.toString().equals("node")){
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("timestamp"))
                    node.put("timeStamp",((JSONObject)nodesValue.get(k.toString())).get("timestamp").toString());
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("sound"))
                    node.put("sound",((JSONObject)nodesValue.get(k.toString())).get("sound").toString());
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("no2"))
                    node.put("no2",((JSONObject)nodesValue.get(k.toString())).get("no2").toString());
                if(((JSONObject)nodesValue.get(k.toString())).containsKey("pm2_5"))
                    node.put("pm2_5",((JSONObject)nodesValue.get(k.toString())).get("pm2_5").toString());
                nodesInfo.get(k.toString()).putAll(node);
            }
        }
        Log.i("my", nodesInfo+"");
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
                if (sound < 57) circleColor = Color.argb(100,0,255,0);//R.drawable.green_circle;
                else if (sound < 70) circleColor = Color.argb(100,255,255,0);//R.drawable.yellow_circle;
                else circleColor = Color.argb(100,255,0,0);//R.drawable.red_circle;
            }
            else
                continue;

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(lat, lon))
                    .radius(400) // In meters
                    .strokeWidth(0)
                    .fillColor(circleColor)
                    .clickable(true);

            // Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);


//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(lat, lon))
//                    .icon(BitmapDescriptorFactory.fromResource(mMarker))
//                    .title((String) entry.getKey())
//                    .alpha(0.45f)
//                    .anchor(0.5f,0.5f)
//                    .flat(true)
//                    );
        }
    }


    public void drawNodesVoronoi( HashMap<String, HashMap<String, String>> nodesInfoParam)
    {
        List<Pnt> pntList = new ArrayList<>();
        List<List<Pnt>> VoronoiRegionVertices = new ArrayList<>();
        VoronoiLayer vLayer = new VoronoiLayer();

        Set set = nodesInfoParam.entrySet();
        Iterator iterator = set.iterator();


        while(iterator.hasNext()) {
            double sound;
            int regionColor = Color.argb(100, 100, 100, 100);

            Map.Entry entry = (Map.Entry) iterator.next();
            double lat = Double.parseDouble((((HashMap) entry.getValue()).get("lat")).toString());
            double lon = Double.parseDouble((((HashMap) entry.getValue()).get("lon")).toString());

            pntList.add(new Pnt(lat, lon));
            vLayer.addSite(new Pnt(lat, lon));
//            if(((HashMap) entry.getValue()).containsKey("sound")) {
//                sound = Double.parseDouble((((HashMap) entry.getValue()).get("sound")).toString());
//                if (sound < 57) regionColor = Color.argb(100,0,255,0);//R.drawable.green_circle;
//                else if (sound < 70) regionColor = Color.argb(100,255,255,0);//R.drawable.yellow_circle;
//                else regionColor  = Color.argb(100,255,0,0);//R.drawable.red_circle;
//            }
//            else
//                continue;
        }

        VoronoiRegionVertices = vLayer.drawAllVoronoi();
        for(List<Pnt> region:VoronoiRegionVertices){
            PolygonOptions polygonOptions = new PolygonOptions();
            for(Pnt vertex: region){

                polygonOptions.add(new LatLng(vertex.coord(0), vertex.coord(1)));

            }
            polygonOptions.strokeJointType(JointType.ROUND);
            polygonOptions.strokeColor(Color.RED);
            polygonOptions.strokeWidth(10);

            mMap.addPolygon(polygonOptions);
        }
    }
}
