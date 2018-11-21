package com.example.alimjan.geofence.db;

import com.example.alimjan.geofence.model.Geofence;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;

/**
 * A Data Access Object for the {@link com.example.alimjan.geofence.model.Geofence} class.
 */
@Dao
public interface GeofenceDao {

    /**
     * Returns a geofence data associated with id synchronously.
     *
     * @param id A geofence Id.
     * @return A geofence instance.
     */
    @SuppressWarnings("unused")
    @Query("SELECT * FROM Geofence WHERE id = :id")
    Geofence getGeofence(long id);

    /**
     * Returns a geofence data associated with id asynchronously in order to unblock main thread.
     *
     * @param id A geofence Id.
     * @return A geofence instance wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Query("SELECT * FROM Geofence WHERE id = :id")
    Single<Geofence> getGeofenceAsync(long id);

    /**
     * Returns a geofence data associated with latitude and longitude synchronously.
     *
     * @param latitude  Latitude.
     * @param longitude Longitude.
     * @return A geofence instance.
     */
    @SuppressWarnings("unused")
    @Query("SELECT * FROM Geofence WHERE latitude = :latitude AND longitude = :longitude")
    Geofence getGeofence(double latitude, double longitude);

    /**
     * Returns a geofence data associated with latitude and longitude asynchronously.
     *
     * @param latitude  Latitude.
     * @param longitude Longitude.
     * @return Geofence instance wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Query("SELECT * FROM Geofence WHERE latitude = :latitude AND longitude = :longitude")
    Single<Geofence> getGeofenceAsync(double latitude, double longitude);


    /**
     * Returns all geofences, result is wrapped with {@link LiveData} observer which you can
     * observe every data change.
     *
     * @return List of geofence instances wrapped with {@link LiveData}.
     */
    @SuppressWarnings("unused")
    @Query("SELECT * FROM Geofence")
    LiveData<List<Geofence>> getGeofencesObserver();

    /**
     * Insert a geofence synchronously.
     *
     * @param geofence A data class that contains geofence information.
     * @return Inserted record Id.
     */
    @SuppressWarnings("unused")
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Geofence geofence);

    /**
     * Insert a geofence asynchronously.
     *
     * @param geofence A data class that contains geofence information.
     * @return Inserted record Id wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Single<Long> insertAsync(Geofence geofence);


    /**
     * Insert a list of geofences synchronously.
     *
     * @param geofenceList A list of geofences.
     * @return A list of inserted record Id.
     */
    @SuppressWarnings("unused")
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertAll(List<Geofence> geofenceList);


    /**
     * Insert a list of geofences asynchronously.
     *
     * @param geofenceList A list of geofences.
     * @return A list of inserted record Id wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Single<List<Long>> insertAllAsync(List<Geofence> geofenceList);

    /**
     * Update specified geofence synchronously.
     *
     * @param geofence A data class that contains geofence information.
     * @return Updated record count.
     */
    @SuppressWarnings("unused")
    @Update
    Integer update(Geofence geofence);

    /**
     * Update specified geofence asynchronously.
     *
     * @param geofence A data class that contains geofence information.
     * @return Updated record count wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Update
    Single<Integer> updateAsync(Geofence geofence);

    /**
     * Update a list of geofences synchronously.
     *
     * @param geofenceList A list of geofences.
     * @return Updated record count.
     */
    @SuppressWarnings("unused")
    @Update
    Integer updateAll(List<Geofence> geofenceList);

    /**
     * Update a list of geofences asynchronously.
     *
     * @param geofenceList A list of geofences.
     * @return Updated record count wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Update
    Single<Integer> updateAllAsync(List<Geofence> geofenceList);

    /**
     * Delete specified geofence synchronously.
     *
     * @param geofence A data class that contains geofence information.
     * @return Deleted record count.
     */
    @SuppressWarnings("unused")
    @Delete
    Integer delete(Geofence geofence);

    /**
     * Delete specified geofence asynchronously.
     *
     * @param geofence A data class that contains geofence information.
     * @return Deleted record count wrapped with {@link Single}.
     */
    @SuppressWarnings("unused")
    @Delete
    Single<Integer> deleteAsync(Geofence geofence);

    /**
     * Deletes all records from geofence table synchronously.
     *
     * @return Deleted record count.
     */
    @SuppressWarnings("unused")
    @Query("DELETE FROM Geofence")
    int deleteAll();

    /**
     * Deletes all records from geofence table asynchronously.*
     */
    @SuppressWarnings("unused")
    @Query("DELETE FROM Geofence")
    void deleteAllAsync();
}
