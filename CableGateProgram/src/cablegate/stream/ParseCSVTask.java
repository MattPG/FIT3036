package cablegate.stream;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.BlockingQueue;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class ParseCSVTask implements Runnable{
	
	private final ICsvBeanReader beanReader;
	private final BlockingQueue<CableBean> resultQueue;
	private static final CellProcessor[] processors = new CellProcessor[]	{   new ParseInt(), // cableNumber
																		        new NotNull(), // dateTime
																		        new NotNull(), // cableID
																		        new NotNull(), // sender
																		        new NotNull(), // classification
																		        new Optional(), // references
																		        new NotNull(), // mailingList
																		        new NotNull(), // cableText
																			};
		
	public ParseCSVTask(StringBuilder inputBuilder, BlockingQueue<CableBean> resultQueue){
		beanReader = new CsvBeanReader(new MyTokenizer(new StringReader(inputBuilder.toString()), CsvPreference.STANDARD_PREFERENCE), CsvPreference.STANDARD_PREFERENCE);
		this.resultQueue = resultQueue;
	}

	@Override
	public void run() {
		
	        try {
	        	
	        	CableBean cable;
                while( (cable = beanReader.read(CableBean.class, CableBean.getHeaderArray(), processors)) != null ) 
                	resultQueue.put(cable);
	                
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
		           try {
					beanReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
	}
}
