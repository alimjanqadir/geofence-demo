package com.example.alimjan.geofence;

import com.mapbox.mapboxsdk.Mapbox;

import timber.log.Timber;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize MapBox SDK
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // Initialize Timber
        Timber.plant(new Timber.DebugTree());
    }
}
