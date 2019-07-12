package com.example.husseinjehadalhroub.betawaytracker.data;

import com.google.android.gms.maps.model.LatLng;

public class TripLineDetails {
    private LatLng latLng1;
    private LatLng latLng2;
    private int color;


    public TripLineDetails(LatLng latLng1, LatLng latLng2, int color) {
        this.latLng1 = latLng1;
        this.latLng2 = latLng2;
        this.color = color;
    }

    public LatLng getLatLng1() {
        return latLng1;
    }

    public LatLng getLatLng2() {
        return latLng2;
    }

    public int getColor() {
        return color;
    }
}
