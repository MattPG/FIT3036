package cablegate.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.BrowserController;
import cablegate.infrastructure.DataBaseManager;
import cablegate.models.Cable;

@FXMLController(value="Table.fxml", title="Table")
public class TableController {
	private static final Logger log = LoggerFactory.getLogger(BrowserController.class);
	private final int NUM_ELEMENTS_PER_PAGE = 100;
	private final String DEFAULT_QUERY = "from Cable cable order by cable.cableID asc";
	private String query = DEFAULT_QUERY;
	
	@FXML
	TextField searchField;
	
	@FXML
	Button searchButton;
	
	@FXML
	@ActionTrigger("ExploreCable")
	Button exploreButton;
	
	@FXML
	TableView<Cable> tablePane;	
	private ObservableList<Cable> tableData;

	@FXML
	@ActionTrigger("NextPage")
	Button nextButton;

	@FXML
	@ActionTrigger("PrevPage")
	Button prevButton;
	
	@FXML
	Label pageNumLabel;
	private int currPage = 0;
	
	@PostConstruct
	public void init(){
		createTableColumns();
		populateTable();
		updatePageNum();
	}
	
	@ActionMethod("NextPage")
	public void onNextPage(){	
		currPage++;
		refresh();
	}
	
	@ActionMethod("PrevPage")
	public void onPrevPage(){
		currPage--;
		refresh();
	}
	
	@ActionMethod("ExploreCable")
	public void onExploreCable(){
		Cable selectedCable = tablePane.getSelectionModel().getSelectedItem();
		if(selectedCable != null){
			createCableWindow(selectedCable);			
		}		
	}
	
	private void updatePageNum(){
		pageNumLabel.setText("Page: " + currPage);
		
		if(tableData.size() < NUM_ELEMENTS_PER_PAGE){  // On the final page
			nextButton.setDisable(true);
		}else{
			nextButton.setDisable(false);
		}
		
		if(currPage == 0){  // On the first page
			prevButton.setDisable(true);
		}else{
			prevButton.setDisable(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateTable(){
		Session session = DataBaseManager.openSession();
		session.beginTransaction();
		
		List<Cable> cables = session.createQuery(query).setMaxResults(NUM_ELEMENTS_PER_PAGE)
													   .setFirstResult(NUM_ELEMENTS_PER_PAGE*currPage)
													   .list();
		cables.forEach(Cable::convertText);  // Turns internal Clob obj. to a String
		tableData.setAll(cables);
				
		session.getTransaction().commit();
		session.close();
	}
	
	private void refresh(){
		updateTable();
		updatePageNum();
	}
	
	private void createCableWindow(Cable cable){
		// Create text labels
		Label mailingListLabel = new Label(cable.getMailingList());
		mailingListLabel.setWrapText(true);
		Label cableStringLabel = new Label(cable.getCableString());
		cableStringLabel.setWrapText(true);
		
		// Make labels scrollable
		ScrollPane scrollableMailingList = new ScrollPane(mailingListLabel);
		scrollableMailingList.setPrefHeight(100);
		ScrollPane scrollableCableString = new ScrollPane(cableStringLabel);
		scrollableCableString.setPrefHeight(500);
		
		// Join them in a pane
		BorderPane rootPane = new BorderPane();
		rootPane.setTop(scrollableMailingList);
		rootPane.setCenter(scrollableCableString);
		
		// Create and display the stage
		Stage stage = new Stage();
		String title = cable.getCableID() + " - " + cable.getCableNumber() + " " + cable.getClassification();
		stage.setTitle(title);
		stage.setScene(new Scene(rootPane, 600, 600));
		stage.show();
	}
	
	@SuppressWarnings("unchecked")
	private void populateTable(){
		Session session = DataBaseManager.openSession();
		session.beginTransaction();
		
		List<Cable> cables = session.createQuery(query).setMaxResults(NUM_ELEMENTS_PER_PAGE).list();
		cables.forEach(Cable::convertText);  // Turns internal Clob obj. to a String
				
		tableData = FXCollections.observableList(cables);
		tablePane.setItems(tableData);
		
		session.getTransaction().commit();
		session.close();
	}
	
	private void createTableColumns(){
		Collection<TableColumn<Cable,?>> cols = new ArrayList<TableColumn<Cable,?>>(10); 
		
		TableColumn<Cable, Integer> idCol = new TableColumn<Cable, Integer>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<Cable, Integer>("cableID"));
		cols.add(idCol);
		
		TableColumn<Cable, String> dateTimeCol = new TableColumn<Cable, String>("CREATION DATE");
		dateTimeCol.setCellValueFactory(new PropertyValueFactory<Cable, String>("dateTime"));
		cols.add(dateTimeCol);
		
		TableColumn<Cable, String> cableNumberCol = new TableColumn<Cable, String>("CABLE NUMBER");
		cableNumberCol.setCellValueFactory(new PropertyValueFactory<Cable, String>("cableNumber"));
		cols.add(cableNumberCol);
		
		TableColumn<Cable, String> senderCol = new TableColumn<Cable, String>("SOURCE");
		senderCol.setCellValueFactory(new PropertyValueFactory<Cable, String>("sender"));
		cols.add(senderCol);
		
		TableColumn<Cable, String> classificationCol = new TableColumn<Cable, String>("CLASSIFICATION");
		classificationCol.setCellValueFactory(new PropertyValueFactory<Cable, String>("classification"));
		cols.add(classificationCol);
		
		TableColumn<Cable, String> referralsCol = new TableColumn<Cable, String>("REFERENCED CABLES");
		referralsCol.setCellValueFactory(new PropertyValueFactory<Cable, String>("referrals"));
		cols.add(referralsCol);
		
		tablePane.getColumns().setAll(cols);
		tablePane.setColumnResizePolicy(param -> true);
	}
}
