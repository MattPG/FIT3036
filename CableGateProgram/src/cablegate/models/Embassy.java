package cablegate.models;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class Embassy implements Comparable<Embassy>{
	
	private String name;
	private LatLong location;
	private int cableCount;
	
	public Embassy(String name, String latitude, String longitude, String cableCount){
		this(name, Double.parseDouble(latitude), Double.parseDouble(longitude), Integer.parseInt(cableCount));
	}
	
	public Embassy(String name, double latitude, double longitude, int cableCount){
		this(name, new LatLong(latitude, longitude), cableCount);
	}
	
	public Embassy(String name, LatLong location, int cableCount){
		this.name = name;
		this.location = location;
		this.cableCount = cableCount;
	}
	
	public String getName() {
		return name;
	}

	public LatLong getLocation() {
		return location;
	}

	public int getCableCount() {
		return cableCount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(LatLong location) {
		this.location = location;
	}

	public void setCableCount(int cableCount) {
		this.cableCount = cableCount;
	}

	@Override
	public int compareTo(Embassy otherEmbassy) {
		return this.name.compareTo(otherEmbassy.getName());
	}
	
	@Override
	public String toString(){
		return name;
	}

}
