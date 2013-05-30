package fe.up.pt.joggingo;

public class Point {

	int id;
	String latitude;
	String longitude;
	long track_id;
	
	public Point(){}

	public Point(int id, String latitude, String longitude, long track) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.track_id = track;
	}
	
	public Point(String latitude, String longitude, long track) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.track_id = track;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public long getTrack_id() {
		return track_id;
	}

	public void setTrack_id(int track_id) {
		this.track_id = track_id;
	}
}
