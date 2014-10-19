package cablegate.tagcloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

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
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Cloud.Case;
import org.mcavallo.opencloud.Tag;

import cablegate.infrastructure.DatabaseManager;
import cablegate.infrastructure.SystemConfig;
import cablegate.models.Cable;
import cablegate.table.TableController;

@FXMLController(value="TagCloud.fxml", title="Tag Cloud")
public class TagCloudController {
	int MAX_TAGS = 50;
	
	@FXML
	@ActionTrigger("Refresh")
	Button refreshButton;
	
	@FXML
	FlowPane flowPane;
	private Collection<Label> tags;
	
	private Set<String> stopWords;
	private Comparator<TermVectorEntry> comparatorTVE;
	private Cloud tagCloud;
	
	@PostConstruct
	public void init(){
		stopWords = SystemConfig.getStopWords();
		tagCloud = new Cloud();
		tagCloud.setMaxTagsToDisplay(MAX_TAGS);
        tagCloud.setMaxWeight(50.0);
        tagCloud.setMinWeight(10.0);
        tagCloud.setTagCase(Case.CAPITALIZATION);
        tags = new ArrayList<Label>(MAX_TAGS);
        for(int i=0; i<MAX_TAGS; i++){
        	tags.add(new Label());
        }
        flowPane.getChildren().setAll(tags);
        flowPane.setVgap(1);
        flowPane.setHgap(1);
        comparatorTVE = new Comparator<TermVectorEntry>() {
			@Override
			public int compare(TermVectorEntry o1, TermVectorEntry o2) {
				return Integer.compare(o2.getFrequency(), o1.getFrequency());
			}
		};
	}
	
	@ActionMethod("Refresh")
	public void onRefresh(){
		 // Generate a list of the embassies
        FullTextSession ftSession = Search.getFullTextSession(
        								DatabaseManager.openSession());
        SearchFactory searchFactory = ftSession.getSearchFactory();
        IndexReader indexReader = searchFactory.getIndexReaderAccessor()
        									.open(Cable.class);
        
        SortedTermVectorMapper map = new SortedTermVectorMapper(true, true, comparatorTVE);
        TableController.tableData.forEach(c -> {
        	try {
				indexReader.getTermFreqVector(c.getCableID()-1, map);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
        List<TermVectorEntry> list = new ArrayList<TermVectorEntry>(map.getTermVectorEntrySet());
        list.sort(comparatorTVE);
        tagCloud.clear();
        Iterator<TermVectorEntry> it = list.iterator();
        int added = 0;
        while(it.hasNext() && added<MAX_TAGS){
        	TermVectorEntry tve = it.next();
        	if(!stopWords.contains(tve.getTerm()) && tve.getFrequency()>5){
        		tagCloud.addTag(new Tag(tve.getTerm(), tve.getFrequency()));
        	}
        }
        searchFactory.getIndexReaderAccessor().close(indexReader);
        ftSession.close();

        Iterator<Label> iter = tags.iterator();
        tagCloud.tags().forEach(t -> {
        	Label label = iter.next();
        	label.setText(t.getName());
        	label.setFont(new Font(t.getWeight()));
        });
	}
}
