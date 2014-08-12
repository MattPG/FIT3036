

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class CableCSVReader implements Callable<Void>{
	
	private static BufferedReader stream;
	private final BlockingQueue<CableBean> resultQueue;
	
	private final ICsvBeanReader beanReader;
	private static final CellProcessor[] processors = new CellProcessor[]	{   new ParseInt(), // cableNumber
																		        new NotNull(), // dateTime
																		        new NotNull(), // cableID
																		        new NotNull(), // sender
																		        new NotNull(), // classification
																		        new Optional(), // references
																		        new NotNull(), // mailingList
																		        new NotNull(), // cableText
																			};
	private static final String[] header =	{	"cableNumber",
												"dateTime", 
												"cableID",
												"sender",
												"classification",
												"references",
												"mailingList",
												"cableText" 
											};
	
	public CableCSVReader(BlockingQueue<CableBean> resultQueue) {
		this.resultQueue = resultQueue;
		getCableStream();
		beanReader = new CsvBeanReader(new MyTokenizer(stream, CsvPreference.STANDARD_PREFERENCE), CsvPreference.STANDARD_PREFERENCE);
	}

	@Override
	public Void call() {		
		int count = 0;
		try {
        	
        	CableBean cable;
            while( (cable = beanReader.read(CableBean.class, header, processors)) != null ) {
            	resultQueue.put(cable);
            	count++;
            	if(count%1000 == 0)
            		System.out.println(count);
            }
                
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

		return null;
	}
			
	private static void getCableStream(){
		try {
			stream = new BufferedReader(
						new FileReader(
							SystemConfig.getCableDirectory()
							));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	

}
