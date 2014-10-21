package cablegate.charts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;

import javax.annotation.PostConstruct;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SortedTermVectorMapper;
import org.apache.lucene.index.TermVectorEntry;
import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DatabaseManager;
import cablegate.infrastructure.SystemConfig;
import cablegate.models.Cable;
import cablegate.table.TableController;

@FXMLController(value="Charts.fxml", title="Charts")
public class ChartsController {
	private static final Logger log = LoggerFactory.getLogger(ChartsController.class);
	@FXML
	@ActionTrigger("Refresh")
	private Button refreshButton;
	
	@FXML
	private BarChart<String, Integer> countChart;

	@FXML
	private PieChart classChart;
	
	private Set<String> stopWords;
	private Comparator<TermVectorEntry> comparatorTVE;
	private Map<String, String> countries;
	
	@PostConstruct
	public void init(){
		stopWords = getStopWords();
		countries = getCountriesList();
        comparatorTVE = new Comparator<TermVectorEntry>() {
			@Override
			public int compare(TermVectorEntry o1, TermVectorEntry o2) {
				return Integer.compare(o2.getFrequency(), o1.getFrequency());
			}
		};
	}
	
	@ActionMethod("Refresh")
	public void onRefresh(){
		FullTextSession ftSession = Search.getFullTextSession(DatabaseManager.openSession());
		SearchFactory searchFactory = ftSession.getSearchFactory();
		IndexReader indexReader = searchFactory.getIndexReaderAccessor().open(Cable.class);
		
		refreshCountriesChart(indexReader);
		refreshPieChart(indexReader);
		
		searchFactory.getIndexReaderAccessor().close(indexReader);
		ftSession.close();
	}
	
	private void refreshCountriesChart(IndexReader indexReader){
		// Get the terms of the curr data set
       SortedTermVectorMapper map = new SortedTermVectorMapper(true, true, comparatorTVE);
       TableController.tableData.forEach(c -> {
       	try {
				indexReader.getTermFreqVector(c.getCableID()-1, map);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
       });
       
       // Extract just the countries and count them together
		List<TermVectorEntry> list = new ArrayList<TermVectorEntry>(map.getTermVectorEntrySet());
		Map<String, Counter> freqs = new HashMap<String, Counter>();
		list.forEach(tve -> {
			String country = countries.get(tve.getTerm());
			if(country != null){
				Counter count = freqs.get(country);
				if(count == null){
					freqs.put(country, new Counter(tve.getFrequency()));
				}
			}
		});
		
		// Plot the frequencies
		Series<String, Integer> countryData = new Series<String, Integer>();
		freqs.forEach((c, f) -> {
			countryData.getData().add(new XYChart.Data<>(c, f.getCount()));
		});
		countChart.getData().clear();
		countChart.getData().add(countryData);
		countChart.setLegendVisible(false);
	}
	
	private void refreshPieChart(IndexReader indexReader){
		// Get the terms of the curr data set
       SortedTermVectorMapper map = new SortedTermVectorMapper(true, true, comparatorTVE);
       TableController.tableData.forEach(c -> {
       	try {
				indexReader.getTermFreqVector(c.getCableID()-1, "classification", map);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
       });
       
       // Extract just the countries and count them together
		List<TermVectorEntry> list = new ArrayList<TermVectorEntry>(map.getTermVectorEntrySet());
		Map<String, Integer> freqs = new HashMap<String, Integer>();
		list.forEach(tve -> freqs.put(tve.getTerm(), tve.getFrequency()));
		
		// Plot the frequencies
		Collection<PieChart.Data> classData = new ArrayList<PieChart.Data>(10);
		freqs.forEach((c, f) -> {
			classData.add(new PieChart.Data(c, f));
		});
		classChart.getData().clear();
		classChart.getData().addAll(classData);
		classChart.setLegendVisible(false);
	}
	
	private Map<String, String> getCountriesList(){		
		Map<String, String> countries = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(SystemConfig.getWorkingDirectory() + "Countries.txt")));
			countries = new HashMap<String, String>();
			String country = reader.readLine();
			String[] cunts;
			while(country != null){
				cunts = country.split(" ");
				for(int i=0; i < cunts.length; i++){
					if(!stopWords.contains(cunts[i])){
						countries.put(cunts[i], country);
					}
				}
				country = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			log.error("Error while trying to open custom Countries.txt", e);
		} catch (IOException e) {
			log.error("Error while trying to read custom Countries.txt", e);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					log.error("Error trying to close reader.", e);
				}
			}
		}
		return countries;
	}
	
	private Set<String> getStopWords(){
		Set<String> stopWords = SystemConfig.getStopWords();
		stopWords.add("united");
		stopWords.add("northern");
		stopWords.add("south");
		stopWords.add("island");
		stopWords.add("islands");
		return stopWords;
	}
	
	private class Counter{
		private int count;
		
		public Counter(int count){
			this.count = count;
		}
		
		public int getCount(){
			return count;
		}
	}
}
