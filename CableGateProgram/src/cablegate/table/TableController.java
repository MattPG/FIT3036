package cablegate.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.ActionMethod;
import org.datafx.controller.flow.action.ActionTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.BrowserController;
import cablegate.infrastructure.DatabaseManager;
import cablegate.models.Cable;

@FXMLController(value="Table.fxml", title="Table")
public class TableController {
	private static final Logger log = LoggerFactory.getLogger(BrowserController.class);
	private final int NUM_ELEMENTS_PER_PAGE = 100;
	private final String DEFAULT_QUERY = "from Cable cable order by cable.cableID asc";
	private boolean isTextSearch = false;
	private String textQuery = "";
	
	@FXML
	TextField searchField;
	private StringProperty searchProperty = new SimpleStringProperty();
	
	@FXML
	@ActionTrigger("SearchText")
	Button searchButton;
	
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
		// Initial Table Setup
		createTableColumns();
		populateTable();
		updatePageNum();
		
		// Attach strings to their textfields
        searchField.textProperty().bindBidirectional(searchProperty);
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
	
	@ActionMethod("SearchText")
	public void onSearchText(){
		String buffer = searchProperty.get();
		// Make sure input isn't null or plain whitespace
		if(buffer != null && !buffer.trim().equals("")){
			isTextSearch = true;
			currPage = 0;
			textQuery = buffer;
		}else{
			isTextSearch = false;
			currPage = 0;
		}
		refresh();
	}
	
	private void refresh(){
		updateTable();
		updatePageNum();
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
	
	private void updateTable(){
		List<Cable> cables = null;
		
		if(isTextSearch){
			cables = DatabaseManager.searchText(textQuery, NUM_ELEMENTS_PER_PAGE, NUM_ELEMENTS_PER_PAGE*currPage);
		}else{
			cables = DatabaseManager.query(DEFAULT_QUERY, NUM_ELEMENTS_PER_PAGE, NUM_ELEMENTS_PER_PAGE*currPage);
		}
		
		if(cables != null){
			tableData.setAll(cables);
		}
	}
	
	/*
	 * Creates a pop-out window to display cable data
	 */
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
	
	private void populateTable(){
		tableData = FXCollections.observableList(
				DatabaseManager.query(DEFAULT_QUERY, NUM_ELEMENTS_PER_PAGE, 0)
				);
		tablePane.setItems(tableData);
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
		
		// Set popup window on double-click
		tablePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                    	Cable lastSelectedCable = tablePane.getSelectionModel().getSelectedItem();
                    	if(lastSelectedCable != null){ // Check a cable has actually been selected
                    		createCableWindow(lastSelectedCable);		
                    	}
                    }
                }				
			}
		});
	}
}
