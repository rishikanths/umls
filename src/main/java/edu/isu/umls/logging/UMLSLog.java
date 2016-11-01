package edu.isu.umls.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import edu.isu.umls.database.DBConnection;

public class UMLSLog {

	private static FileHandler fileHandler = null;
	private static ConsoleHandler consoleHandler = null;
	private static JDBCHandler jdbcHandler = null;
	private static UMLSLog log = null;
	private Logger logger = Logger.getLogger(UMLSLog.class.getName());
	private DBConnection db= null;
	private UMLSLog(){
		try{
			
            LogManager.getLogManager().readConfiguration(UMLSLog.class.getResourceAsStream("logger.properties"));
			fileHandler = new FileHandler();
			consoleHandler = new ConsoleHandler();
			jdbcHandler = new JDBCHandler();
			logger.addHandler(jdbcHandler);
			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
		}catch(Exception e){
			logger.log(Level.SEVERE, e.getMessage(),e);
		}
	}
	public static Handler getFileHandler(){
		if(fileHandler==null)
			log = new UMLSLog();
		return fileHandler;
	}
	
	public static Handler getConsoleHandler(){
		if(consoleHandler==null)
			log = new UMLSLog();
		return consoleHandler;
	}
	
	public static UMLSLog getLog(){
		if(log==null)
			log = new UMLSLog();
		return log;
	}
	public static void addFileHandler(Logger logger){
		if(fileHandler==null){
			getLog();
		}else{
			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
		}
	}
	public static void addAllHandlers(Logger logger){
		if(fileHandler==null || jdbcHandler ==null){
			getLog();
		}else{
			logger.addHandler(jdbcHandler);
			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
		}
	}
	
}
