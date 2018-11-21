package com.example.alimjan.geofence.db;

import android.content.Context;

import com.example.alimjan.geofence.model.Geofence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * A RoomDatabase implementation for data persistence.
 */
@Database(
        entities = Geofence.class,
        version = 1,
        exportSchema = false
)
public abstract class GeofenceDataBase extends RoomDatabase {
    private static GeofenceDataBase INSTANCE;

    public abstract GeofenceDao getDao();

    /**
     * Returns {@link RoomDatabase} instance for database access.
     */
    public static GeofenceDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GeofenceDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, GeofenceDataBase.class, "geofence.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Used for testing purpose only. information stored in an in memory database disappears
     * when the process is killed.
     */
    public static GeofenceDataBase getInMemoryInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GeofenceDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.inMemoryDatabaseBuilder(context, GeofenceDataBase.class)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Destroys database instance when it no longer to be used, typically when component destroys.
     */
    @SuppressWarnings("unused")
    public static void destroyDatabaseInstance() {
        INSTANCE = null;
    }
}
