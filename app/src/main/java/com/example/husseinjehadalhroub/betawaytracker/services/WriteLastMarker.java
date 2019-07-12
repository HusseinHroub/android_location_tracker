package com.example.husseinjehadalhroub.betawaytracker.services;

import android.app.IntentService;
import android.content.Intent;

import static com.example.husseinjehadalhroub.betawaytracker.data.TripInfo.helper;

public class WriteLastMarker extends IntentService {

    public WriteLastMarker() {
        super("WriteLastMarker");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        helper.setLastMarker();
    }


    @Override
    public void onDestroy() {
        System.out.println("LastMarker is saved");
        super.onDestroy();
    }
}
