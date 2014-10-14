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

import javax.annotation.PostConstruct;

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
		// TODO: Run on background thread
//		generateEmbassies();
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
	}
	
	private void generateEmbassies(){
		// Open Embassies data file
		// TODO: Change this to generic location!
        File file = new File("D:\\Users\\Matthew\\git\\FIT3036\\CableGateProgram\\src\\Embassies.txt");
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
			String[] elements, name;
			Embassy embassy;
			while(currLine != null){
				elements = currLine.split(",");
				name = elements[0].split("\"");
				
				embassy = new Embassy(name[0], elements[1], elements[2], elements[3]);
				tempSet.add(embassy);
				log.debug("Added {} to Set", embassy);
				
				currLine = reader.readLine();
			}
		} catch (IOException e) {
			log.error("Failed to read Embassies.txt", e);
			Platform.exit();
		}
        embassies = tempSet;
	}

}
