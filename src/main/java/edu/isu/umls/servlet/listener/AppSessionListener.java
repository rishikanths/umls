package edu.isu.umls.servlet.listener;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.utils.LoggerUtil;


@WebListener
public class AppSessionListener implements HttpSessionListener {

	private static Logger logger = LogManager.getLogger(AppSessionListener.class.getName());
	private AtomicInteger noSessions = new AtomicInteger(0);

	public void sessionCreated(HttpSessionEvent arg0) {
		LoggerUtil.logFine(logger, "Session is created...");
		noSessions.incrementAndGet();
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		LoggerUtil.logFine(logger, "Session is destroyed...");
		noSessions.decrementAndGet();
	}

}
