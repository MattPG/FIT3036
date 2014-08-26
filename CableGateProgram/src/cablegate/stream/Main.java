package cablegate.stream;

import java.io.File;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.profiler.Profiler;

public class Main {
	
	// TODO: discovered "UNCLASSIFIED//FOR OFFICIAL USE ONLY" in classification column of a cable
	public static void main(String[] args) {
		File dataBase = new File(DataBaseManager.getDataBaseName());
		
		if(!dataBase.isDirectory()){
			System.out.println("Database doesn't exist, creating Database...");
			createAndImportDataBase();
		}
		
		System.exit(0);
	}
	
	private static void createAndImportDataBase(){
    	Profiler timer = new Profiler("Main.java");
    	timer.start("Adding ");
    	
		// Create the thread to handle cable.csv reading
    	CompletionService<Void> singleThread = new ExecutorCompletionService<Void>(Executors.newSingleThreadExecutor());
		// Run the cable.csv reading thread
    	Future<Void> allCablesRead = singleThread.submit(new CableCSVReader());
    	
    	try {
    		allCablesRead.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	timer.stop().print();
	}
}
