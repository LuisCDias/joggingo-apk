package fe.up.pt.joggingo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "joggingo";
 
    // Contacts table name
    private static final String TABLE_TRACKS = "track";
    private static final String TABLE_POINTS = "point";
 
    // Track Table Columns names
    private static final String TRACK_KEY_ID = "id";
    private static final String TRACK_KEY_NAME = "name";
    private static final String TRACK_KEY_CITY = "city";
    private static final String TRACK_KEY_COUNTRY = "country";
    private static final String TRACK_KEY_USERID = "user_id";
    private static final String TRACK_KEY_PRIVATE = "private";
    private static final String TRACK_KEY_APPROVED = "approved";
    
    // Point Table Columns names
    private static final String POINT_KEY_ID = "id";
    private static final String POINT_KEY_LATITUDE = "latitude";
    private static final String POINT_KEY_LONGITUDE = "longitude";
    private static final String POINT_KEY_TRACK = "track_id";
        
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
                + TRACK_KEY_ID + " INTEGER PRIMARY KEY," + TRACK_KEY_NAME + " TEXT,"
                + TRACK_KEY_CITY + " TEXT," + TRACK_KEY_COUNTRY + " TEXT,"
                + TRACK_KEY_USERID + " INTEGER,"+ TRACK_KEY_PRIVATE + " INTEGER,"
                + TRACK_KEY_APPROVED + " INTEGER"
                +")";
        db.execSQL(CREATE_TRACKS_TABLE);
        
        String CREATE_POINT_TABLE = "CREATE TABLE " + TABLE_POINTS + "("
        		+ POINT_KEY_ID + " INTEGER PRIMARY KEY," + POINT_KEY_LATITUDE + " TEXT," +
        				POINT_KEY_LONGITUDE + " TEXT,"+ POINT_KEY_TRACK + " INTEGER"+ ")";
        
        db.execSQL(CREATE_POINT_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
        // Create tables again
        onCreate(db);
    }
    
    // Adding new track
    public long addTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        long i;
        ContentValues values = new ContentValues();
        values.put(TRACK_KEY_NAME, track.getName()); 
        values.put(TRACK_KEY_CITY, track.getCity());
        values.put(TRACK_KEY_COUNTRY, track.getCountry());
        values.put(TRACK_KEY_USERID, track.getUserId());
        values.put(TRACK_KEY_PRIVATE, track.isPrivat());
        values.put(TRACK_KEY_APPROVED, track.isApproved());
     
        // Inserting Row
        i = db.insert(TABLE_TRACKS, null, values);
        db.close(); // Closing database connection
        return i;
    }
     
    // Getting single track
    public Track getTrack(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
     
        Cursor cursor = db.query(TABLE_TRACKS, new String[] { TRACK_KEY_ID,
        		TRACK_KEY_NAME, TRACK_KEY_CITY,
        		TRACK_KEY_COUNTRY, TRACK_KEY_USERID,
        		TRACK_KEY_PRIVATE, TRACK_KEY_APPROVED}, TRACK_KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        
     
        Track track = new Track(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)), 1, 0);
        // return contact
        return track;
    }
     
    // Getting All tracks
    public List<Track> getAllTracks() {
        List<Track> trackList = new ArrayList<Track>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRACKS;
     
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        /*
        0 - TRACK_KEY_ID + " INTEGER PRIMARY KEY," 
        1 - TRACK_KEY_NAME + " TEXT,"
        2 - TRACK_KEY_CITY + " TEXT," 
        3 - TRACK_KEY_COUNTRY + " TEXT,"
        4 - TRACK_KEY_USERID +" INTEGER,"
        5 - TRACK_KEY_PRIVATE + "BOOLEAN, "+ 
        6 - TRACK_KEY_APPROVED + "BOOLEAN"
        */
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Track track = new Track();
                track.setId(Integer.parseInt(cursor.getString(0)));
                track.setName(cursor.getString(1));
                track.setCity(cursor.getString(2));
                track.setCountry(cursor.getString(3));
                track.setUserId(Integer.parseInt(cursor.getString(4)));
                track.setPrivat(0);
                track.setApproved(1);
                
                // Adding contact to list
                trackList.add(track);
            } while (cursor.moveToNext());
        }
        // return contact list
        return trackList;
    }
    
    // Deleting single Track
    public void deleteTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACKS, TRACK_KEY_ID + " = ?",
                new String[] { String.valueOf(track.getId()) });
        db.close();
    }
    
    public void deleteAllTracks(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_TRACKS, null, null);
    	db.close();
    }
    
    // Adding new Point
    public void addPoint(Point point) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(POINT_KEY_LATITUDE, point.getLatitude()); 
        values.put(POINT_KEY_LONGITUDE, point.getLongitude());
        values.put(POINT_KEY_TRACK, point.getTrack_id());
     
        // Inserting Row
        db.insert(TABLE_POINTS, null, values);
        db.close(); // Closing database connection
    }
    
    // Getting All Points
    public List<Point> getAllPoint(long track_id) {
        List<Point> pointList= new ArrayList<Point>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_POINTS +
        		" WHERE "+POINT_KEY_TRACK+" = '"+track_id+"'";
     
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Point p = new Point();
                p.setId(Integer.parseInt(cursor.getString(0)));
                p.setLatitude(cursor.getString(1));
                p.setLongitude(cursor.getString(2));
                
                
                // Adding contact to list
                pointList.add(p);
            } while (cursor.moveToNext());
        }
        // return contact list
        return pointList;
    }
    
    public void deleteAllPoints(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_POINTS, null, null);
    	db.close();
    }
    
    public void restartDB(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
    	onCreate(db);
    	db.close();
    }

    
}
