package fe.up.pt.joggingo;

public class Track {
	
	int id;
	String name;
	String city;
	String country;
	int user_id;
	int privat;
	int approved;
	
	public Track() {
	}
	
	public Track(String name, String city, String country, int user_id,
			int privat, int approved) {
		super();
		this.name = name;
		this.city = city;
		this.country = country;
		this.user_id = user_id;
		this.privat = privat;
		this.approved = approved;
	}
	
	/*Isto não vai ser usado*/
	
	public Track(int id, String name, String city, String country, int user_id,
			int privat, int approved) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.country = country;
		this.user_id = user_id;
		this.privat = privat;
		this.approved = approved;
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
	
	
	

}
