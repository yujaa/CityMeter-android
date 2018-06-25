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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    private double[][] chicago_boundary = {
            {-87.664207,42.021263}, {-87.64545,41.971456}, {-87.645323,41.971252}, {-87.630898,41.968581}, {-87.630882,41.96854}, {-87.642289,41.961451}, {-87.642296,41.961371}, {-87.633004,41.942403}, {-87.633028,41.942335},
            {-87.642142,41.947903}, {-87.642141,41.94788}, {-87.623655,41.904062}, {-87.623723,41.904049}, {-87.600582,41.89224}, {-87.598549,41.892274}, {-87.616497,41.881779}, {-87.616506,41.881727}, {-87.604944,41.834036},
            {-87.604868,41.833796}, {-87.567644,41.784622}, {-87.567532,41.784597}, {-87.575079,41.775931}, {-87.575043,41.775785}, {-87.52448,41.741468}, {-87.524462,41.741469}, {-87.536849,41.741045}, {-87.539855,41.741013},
            {-87.524734,41.731221}, {-87.524688,41.731093}, {-87.525166,41.644555}, {-87.525166,41.644543}, {-87.616847,41.644584}, {-87.617216,41.644584}, {-87.619721,41.661002}, {-87.619857,41.661141}, {-87.646817,41.657662},
            {-87.646988,41.65763}, {-87.641604,41.668715}, {-87.641665,41.67054}, {-87.661382,41.677447}, {-87.661365,41.677544}, {-87.73884,41.683643}, {-87.73945,41.683632}, {-87.720533,41.691315}, {-87.720317,41.691285},
            {-87.721125,41.713159}, {-87.721125,41.71317}, {-87.682103,41.713669}, {-87.681842,41.713662}, {-87.682512,41.73546}, {-87.682514,41.735527}, {-87.740165,41.734547}, {-87.741067,41.734524}, {-87.742145,41.774454},
            {-87.742165,41.77478}, {-87.799751,41.773716}, {-87.800806,41.773697}, {-87.80162,41.797778}, {-87.801626,41.797995}, {-87.752848,41.800184}, {-87.752736,41.800187}, {-87.739977,41.86582}, {-87.739982,41.865959},
            {-87.77386,41.865462}, {-87.774139,41.865459}, {-87.775598,41.909211}, {-87.775599,41.909254}, {-87.803942,41.908892}, {-87.805745,41.908839}, {-87.806618,41.934376}, {-87.806624,41.934511}, {-87.850187,41.937751},
            {-87.85066,41.938079}, {-87.854979,41.972341}, {-87.855158,41.972458}, {-87.87807,41.972922}, {-87.880773,41.972855}, {-87.892877,41.950808}, {-87.892877,41.950794}, {-87.926131,41.95481}, {-87.926353,41.954864},
            {-87.939923,41.993283}, {-87.93993,41.993491}, {-87.906386,42.008968}, {-87.90575,42.008991}, {-87.862388,41.973852}, {-87.862009,41.973864}, {-87.855766,41.988927}, {-87.855835,41.989052}, {-87.823517,41.984533},
            {-87.823327,41.984509}, {-87.821211,42.018547}, {-87.82121,42.018642}, {-87.806781,42.018962}, {-87.806547,42.018965}, {-87.806758,42.000853}, {-87.806759,42.000837}, {-87.777347,42.015396}, {-87.776972,42.015386},
            {-87.753009,41.997352}, {-87.7529,41.997301}, {-87.711735,41.997348}, {-87.711597,41.99735}, {-87.709017,42.019041}, {-87.709014,42.019131}, {-87.664207,42.021263}
    };


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
            Location loc = new Location("temporary");
            loc.setLongitude(-87.590228);
            loc.setLatitude(41.810342);
            //Log.i("my",getNearestNodes(loc));
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
        //Log.i("my", nodesInfo+"");
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
//                    .alpha(0.0f)
//                    .flat(true)
//                    );
        }
    }


    public void drawNodesVoronoi( HashMap<String, HashMap<String, String>> nodesInfoParam)
    {
        HashMap<List<Pnt>,List<Double>> VoronoiRegionVertices = new HashMap<>();
        VoronoiLayer vLayer = new VoronoiLayer();

        Set set = nodesInfoParam.entrySet();
        Iterator iterator = set.iterator();


        while(iterator.hasNext()) {
            double sound;
            int regionColor = Color.argb(100, 100, 100, 100);

            Map.Entry entry = (Map.Entry) iterator.next();
            double lat = Double.parseDouble((((HashMap) entry.getValue()).get("lat")).toString());
            double lon = Double.parseDouble((((HashMap) entry.getValue()).get("lon")).toString());


            if(((HashMap) entry.getValue()).containsKey("sound")) {
                sound = Double.parseDouble((((HashMap) entry.getValue()).get("sound")).toString());
                if (sound < 57) regionColor = Color.argb(40,0,255,0);
                else if (sound < 70) regionColor = Color.argb(40,255,255,0);
                else regionColor  = Color.argb(40,255,0,0);
            }
//            else
//                continue;

            vLayer.addSite(new Pnt(lat, lon), regionColor);

        }

        VoronoiRegionVertices = vLayer.drawAllVoronoi();
        //Log.i("my",VoronoiRegionVertices.size()+"");
        for(HashMap.Entry<List<Pnt>, List<Double>> region: VoronoiRegionVertices.entrySet()){
            PolygonOptions polygonOptions = new PolygonOptions();
            for(Pnt vertex: region.getKey()){
                double lat = vertex.coord(0);
                double lng = vertex.coord(1);
                Log.i("my",lat+","+lng);
//                if(lng > -87.54|| lng < - 87.9){//|| lat < 41.645 ||lat > 42.02) {
//                    double closestPoint[] = {0,0};
//                    double closestDist = -1;
//                    for(double points[]: chicago_boundary) {
//                        if(closestPoint[0]==0)
//                        {
//                            closestDist = getDistance(new double[]{lng, lat}, points);
//                            closestPoint = points;
//                        }
//                        else {
//                            if (getDistance(new double[]{lng, lat}, points) < closestDist)
//                                closestPoint = points;
//                        }
//                    }
//                    lng = closestPoint[0];
//                    lat = region.getValue().get(0);
//                }
                polygonOptions.add(new LatLng(lat, lng));
            }
            polygonOptions.strokeJointType(JointType.ROUND);
            double color = region.getValue().get(2);
            polygonOptions.fillColor((int)color);
            polygonOptions.strokeColor(Color.argb(100,0,0,0));
            polygonOptions.strokeWidth(0);
            polygonOptions.geodesic(true);
            mMap.addPolygon(polygonOptions);
        }
    }

    private double getDistance(double[] a, double[]b){
        return Math.sqrt((a[0]-b[0])*(a[0]-b[0])+(a[1]-b[1])*(a[1]-b[1]));

    }

    private String getNearestNodes(Location current) {
        Set set = nodesInfo.entrySet();
        Iterator iterator = set.iterator();

        double nearestDistance = 1000000;
        String nearestNode = "null";
        Location nLoc = new Location("node");
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            double lat = Double.parseDouble((((HashMap) entry.getValue()).get("lat")).toString());
            double lon = Double.parseDouble((((HashMap) entry.getValue()).get("lon")).toString());
            nLoc.setLatitude(lat);
            nLoc.setLongitude(lon);

            if (nLoc.distanceTo(current) < nearestDistance) {
                nearestDistance = nLoc.distanceTo(current);
                nearestNode = entry.getKey().toString();
            }
        }
        return nearestNode;
    }

}
