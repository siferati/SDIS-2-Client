package com.feup.sdis.mapapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Tirafesi on 20-05-2017.
 */

public class MazePlayerActivity extends AppCompatActivity implements OnMapReadyCallback {

    /** GoogleMap */
    private GoogleMap map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze_player);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_maze_player);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        UiSettings uiSettings = map.getUiSettings();

        // disable toolbar when clicking markers
        uiSettings.setMapToolbarEnabled(false);

        LatLngBounds feup = new LatLngBounds(new LatLng(41.177509, -8.598646), new LatLng(41.179133, -8.593830));
        // can't scroll past feup bounds
        map.setLatLngBoundsForCameraTarget(feup);
        map.setMinZoomPreference(17);
        // center camera on feup
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(feup.getCenter(), 18));
    }
}
