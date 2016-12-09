package edu.isu.umls.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.isu.umls.database.DBStatements;
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
			LoggerUtil.logInfo(logger, "Initiating database connection .... ");
			
			Configuration config = new Configuration();
			config.configure("/edu/isu/umls/database/hibernate.cfg.xml");
			SessionFactory factory = config.buildSessionFactory();
			
			contextEvent.getServletContext().setAttribute(DBStatements.HIBERNATE_SESSION_FACTORY,factory);
			LoggerUtil.logInfo(logger, "Connected to the database.... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

	public void contextDestroyed(ServletContextEvent contextEvent)
	{
		try{
			LoggerUtil.logInfo(logger, "Destroying the context.... ");
			SessionFactory factory =  (SessionFactory)contextEvent.getServletContext().getAttribute(DBStatements.HIBERNATE_SESSION_FACTORY);
			if(factory!=null){
				factory.close();
			}
			LoggerUtil.logInfo(logger, "Database connection closed .... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

}
