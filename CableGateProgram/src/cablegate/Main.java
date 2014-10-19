package cablegate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SortedTermVectorMapper;
import org.apache.lucene.index.TermVectorEntry;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowHandler;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Cloud.Case;
import org.mcavallo.opencloud.Tag;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DatabaseManager;
import cablegate.infrastructure.SystemConfig;
import cablegate.models.Cable;
import cablegate.table.TableController;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class Main extends Application{ 
	 @Override  
	 public void start(Stage primaryStage) throws Exception {
		// Setup the Logger configurations
		configureLogger();
		 
        // Setup the configurations for Hibernate
        DatabaseManager.configureHibernateSession();
		 
		// Setup and display the main stage
		primaryStage.setTitle("WikiBrow");
		Flow flow = new Flow(RootController.class);
		FlowHandler flowHandler = flow.createHandler();
		StackPane mainPane = flowHandler.start();
		
        primaryStage.setScene(new Scene(mainPane, 800, 500));
        primaryStage.setOnCloseRequest(event -> Platform.exit());
        primaryStage.show();
	 }  
	 
	 public static void main(String[] args) {  
		 Application.launch(args);  
	 }  
	 
	 public void stop() throws Exception{
		 DatabaseManager.closeHibernateSession();
	 }
	 
	 private void configureLogger(){
		 LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			try {
				  JoranConfigurator configurator = new JoranConfigurator();
				  configurator.setContext(context);
				  context.reset();
				  configurator.doConfigure(SystemConfig.getWorkingDirectory() + "logback.xml"); // loads logback config file
				} catch (JoranException je) {
				  // StatusPrinter will handle this
				} catch (Exception ex) {
				  ex.printStackTrace(); // Just in case, so we see a stacktrace
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(context); // Internal status data is printed in case of warnings or errors.
	 }
}
