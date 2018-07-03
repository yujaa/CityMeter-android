package uic.hcilab.citymeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import voronoi.Intersection;
import voronoi.Line;
import voronoi.Pnt;
import voronoi.VoronoiLayer;

public class SearchActivity extends TabHost implements OnMapReadyCallback, ApiCallback, LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LatLng uic = new LatLng(41.8693, -87.6475);
    String curLat = "41.8693";
    String curLng = "-87.6475";
    private GoogleMap mMap;

    private JSONObject nodesLocation = null;
    HashMap<String, HashMap<String, String>> nodesInfo = new HashMap<String,  HashMap<String, String>>();
    HashMap<String, String> currentLocationData = new HashMap<String, String>();
    private double[][] chicagoLonLat = {
            {42.021263,-87.664207}, {41.971456,-87.64545}, {41.971252,-87.645323}, {41.968581,-87.630898}, {41.96854,-87.630882}, {41.960198, -87.631689},{41.961451,-87.642289}, {41.961371,-87.642296},
            {41.942403,-87.633004}, {41.942335,-87.633028}, {41.947903,-87.642142}, {41.94788,-87.642141}, {41.926581, -87.627703},{41.904062,-87.623655}, {41.904049,-87.623723}, {41.89224,-87.600582},
            {41.892274,-87.598549}, {41.881779,-87.616497}, {41.881727,-87.616506}, {41.834036,-87.604944}, {41.833796,-87.604868}, {41.784622,-87.567644}, {41.784597,-87.567532},
            {41.775931,-87.575079}, {41.775785,-87.575043}, {41.741468,-87.52448}, {41.741469,-87.524462},  {41.731221,-87.524734},
            {41.731093,-87.524688}, {41.644555,-87.525166}, {41.644543,-87.525166}, {41.644584,-87.616847}, {41.644584,-87.617216}, {41.661002,-87.619721}, {41.661141,-87.619857},
            {41.657662,-87.646817}, {41.65763,-87.646988}, {41.668715,-87.641604}, {41.67054,-87.641665}, {41.677447,-87.661382}, {41.677544,-87.661365}, {41.683643,-87.73884},
            {41.683632,-87.73945}, {41.691315,-87.720533}, {41.691285,-87.720317}, {41.713159,-87.721125}, {41.71317,-87.721125},  {41.734547,-87.740165}, {41.734524,-87.741067}, {41.774454,-87.742145}, {41.77478,-87.742165}, {41.773716,-87.799751},
            {41.773697,-87.800806}, {41.797778,-87.80162}, {41.797995,-87.801626}, {41.800184,-87.752848}, {41.800187,-87.752736}, {41.86582,-87.739977}, {41.865959,-87.739982},
            {41.865462,-87.77386}, {41.865459,-87.774139}, {41.909211,-87.775598}, {41.909254,-87.775599}, {41.908892,-87.803942}, {41.908839,-87.805745}, {41.934376,-87.806618},
            {41.934511,-87.806624}, {41.937751,-87.850187}, {41.938079,-87.85066}, {41.972341,-87.854979}, {41.972458,-87.855158}, {41.972922,-87.87807}, {41.972855,-87.880773},
            {41.950808,-87.892877}, {41.950794,-87.892877}, {41.95481,-87.926131}, {41.954864,-87.926353}, {41.993283,-87.939923}, {41.993491,-87.93993}, {42.008968,-87.906386},
            {42.008991,-87.90575}, {41.973852,-87.862388}, {41.973864,-87.862009}, {41.988927,-87.855766}, {41.989052,-87.855835}, {41.984533,-87.823517}, {41.984509,-87.823327},
            {42.018547,-87.821211}, {42.018642,-87.82121}, {42.018962,-87.806781}, {42.018965,-87.806547}, {42.000853,-87.806758}, {42.000837,-87.806759}, {42.015396,-87.777347},
            {42.015386,-87.776972}, {41.997352,-87.753009}, {41.997301,-87.7529}, {41.997348,-87.711735}, {41.99735,-87.711597}, {42.019041,-87.709017}, {42.019131,-87.709014},
            {42.021263,-87.664207}, {41.998108,-87.940013}, {41.998108,-87.940013}, {42.005424,-87.933485}, {42.005424,-87.933485}
    };

    List<Line> chicagoBoundary = new ArrayList<>();

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
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        setSupportActionBar(myToolbar);

        boolean first= true;
        Intersection.Point prevPnt = new Intersection.Point(0,0);
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
        LatLng curLoc = new LatLng(Double.parseDouble(curLat), Double.parseDouble(curLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));
        new AoTData(SearchActivity.this).execute("value/nearest/"+curLat+"/"+curLng);
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
            loc.setLongitude(Double.parseDouble(curLng));
            loc.setLatitude(Double.parseDouble(curLat));

        }
        else if(urlApi.contains("value/nearest/")){
            getCurrentLocationData(jsonData);
            Log.i("myy",""+Double.parseDouble(currentLocationData.get("lon")));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(currentLocationData.get("lat")), Double.parseDouble(currentLocationData.get("lon"))))
                    .title(currentLocationData.get("node_id"))
                    .alpha(5.0f)
                    .flat(true)
            );
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
    }

    public void getCurrentLocationData(JSONObject nodesValue)
    {
        //parse json

        if(nodesValue.containsKey("lat"))
            currentLocationData.put("lat",nodesValue.get("lat").toString());
        if(nodesValue.containsKey("lon"))
            currentLocationData.put("lon",nodesValue.get("lon").toString());
        if(nodesValue.containsKey("node_id"))
            currentLocationData.put("node_id",nodesValue.get("node_id").toString());
        if(nodesValue.containsKey("distance"))
            currentLocationData.put("distance",nodesValue.get("distance").toString());

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
//                    .alpha(5.0f)
//                    .flat(true)
//                    );
        }
    }


    public void drawNodesVoronoi( HashMap<String, HashMap<String, String>> nodesInfoParam)
    {

        boolean first= true;
        Intersection.Point prevPnt = new Intersection.Point(0,0);

        for(double[] point: chicagoLonLat) {
            if(first) {
                prevPnt.x = (float) point[0];
                prevPnt.y = (float) point[1];
                first = false;
            }
            else {
                chicagoBoundary.add(new Line(prevPnt.x, prevPnt.y, point[0], point[1]));
                prevPnt.x = point[0];
                prevPnt.y = point[1];
            }
        }


        HashMap<List<Pnt>, Integer> VoronoiRegionVertices = new HashMap<>();
        VoronoiLayer vLayer = new VoronoiLayer();
        Set set = nodesInfoParam.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            double sound;
            int regionColor = Color.argb(100, 100, 100, 100);

            Map.Entry entry = (Map.Entry) iterator.next();
            double lat = Double.parseDouble((((HashMap) entry.getValue()).get("lat")).toString());
            double lng = Double.parseDouble((((HashMap) entry.getValue()).get("lon")).toString());

            if (((HashMap) entry.getValue()).containsKey("sound")) {
                sound = Double.parseDouble((((HashMap) entry.getValue()).get("sound")).toString());
                if (sound < 58) regionColor = Color.argb(50, 0, 255, 0);
                else if (sound < 65) regionColor = Color.argb(50, 255, 255, 0);
                else if (sound < 70)
                    regionColor = Color.argb(50, 255, 94, 00);
                else if (sound < 85)
                    regionColor = Color.argb(50, 255, 0, 0);
                else
                    regionColor = Color.argb(50, 93, 0, 0);
            }
//            else
//                continue;

            vLayer.addSite(new Pnt(lat, lng), regionColor);
        }

        VoronoiRegionVertices = vLayer.drawAllVoronoi();

        for(HashMap.Entry<List<Pnt>, Integer> region: VoronoiRegionVertices.entrySet()){
            PolygonOptions polygonOptions = new PolygonOptions();
            Intersection.Point prevVertex = new Intersection.Point(0,0);
            first = true;
            boolean second = true;
            int modify = 0;
            List<Pnt> verticesList = region.getKey();
            for(Pnt vertex: verticesList) {
                double lat = vertex.coord(0);
                double lng = vertex.coord(1);

                if(first) {
                    prevVertex = new Intersection.Point(vertex.coord(0), vertex.coord(1));
                    first =false;
                    continue;
                }
                else{
                    for(Line boundL : chicagoBoundary) {
                        Intersection.Point intersectPnt =  Intersection.detect(boundL, new Line(prevVertex.x, prevVertex.y, vertex.coord(0), vertex.coord(1)));
                        if (intersectPnt != null){
                            lat = intersectPnt.x;
                            lng = intersectPnt.y;
                            if(modify ==0)  modify= 1;
                            if(modify ==2)  modify = 3;
                            break;
                        }
                    }
                }
                prevVertex.x = vertex.coord(0);
                prevVertex.y = vertex.coord(1);

                if(modify==2) continue;
                polygonOptions.add(new LatLng(lat, lng));

                if(modify==3){
                    polygonOptions.add(new LatLng(vertex.coord(0), vertex.coord(1)));
                    modify =0;
                }
                if(modify ==1) modify = 2;
            }

            for(Line boundL : chicagoBoundary) {
                Intersection.Point intersectPnt =  Intersection.detect(boundL, new Line(prevVertex.x, prevVertex.y, region.getKey().get(1).coord(0), region.getKey().get(1).coord(1)));
                if (intersectPnt != null){
                    polygonOptions.add(new LatLng(intersectPnt.x, intersectPnt.y));
                    break;
                }
            }

            polygonOptions.strokeJointType(JointType.ROUND);
            polygonOptions.strokeColor(Color.argb(100,0,0,0));
            polygonOptions.strokeWidth(0);
            polygonOptions.fillColor(region.getValue());
            mMap.addPolygon(polygonOptions);
        }
    }



    ///gps

    @Override
    public void onLocationChanged(Location loc) {

        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();
        new AoTData(SearchActivity.this).execute("value/nearest/"+loc.getLatitude()+"/"+loc.getLongitude());

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
