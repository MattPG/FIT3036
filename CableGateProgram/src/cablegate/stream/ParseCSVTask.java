package cablegate.stream;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.csveed.api.CsvClient;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class ParseCSVTask implements Runnable{
	
	private StringBuilder inputBuilder;
	private String inputString;
	private CsvClient<CableBean> csvReader;
	private List<CableBean> myCables;
	
	public ParseCSVTask(StringBuilder inputBuilder){
		this.inputBuilder = inputBuilder;
	}

	@Override
	public void run() {
		
		 ICsvBeanReader beanReader = null;
	        try {
	                beanReader = new CsvBeanReader(new MyTokenizer(new StringReader(inputBuilder.toString()), CsvPreference.STANDARD_PREFERENCE), CsvPreference.STANDARD_PREFERENCE);
	                String[] header = {"cableNumber", "dateTime", "cableID", "sender", "classification", "references", "mailingList", "cableText" };
	                final CellProcessor[] processors = getProcessors();
	                
	                CableBean cable;
	                while( (cable = beanReader.read(CableBean.class, header, processors)) != null ) {
	                	if(cable.getCableNumber()%10000 == 0)
	                        System.out.println(cable.getCableNumber());
	                	if(cable.getCableNumber() == 251287)
	                		System.out.println(cable.getCableText());
	                        
	                }
	                
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        finally {
	                if( beanReader != null ) {
	                        try {
								beanReader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                }
	        }
		
//		csvReader = new CsvClientImpl<CableBean>(new StringReader(inputString), CableBean.class);
//		myCables = csvReader.readBeans();
//		if(myCables.get(myCables.size()-1).getCableNumber()%10000 == 0)
//			System.out.println(myCables.get(myCables.size()-1).getCableNumber());
	}
	
	private static CellProcessor[] getProcessors() {
	        
	        final CellProcessor[] processors = new CellProcessor[] { 
	                new ParseInt(), // cableNumber
	                new ParseDate("M/d/yyyy H:mm"), // dateTime
	                new NotNull(), // cableID
	                new NotNull(), // sender
	                new NotNull(), // classification
	                new Optional(), // references
	                new NotNull(), // mailingList
	                new NotNull(), // cableText
	        };
	        
	        return processors;
	}
	
}
