package com.example.alimjan.geofence.data;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

/**
 * A callback interface that simplifies asynchronous data access.
 *
 * @param <T> A generic data type.
 */
public interface OnAsyncTaskCallback<T> {

    @UiThread
    void onSuccess(@NonNull T data);

    @UiThread
    void onError(@NonNull Throwable throwable);
}
