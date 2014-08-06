package cablegate.stream;

import java.io.Reader;
import java.util.List;

import org.csveed.api.CsvClient;
import org.csveed.api.CsvClientImpl;

public class ParseCSVTask implements Runnable{
	
	private final CsvClient<CableBean> csvReader;
	private List<CableBean> myCables;
	
	public ParseCSVTask(Reader reader){
		csvReader = new CsvClientImpl<CableBean>(reader, CableBean.class);
	}

	@Override
	public void run() {
		myCables = csvReader.readBeans();
		System.out.println(myCables.size());
		System.out.println(myCables.get(myCables.size()-1));
	}
	
}
