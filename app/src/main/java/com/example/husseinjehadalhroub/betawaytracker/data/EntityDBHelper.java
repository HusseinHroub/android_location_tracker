package com.example.husseinjehadalhroub.betawaytracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EntityDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATA_BASE_NAME = "Entities.db";

    public EntityDBHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        //Save the start Marker..
        ContentValues values = new ContentValues();
        values.put(TripInfo.TripTable.COLUMN_TRIBNAME, "Trip " + TripInfo.getCurrentTripId());
        long id = db.insert(TripInfo.TripTable.TABLE_NAME, null, values);
        System.out.println("table id =" + id);
        values.clear();
        values.put(TripInfo.Marker.COLUMN_TYPE, TripInfo.START_MARKER);
        values.put(TripInfo.Marker.COLUMN_LINE_ID, getLastLineId(db) + 1);
        db.insert(TripInfo.Marker.TABLE_NAME, null, values);
        db.execSQL("PRAGMA foreign_keys = ON");
        System.out.println("onOpen");


    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TripInfo.CREATE_TRIBE_TABLE);
        db.execSQL(TripInfo.CREATE_LINE_TABLE);
        db.execSQL(TripInfo.CREATE_MARKER_TABLE);
        db.execSQL(TripInfo.CREATE_DELAY_MARKER_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public void insert(String long1, String long2, String lat1, String lat2, String lineColor, int delay) {

        insertMarker(insert(long1, long2, lat1, lat2, lineColor), delay);

        System.out.println("Added marker");

    }

    private void insertMarker(long lineId, int delay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TripInfo.Marker.COLUMN_TYPE, TripInfo.DELAY_MARKER);
        values.put(TripInfo.Marker.COLUMN_LINE_ID, lineId);
        long markerID = db.insert(TripInfo.Marker.TABLE_NAME, null, values);

        values.clear();
        values.put(TripInfo.DelayMarker.COLUMN_ID, markerID);
        values.put(TripInfo.DelayMarker.COLUMN_DELAY_TIME, delay);
        db.insert(TripInfo.DelayMarker.TABLE_NAME, null, values);
    }

    public long insert(String long1, String long2, String lat1, String lat2, String lineColor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TripInfo.Line.COLUMN_LAT1, lat1);
        values.put(TripInfo.Line.COLUMN_LAT2, lat2);
        values.put(TripInfo.Line.COLUMN_LONG1, long1);
        values.put(TripInfo.Line.COLUMN_LONG2, long2);
        values.put(TripInfo.Line.COLUMN_LINE_COLOR, lineColor);
        values.put(TripInfo.Line.COLUMN_TRIB_ID, TripInfo.getCurrentTripId());

        TripInfo.setCurrentLineId(db.insert(TripInfo.Line.TABLE_NAME, null, values));
        System.out.println("added line number " + TripInfo.getCurrentLineId());
        return TripInfo.getCurrentLineId();


    }


    private int getLastLineId(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT " + TripInfo.Line.COLUMN_ID + " FROM " +
                TripInfo.Line.TABLE_NAME + " ORDER BY " + TripInfo.Line.COLUMN_ID +
                " DESC LIMIT 1", null);

        if (!cursor.moveToFirst()) {
            int id = 0;
            TripInfo.setCurrentLineId(id);

            return id;
        }
        int id = cursor.getInt(0);
        cursor.close();
        TripInfo.setCurrentLineId(id);
        System.out.println("I am in getLastLineId was " + id);
        return id;

    }


    public void setLastMarker() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TripInfo.Marker.COLUMN_TYPE, TripInfo.END_MARKER);
        values.put(TripInfo.Marker.COLUMN_LINE_ID, TripInfo.getCurrentLineId());
        db.insert(TripInfo.Marker.TABLE_NAME, null, values);
        System.out.println("last line was, " + TripInfo.getCurrentLineId());
        TripInfo.setCurrentLineId(0);


    }

}
