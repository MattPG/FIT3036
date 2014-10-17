package cablegate.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.map.MapController;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class Embassy implements Comparable<Embassy>{
	private static final Logger log = LoggerFactory.getLogger(Embassy.class);

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
	
	public static Embassy parse(String input){
		String[] output = new String[4];
		
		// Get the name
		String[] buff = input.split("\"");
		output[0] = buff[1];
		
		// Get the remaining values
		buff = buff[2].split(",");
		output[1] = buff[0];
		output[2] = buff[1];
		output[3] = buff[2];
		
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
