package com.feup.sdis.mapapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.feup.sdis.mapapp.client.ServerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * This class implements the Maze Builder activity
 * It's where the user can create a maze and send it to the server
 */
public class MazeBuilderActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnPolylineClickListener {

    /** Max range entrance and exit can be of the rest of the maze */
    public static double TOLERANCE = 3.5;

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

    /** Entrance to the maze */
    private Marker entrance = null;

    /** True if next click on map is the entrance position. False otherwise */
    private boolean pickingEntrance = false;

    /** Exit to the maze */
    private Marker exit = null;

    /** True if the next click on map is the exit position. False otherwise */
    private boolean pickingExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze_builder);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maze_builder_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.erase_line:
                for (Polyline polyline: maze) {
                    polyline.setClickable(true);
                }
                break;
            case R.id.maze_entrance:
                pickingEntrance = true;
                break;
            case R.id.maze_exit:
                pickingExit = true;
                break;
            case R.id.send_maze:

                if (entrance == null || exit == null  || !isLocationOnMaze(entrance.getPosition(), maze, TOLERANCE) || !isLocationOnMaze(exit.getPosition(), maze, TOLERANCE)) {

                    Toast toast = Toast.makeText(this, getText(R.string.wrong_entrance_or_exit), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    sendMaze(maze);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
        map.setOnPolylineClickListener(this);

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
                .add(marker.getPosition())
                .color(Color.BLUE));
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

        // add drawn line to the maze
        maze.add(activePolyline);

        // cleanup for next event
        activeMarkerStandingPos = marker.getPosition();
        activePolyline = null;
    }


    @Override
    public void onMapClick(LatLng latLng) {

        if (pickingEntrance) {

            if (entrance != null)
                entrance.remove();

            entrance = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            pickingEntrance = false;
        }
        else if (pickingExit) {

            if (exit != null)
                exit.remove();

            exit = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            pickingExit = false;
        }
        else {

            if (activeMarker != null)
                activeMarker.remove();

            activeMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .draggable(true));

            activeMarkerStandingPos = activeMarker.getPosition();
        }
    }


    @Override
    public void onPolylineClick(Polyline polyline) {

        // remove this polyline from the maze
        for (Polyline line: maze) {
            if (line.getId().equals(polyline.getId()))
            {
                maze.remove(line);
                break;
            }
        }

        // remove this polyline from the map
        polyline.remove();

        // only one line is deleted at a time
        for (Polyline line: maze) {
            line.setClickable(false);
        }
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
     * @param delta  Offset amount
     */
    public void offsetMarker(Marker marker, LatLng delta) {

        marker.setPosition(new LatLng(
                marker.getPosition().latitude + delta.latitude,
                marker.getPosition().longitude + delta.longitude
        ));
    }


    /**
     * Computes whether the given point lies on or near a maze, within a specified tolerance in meters.
     *
     * @param point Point to test
     * @param maze Arraylist of polylines
     * @param tolerance Max number (in meters) point can be of maze
     */
    public boolean isLocationOnMaze(LatLng point, ArrayList<Polyline> maze, double tolerance) {

        for (Polyline polyline : maze) {

            if (PolyUtil.isLocationOnPath(point, polyline.getPoints(), polyline.isGeodesic(), tolerance)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Sends the maze to the server
     *
     * @param maze Maze to send
     *
     * @return True if send was successful. False otherwise
     */
    public boolean sendMaze(ArrayList<Polyline> maze) {

        JSONObject mapJSON = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        JSONObject singleLine = new JSONObject();
        JSONArray lineArray = new JSONArray();

        try {

            jsonAll.put("username", "user1");
            jsonAll.put("userhash", "ABC");
            mapJSON.put("mapname", "o_mapa");

            mapJSON.put("startlat", Double.valueOf(entrance.getPosition().latitude).toString());
            mapJSON.put("startlng", Double.valueOf(entrance.getPosition().longitude).toString());

            mapJSON.put("finishlat", Double.valueOf(exit.getPosition().latitude).toString());
            mapJSON.put("finishlng", Double.valueOf(exit.getPosition().longitude).toString());

            jsonAll.put("map", mapJSON);

        } catch(org.json.JSONException e){
            e.printStackTrace();
            return false;
        }

        try{

            for (Polyline polyline: maze) {

                String encodedPolyline = PolyUtil.encode(polyline.getPoints());
                singleLine = new JSONObject();
                singleLine.put(encodedPolyline, "");

            }

            lineArray.put(singleLine);
            jsonAll.put("lines",lineArray);


            // TODO: localhost being used.
            String mapId = new ServerService().execute("maps", "PUT", jsonAll.toString()).get();
            Log.i("tag", "MapId: " + mapId);

        } catch(java.util.concurrent.ExecutionException e){
            e.printStackTrace();
        } catch(java.lang.InterruptedException e){
            e.printStackTrace();
        } catch(org.json.JSONException e){
            e.printStackTrace();
        }

        return true;
    }
}
