package cablegate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application{ 
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	 @Override  
	 public void start(Stage primaryStage) throws Exception {  
		primaryStage.setTitle("WikiBrow");
		
		Flow flow = new Flow(RootController.class);
		FlowHandler flowHandler = flow.createHandler();
		StackPane mainPane = flowHandler.start(new DefaultFlowContainer());
		
        primaryStage.setScene(new Scene(mainPane, 640, 480));
        primaryStage.show();
	 }  
	 
	 public static void main(String[] args) {  
		 Application.launch(args);  
	 }  
}
