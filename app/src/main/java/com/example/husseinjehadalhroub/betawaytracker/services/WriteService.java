package com.example.husseinjehadalhroub.betawaytracker.services;

import android.app.IntentService;
import android.content.Intent;

import com.example.husseinjehadalhroub.betawaytracker.data.TripInfo;
import com.google.android.gms.maps.model.LatLng;

import static com.example.husseinjehadalhroub.betawaytracker.data.TripInfo.helper;

public class WriteService extends IntentService {




    public WriteService() {
        super("WriteData");

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.


        //LineInfo
        String long1 = Double.toString(((LatLng) intent.getParcelableExtra("latlng1")).longitude);
        String long2 = Double.toString(((LatLng) intent.getParcelableExtra("latlng2")).longitude);
        String lat1 = Double.toString(((LatLng) intent.getParcelableExtra("latlng1")).latitude);
        String lat2 = Double.toString(((LatLng) intent.getParcelableExtra("latlng2")).latitude);
        String lineColor = intent.getStringExtra("color");

        int delay = intent.getIntExtra("delay", -1);
        if (delay != -1) {
            helper.insert(long1, long2, lat1, lat2, lineColor, delay);
        } else
            helper.insert(long1, long2, lat1, lat2, lineColor);





    }




    @Override
    public void onDestroy() {
        System.out.println("Saved line number " + TripInfo.getCurrentLineId());
        super.onDestroy();
    }


}
