package edu.isu.umls.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.database.DataConnection;
import edu.isu.umls.utils.LoggerUtil;

/**
 * @author Rishi Saripalle
 * 
 */
@WebListener
public class AppContextListener implements ServletContextListener
{

	private final static Logger logger = LogManager.getLogger(AppContextListener.class.getName());

	public void contextInitialized(ServletContextEvent contextEvent)
	{
		try{
			LoggerUtil.logInfo(logger, "Initiating datasource connection .... ");
			DataConnection.startDataConnection();
			LoggerUtil.logInfo(logger, "Connected to the datasource.... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

	public void contextDestroyed(ServletContextEvent contextEvent)
	{
		try{
			LoggerUtil.logInfo(logger, "Destroying the Datasource.... ");
			DataConnection.stopDataConnection();
			LoggerUtil.logInfo(logger, "Datasource is closed .... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

}
