package cablegate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;

import cablegate.importer.ImporterController;

@FXMLController(value="Root.fxml")
public class RootController {
	@FXML
	private BorderPane parentPane;
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private MenuBar menuBar;
	
	@FXML
	@ActionTrigger("ExitProgram")
	private MenuItem exitItem;
	
	@FXML
	@ActionTrigger("ImportDatabase")
	private MenuItem importItem;
	
	@FXML
	@ActionTrigger("BrowseDatabase")
	private MenuItem browseItem;
	
	private FlowHandler tabFlowHandler;
	private StackPane tabFlowPane;
	
	private FlowHandler importFlowHandler;
	private StackPane importFlowPane;
	
	@PostConstruct
	public void init() throws FlowException {
		browseItem.setDisable(true);
		
		Flow tabFlow = new Flow(BrowserController.class);
		tabFlowHandler = tabFlow.createHandler();
		tabFlowPane = tabFlowHandler.start();
		
		Flow importFlow = new Flow(ImporterController.class);
		importFlowHandler = importFlow.createHandler();
		importFlowPane = importFlowHandler.start();

		parentPane.setCenter(tabFlowPane);
		
		// TODO: Check if database exists and set parameters in SystemConfig
	}
	
	@ActionMethod("ExitProgram")
	public void onExitProgram(){
		Platform.exit();
	}
	
	@ActionMethod("ImportDatabase")
	public void onImportDatabase() throws FlowException{
		importItem.setDisable(true);
		browseItem.setDisable(false);
		parentPane.setCenter(importFlowPane);
	}
	
	@ActionMethod("BrowseDatabase")
	public void onBrowseDatabase(){
		importItem.setDisable(false);
		browseItem.setDisable(true);
		parentPane.setCenter(tabFlowPane);
	}
	
}
