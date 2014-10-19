package cablegate.models;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class Embassy implements Comparable<Embassy>{
	private String name;
	private double latitude;
	private double longitude;
	private int cableCount;
	private LatLong centre;
	
	public Embassy(String name, String latitude, String longitude, String cableCount){
		this(name, Double.parseDouble(latitude), Double.parseDouble(longitude), Integer.parseInt(cableCount));
	}
	
	public Embassy(String name, double latitude, double longitude, int cableCount){
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.cableCount = cableCount;
	}
	public String getName() {
		return name;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getCableCount() {
		return cableCount;
	}
	
	public LatLong getCentre() {
		return centre;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setCableCount(int cableCount) {
		this.cableCount = cableCount;
	}
	
	public void setCentre(){
		centre = new LatLong(getLatitude(), getLongitude());
	}
	
	public static Embassy parse(String input){
		String[] output = new String[4];
		
		// Get the name
		String[] buff = input.split("\"");
		output[0] = buff[1];
		
		// Get the remaining values
		buff = buff[2].split(",");
		output[1] = buff[1];
		output[2] = buff[2];
		output[3] = buff[3];
		
		return new Embassy(output[0], output[1], output[2], output[3]);
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
