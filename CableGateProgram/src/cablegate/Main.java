package cablegate;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.stage.Stage;
import cablegate.infrastructure.DataBaseManager;

@SuppressWarnings("deprecation")
public class Main extends Application{ 
	// TODO: discovered "UNCLASSIFIED//FOR OFFICIAL USE ONLY" in classification column of a cable

	 @Override  
	 public void start(Stage stage) throws Exception {  
	    
		 Label label = LabelBuilder.create()  
		                    .text("Maven + JavaFX = true")  
		                    .alignment(Pos.CENTER).build();  
		    
		 Scene scene = new Scene(label, 200, 100);  
		 stage.setScene(scene);  
		 stage.show();  
	  
		File dataBase = new File(DataBaseManager.getDataBaseName());
		
		if(!dataBase.isDirectory()){
			System.out.println("Database doesn't exist, creating Database...");
			DataBaseManager.instantiateDatabase();
		}
		
		//System.exit(0);
	 }  
	   
	 public static void main(String[] args) {  
		 Application.launch(args);  
	 }  
}
