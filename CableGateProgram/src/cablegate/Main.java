package cablegate;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;

import cablegate.gui.control.Root;
import cablegate.infrastructure.DataBaseManager;

public class Main extends Application{ 
	
	 @Override  
	 public void start(Stage primaryStage) throws Exception {  

        Flow flow  = new Flow(Root.class);
 
        FlowHandler flowHandler = flow.createHandler();
 
        StackPane pane = flowHandler.start(new DefaultFlowContainer());
        
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
	  
		File dataBase = new File(DataBaseManager.getDataBaseName());
		if(!dataBase.isDirectory()){
			System.out.println("Database doesn't exist, creating Database...");
			DataBaseManager.instantiateDatabase();
		}
	 }  
	   
	 public static void main(String[] args) {  
		 Application.launch(args);  
	 }  
}
