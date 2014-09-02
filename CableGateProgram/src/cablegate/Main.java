package cablegate;

import java.io.File;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.stage.Stage;

import org.slf4j.profiler.Profiler;

import cablegate.infrastructure.DataBaseManager;
import cablegate.stream.CSVReader;

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
			createAndImportDataBase();
		}
		
		//System.exit(0);
	 }  
	   
	 public static void main(String[] args) {  
		 Application.launch(args);  
	 }  
	
	private static void createAndImportDataBase(){
    	Profiler timer = new Profiler("Main.java");
    	timer.start("Adding ");
    	
		// Create the thread to handle cable.csv reading
    	CompletionService<Void> singleThread = new ExecutorCompletionService<Void>(Executors.newSingleThreadExecutor());
		// Run the cable.csv reading thread
    	Future<Void> allCablesRead = singleThread.submit(new CSVReader());
    	
    	try {
    		allCablesRead.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	timer.stop().print();
	}
}
