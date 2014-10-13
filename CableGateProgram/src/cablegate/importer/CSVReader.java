package cablegate.importer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import javafx.concurrent.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import cablegate.infrastructure.SystemConfig;
import cablegate.models.Cable;

public class CSVReader extends Task<Void> {

	private final Logger log = LoggerFactory.getLogger(CSVReader.class);
	
	private BufferedReader stream;
	private final BlockingQueue<Cable> resultQueue;
	private ICsvBeanReader cableReader;
	
	private final CellProcessor[] processors = new CellProcessor[]	{   new ParseInt(), // cableID
																        new NotNull(), // dateTime
																        new NotNull(), // cableNumber
																        new NotNull(), // sender
																        new NotNull(), // classification
																        new Optional(), // references
																        new NotNull(), // mailingList
																        new NotNull(), // cableString
																	};
	
	public CSVReader(BlockingQueue<Cable> resultQueue) {
		this.resultQueue = resultQueue;
	}

	@Override
	public Void call() {	
		
		// Initialise the csvReader to cable.csv
		getCableStream();
		
        try {    
        	Cable cable;
            while( (cable = cableReader.read(Cable.class, Cable.HEADER_ARRAY, processors)) != null ){
            	resultQueue.put(cable);      
            }
        } catch (IOException | InterruptedException e) {
        	log.error("Error parsing csv file", e);
		} finally {
       		// Make sure the streams are closed
			try {
        	   cableReader.close();
			} catch (IOException e) {
				log.error("Error closing cable reader", e);
			}
        }
		// Return to main
		return null;
	}
	
	private void getCableStream(){
		try {
			stream = new BufferedReader(
						new FileReader(
							SystemConfig.getArchiveDirectory()
							)
						);
			
			cableReader = new CsvBeanReader(new CSVTokenizer(
													stream,
													CsvPreference.STANDARD_PREFERENCE
													), 
											CsvPreference.STANDARD_PREFERENCE
											);
			
		} catch (FileNotFoundException e) {
			log.error("File not found", e);
			System.exit(1);
		}
	}
}
