package cablegate.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SystemUtils;
import org.datafx.controller.FXMLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.BrowserController;
import cablegate.models.Embassy;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;

@FXMLController(value="Map.fxml", title="Map")
public class MapController implements MapComponentInitializedListener {
	private static final Logger log = LoggerFactory.getLogger(MapController.class);

	@FXML
	private GoogleMapView mapView;
	private GoogleMap map;
	
	private Set<Embassy> embassies;
	
	@PostConstruct
	public void init(){
		mapView.addMapInializedListener(this);
	}
	
	@Override
	public void mapInitialized() {
        LatLong center = new LatLong(0, 0);
        
		MapOptions options = new MapOptions();
        options.center(center)
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
		

		generateEmbassies();
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
		Set<Embassy> tempSet = new TreeSet<Embassy>();
        try {
			String currLine = reader.readLine();
			while(currLine != null){
				tempSet.add(Embassy.parse(currLine));
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
        
        try {
			reader.close();
		} catch (IOException e1) {
			log.error("Failed to close Embassies.txt reader",e1);
		}
        embassies = tempSet;
        embassies.forEach(e -> log.info("",e));
	}

}
