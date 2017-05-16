package com.feup.sdis.mapapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * This class implements the Google Maps activity.
 * It's where the map is shown and the user can interact with it
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {

    /** GoogleMap */
    private GoogleMap map = null;

    /** List of all polylines drawn so far */
    private ArrayList<Polyline> maze = new ArrayList<>();

    /** Marker currently shown in the map. Map only holds 1 marker at a time */
    private Marker activeMarker = null;

    /** Position {@link #activeMarker} was standing when dragging started */
    private LatLng activeMarkerStandingPos = null;

    /** Delta (latitude, longitude) amount a marker jumps upwards when dragged */
    private LatLng offset = null;

    /** The current polyline being drawn by the user */
    private Polyline activePolyline = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        UiSettings uiSettings = map.getUiSettings();

        // disable toolbar when clicking markers
        uiSettings.setMapToolbarEnabled(false);

        // set listeners
        map.setOnMarkerDragListener(this);
        map.setOnMapClickListener(this);

        LatLngBounds feup = new LatLngBounds(new LatLng(41.177509, -8.598646), new LatLng(41.179133, -8.593830));
        // can't scroll past feup bounds
        map.setLatLngBoundsForCameraTarget(feup);
        map.setMinZoomPreference(17);
        // center camera on feup
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(feup.getCenter(), 18));
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

        // tell the user what to do
        Toast toast = Toast.makeText(this, getText(R.string.marker_rdy_to_drag), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();

        // marker jumps upwards on drag, so we need to bring it back down
        offset = new LatLng(
                activeMarkerStandingPos.latitude - marker.getPosition().latitude,
                activeMarkerStandingPos.longitude - marker.getPosition().longitude
        );

        // offset marker
        offsetMarker(marker, offset);

        // save a reference of the line being drawn
        activePolyline = map.addPolyline(new PolylineOptions()
                .add(marker.getPosition()));
    }


    @Override
    public void onMarkerDrag(Marker marker) {

        // offset marker
        offsetMarker(marker, offset);

        // extend the line being drawn to this position
        extendPolyline(activePolyline, marker.getPosition());
    }


    @Override
    public void onMarkerDragEnd(Marker marker) {

        // offset marker
        offsetMarker(marker, offset);

        // extend the line being drawn to this position
        extendPolyline(activePolyline, marker.getPosition());

        // add drawn line to the maze
        maze.add(activePolyline);

        // cleanup for next event
        activeMarkerStandingPos = marker.getPosition();
        activePolyline = null;
    }


    @Override
    public void onMapClick(LatLng latLng) {

        if (activeMarker != null)
            activeMarker.remove();

        activeMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));

        activeMarkerStandingPos = activeMarker.getPosition();
    }


    /**
     * Extends a polyline by adding a point to it
     *
     * @param polyline Polyline to extend
     * @param point    Point to add to the polyline
     */
    public void extendPolyline(Polyline polyline, LatLng point) {

        List<LatLng> polylinePts = polyline.getPoints();
        polylinePts.add(point);
        polyline.setPoints(polylinePts);
    }


    /**
     * Offsets a marker position by delta
     *
     * @param marker Marker to change position
     * @param delta  Offset ammount
     */
    public void offsetMarker(Marker marker, LatLng delta) {

        marker.setPosition(new LatLng(
                marker.getPosition().latitude + delta.latitude,
                marker.getPosition().longitude + delta.longitude
        ));
    }
}
