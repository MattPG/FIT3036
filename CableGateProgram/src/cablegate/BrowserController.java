package cablegate;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.map.MapController;
import cablegate.table.TableController;

@FXMLController(value="Browser.fxml")
public class BrowserController {
	private static final Logger log = LoggerFactory.getLogger(BrowserController.class);
	
	@FXML
	private TabPane tabPane;

	@PostConstruct
	public void init() {
		Flow tableFlow = new Flow(TableController.class);
		Flow mapFlow = new Flow(MapController.class);
		
		try {
			tabPane.getTabs().addAll(tableFlow.startInTab(),
									mapFlow.startInTab());
		} catch (FlowException e) {
			log.error("failed to add tableFlow to browser", e);
		}
    }
}
