package cablegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

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
        
        // Generate a list of the embassies
//        FullTextSession ftSession = Search.getFullTextSession(
//        								DatabaseManager.openSession());
//        SearchFactory searchFactory = ftSession.getSearchFactory();
//        IndexReader indexReader = searchFactory.getIndexReaderAccessor()
//        									.open(Cable.class);
//        LuceneDictionary dictionary = new LuceneDictionary(indexReader, "sender");
//        BytesRefIterator iterator = dictionary.getWordsIterator();
//        BytesRef byteRef = null;
//        while( (byteRef = iterator.next()) != null ){
//        	System.out.println(byteRef.utf8ToString());
//        }
//        
//        searchFactory.getIndexReaderAccessor().close(indexReader);
//        ftSession.close();
        
//        final String DEFAULT_QUERY = "from Cable cable order by cable.cableID asc";
//        final int NUM_ELEMENTS_PER_PAGE = 10000;
//        List<Cable> cables;
//        Map<String, Integer> wordMap = new HashMap<String, Integer>();
//        for(int currPage=0; currPage <= 26; currPage++){
//        	cables = DatabaseManager.query(DEFAULT_QUERY, NUM_ELEMENTS_PER_PAGE, NUM_ELEMENTS_PER_PAGE*currPage);
//        	cables.forEach(e -> {
//        		String sender = e.getSender();
//                Integer freq = wordMap.get(sender);
//                
//                if(freq == null)
//                	wordMap.put(sender, new Integer(1));
//                else
//                	wordMap.put(sender, new Integer(freq.intValue() + 1));
//        	});
//        }
//        
//        wordMap.forEach((s,i) -> System.out.println(s + " " + i));
        
//        File text = new File("D:\\Users\\Matthew\\git\\FIT3036\\CableGateProgram\\src\\embassies.txt");
//        File embs = new File("D:\\Users\\Matthew\\git\\FIT3036\\CableGateProgram\\src\\rawEmbassies.txt");
//        File Embs = new File("D:\\Users\\Matthew\\git\\FIT3036\\CableGateProgram\\src\\sfEmbassies.txt");
//        BufferedReader brText = new BufferedReader(new FileReader(text));
//        BufferedReader brEmbs = new BufferedReader(new FileReader(embs));
//        PrintWriter fwEmbs = new PrintWriter(Embs);
//        String[] wordsText;
//        String currLineText = brText.readLine();
//        String currLineEmbs = brEmbs.readLine();
//        
//        while(currLineText != null){
//        	wordsText = currLineText.split(" ");
//        	currLineEmbs += "," + wordsText[wordsText.length-1];
//        	fwEmbs.println(currLineEmbs);
//        	System.out.println(currLineEmbs);
//        	currLineText = brText.readLine();
//            currLineEmbs = brEmbs.readLine();
//        }
//        
//        brText.close();
//        brEmbs.close();
//        fwEmbs.close();
        
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
