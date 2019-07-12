package com.example.husseinjehadalhroub.betawaytracker.activities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;

import com.example.husseinjehadalhroub.betawaytracker.R;
import com.example.husseinjehadalhroub.betawaytracker.data.DBHelperReaderOnly;
import com.example.husseinjehadalhroub.betawaytracker.data.TripLineDetails;
import com.example.husseinjehadalhroub.betawaytracker.data.TripLineDetailsWMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ReaderMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int tripId;
    ArrayList<ArrayList> arrayLists = null;
    private DBHelperReaderOnly db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tripId = getIntent().getIntExtra("triptable", -1);
        if (tripId == -1) {
            finish();
            System.out.println("Hmmm, I have no idea what happened but I decided to not load");
            return;
        }


        System.out.println("I am reader Map Activity class!!!!!!");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        db = new DBHelperReaderOnly(this);
        try {
            arrayLists = new GetTripInfo().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mMap = googleMap;
        ArrayList<TripLineDetails> tripLineDetailsArrayList = arrayLists.get(0);
        ArrayList<TripLineDetailsWMarker> tripLineDetailsArrayListWMarker = arrayLists.get(1);

        for (int i = 0; i < tripLineDetailsArrayList.size(); i++) {
            TripLineDetails tripLineDetails = tripLineDetailsArrayList.get(i);
            LatLng latLng1 = tripLineDetails.getLatLng1();
            LatLng latLng2 = tripLineDetails.getLatLng2();
            int color = tripLineDetails.getColor();
            mMap.addPolyline(new PolylineOptions()
                    .add(latLng1, latLng2)
                    .width(5)
                    .color(color));

        }

        for (int i = 0; i < tripLineDetailsArrayListWMarker.size(); i++) {
            TripLineDetailsWMarker tripLineDetailsWMarker = tripLineDetailsArrayListWMarker.get(i);
            LatLng latLng1 = tripLineDetailsWMarker.getLatLng1();
            LatLng latLng2 = tripLineDetailsWMarker.getLatLng2();
            int color = tripLineDetailsWMarker.getColor();
            MarkerOptions markerOptions = tripLineDetailsWMarker.getMarker();
            mMap.addPolyline(new PolylineOptions()
                    .add(latLng1, latLng2)
                    .width(5)
                    .color(color));

            mMap.addMarker(markerOptions);


        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tripLineDetailsArrayListWMarker.get(0).getLatLng1(), 17.7f));

    }


    private class GetTripInfo extends AsyncTask<Void, Void, ArrayList<ArrayList>> {


        @Override
        protected ArrayList<ArrayList> doInBackground(Void... voids) {
            System.out.println("hmmmmmmmmmmm...?!??!?!?!!");
            return db.readTripsDetails(tripId);
        }
    }
}
