package com.example.alimjan.geofence.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * A model class that represents a table in room database, and also used by many other components
 * throughout application for data handling.
 */
@Entity(tableName = "geofence")
public class Geofence implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "expireTime")
    private long expireTime;

    @ColumnInfo(name = "isTriggered")
    private boolean isTriggered;

    @Ignore
    public Geofence() {
    }

    public Geofence(String address, double latitude, double longitude, long expireTime) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expireTime = expireTime;
    }


    protected Geofence(Parcel in) {
        id = in.readLong();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        expireTime = in.readLong();
        isTriggered = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(expireTime);
        dest.writeByte((byte) (isTriggered ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Geofence> CREATOR = new Creator<Geofence>() {
        @Override
        public Geofence createFromParcel(Parcel in) {
            return new Geofence(in);
        }

        @Override
        public Geofence[] newArray(int size) {
            return new Geofence[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setTriggered(boolean triggered) {
        isTriggered = triggered;
    }

    @NonNull
    @Override
    public String toString() {
        return "Geofence {" +
                "id = '" + this.id + '\'' +
                ", address = '" + this.address + '\'' +
                ", latitude = '" + this.latitude + '\'' +
                ", longitude = '" + this.longitude + '\'' +
                ", expireTime = '" + this.expireTime + '\'' +
                ", isTriggered = '" + this.isTriggered + '\'' +
                "}";
    }
}
