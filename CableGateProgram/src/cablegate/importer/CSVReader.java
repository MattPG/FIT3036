package cablegate.importer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import cablegate.Main;
import cablegate.infrastructure.DataBaseManager;
import cablegate.infrastructure.SystemConfig;

public class CSVReader implements Callable<Void>{

	private static final Logger log = LoggerFactory.getLogger(CSVReader.class);
	private static final int MAX_CABLES_LOADED = 1000;
	
	private static BufferedReader stream;
	private final ExecutorService dataBaseWriter;
	private final BlockingQueue<CableBean> resultQueue;
	private static ICsvBeanReader cableReader;
	private static final CellProcessor[] processors = new CellProcessor[]	{   new ParseInt(), // cableNumber
																		        new NotNull(), // dateTime
																		        new NotNull(), // cableID
																		        new NotNull(), // sender
																		        new NotNull(), // classification
																		        new Optional(), // references
																		        new NotNull(), // mailingList
																		        new NotNull(), // cableText
																			};
	
	public CSVReader() {
		this.dataBaseWriter = Executors.newSingleThreadExecutor();
		this.resultQueue = new ArrayBlockingQueue<CableBean>(MAX_CABLES_LOADED);
	}

	@Override
	public Void call() {	
		
		// Create the database and a blank table
		log.debug("Instantiating DataBase...");
		CSVReader.createDBandCableTable();
		
		// Initialise the csvReader to cable.csv
		getCableStream();
		
		//Spawn the datbaseWriter
		Future<Void> dataBaseWriterFuture = dataBaseWriter.submit(new DBWriter(resultQueue));
		
        try {    
        	
        	CableBean cable;
            while( (cable = cableReader.read(CableBean.class, CableBean.getHeaderArray(), processors)) != null ) 
            	resultQueue.put(cable);
                
        } catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
       		// Make sure the streams are closed
			try {
        	   cableReader.close();
        	   dataBaseWriter.shutdown();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		// Make sure the whole DB creation process is finished
		try {
			dataBaseWriterFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Return to main
		return null;
	}
	
	private static void getCableStream(){
		try {
			stream = new BufferedReader(
						new FileReader(
							SystemConfig.getCableDirectory()
							));
			
			cableReader = new CsvBeanReader(new CSVTokenizer(stream,
															CsvPreference.STANDARD_PREFERENCE
															),
											CsvPreference.STANDARD_PREFERENCE
											);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void createDBandCableTable(){
		Connection con = DataBaseManager.getConnection(); // Connect to the database
		Statement createTableStatement = null;
		try{
			
			// Create Cable Table
			createTableStatement = con.createStatement();
			DataBaseManager.createTable(createTableStatement,
										DataBaseManager.getTableName(),
										DataBaseManager.getTableSchemaCreate()
										);

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			// close all open connections
			try {
				if(createTableStatement != null)
					createTableStatement.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
