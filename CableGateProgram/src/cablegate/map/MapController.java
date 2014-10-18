package cablegate.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SystemUtils;
import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.models.Embassy;
import cablegate.table.TableController;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;

@FXMLController(value="Map.fxml", title="Map")
public class MapController implements MapComponentInitializedListener {
	private static final Logger log = LoggerFactory.getLogger(MapController.class);
	
	@FXML
	@ActionTrigger("RefreshMap")
	Button refreshButton;
	
	@FXML
	private GoogleMapView mapView;
	private GoogleMap map;
	
	private Map<String, Embassy> embassies;
	private List<Circle> circles = new ArrayList<Circle>(150);
	private Map<String, Counter> freqs = new HashMap<String, Counter>(100);
	
	@PostConstruct
	public void init(){
		generateEmbassies();
		mapView.addMapInializedListener(this);
	}
	
	@Override
	public void mapInitialized() {
		createMap();
		embassies.forEach((s,emb) -> emb.setCentre());
		embassies.forEach((s,embassy) -> {
			if(embassy.getCableCount() > 1000){
				displayEmbassy(embassy, "purple", 50*embassy.getCableCount());
			}
		});
	}
	
	@ActionMethod("RefreshMap")
	public void onRefreshMap(){
		circles.forEach(c -> map.removeMapShape(c));
		circles.clear();
		getFreqs();
		freqs.forEach((s,c) -> {
			displayEmbassy(embassies.get(s), "red", 5000*c.getCount());
		});
	}
	
	private void createMap(){
        LatLong mapCentre = new LatLong(0, 0);
		MapOptions options = new MapOptions();
        options.center(mapCentre)
                .mapMarker(true)
                .zoom(2)
                .overviewMapControl(true)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(true)
                .mapType(MapTypeIdEnum.ROADMAP);
		map = mapView.createMap(options);
        log.info("Map created!");
	}
	
	private void displayEmbassy(Embassy embassy, String colour, int radius){
			CircleOptions circleOptions = new CircleOptions();
			circleOptions.clickable(true)
						.editable(false)
						.draggable(false)
						.fillColor(colour)
						.strokeColor(colour)
						.visible(true)
						.center(embassy.getCentre())
						.radius(radius);
			Circle circle = new Circle(circleOptions);
			circles.add(circle);
			map.addMapShape(circle);
	}
	
	private void generateEmbassies(){
		// Open Embassies data file
		String directory = SystemUtils.getUserDir().getAbsolutePath();
		if(SystemUtils.IS_OS_WINDOWS){
			directory += "\\src\\Embassies.txt";
		}else{
			directory += "/src/Embassies.txt";
		}
        File file = new File(directory);
        
        BufferedReader reader = null;
        try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			log.error("Failed to open Embassies.txt", e);
			Platform.exit();
		}
        
        // Parse file and add embassies to set
        embassies = new HashMap<String, Embassy>(300);
        try {
        	Embassy emb;
			String currLine = reader.readLine();
			while(currLine != null){
				emb = Embassy.parse(currLine);
				embassies.put(emb.getName(), emb);
				currLine = reader.readLine();
			}
		} catch (IOException e) {
			log.error("Failed to read Embassies.txt", e);
        	try {
				reader.close();
			} catch (IOException c) {
				log.error("Failed to close Embassies.txt reader",c);
			}
			Platform.exit();
		}
        
        // Close reader
        try {
			reader.close();
		} catch (IOException e1) {
			log.error("Failed to close Embassies.txt reader",e1);
		}
        
        log.info("Embassies imported!");
	}
	
	private void getFreqs(){
		freqs.clear();
		TableController.tableData.forEach(cable -> {
			String name = cable.getSender();
			Counter count = freqs.get(name);
			if(count == null){
				freqs.put(name, new Counter());
			}else{
				count.increment();
			}
		});
	}
	
	private class Counter{
		private int count;
		
		public Counter(){
			count = 1;
		}
		
		public void increment(){
			count++;
		}
		
		public int getCount(){
			return count;
		}
	}
	
}
