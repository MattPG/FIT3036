package cablegate.importer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.SystemConfig;
import cablegate.models.Cable;

@FXMLController(value="Importer.fxml")
public class ImporterController {
	private static Logger log = LoggerFactory.getLogger(ImporterController.class);
	
	@FXML
	Label outlineLabel;
	
	@FXML
	Label inputPromptLabel;
	
	@FXML
	TextField directoryField;
	private StringProperty directoryProperty = new SimpleStringProperty();
	
	@FXML
	@ActionTrigger("importDB")
	Button importButton; 
	
	@FXML
	Label warningLabel;
	
	@FXML
	Label importUpdateLabel;
	
	@FXML
	ProgressBar importProgressBar;
	
	private BlockingQueue<Cable> commQueue = new ArrayBlockingQueue<Cable>(200);
	private CSVReader archiveReader = new CSVReader(commQueue);
	private DBWriter dbWriter = new DBWriter(commQueue);
	
	@ActionMethod("importDB")
    public void onImportDB() {	
		if(!archiveReader.isRunning() || !dbWriter.isRunning()){
			//TODO: delete current database table
			
			// Change archive directory to value given and import the cables
			SystemConfig.setArchiveDirectory(directoryProperty.get());
			log.debug("Setting archive path to {} and inserting to database", SystemConfig.getArchiveDirectory());
			
			// Import the database in background threads
			new Thread(dbWriter).start();
			new Thread(archiveReader).start();
		} else {
			warningLabel.setVisible(true);  // Already importing!
		}
    }
	
    @PostConstruct
    public void init() {
    	// Set visibility of field and attach text input to directoryProperty
        directoryField.textProperty().bindBidirectional(directoryProperty);
        importProgressBar.progressProperty().bind(dbWriter.progressProperty());
        importUpdateLabel.setVisible(false);
        warningLabel.setVisible(false);
        
        // Assign progress message to the message update values of dbWriter
        dbWriter.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                importUpdateLabel.setText(newValue);
                importUpdateLabel.setVisible(true);
            }
		});
        
    }
	
	

}
