package cablegate.importer;

import java.util.concurrent.Future;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

import org.datafx.concurrent.ProcessChain;
import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DataBaseManager;
import cablegate.infrastructure.SystemConfig;

@FXMLController(value="inputDir.fxml", title = "Import")
public class inputDir {
	
	private static Logger log = LoggerFactory.getLogger(inputDir.class);
	
	@FXML
	Label outlineLabel;
	
	@FXML
	Label inputPromptLabel;
	
	@FXML
	TextField directoryField;
	
	@FXML
	@ActionTrigger("importDB")
	Button importButton; 
	
	private StringProperty directoryProperty = new SimpleStringProperty();
	private Future<Void> importingDB;

	@ActionMethod("importDB")
    public void onImportDB() {
		log.debug("Setting cable directory path to {} and creating database",directoryProperty.get());
		SystemConfig.setDataBaseDirectory(directoryProperty.get());
		importButton.setDisable(true);
//		importingDB = DataBaseManager.instantiateDatabase();
		
    }
	
    @PostConstruct
    public void init() {
        directoryField.textProperty().bindBidirectional(directoryProperty);
    }
	
	

}
