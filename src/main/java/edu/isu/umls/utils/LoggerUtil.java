package edu.isu.umls.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {

	public static void logFine(Logger logger, String message){
		
		logger.log(Level.FINE,message);
	}
	
	public static void logError(Logger logger, Exception e){
		
		logger.log(Level.SEVERE,e.getMessage(),e);
	}
	
	public static void logInfo(Logger logger, String message){
		logger.log(Level.INFO,message);
	}
	
	public static void logWarning(Logger logger, String message){
		
		logger.log(Level.WARNING,message);
	}
	
}
