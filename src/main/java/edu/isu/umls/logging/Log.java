package edu.isu.umls.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Log {

	private static FileHandler fileHandler = null;
	private static ConsoleHandler consoleHandler = null;
	private static Log log = null;
	private Logger logLogger = Logger.getLogger(Log.class.getName());
	
	private Log(){
		try{
            LogManager.getLogManager().readConfiguration(Log.class.getResourceAsStream("logger.properties"));
			fileHandler = new FileHandler();
			System.out.println(fileHandler.getLevel());
			consoleHandler = new ConsoleHandler();
			logLogger.addHandler(fileHandler);
			logLogger.addHandler(consoleHandler);
		}catch(Exception e){
			logLogger.log(Level.SEVERE, e.getMessage(),e);
		}
	}
	
	public static Handler getFileHandler(){
		if(fileHandler==null)
			log = new Log();
		return fileHandler;
	}
	
	public static Handler getConsoleHandler(){
		if(consoleHandler==null)
			log = new Log();
		return consoleHandler;
	}
	
	public static Log getLog(){
		if(log==null)
			log = new Log();
		return log;
	}
	
	public static void addHandlers(Logger logger){
		if(fileHandler==null){
			getLog();
		}
		logger.addHandler(fileHandler);
		logger.addHandler(consoleHandler);
	}
	
}
