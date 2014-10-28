package cablegate.charts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.lucene.index.TermFreqVector;
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
	private Map<String, String> countries;
	
	@PostConstruct
	public void init(){
		stopWords = getStopWords();
		countries = getCountriesList();
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
		Map<String, Counter> termsMap = new HashMap<String, Counter>();
		TableController.tableData.forEach(c -> {
			TermFreqVector tveArray[] = null;
			
			try {
				tveArray = indexReader.getTermFreqVectors(c.getCableID()-1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Arrays.asList(tveArray).forEach(tve -> {
				String terms[] = tve.getTerms();
				int counts[] = tve.getTermFrequencies();
				Counter count;
				for(int i=0; i < tve.size(); i++){
					count = termsMap.get(terms[i]);
					if(count == null){
						termsMap.put(terms[i], new Counter(counts[i]));
					}else{
						count.add(counts[i]);
					}
				}
			});
		});
       
       // Extract just the countries and count them together
		
		Map<String, Counter> countryCount = new HashMap<String, Counter>();
		termsMap.forEach((t, c) -> {
			String country = countries.get(t);
			if(country != null){
				Counter count = countryCount.get(country);
				if(count == null){
					countryCount.put(country, c);
				}
			}
		});
		
		List<Country> countrySet = new ArrayList<Country>(100);
		countryCount.forEach((s, c) -> {
			countrySet.add(new Country(s, c.getCount()));
		});
		
		Collections.sort(countrySet);
		countrySet.forEach(c -> System.out.println(c));
		
		// Plot the frequencies
		Series<String, Integer> countryData = new Series<String, Integer>();
		Iterator<Country> it = countrySet.iterator();
		int max = 15;
		int count = 0;
		while(it.hasNext() && count < max){
			count++;
			Country cunt = it.next();
			countryData.getData().add(new XYChart.Data<>(cunt.getName(), cunt.getCount()));
		}
		
		countChart.getData().clear();
		countChart.getData().add(countryData);
		countChart.setLegendVisible(false);
	}
	
	private void refreshPieChart(IndexReader indexReader){
		// Get the terms of the curr data set
		Map<String, Counter> map = new HashMap<String, Counter>();
       TableController.tableData.forEach(c -> {
    	   String classification = c.getClassification();
    	   Counter counter = map.get(classification);
    	   if(counter == null){
    		   map.put(classification, new Counter(1));
    	   }else{
    		   counter.increment();
    	   }
       });
       
		// Plot the frequencies
		Collection<PieChart.Data> classData = new ArrayList<PieChart.Data>(10);
		map.forEach((c, f) -> {
			classData.add(new PieChart.Data(c, f.getCount()));
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
		
		public void add(int toAdd){
			count += toAdd;
		}
		
		public void increment(){
			count++;
		}
		
		public Counter(int count){
			this.count = count;
		}
		
		public int getCount(){
			return count;
		}
	}
	
	private class Country implements Comparable<Country>{
		
		private String name;
		private Integer count;
		
		public Country(String name, Integer count){
			this.name = name;
			this.count = count;
		}
		
		public String getName(){
			return name;
		}
		
		public Integer getCount(){
			return count;
		}
		
		@Override
		public String toString(){
			return name + " " + count;
		}

		@Override
		public int compareTo(Country o) {
			return o.getCount().compareTo(count);
		}
		
	}
}
