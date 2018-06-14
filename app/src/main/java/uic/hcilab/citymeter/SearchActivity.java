package uic.hcilab.citymeter;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class SearchActivity extends TabHost implements OnMapReadyCallback{
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyDVvTGEXE19Gvk0f49-Qq_F7ocf0J8DNa8";
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

//        //Map
////        Bundle mapViewBundle = null;
////        if (savedInstanceState != null) {
////            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
////        }
////
////        mapView = findViewById(R.id.map_view);
////        mapView.onCreate(mapViewBundle);
////        mapView.getMapAsync(this);

            initMap();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
//        if (mapViewBundle == null) {
//            mapViewBundle = new Bundle();
//            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
//        }
//
//        mapView.onSaveInstanceState(mapViewBundle);
//    }

    public void initMap() {
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }
}
