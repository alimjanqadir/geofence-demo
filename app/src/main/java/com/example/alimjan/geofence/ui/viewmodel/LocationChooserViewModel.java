package com.example.alimjan.geofence.ui.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.alimjan.geofence.data.GeofenceRepository;
import com.example.alimjan.geofence.data.OnAsyncTaskCallback;
import com.example.alimjan.geofence.db.GeofenceDao;
import com.example.alimjan.geofence.db.GeofenceDataBase;
import com.example.alimjan.geofence.model.Geofence;
import com.example.alimjan.geofence.model.Place;
import com.example.alimjan.geofence.model.Point;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * A {@link androidx.lifecycle.ViewModel} implementation, that it's sole purpose it to manager
 * UI-related data in a lifecycle conscious way.
 */
public class LocationChooserViewModel extends AndroidViewModel {
    // Repository that handles all the data related to geofence
    private final GeofenceRepository mRepository;

    private final MutableLiveData<Point> mSelectedPoint = new MutableLiveData<>();
    // A place that contains place and point information.
    private final LiveData<Place> mPlace;

    public LocationChooserViewModel(@NonNull Application application) {
        super(application);

        // Initialize repository
        Context context = application.getApplicationContext();
        GeofenceDao geofenceDao = GeofenceDataBase.getInstance(context).getDao();
        this.mRepository = new GeofenceRepository(context, geofenceDao);

        // Initialize place LiveData
        this.mPlace = Transformations.switchMap(mSelectedPoint, this.mRepository::getPlace);
    }


    /**
     * A trigger that indicates user selected new point on the map, this leads to a place information
     * request to associated point.
     *
     * @param point User selected point on the map.
     */
    public void setSelectedPosition(Point point) {
        this.mSelectedPoint.setValue(point);
    }

    /**
     * Returns a place information associated with user selected point on the map.
     */
    public LiveData<Place> getSelectedPlace() {
        return mPlace;
    }

    /**
     * Returns all geofence alerts.
     */
    public LiveData<List<Geofence>> getGeofences() {
        return this.mRepository.getGeofencesObserver();
    }

    /**
     * Retrieve a geofence data from repository asynchronously.
     *
     * @param geofence Geofence data used for query.
     * @param callback An asynchronous result callback called on main thread.
     */
    public void getGeofenceAsync(@NonNull Geofence geofence, @NonNull OnAsyncTaskCallback<Geofence> callback) {
        this.mRepository.getGeofenceAsync(geofence.getLatitude(), geofence.getLongitude(), callback);
    }

    /**
     * Add a geofence alert.
     *
     * @param geofence A data class that contains geofence information.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void addGeofenceAsync(final Geofence geofence) {
        this.addGeofenceAsync(geofence, null);
    }

    /**
     * Add a geofence alert.
     *
     * @param geofence A data class that contains geofence information.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void addGeofenceAsync(Geofence geofence, OnAsyncTaskCallback<Long> callback) {
        this.mRepository.addGeofenceAsync(geofence, callback);
    }

    /**
     * Remove a geofence alert.
     *
     * @param geofence A data class that contains geofence information.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void removeGeofenceAsync(Geofence geofence) {
        this.removeGeofenceAsync(geofence, null);
    }


    /**
     * Remove a geofence alert.
     *
     * @param geofence A data class that contains geofence information.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void removeGeofenceAsync(Geofence geofence, @Nullable OnAsyncTaskCallback<Integer> callback) {
        this.mRepository.removeGeofenceAsync(geofence, callback);
    }

    /**
     * Remove a geofence alert.
     *
     * @param id Id that represents a geofence.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void removeGeofenceAsync(final long id) {
        this.removeGeofenceAsync(id, null);
    }

    /**
     * Remove a geofence alert.
     *
     * @param id       Id that represents a geofence.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void removeGeofenceAsync(final long id, @Nullable OnAsyncTaskCallback<Integer> callback) {
        Geofence geofence = new Geofence();
        geofence.setId(id);
        this.removeGeofenceAsync(geofence, callback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.mRepository.onCleared();
    }
}
