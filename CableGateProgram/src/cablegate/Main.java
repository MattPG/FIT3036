package cablegate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.apache.commons.lang3.SystemUtils;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DatabaseManager;
import cablegate.infrastructure.SystemConfig;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class Main extends Application{ 
	private static final Logger log = LoggerFactory.getLogger(Main.class);

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
        primaryStage.setOnCloseRequest(e -> Platform.exit());
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
				  
				  String systemLogFilePath = null;
				  if(SystemUtils.IS_OS_WINDOWS){
					  systemLogFilePath = SystemUtils.getUserDir() + "\\src\\cablegate\\infrastructure\\logback.xml";
				  }else {
					  systemLogFilePath = SystemUtils.getUserDir() + "/src/cablegate/infrastructure/logback.xml";
				  }
				  
				  configurator.doConfigure(systemLogFilePath); // loads logback file
				} catch (JoranException je) {
				  // StatusPrinter will handle this
				} catch (Exception ex) {
				  ex.printStackTrace(); // Just in case, so we see a stacktrace
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(context); // Internal status data is printed in case of warnings or errors.
	 }
}
