package com.example.alimjan.geofence.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.example.alimjan.geofence.R;
import com.example.alimjan.geofence.data.GeofenceRepository;
import com.example.alimjan.geofence.db.GeofenceDao;
import com.example.alimjan.geofence.db.GeofenceDataBase;
import com.example.alimjan.geofence.model.Geofence;
import com.example.alimjan.geofence.ui.notification.GeofenceNotification;

/**
 * An IntentService that handles geofence updateAsync from LocationManager.
 */
public class GeofenceTransitionsService extends IntentService {

    // Key for data transfer
    public static final String KEY_ID = "id";

    // Repository that handles all the data related to geofence
    private GeofenceRepository mRepository;

    public GeofenceTransitionsService() {
        super("GeofenceTransitionsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize repository and updateAsync the geofence data
        Context context = getApplicationContext();
        GeofenceDao geofenceDao = GeofenceDataBase.getInstance(context).getDao();
        this.mRepository = new GeofenceRepository(context, geofenceDao);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // Extract data from intent
            // Indicating enter when flag is true, exit when flag is false
            boolean triggerFlag = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
            long id = intent.getLongExtra(KEY_ID, -1);
            Geofence geofence = this.mRepository.getGeofence(id);

            // If this is a exit trigger which means all geofence action triggered and data should be
            // updated.
            if (!triggerFlag) {
                geofence.setTriggered(true);
                this.mRepository.updateGeofenceAsync(geofence);
            }

            // Show different message according to flag
            String title;
            String message;
            int notificationId;
            if (triggerFlag) {
                notificationId = GeofenceNotification.NOTIFICATION_ID_GEOFENCE_ENTER;
                title = getString(R.string.title_geofence_enter);
                message = String.format(getString(R.string.message_geofence_enter_format), geofence.getAddress());
            } else {
                notificationId = GeofenceNotification.NOTIFICATION_ID_GEOFENCE_EXIT;
                title = getString(R.string.title_geofence_exit);
                message = String.format(getString(R.string.message_geofence_exit_format), geofence.getAddress());
            }

            // Notify the result of geofence trigger
            GeofenceNotification.notify(this, title, message, notificationId);
        }
    }
}
