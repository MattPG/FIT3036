package cablegate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

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
	
	private FlowHandler browserFlowHandler;	
	private Node browserFlowView;
	private FlowHandler importFlowHandler;
	private Node importFlowView;
	
	@PostConstruct
	public void init() throws FlowException {
		browseItem.setDisable(true);
		
		Flow tabFlow = new Flow(BrowserController.class);
		browserFlowHandler = tabFlow.createHandler();
		browserFlowView = browserFlowHandler.start();		
		
		Flow importFlow = new Flow(ImporterController.class);
		importFlowHandler = importFlow.createHandler();
		importFlowView = importFlowHandler.start();

		parentPane.setCenter(browserFlowView);
	}
	
	@ActionMethod("ExitProgram")
	public void onExitProgram(){
		Platform.exit();
	}
	
	@ActionMethod("ImportDatabase")
	public void onImportDatabase() throws FlowException{
		importItem.setDisable(true);
		browseItem.setDisable(false);
		parentPane.setCenter(importFlowView);
	}
	
	@ActionMethod("BrowseDatabase")
	public void onBrowseDatabase(){
		importItem.setDisable(false);
		browseItem.setDisable(true);
		parentPane.setCenter(browserFlowView);
	}
	
}
