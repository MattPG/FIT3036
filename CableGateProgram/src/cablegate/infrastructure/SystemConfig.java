package cablegate.infrastructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SystemConfig {
	private static final Logger log = LoggerFactory.getLogger(SystemConfig.class);
	private static String archiveDirectory = null;
	private static long javaRam = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
	private static final String WORKING_FOLDER = "CableGateFiles";
	private static Set<String> stopWords = null;
	
	public static String getArchiveDirectory() {
		return archiveDirectory;
	}

	public static void setArchiveDirectory(String archiveDirectory) {
		SystemConfig.archiveDirectory = archiveDirectory;
	}
	
	public static long getJavaRam(){
		return javaRam;
	}
	
	public static Set<String> getStopWords(){
		if(stopWords == null){
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(new File(getWorkingDirectory() + "stopwords.txt")));
				stopWords = new TreeSet<String>();
				String word = reader.readLine();
				while(word != null){
					stopWords.add(word);
					word = reader.readLine();
				}
			} catch (FileNotFoundException e) {
				log.error("Error while trying to open custom stopwords.txt", e);
			} catch (IOException e) {
				log.error("Error while trying to read custom stopwords.txt", e);
			}finally{
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						log.error("Error trying to close reader.", e);
					}
				}
			}
		}
		return stopWords;
	}
	
	public static String getWorkingDirectory(){
		String path = SystemUtils.getUserDir().getAbsolutePath();
		if(SystemUtils.IS_OS_WINDOWS){
			path += ('\\' + WORKING_FOLDER + '\\');
		}else {
			path += ('/' + WORKING_FOLDER + '/');
		}
		return path;
	}
}
