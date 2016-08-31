package edu.isu.umls.servlet.listener;

import java.util.logging.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.isu.umls.utils.LoggerUtil;

@WebListener
public class AppSessionListener implements
		HttpSessionListener {

	private static Logger logger = Logger.getLogger(AppSessionListener.class.getName());

	public void sessionCreated(HttpSessionEvent arg0) {
		try{
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		try{
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}

}
