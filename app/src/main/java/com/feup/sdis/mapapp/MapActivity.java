package com.feup.sdis.mapapp;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) findViewById(R.id.view_map);
        mapView.onCreate(mapViewBundle);

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                Log.e("test", "ola");
                int action = event.getAction();

                switch (action) {

                    case (MotionEvent.ACTION_DOWN) :
                        Log.d("debug","Action was DOWN");
                        break;
                    case (MotionEvent.ACTION_MOVE) :
                        Log.d("debug","Action was MOVE");
                        break;
                    case (MotionEvent.ACTION_UP) :
                        Log.d("debug","Action was UP");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        LatLngBounds feup = new LatLngBounds(new LatLng(41.177509, -8.598646), new LatLng(41.179133, -8.593830));

        // can't scroll past feup bounds
        map.setLatLngBoundsForCameraTarget(feup);
        map.setMinZoomPreference(17);
        // center camera on feup
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(feup.getCenter(), 18));

        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(41.177730, -8.598479))
                .add(new LatLng(41.177113, -8.596320));

        // add polyline to map
        map.addPolyline(rectOptions);

        // detect long clicks
        /*map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d("debug", latLng.toString());
            }
        });*/
    }
}
