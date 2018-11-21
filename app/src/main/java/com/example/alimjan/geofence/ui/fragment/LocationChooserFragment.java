package com.example.alimjan.geofence.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.alimjan.geofence.R;
import com.example.alimjan.geofence.data.OnAsyncTaskCallback;
import com.example.alimjan.geofence.model.Geofence;
import com.example.alimjan.geofence.model.Place;
import com.example.alimjan.geofence.model.Point;
import com.example.alimjan.geofence.ui.viewmodel.LocationChooserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

/**
 * A {@link Fragment} implementation that encapsulates location choosing for geofence alert.
 */
public class LocationChooserFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener {
    // Location permission request
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1 << 1;

    private Activity mContext;
    private MapView mMapView;
    private MapboxMap mMap;
    // Marker specifically indicates user clicked position.
    private Marker mUserClickMarker;
    // ViewModel instance that includes UI related data.
    private LocationChooserViewModel mViewModel;

    public LocationChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment LocationChooserFragment.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public static LocationChooserFragment newInstance() {
        return new LocationChooserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Context used all around the fragment this field created for convenience
        this.mContext = getActivity();

        // Request permission
        requestPermission();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Sync activity lifecycle to map lifecycle
        this.mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sync activity lifecycle to map lifecycle
        this.mMapView.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_chooser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initiate the MapView
        this.mMapView = view.findViewById(R.id.map);
        // Get map instance asynchronously
        this.mMapView.getMapAsync(this);
    }

    /**
     * Called when map is ready.
     *
     * @param map Mapbox map instance.
     */
    @Override
    public void onMapReady(MapboxMap map) {
        this.mMap = map;

        // Add listeners
        this.mMap.addOnMapClickListener(this);
        this.mMap.setOnMarkerClickListener(this);

        // Enable device location
        enableLocationComponent();

        // UI setup
        configureUiSettings();

        // ViewModel created and essential data observed
        this.mViewModel = ViewModelProviders.of(this).get(LocationChooserViewModel.class);
        // Observe selected place information
        this.mViewModel.getSelectedPlace().observe(this, place -> {
            // Show a snackbar for geofence confirmation
            showSnackBar(place);

            // Position map to center
            this.mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(place.getPoint().getLatitude(), place.getPoint().getLongitude())));
        });

        // Add geofence markers on the map
        this.mViewModel.getGeofences().observe(this, geofences -> {
            clearMarkers();

            this.addGeofenceMarkers(geofences);
        });
    }

    /**
     * Clear all the markers on the map including user click marker.
     */
    private void clearMarkers() {
        this.mMap.clear();
        this.mUserClickMarker = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        // Sync activity lifecycle to map lifecycle
        mMapView.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
        // Sync activity lifecycle to map lifecycle
        mMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Sync activity lifecycle to map lifecycle
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Sync activity lifecycle to map lifecycle
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Sync activity lifecycle to map lifecycle
        mMapView.onDestroy();

    }

    /**
     * Add geofence markers on the map.
     *
     * @param geofences A list of geofences that marked on the map.
     */
    private void addGeofenceMarkers(final List<Geofence> geofences) {
        List<MarkerOptions> markerList = new ArrayList<>();
        for (Geofence geofence : geofences) {
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(geofence.getLatitude(), geofence.getLongitude()));
            markerList.add(markerOptions);
        }

        this.mMap.addMarkers(markerList);
    }

    /**
     * Shows a snackbar for geofence confirmation.
     *
     * @param place Used for showing address and {@link Snackbar#setAction}.
     */
    private void showSnackBar(final Place place) {
        String message = place.getAddress();
        Snackbar snackbar = Snackbar.make(mMapView, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.button_confirm, view -> addGeofence(place)).show();
    }

    /**
     * Add a geofence for reminding user when they enter and exit specific area.
     *
     * @param place A place that used to create a geofence.
     */
    @SuppressLint("MissingPermission")
    private void addGeofence(final Place place) {
        if (PermissionsManager.areLocationPermissionsGranted(this.mContext)) { // Check permission first
            // Create a geofence
            Geofence geofence = new Geofence();
            geofence.setAddress(place.getAddress());
            geofence.setLatitude(place.getPoint().getLatitude());
            geofence.setLongitude(place.getPoint().getLongitude());

            // Add the geofence
            this.mViewModel.addGeofenceAsync(geofence, new OnAsyncTaskCallback<Long>() {
                @Override
                public void onSuccess(@NonNull Long data) {
                    Timber.d("A geofence is created, latitude: %f longitude: %f", place.getPoint().getLatitude(), place.getPoint().getLongitude());
                }

                @Override
                public void onError(@NonNull Throwable throwable) {

                }

            });
        } else {
            Toast.makeText(this.mContext, getString(R.string.error_no_location_permission), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Enable tracking device location.
     */
    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        if (isPermissionAcquired()) {
            LocationComponent locationComponent = this.mMap.getLocationComponent();
            locationComponent.activateLocationComponent(this.mContext);
            locationComponent.setRenderMode(RenderMode.NORMAL);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.zoomWhileTracking(15);
        }

    }

    /**
     * Configures map ui.
     */
    private void configureUiSettings() {
        this.mMap.getUiSettings().setRotateGesturesEnabled(false);
        this.mMap.getUiSettings().setCompassEnabled(true);
        this.mMap.getUiSettings().setCompassFadeFacingNorth(false);

        // TODO used only for emulator testing can be customized later
        //noinspection deprecation
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    /**
     * Called when the user clicks on the map view.
     *
     * @param point The projected map coordinate the user clicked on.
     */
    @Override
    public void onMapClick(@NonNull LatLng point) {
        // Position a geofence indicator
        positionGeofenceIndicator(point);

        // Change the user selected position.
        this.mViewModel.setSelectedPosition(new Point(point.getLatitude(), point.getLongitude()));

        Timber.d("onMapClick: La: %f Lo: %f", point.getLatitude(), point.getLongitude());
    }

    /**
     * Position a marker for indicating a geofence to choose.
     *
     * @param point The projected map coordinate the user clicked on.
     */
    private void positionGeofenceIndicator(@NonNull LatLng point) {
        if (this.mUserClickMarker != null) {
            this.mUserClickMarker.setPosition(point);
        } else {
            this.mUserClickMarker = mMap.addMarker(new MarkerOptions().position(point));
        }
    }

    /**
     * Called when the user clicks on a marker.
     *
     * @param marker The marker the user clicked on.
     * @return If true the listener has consumed the event and the info window will not be shown.
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        LatLng point = marker.getPosition();
        Geofence geofence = new Geofence();
        geofence.setLatitude(point.getLatitude());
        geofence.setLongitude(point.getLongitude());

        mViewModel.getGeofenceAsync(geofence, new OnAsyncTaskCallback<Geofence>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(@NonNull Geofence result) {
                // Check for permission
                if (PermissionsManager.areLocationPermissionsGranted(LocationChooserFragment.this.mContext)) {
                    mViewModel.removeGeofenceAsync(result); // remove geofence
                } else {
                    Toast.makeText(LocationChooserFragment.this.mContext, getString(R.string.error_no_location_permission), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Timber.e(throwable);
            }

        });

        return false;
    }

    /**
     * Check location permission approved or not.
     *
     * @return True if approved false otherwise.
     */
    private boolean isPermissionAcquired() {
        return ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Show a explanation dialog about permission.
     */
    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder
                .setMessage(R.string.warning_request_permission_rationale)
                .setTitle(R.string.dialog_title_ask_for_permission)
                .setPositiveButton(R.string.button_positive, (dialog, which) -> requestPermission())
                .setNegativeButton(R.string.button_negative, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Request permission required for this fragment.
     */
    private void requestPermission() {
        if (!isPermissionAcquired()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission_group.LOCATION)) {
                // Show Reason
                Toast.makeText(this.mContext, getString(R.string.warning_request_permission_rationale), Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the permission is what we interested
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Enable device location track
                enableLocationComponent();
            } else {
                // Show explanation
                showPermissionExplanationDialog();
            }
        }
    }
}
