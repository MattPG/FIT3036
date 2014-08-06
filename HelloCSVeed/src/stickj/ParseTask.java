package stickj;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.csveed.api.CsvClient;
import org.csveed.api.CsvClientImpl;

public class ParseTask implements Runnable{
	
	private static final int DEFAULT_NUMBER_OF_CABLES = 1000;
	
	private final int numberOfCables;
	private final Reader reader;
	private List<CableBean> cables;
	
	public ParseTask(StringBuilder input){
		this(new StringReader(input.toString()), DEFAULT_NUMBER_OF_CABLES);
	}
	
	public ParseTask(Reader reader){
		this(reader, DEFAULT_NUMBER_OF_CABLES);
	}
	
	public ParseTask(Reader reader, int numberOfCables){
		this.reader = reader;
		this.numberOfCables = numberOfCables; 
	}
		
	@Override
	public void run() {
		
		CsvClient<CableBean> csvReader = 
				new CsvClientImpl<CableBean>(reader, CableBean.class);
		
		cables = csvReader.readBeans();
		System.out.println(cables.get(0));
	}

}
