package com.feup.sdis.mapapp;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

/**
 * This class implements the Maze Player activity
 * It's where the user can play a maze he downloaded from the server
 */
public class MazePlayerActivity extends AppCompatActivity
        implements OnMapReadyCallback,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                ResultCallback<LocationSettingsResult> {


    /** Tag used for logs */
    private static final String TAG = MazePlayerActivity.class.getSimpleName();

    /** Location request interval, in milliseconds */
    private static final int LOCATION_REQUEST_INTERVALL = 5000;

    /** Location request fastest interval, in milliseconds */
    private static final int LOCATION_REQUEST_FASTEST_INTERVALL = 5000;

    /** GoogleApiClient */
    private GoogleApiClient googleApiClient = null;

    /** MapFragment */
    private SupportMapFragment mapFragment = null;

    /** LocationRequest */
    private LocationRequest locationRequest = null;

    /** GoogleMap */
    private GoogleMap map = null;

    /** GoogleMap camera position */
    private CameraPosition cameraPosition = null;

    /** Request code used for callback onRequestPermissionsResult */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /** Request code user for callback onActivityResult */
    private static final int REQUEST_CHECK_SETTINGS = 2;

    /** True if user gave permissions. False otherwise */
    private boolean locationPermissionGranted = false;

    /** Last known location */
    private Location lastKnownLocation = null;

    /** List of all polylines that make up this maze */
    private ArrayList<Polyline> maze = new ArrayList<>();

    /** Entrance to the maze */
    private Marker entrance = null;

    /** Exit to the maze */
    private Marker exit = null;

    /** Key for storing activity state */
    private static final String KEY_CAMERA_POSITION = "camera_position";

    /** Key for storing activity state */
    private static final String KEY_LOCATION = "location";

    /** Default map zoom */
    private static final int DEFAULT_ZOOM = 18;

    /** Default camera target */
    private static final LatLng DEFAULT_LOCATION = new LatLng(41.1785749, -8.5962507);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maze_player);

        // Build the Play services client for use by the Fused Location Provider
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        // Create location request
        locationRequest = LocationRequest.create()
                .setInterval(LOCATION_REQUEST_INTERVALL)
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVALL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO check if this is before or after super call
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
    }


    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Build location settings request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        // Check whether current location settings are satisfied
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
    }


    /**
     * Checks whether current location settings are satisfied
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult result) {

        final Status status = result.getStatus();

        switch (status.getStatusCode()) {

            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can initialize location
                // requests here.

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map_maze_player);
                mapFragment.getMapAsync(this);

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.

                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {
                    // Log error
                    Log.d(TAG, "Location settings request failed: Exception at startResolutionForResult: "
                            + e.getMessage());
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
                Toast toast = Toast.makeText(this, "Can't use location services. Impossible to play a maze", Toast.LENGTH_LONG);
                toast.show();
                finish();

                break;

            default:
                break;
        }
    }


    /**
     * Handles the result of the request for location settings
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:

                switch (resultCode) {

                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map_maze_player);
                        mapFragment.getMapAsync(this);

                        break;

                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast toast = Toast.makeText(this, "Can't use location services. Impossible to play a maze", Toast.LENGTH_LONG);
                        toast.show();
                        finish();

                        break;

                    default:
                        break;
                }

                break;

            default:
                break;
        }
    }


    /**
     * Handles failure to connect to the Google Play services client
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }


    /**
     * Handles suspension of the connection to the Google Play services client
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        UiSettings uiSettings = map.getUiSettings();

        // disable toolbar when clicking markers
        uiSettings.setMapToolbarEnabled(false);

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        /*
        LatLngBounds feup = new LatLngBounds(new LatLng(41.177509, -8.598646), new LatLng(41.179133, -8.593830));
        // can't scroll past feup bounds
        map.setLatLngBoundsForCameraTarget(feup);
        map.setMinZoomPreference(17);
        // center camera on feup
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(feup.getCenter(), 18));

        // TODO download maze from server and remove hardcoded stuff
        Polyline polyline1 = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode("ayizFjcns@F@??@???@???@?????@???@???@??@@????@@???@@??@@??@?@@????@???@??@??@??????@@A@???@???@??@@???@@?A@???@???@@@?????@???@??@????@???@????@@?@?????@???@???@?????@@???@@?@?????@@???@@A?@??@??@???@@??@?A?@@?A?@@???@@?A@?@@@?????B???@?@???@???@A????@???@?@??A??@?@?@A@???@???@???@A??@?@?@A@???@A@???@?????@???@?@???@?@A??@??A@@??@?@??AB?????@A??@?@???@???@?@???@??A?A@?????@????A@???@?@A@?@?????BA??@?B?@?@A@@??@?@A@?@?@A@?@?@??A@?B?BA@AB@?A??@???@A@???@???@??A??@?@???@A@@@A??@?@??A??@?@???@?@A??@???@"))
                .color(Color.BLUE));

        Polyline polyline2 = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode("gvizF`ens@F???@?????@???@???@?????@?@???@????@?@???@???B???@?@???B?????@???@?@???@?@???@???@?B?@?@?@?@?@???B?@??A@???@???@?@?????@A@?@???@?@???@?@???@?@?@?@???B???@?????@?@???@?@?@???@???B???@??@@???@@@??"))
                .color(Color.BLUE));

        Polyline polyline3 = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode("uuizF`ens@DC?A?A?A?C@C?A?A?A???A?A?A?C?C?A?A?A???A?A?A?A?A?C?A?A?A???A???A?A?C?A@C?C?A?A@C?A?A???A?A?A??"))
                .color(Color.BLUE));

        Polyline polyline4 = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode("uuizFjhns@DD?????@@?@@?A?@?@@???@??@@????@??@??@@??@??@?????@????@@?@???@???@@??????@??@@???@???@@@@@??@??@???@???@@??@@??@???@????@@??@?A@@??@?????@??@?@@???@??@@???@@??@@????@????@@?@???@???@@??@????@??@?@???@?"))
                .color(Color.BLUE));

        Polyline polyline5 = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode("quizFrhns@CFA@??A@???@A??@A??BAA?@?@??A??@A??????@??A??@A@@??@A??@A??@??A@???@A@???@??A??@????A?????A???A????@?@A?@??@A@???@?@A??@?@A????@A@????A@?@??A@?@A@??A@"))
                .color(Color.BLUE));

        maze.add(polyline1);
        maze.add(polyline2);
        maze.add(polyline3);
        maze.add(polyline4);
        maze.add(polyline5);

        entrance = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.17919904393778, -8.597205094993114))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        exit = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.17829134579214, -8.598243109881878))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        */
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (locationPermissionGranted) {
            lastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
        }

        // Fetch the maze from the server and show it
        updateLocationUI();

    }


    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        // Set the map's camera position to the current location of the device.
        if (cameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (lastKnownLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }
}
