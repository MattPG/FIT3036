package cablegate.table;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.BrowserController;
import cablegate.models.Cable;

@FXMLController(value="Table.fxml", title="Table")
public class TableController {
	private static final Logger log = LoggerFactory.getLogger(BrowserController.class);
	
	@FXML
	TableView<Cable> tablePane;
	
	@PostConstruct
	public void init(){
		
	}
	
}
