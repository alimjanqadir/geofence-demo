package com.example.alimjan.geofence.data;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.alimjan.geofence.R;
import com.example.alimjan.geofence.db.GeofenceDao;
import com.example.alimjan.geofence.model.Geofence;
import com.example.alimjan.geofence.model.Place;
import com.example.alimjan.geofence.model.Point;
import com.example.alimjan.geofence.service.GeofenceTransitionsService;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * A single source of truth for geofence related data.
 */
public class GeofenceRepository {
    // Geofence alert expire time.
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;// A week
    private static final int GEOFENCE_RADIUS = 200; // 200 meters in radius.

    private Context mContext;
    // Dao for database access.
    private GeofenceDao mDao;
    // A place retrieved from geocoding api
    private MutableLiveData<Place> mPlace = new MutableLiveData<>();
    // Includes all the Rx disposable.
    private CompositeDisposable mCompositeDisposable;

    public GeofenceRepository(@NonNull Context context, @NonNull GeofenceDao mDao) {
        this.mCompositeDisposable = new CompositeDisposable();
        this.mContext = context;
        this.mDao = mDao;
    }

    /**
     * Returns all geofence alerts.
     *
     * @return A {@link LiveData} that you can observe geofence table.
     */
    public LiveData<List<Geofence>> getGeofencesObserver() {
        return this.mDao.getGeofencesObserver();
    }

    /**
     * Retrieve a geofence data from database synchronously.
     *
     * @param id Geofence data used for query.
     */
    public Geofence getGeofence(long id) {
        return this.mDao.getGeofence(id);
    }

    /**
     * Retrieve a geofence data from database asynchronously.
     *
     * @param id       A geofence id.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressWarnings("unused")
    public void getGeofenceAsync(long id, @NonNull final OnAsyncTaskCallback<Geofence> callback) {
        //noinspection Convert2MethodRef
        mCompositeDisposable.add(this.mDao.getGeofenceAsync(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> callback.onSuccess(result),
                        throwable -> callback.onError(throwable)
                ));
    }

    /**
     * Retrieve a geofence data from database asynchronously.
     *
     * @param latitude  A latitude.
     * @param longitude A longitude.
     * @param callback  An asynchronous result callback called on main thread.
     */
    @SuppressWarnings("unused")
    public void getGeofenceAsync(double latitude, double longitude, @NonNull final OnAsyncTaskCallback<Geofence> callback) {
        //noinspection Convert2MethodRef
        mCompositeDisposable.add(this.mDao.getGeofenceAsync(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> callback.onSuccess(result),
                        throwable -> callback.onError(throwable)
                ));
    }


    /**
     * Add a geofence data to database and proximity alert to {@link LocationManager} asynchronously.
     *
     * @param geofence A model class that includes geofence data.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void addGeofenceAsync(@NonNull final Geofence geofence, @Nullable OnAsyncTaskCallback<Long> callback) {
        double latitude = geofence.getLatitude();
        double longitude = geofence.getLongitude();

        // Add geofence to database
        this.mCompositeDisposable.add(this.mDao.insertAsync(geofence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        insertedRecordId -> {
                            // Initiate LocationManger instance
                            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                            // Add a geofence
                            if (locationManager != null) {
                                // Supply missing attributes
                                geofence.setId(insertedRecordId);
                                geofence.setExpireTime(EXPIRATION_TIME);

                                // Service that handles geofence trigger
                                Intent intent = new Intent(this.mContext, GeofenceTransitionsService.class);
                                intent.putExtra(GeofenceTransitionsService.KEY_ID, insertedRecordId);

                                // Add geofence to location manager
                                locationManager.addProximityAlert(latitude, longitude, GEOFENCE_RADIUS, EXPIRATION_TIME,
                                        PendingIntent.getService(this.mContext, insertedRecordId.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT));

                                // Inform the observer
                                if (callback != null) {
                                    callback.onSuccess(insertedRecordId);
                                }
                                Timber.d("addGeofenceAsync: Succeed latitude: %f longitude: %f", latitude, longitude);
                            }
                        }, throwable -> {
                            if (callback != null) {
                                callback.onError(throwable);
                            }
                        }
                ));
    }


    /**
     * Remove a geofence data from database and proximity alert from {@link LocationManager}
     * asynchronously.
     *
     * @param geofence A model class that includes geofence data.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void removeGeofenceAsync(@NonNull final Geofence geofence, @Nullable final OnAsyncTaskCallback<Integer> callback) {
        // Remove geofence from database
        // Initiate LocationManger instance
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // Add a geofence
        if (locationManager != null) {
            // Service that handles geofence trigger
            Intent intent = new Intent(this.mContext, GeofenceTransitionsService.class);
            intent.putExtra(GeofenceTransitionsService.KEY_ID, geofence);

            // Remove geofence from location manager
            locationManager.removeProximityAlert(PendingIntent.getService(this.mContext
                    , (int) geofence.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT));

            // Remove from database
            //noinspection Convert2MethodRef
            this.mCompositeDisposable.add(this.mDao.deleteAsync(geofence)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                if (callback != null) {
                                    callback.onSuccess(result);
                                    Timber.d("removeGeofenceAsync: Succeed latitude: %f longitude: %f", geofence.getLatitude(), geofence.getLongitude());
                                }
                            },
                            throwable -> {
                                if (callback != null) {
                                    callback.onError(throwable);
                                }
                            }
                    ));
        }
    }

    /**
     * Update a geofence data asynchronously.
     *
     * @param geofence A model class that includes geofence data.
     */
    public void updateGeofenceAsync(Geofence geofence) {
        this.updateGeofenceAsync(geofence, null);
    }

    /**
     * Update a geofence data asynchronously.
     *
     * @param geofence A model class that includes geofence data.
     * @param callback An asynchronous result callback called on main thread.
     */
    @SuppressWarnings("WeakerAccess")
    public void updateGeofenceAsync(Geofence geofence, @Nullable final OnAsyncTaskCallback<Integer> callback) {
        this.mCompositeDisposable
                .add(this.mDao.updateAsync(geofence)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updatedRecordCount -> {
                                    if (callback != null) {
                                        callback.onSuccess(updatedRecordCount);
                                    }
                                }, throwable -> {
                                    if (callback != null) {
                                        callback.onError(throwable);
                                    }
                                })
                );
    }

    /**
     * Converts a point on the map to a place.
     *
     * @param point A point on the map,
     * @return A place that contains contextual information about specified point.
     */
    public LiveData<Place> getPlace(Point point) {
        try {
            // Build a Mapbox geocoding request
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(this.mContext.getString(R.string.mapbox_access_token))
                    .query(com.mapbox.geojson.Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_POI)
                    .mode(GeocodingCriteria.MODE_PLACES)
                    .build();

            // Request data
            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeocodingResponse> call,
                                       @NonNull Response<GeocodingResponse> response) {
                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            // Get the first Feature from the successful geocoding response
                            CarmenFeature feature = results.get(0);
                            Place place = new Place();
                            String placeName = feature.placeName();
                            // Show coordinate while there is no place name.
                            if (placeName != null) {
                                place.setAddress(placeName);
                            } else {
                                place.setAddress(String.format(mContext.getString(R.string.address_in_coordinates_format), point.getLatitude(), point.getLongitude()));
                            }
                            place.setPoint(point);
                            mPlace.postValue(place);
                        } else {
                            Place place = new Place();
                            place.setAddress(String.format(mContext.getString(R.string.address_in_coordinates_format), point.getLatitude(), point.getLongitude()));
                            place.setPoint(point);
                            mPlace.postValue(place);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString());
        }

        return this.mPlace;
    }

    /**
     * A callback as a window to dispose repository
     */
    public void onCleared() {
        this.mCompositeDisposable.dispose();
    }
}
