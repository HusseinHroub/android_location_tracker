package com.example.husseinjehadalhroub.betawaytracker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.husseinjehadalhroub.betawaytracker.R;
import com.example.husseinjehadalhroub.betawaytracker.data.EntityDBHelper;
import com.example.husseinjehadalhroub.betawaytracker.data.TripInfo;
import com.example.husseinjehadalhroub.betawaytracker.services.GpsService;
import com.example.husseinjehadalhroub.betawaytracker.services.WriteLastMarker;
import com.example.husseinjehadalhroub.betawaytracker.services.WriteService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private DrawerLayout mDrawerLayout;

    LocationManager locationManager;
    Location locationA;
    Location lastKnowLocation;

    private boolean firstCall = true;
    private Marker endMarker;
    private GoogleMap mMap;
    private CountDownTimer countDownTimer;
    private boolean isCountDownTimerFinished = true;
    private Handler handler;
    private static final long HANDLER_INTERVAL = 1000;
    int counterTest = 0;


    private static final float SPEED_25 = 6.944f;
    private static final float SPEED_55 = 15.27f;


    //services
    Intent serviceIntent;

    //TEMP
    TextView timerView;
    TextView counterView;

    //Timer counterDonw
    private static final long COUNTDOWN = 5000;

    //Testing BroadCast
    private DataUpdateReceiver dataUpdateReceiver;


    private static final String ACTION = GpsService.ACTION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        counterView = findViewById(R.id.counter);
        timerView = findViewById(R.id.timer);

        handler = new Handler();

        countDownTimer = new CountDownTimer(COUNTDOWN, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
//                System.out.println("I am onTick, millis until finished are: " + millisUntilFinished);
                timerView.setText(Long.toString(millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {
                counterTest = (int) (COUNTDOWN / 1000) - 1;
                handler.post(runnableCode);
//                System.out.println("I am on finish");
                isCountDownTimerFinished = true;
            }
        };


        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        System.out.println("Now I must appear list of trips!!");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        return true;
                    }
                });


    }

    private void requestRec() {
        dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION);
        registerReceiver(dataUpdateReceiver, intentFilter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //REMOVE AFTER DEBUGGING
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                System.out.println("LAT = " + latLng.latitude + " LongTit = " + latLng.longitude);
//            }
//        });


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void startButton(View view) {
        System.out.println("start button");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        } else {

            startInit();


        }


    }


    private void startInit() {
        TextView textView = findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);
        Button buttonStart = findViewById(R.id.button);
        buttonStart.setVisibility(View.INVISIBLE);

        addMakrer();

    }

    @SuppressLint("MissingPermission")
    private void addMakrer() {
        // locationA = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationA == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0, new LocationWaiting());
            Toast.makeText(getApplicationContext(), "Please Wait,", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.clear();
        TripInfo.helper = new EntityDBHelper(this);
        setTripId();
        LatLng latLng = new LatLng(locationA.getLatitude(), locationA.getLongitude());
        lastKnowLocation = locationA;
        endMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("End").visible(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.7f));
        serviceIntent = new Intent(this, GpsService.class);
        startService(serviceIntent);


        requestRec();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
        startTimer();

        Button button = findViewById(R.id.button2);
        button.setVisibility(View.VISIBLE);
    }

    private void setTripId() {
        //return last known trip ID and increment
        SharedPreferences prefs = getSharedPreferences("tripId", MODE_PRIVATE);
        int lastId = prefs.getInt("tripid", 0) + 1;

        //save
        SharedPreferences.Editor editor = getSharedPreferences("tripId", MODE_PRIVATE).edit();
        TripInfo.setCurrentTripId(lastId);
        editor.putInt("tripid", lastId);
        editor.apply();
        System.out.println("saved current trip ID = " + TripInfo.getCurrentTripId());

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startInit();

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void endTrip(View v) {
        Button button = findViewById(R.id.button2);
        button.setVisibility(View.INVISIBLE);
        System.out.println("end trip button");
        stopServiceAndRec();

        //save last marker
        lastMarkerSave();

        reset();


        Button button1 = findViewById(R.id.button);
        button1.setVisibility(View.VISIBLE);


    }

    private void lastMarkerSave() {


        if (TripInfo.getCurrentLineId() != 0) {
            Intent intent = new Intent(this, WriteLastMarker.class);
            startService(intent);
            System.out.println("Service was rning AND u closed the app suddenly, therefore I saved the last marker :DDDDD np");
        } else {

            SharedPreferences.Editor editor = getSharedPreferences("tripId", MODE_PRIVATE).edit();
            TripInfo.setCurrentTripId(TripInfo.getCurrentTripId() - 1);
            editor.putInt("tripid", TripInfo.getCurrentTripId());
            System.out.println("I have removed trip" + TripInfo.getCurrentTripId());
            editor.apply();
        }
    }

    private void reset() {
        countDownTimer.cancel();
        handler.removeCallbacks(runnableCode);
        firstCall = true;
        locationA = null;
        lastKnowLocation = null;
        dataUpdateReceiver = null;
        isCountDownTimerFinished = true;

    }


    private void setEndMarker(LatLng latLng) {
        if (!endMarker.isVisible())
            endMarker.setVisible(true);


        endMarker.setPosition(latLng);


    }


//    public void secondButton(View v) {
//        Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationA.getLatitude(), locationA.getLongitude())).title("rPoint"));
//        mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(lastKnowLocation.getPosition().latitude, lastKnowLocation.getPosition().longitude), new LatLng(locationA.getLatitude(), locationA.getLongitude()))
//                .width(5)
//                .color(Color.RED));
//
//        lastKnowLocation = newMarkerf;
//    }


    public class LocationWaiting implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            locationA = location;
//            Log.i("CLASS wAITING", "Yup, I found the location, I will shutdown now ;) GOOD BYE");
            Toast.makeText(getApplicationContext(), "FOund location", Toast.LENGTH_SHORT).show();
            locationManager.removeUpdates(this);

            addMakrer();


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


    private void startTimer() {
        isCountDownTimerFinished = false;
        countDownTimer.start();
    }

    private void cancleTimerAndHandle(Intent saveData) {
        if (isCountDownTimerFinished) {
            handler.removeCallbacks(runnableCode);
            mMap.addMarker(new MarkerOptions().position(new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude()))
                    .title(counterTest + " Seconds.")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//            saveData.putExtra("isThereMarker", true);
            saveData.putExtra("delay", counterTest);
            counterTest = 0;
        } else {

            countDownTimer.cancel();

        }

        startService(saveData);


    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
//            System.out.println("I am the Runnable code.");
//            System.out.println(++counterTest);
            counterTest++;
            counterView.setText(Integer.toString(counterTest));
            handler.postDelayed(runnableCode, HANDLER_INTERVAL);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServiceAndRec();
    }

    private void stopServiceAndRec() {
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
        if (isMyServiceRunning(GpsService.class)) {
            lastMarkerSave();

        }

        stopService(serviceIntent);


    }


    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {

                updateMap(intent);
            }
        }
    }

    private void updateMap(Intent intent) {
        Location location = intent.getExtras().getParcelable("location");
        locationA = location;
        if (firstCall) {
            System.out.println("first call...");
            firstCall = false;
            return;
        }

        String colorName;
        LatLng latLng2 = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng latLng1 = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
        float speed = location.getSpeed();
        int color;
        if (speed > -1.0 && speed < SPEED_25) {//Speed is between 0 and 25km/hour
            color = Color.RED;
            colorName = "red";
        } else if (speed >= SPEED_25 && speed < SPEED_55) {//Speed is between 25 and 55
            color = Color.rgb(102, 34, 0);
            colorName = "brown";
        } else {
            color = Color.GREEN;
            colorName = "green";
        }

        mMap.addPolyline(new PolylineOptions()
                .add(latLng1, latLng2)
                .width(5)
                .color(color));
        setEndMarker(latLng2);

        //save data info
        Intent saveData = new Intent(getApplicationContext(), WriteService.class);
        saveData.putExtra("latlng1", latLng1);
        saveData.putExtra("latlng2", latLng2);
        saveData.putExtra("color", colorName);

        //Reset the timer, and handle the counterTimer if Found, and complete saving pross
        cancleTimerAndHandle(saveData);
        startTimer();


        //Line Counter
//        counter++;
//        counterView.setText(Integer.toString(counter));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng2));
        lastKnowLocation = location;
    }

    private void teswsst()
    {

    }


}
