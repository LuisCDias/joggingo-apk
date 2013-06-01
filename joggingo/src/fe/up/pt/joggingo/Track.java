package fe.up.pt.joggingo;

public class Track {
	
	int id;
	String name;
	String city;
	String country;
	int user_id;
	int privat;
	int approved;
	String initial_time;
	String final_time;
	double vMedia;
	double distance;
	
	

	public Track() {
	}
	
	public Track(String name, String city, String country, int user_id,
			int privat, int approved, String initial_time) {
		super();
		this.name = name;
		this.city = city;
		this.country = country;
		this.user_id = user_id;
		this.privat = privat;
		this.approved = approved;
		this.initial_time = initial_time;
	}
	
	
	public Track(int id, String name, String city, String country, int user_id,
			int privat, int approved,  String initial_time) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.country = country;
		this.user_id = user_id;
		this.privat = privat;
		this.approved = approved;
		this.initial_time = initial_time;
	}
	
	public Track(int id, String name, String city, String country, int user_id,
			int privat, int approved,  String initial_time, String final_time, double vmedia, double distance) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.country = country;
		this.user_id = user_id;
		this.privat = privat;
		this.approved = approved;
		this.initial_time = initial_time;
		this.final_time = final_time;
		this.vMedia = vmedia;
		this.distance = distance;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public int getUserId() {
		return user_id;
	}
	public void setUserId(int user_id) {
		this.user_id = user_id;
	}
	public int isPrivat() {
		return privat;
	}
	public void setPrivat(int privat) {
		this.privat = privat;
	}
	public int isApproved() {
		return approved;
	}
	public void setApproved(int approved) {
		this.approved = approved;
	}

	public String getInitial_time() {
		return initial_time;
	}

	public void setInitial_time(String initial_time) {
		this.initial_time = initial_time;
	}

	public String getFinal_time() {
		return final_time;
	}

	public void setFinal_time(String final_time) {
		this.final_time = final_time;
	}
	
	public double getvMedia() {
		return vMedia;
	}

	public void setvMedia(double vMedia) {
		this.vMedia = vMedia;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	

}
