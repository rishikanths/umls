package edu.isu.umls.utils;

import org.apache.logging.log4j.Logger;

public class LoggerUtil {

	public static void logFine(Logger logger, String message){
		logger.info(message);
	}
	
	public static void logError(Logger logger, Exception e){
		
		logger.error(e.getMessage(),e);
	}
	
	public static void logInfo(Logger logger, String message){
		logger.info(message);
	}
	
	public static void logWarning(Logger logger, String message){
		
		logger.warn(message);
	}
	
}
