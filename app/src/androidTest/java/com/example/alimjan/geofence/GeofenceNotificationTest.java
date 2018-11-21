package com.example.alimjan.geofence;

import android.content.Context;

import com.example.alimjan.geofence.ui.notification.GeofenceNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)

public class GeofenceNotificationTest {

    private static final long TIMEOUT = 1000 * 5;

    private Context mContext;

    private String mNotificationTitleGeofenceEnter;
    private String mNotificationTitleGeofenceExit;
    private String mNotificationMessageGeofenceEnter;
    private String mNotificationMessageGeofenceExit;

    @Before
    public void setUp() {
        // Prepare service and data
        this.mContext = ApplicationProvider.getApplicationContext();
        this.mNotificationTitleGeofenceEnter = mContext.getString(R.string.title_geofence_enter);
        this.mNotificationTitleGeofenceExit = mContext.getString(R.string.title_geofence_exit);
        final String address = "Oriental Perl";
        this.mNotificationMessageGeofenceEnter = String.format(mContext.getString(R.string.message_geofence_enter_format), address);
        this.mNotificationMessageGeofenceExit = String.format(mContext.getString(R.string.message_geofence_exit_format), address);
    }

    @Test
    public void testWithGeofenceEnter() {
        // Notify the result of geofence trigger
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceEnter, mNotificationMessageGeofenceEnter, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_ENTER);

        // Query
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(mNotificationTitleGeofenceEnter)), TIMEOUT);
        UiObject2 title = device.findObject(By.text(mNotificationTitleGeofenceEnter));
        UiObject2 message = device.findObject(By.text(mNotificationMessageGeofenceEnter));

        // Assert
        assertEquals(mNotificationTitleGeofenceEnter, title.getText());
        assertEquals(mNotificationMessageGeofenceEnter, message.getText());

        // TODO Just for seeing notification result on device can be removed at any time.
        device.waitForIdle(TIMEOUT);
    }

    @Test
    public void testWithGeofenceExit() {
        // Notify the result of geofence trigger
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceExit, mNotificationMessageGeofenceExit, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_EXIT);

        // Query
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(mNotificationTitleGeofenceExit)), TIMEOUT);
        UiObject2 title = device.findObject(By.text(mNotificationTitleGeofenceExit));
        UiObject2 message = device.findObject(By.text(mNotificationMessageGeofenceExit));

        // Assert
        assertEquals(mNotificationTitleGeofenceExit, title.getText());
        assertEquals(mNotificationMessageGeofenceExit, message.getText());

        // TODO Just for seeing notification result on device can be removed at any time.
        device.waitForIdle(TIMEOUT);
    }

    @Test
    public void testWithBoth() {
        // Notify the result of geofence trigger
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceEnter, mNotificationMessageGeofenceEnter, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_ENTER);
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceExit, mNotificationMessageGeofenceExit, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_EXIT);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(mNotificationTitleGeofenceEnter)), TIMEOUT);
        // Geofence enter
        UiObject2 titleGeofenceEnter = device.findObject(By.text(mNotificationTitleGeofenceEnter));
        UiObject2 messageGeofenceEnter = device.findObject(By.text(mNotificationMessageGeofenceEnter));

        // Geofence exit
        UiObject2 titleGeofenceExit = device.findObject(By.text(mNotificationTitleGeofenceExit));
        UiObject2 messageGeofenceExit = device.findObject(By.text(mNotificationMessageGeofenceExit));

        // Assert geofence enter
        assertEquals(mNotificationTitleGeofenceEnter, titleGeofenceEnter.getText());
        assertEquals(mNotificationMessageGeofenceEnter, messageGeofenceEnter.getText());

        // Assert geofence enter
        assertEquals(mNotificationTitleGeofenceExit, titleGeofenceExit.getText());
        assertEquals(mNotificationMessageGeofenceExit, messageGeofenceExit.getText());
    }

    @Test
    public void testWithBothAndOutOfOrder() {
        // Notify the result of geofence trigger
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceEnter, mNotificationMessageGeofenceEnter, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_ENTER);
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceExit, mNotificationMessageGeofenceExit, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_EXIT);
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceEnter, mNotificationMessageGeofenceEnter, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_ENTER);
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceExit, mNotificationMessageGeofenceExit, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_EXIT);
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceExit, mNotificationMessageGeofenceExit, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_EXIT);
        GeofenceNotification.notify(mContext, mNotificationTitleGeofenceEnter, mNotificationMessageGeofenceEnter, GeofenceNotification.NOTIFICATION_ID_GEOFENCE_ENTER);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(mNotificationTitleGeofenceEnter)), TIMEOUT);
        // Geofence enter
        UiObject2 titleGeofenceEnter = device.findObject(By.text(mNotificationTitleGeofenceEnter));
        UiObject2 messageGeofenceEnter = device.findObject(By.text(mNotificationMessageGeofenceEnter));

        // Geofence exit
        UiObject2 titleGeofenceExit = device.findObject(By.text(mNotificationTitleGeofenceExit));
        UiObject2 messageGeofenceExit = device.findObject(By.text(mNotificationMessageGeofenceExit));

        // Assert geofence enter
        assertEquals(mNotificationTitleGeofenceEnter, titleGeofenceEnter.getText());
        assertEquals(mNotificationMessageGeofenceEnter, messageGeofenceEnter.getText());

        // Assert geofence enter
        assertEquals(mNotificationTitleGeofenceExit, titleGeofenceExit.getText());
        assertEquals(mNotificationMessageGeofenceExit, messageGeofenceExit.getText());
    }

}
