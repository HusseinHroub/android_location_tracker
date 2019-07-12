package com.example.husseinjehadalhroub.betawaytracker.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TripLineDetailsWMarker extends TripLineDetails {
    private MarkerOptions marker;

    public TripLineDetailsWMarker(LatLng latLng1, LatLng latLng2, int color, MarkerOptions marker) {
        super(latLng1, latLng2, color);
        this.marker = marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public MarkerOptions getMarker() {

        return marker;
    }
}
