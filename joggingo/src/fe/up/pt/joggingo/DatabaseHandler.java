package fe.up.pt.joggingo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String TRACK_KEY_VMEDIA = "vMedia";
    private static final String TRACK_KEY_DISTANCE = "distant";
    private static final String TRACK_KEY_INITIAL_TIME = "initial_time";
    private static final String TRACK_KEY_FINAL_TIME = "final_time";
    
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
                + TRACK_KEY_INITIAL_TIME + " TEXT,"+ TRACK_KEY_FINAL_TIME + " TEXT,"
                + TRACK_KEY_VMEDIA + " TEXT," + TRACK_KEY_DISTANCE + " TEXT,"
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
        values.put(TRACK_KEY_INITIAL_TIME, track.getInitial_time());
        values.put(TRACK_KEY_FINAL_TIME, track.getFinal_time());
        values.put(TRACK_KEY_VMEDIA, track.getvMedia());
        values.put(TRACK_KEY_DISTANCE, track.getDistance());
     
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
        		TRACK_KEY_PRIVATE, TRACK_KEY_APPROVED,
        		TRACK_KEY_INITIAL_TIME, TRACK_KEY_FINAL_TIME,
        		TRACK_KEY_VMEDIA, TRACK_KEY_DISTANCE}, TRACK_KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
            
        /*0 - int id, 
         * 1 - String name, 
         * 2 - String city, 
         * 3 - String country, 
         * 4 - int user_id,
			5 - int privat, 
			6 - int approved,  
			7 - String initial_time, 
			8 - String final_time
			9 - double vMedia;
			10 - double distance;
		*/
        
        Track track = new Track(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), cursor.getString(7),
                cursor.getString(8), Double.parseDouble(cursor.getString(9)), Double.parseDouble(cursor.getString(10)));
        // return track
        return track;
    }
     
    // Getting All tracks
    public List<Track> getAllTracks() {
        List<Track> trackList = new ArrayList<Track>();
        // Select All Query
        String selectQuery = "SELECT  " + TRACK_KEY_ID+"," +TRACK_KEY_NAME + "," + TRACK_KEY_CITY + "," +
        		TRACK_KEY_COUNTRY + "," + TRACK_KEY_USERID+ ","+
        		TRACK_KEY_PRIVATE +"," +TRACK_KEY_APPROVED+ ","+
        		TRACK_KEY_INITIAL_TIME +","+ TRACK_KEY_FINAL_TIME+","+
        		TRACK_KEY_VMEDIA+","+ TRACK_KEY_DISTANCE+" FROM " + TABLE_TRACKS;
     
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        /*0 - int id, 
         * 1 - String name, 
         * 2 - String city, 
         * 3 - String country, 
         * 4 - int user_id,
			5 - int privat, 
			6 - int approved,  
			7 - String initial_time, 
			8 - String final_time
			9 - double vMedia;
			10 - double distance;
		*/
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	Track track = new Track(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), cursor.getString(7),
                        cursor.getString(8), Double.parseDouble(cursor.getString(9)), Double.parseDouble(cursor.getString(10)));
                
                // Adding contact to list
                trackList.add(track);
            } while (cursor.moveToNext());
        }
        // return contact list
        return trackList;
    }
    
    // Deleting single Track
    public void deleteTrack(int track_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACKS, TRACK_KEY_ID + " = ?",
                new String[] { String.valueOf(track_id) });        
        db.close();
        deleteAllPoints(track_id);
    }
    
    //Delete all tracks
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
    
    public List<Point> getAllPoint() {
        List<Point> pointList= new ArrayList<Point>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_POINTS;
     
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
    
    public void deleteAllPoints(int track_id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_POINTS, POINT_KEY_TRACK + " = ?",
                new String[] { String.valueOf(track_id) });
    	db.close();
    }
    
    public void restartDB(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
    	onCreate(db);
    	db.close();
    }

    public void updateTrack(Track t){
    	Log.d("update", t.getId()+"");
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues cv = new ContentValues();
    	cv.put(TRACK_KEY_FINAL_TIME,t.getFinal_time()); //These Fields should be your String values of actual column names
    	db.update(TABLE_TRACKS, cv, TRACK_KEY_ID+"="+t.getId(), null);
    	db.close();
    }
    
}
