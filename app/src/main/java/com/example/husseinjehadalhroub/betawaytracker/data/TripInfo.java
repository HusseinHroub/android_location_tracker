package com.example.husseinjehadalhroub.betawaytracker.data;

public class TripInfo {

    private static int CURRENT_TRIP_ID;
    private static long CURRENT_LINE_ID = 0;


    //LastKnowLocation and CurrentLocation
//    private static Location currentLocation;
//    private static Location lastKnowLocation;


    //DBM
    public static EntityDBHelper helper;


    public static final int START_MARKER = 0;
    public static final int DELAY_MARKER = 1;
    public static final int END_MARKER = 2;




    public static final String CREATE_TRIBE_TABLE = "CREATE TABLE IF NOT EXISTS " + TripTable.TABLE_NAME
            + " (" + TripTable.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + TripTable.COLUMN_TRIBNAME + " TEXT);";


    public static final String CREATE_LINE_TABLE = "CREATE TABLE IF NOT EXISTS " + Line.TABLE_NAME
            + " (" + Line.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + Line.COLUMN_LAT1 + " TEXT," + Line.COLUMN_LAT2 + " TEXT,"
            + Line.COLUMN_LONG1 + " TEXT," + Line.COLUMN_LONG2 + " TEXT,"
            + Line.COLUMN_LINE_COLOR + " TEXT,"
            + Line.COLUMN_TRIB_ID + " INTEGER, FOREIGN KEY(" + Line.COLUMN_TRIB_ID + ") REFERENCES " + TripTable.TABLE_NAME + '(' + TripTable.COLUMN_ID + ")" +
            " ON DELETE CASCADE);";

    public static final String CREATE_MARKER_TABLE = "CREATE TABLE IF NOT EXISTS " + Marker.TABLE_NAME
            + " (" + Marker.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + Marker.COLUMN_TYPE + " INTEGER," + Marker.COLUMN_LINE_ID + " INTEGER,"
            + " FOREIGN KEY (" + Marker.COLUMN_LINE_ID + ") REFERENCES " + Line.TABLE_NAME + '(' + Line.COLUMN_ID + ") ON DELETE CASCADE);";

    public static final String CREATE_DELAY_MARKER_TABLE = "CREATE TABLE IF NOT EXISTS " + DelayMarker.TABLE_NAME
            + " (" + DelayMarker.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + DelayMarker.COLUMN_DELAY_TIME + " INTEGER,"
            + " FOREIGN KEY (" + DelayMarker.COLUMN_ID + ") REFERENCES " + Marker.TABLE_NAME + '(' + Marker.COLUMN_ID + ") ON DELETE CASCADE);";


    public static int getCurrentTripId() {
        return CURRENT_TRIP_ID;
    }

    public static void setCurrentTripId(int currentTripId) {
        CURRENT_TRIP_ID = currentTripId;
    }

//    public static Location getCurrentLocation() {
//        return currentLocation;
//    }
//
//    public static Location getLastKnowLocation() {
//        return lastKnowLocation;
//    }
//
//    public static void setCurrentLocation(Location currentLocation) {
//        TripInfo.currentLocation = currentLocation;
//    }
//
//    public static void setLastKnowLocation(Location lastKnowLocation) {
//        TripInfo.lastKnowLocation = lastKnowLocation;
//    }

    public static long getCurrentLineId() {
        return CURRENT_LINE_ID;
    }

    public static void setCurrentLineId(long currentLineId) {
        CURRENT_LINE_ID = currentLineId;
    }

    private TripInfo() {

    }

    public static class TripTable {
        public static final String TABLE_NAME = "TripTable";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_TRIBNAME = "TripName";

    }

    public static class Line {
        public static final String TABLE_NAME = "LinesTable";
        public static final String COLUMN_ID = "lID";
        public static final String COLUMN_LAT1 = "Lat_1";
        public static final String COLUMN_LAT2 = "Lat_2";
        public static final String COLUMN_LONG1 = "Long_1";
        public static final String COLUMN_LONG2 = "Long_2";
        public static final String COLUMN_LINE_COLOR = "LineColor";
        public static final String COLUMN_TRIB_ID = TripTable.COLUMN_ID;

    }

    public static class Marker {
        public static final String TABLE_NAME = "Marker";
        public static final String COLUMN_ID = "mID";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_LINE_ID = Line.COLUMN_ID;

    }

    public static class DelayMarker {
        public static final String TABLE_NAME = "MarkerDelay";
        public static final String COLUMN_ID = Marker.COLUMN_ID;
        public static final String COLUMN_DELAY_TIME = "DelayTime";

    }
}
