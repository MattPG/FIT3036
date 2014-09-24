package cablegate;

import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.importer.inputDir;

public class Main extends Application{ 

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	 @Override  
	 public void start(Stage primaryStage) throws Exception {  

		primaryStage.setTitle("WikiBrow");
		BorderPane mainPane = new BorderPane();
		
		MenuBar menuPane = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem importItem = new MenuItem("Import...");
		importItem.setAccelerator(KeyCombination.keyCombination("Ctrl+I"));
		fileMenu.getItems().add(importItem);
		menuPane.getMenus().add(fileMenu);
		mainPane.setTop(menuPane);
		
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		Collection<Tab> tabs = new ArrayList<Tab>(10);
		
		Tab tableTab = new Tab("Table");
		tabs.add(tableTab);
		
		Tab timelineTab = new Tab("Timeline");
		tabs.add(timelineTab);
		
		Tab tagCloudTab = new Tab("Tag Cloud");
		tabs.add(tagCloudTab);

		Flow importFlow = new Flow(inputDir.class);
		tabs.add(importFlow.startInTab());
		
		tabPane.getTabs().addAll(tabs);
		mainPane.setCenter(tabPane);
		
        primaryStage.setScene(new Scene(mainPane, 640, 480));
        primaryStage.show();
	 }  
	   
	 public static void main(String[] args) {  
		 Application.launch(args);  
	 }  
}
