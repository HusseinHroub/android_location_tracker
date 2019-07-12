package com.example.husseinjehadalhroub.betawaytracker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DBHelperReaderOnly extends EntityDBHelper {

    public DBHelperReaderOnly(Context context) {
        super(context);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        //Nothing..
        System.out.println("onOPen NOTHING");
    }

    public ArrayList<String> readingTrips() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + TripInfo.TripTable.COLUMN_TRIBNAME + " FROM " + TripInfo.TripTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> arrayOfStrings = new ArrayList<>();
        int i = 0;
        while (cursor.moveToNext()) {
            arrayOfStrings.add(cursor.getString(0));
            i++;
            System.out.println(cursor.getString(0));
        }

        cursor.close();
        return arrayOfStrings;

    }
//    String query = "SELECT " + TripInfo.Line.COLUMN_LAT1 + ", " + TripInfo.Line.COLUMN_LAT2 + ", " + TripInfo.Line.COLUMN_LONG1 + ", " + TripInfo.Line.COLUMN_LONG2 + ", " + TripInfo.Line.COLUMN_LINE_COLOR + ", " + TripInfo.Marker.COLUMN_TYPE
//            + ", " + TripInfo.DelayMarker.COLUMN_DELAY_TIME +
//            " FROM " + TripInfo.Line.TABLE_NAME
//            + " left natural join " + TripInfo.Marker.TABLE_NAME + " left natural join " + TripInfo.DelayMarker.TABLE_NAME + " natural join " + TripInfo.TripTable.TABLE_NAME
//            + " WHERE " + TripInfo.TripTable.COLUMN_ID + '=' + "'" + tribId + "'";

    public ArrayList<ArrayList> readTripsDetails(int tribId) {
        tribId++;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + TripInfo.Line.COLUMN_LAT1 + ", " + TripInfo.Line.COLUMN_LAT2 + ", " + TripInfo.Line.COLUMN_LONG1 + ", " + TripInfo.Line.COLUMN_LONG2 + ", " + TripInfo.Line.COLUMN_LINE_COLOR + ", " + TripInfo.Marker.COLUMN_TYPE
                + ", " + TripInfo.DelayMarker.COLUMN_DELAY_TIME +
                " FROM " + TripInfo.Line.TABLE_NAME
                + " left natural join " + TripInfo.Marker.TABLE_NAME + " left natural join " + TripInfo.DelayMarker.TABLE_NAME + " natural join " + TripInfo.TripTable.TABLE_NAME
                + " WHERE " + TripInfo.TripTable.COLUMN_ID + '=' + "'" + tribId + "'";
        System.out.println(query);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<TripLineDetails> tripLineDetailsArrayList = new ArrayList<>();
        ArrayList<TripLineDetailsWMarker> tripLineDetailsWMarkers = new ArrayList<>();


        int lat1_index = cursor.getColumnIndex(TripInfo.Line.COLUMN_LAT1);
        int lat2_index = cursor.getColumnIndex(TripInfo.Line.COLUMN_LAT2);
        int long1_index = cursor.getColumnIndex(TripInfo.Line.COLUMN_LONG1);
        int long2_index = cursor.getColumnIndex(TripInfo.Line.COLUMN_LONG2);
        int type_index = cursor.getColumnIndex(TripInfo.Marker.COLUMN_TYPE);
        int line_color_index = cursor.getColumnIndex(TripInfo.Line.COLUMN_LINE_COLOR);
        int delay_index = cursor.getColumnIndex(TripInfo.DelayMarker.COLUMN_DELAY_TIME);

        System.out.println("Before while cursor move next");

        while (cursor.moveToNext()) {

            String lat1 = cursor.getString(lat1_index);
            String lat2 = cursor.getString(lat2_index);
            String long1 = cursor.getString(long1_index);
            String long2 = cursor.getString(long2_index);

            LatLng latLng1 = new LatLng(Double.valueOf(lat1), Double.valueOf(long1));
            LatLng latLng2 = new LatLng(Double.valueOf(lat2), Double.valueOf(long2));

            String stringColor = cursor.getString(line_color_index);
            int color;
            if (stringColor.equals("red"))
                color = Color.RED;
            else if (stringColor.equals("green"))
                color = Color.GREEN;
            else
                color = Color.rgb(102, 34, 0);

            if (!cursor.isNull(type_index)) {
                int markerType = cursor.getInt(type_index);
                MarkerOptions marker;

                if (markerType == 0)
                    marker = new MarkerOptions().title("Start").position(latLng1);
                else if (markerType == 1)
                    marker = new MarkerOptions().title(cursor.getInt(delay_index) + " Seconds").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(latLng1);
                else
                    marker = new MarkerOptions().title("End").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng2);


                TripLineDetailsWMarker tripLineDetailsM = new TripLineDetailsWMarker(latLng1, latLng2, color
                        , marker);
                tripLineDetailsWMarkers.add(tripLineDetailsM);
            } else {
                TripLineDetails tripLineDetails = new TripLineDetails(latLng1, latLng2, color);
                tripLineDetailsArrayList.add(tripLineDetails);
            }


        }

        cursor.close();

        ArrayList<ArrayList> arrayLists = new ArrayList<>();
        arrayLists.add(tripLineDetailsArrayList);
        arrayLists.add(tripLineDetailsWMarkers);

        return arrayLists;


    }
}
