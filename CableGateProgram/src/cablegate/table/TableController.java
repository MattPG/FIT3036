package cablegate.table;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;

import org.datafx.controller.FXMLController;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.BrowserController;
import cablegate.infrastructure.DataBaseManager;
import cablegate.models.Cable;

@FXMLController(value="Table.fxml", title="Table")
public class TableController {
	private static final Logger log = LoggerFactory.getLogger(BrowserController.class);
	
	@FXML
	TableView<Cable> tablePane;
	
	@PostConstruct
	public void init(){
		Session session = DataBaseManager.openSession();
		session.beginTransaction();
		
		List<?> cables = session.createQuery("from Cable").setMaxResults(10).list();
		cables.forEach(e -> log.info("{}", e)); 
		
		session.getTransaction().commit();
		session.disconnect();
	}
	
}
