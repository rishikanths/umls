package edu.isu.umls.servlet.listener;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import edu.isu.umls.database.DBConnection;
import edu.isu.umls.utils.LoggerUtil;

/**
 * @author Herak Sen
 * 
 */
@WebListener
public class AppContextListener implements ServletContextListener
{

	private static Logger logger = Logger.getLogger(AppContextListener.class.getName());

	public void contextInitialized(ServletContextEvent contextEvent)
	{
		try{
			LoggerUtil.logInfo(logger, "Initiating Connection .... ");
			DBConnection.initConnection();
			LoggerUtil.logInfo(logger, "Connected to the database.... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

	public void contextDestroyed(ServletContextEvent contextEvent)
	{
		try{
			LoggerUtil.logInfo(logger, "Destroying the context.... ");
			if(DBConnection.getConnection()!=null){
				DBConnection.closeConnection();
			}
			LoggerUtil.logInfo(logger, "Database connection closed .... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

}
