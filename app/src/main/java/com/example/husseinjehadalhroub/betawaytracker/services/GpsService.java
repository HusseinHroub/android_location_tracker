package com.example.husseinjehadalhroub.betawaytracker.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.husseinjehadalhroub.betawaytracker.activities.MapsActivity;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class GpsService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocationManager locationManager;
    private final int FOR_GROUND_ID = 1;
    LocationListener locationListener;
    private static boolean stopped = true;


    private Intent intent;
    public static final String ACTION = "UPD";

    public static boolean isStopped() {
        return stopped;
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler implements LocationListener {
        public ServiceHandler(Looper looper) {
            super(looper);
        }


        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {
            initializeLocationManager();
            intent = new Intent(ACTION);
            locationListener = this;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, locationListener);


//            stopSelf(msg.arg1);
        }

        @Override
        public void onLocationChanged(Location loc) {
            System.out.println("Yes, I have been changed");
            intent.putExtra("location", loc);
            sendBroadcast(intent);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.

        setInForGround();

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    private void setInForGround() {
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String Channel_Id = createNotificationChannel("gps_updater", "gps_service");
            Notification notification =
                    new Notification.Builder(this, Channel_Id)
                            .setContentTitle("Gps App")
                            .setContentText("Updating Gps")
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setContentIntent(pendingIntent)
                            .setTicker("test")
                            .build();

            startForeground(FOR_GROUND_ID, notification);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);

        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(channel);

        return channelId;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopped = false;
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        if (locationListener != null)
            locationManager.removeUpdates(locationListener);
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
        System.out.println("Done");
        stopped = true;
    }


    private void initializeLocationManager() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


}