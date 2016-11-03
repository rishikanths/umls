package edu.isu.umls.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.database.DBConnection;
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
			String dbURL = contextEvent.getServletContext().getInitParameter("dbURL");
			String dbUser = contextEvent.getServletContext().getInitParameter("dbUser");
			String dbPwd = contextEvent.getServletContext().getInitParameter("dbPwd");
			DBConnection db = new DBConnection(dbURL, dbUser,dbPwd);
			contextEvent.getServletContext().setAttribute(DBStatements.DB_CONN,db);
			LoggerUtil.logInfo(logger, "Connected to the database.... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

	public void contextDestroyed(ServletContextEvent contextEvent)
	{
		try{
			LoggerUtil.logInfo(logger, "Destroying the context.... ");
			DBConnection db =  (DBConnection)contextEvent.getServletContext().getAttribute(DBStatements.DB_CONN);
			if(db!=null){
				db.closeConnection();
			}
			LoggerUtil.logInfo(logger, "Database connection closed .... ");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

}
