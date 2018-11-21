package com.example.alimjan.geofence.model;

/**
 * A data class that represents a point in map. it includes point and associated address.
 */
public class Place {
    private Point point;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
