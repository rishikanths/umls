package edu.isu.umls.servlet.listener;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.isu.umls.utils.LoggerUtil;

@WebListener
public class AppSessionListener implements HttpSessionListener {

	private static Logger logger = Logger.getLogger(AppSessionListener.class.getName());
	private AtomicInteger noSessions = new AtomicInteger(0);

	public void sessionCreated(HttpSessionEvent arg0) {
		noSessions.incrementAndGet();
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		noSessions.decrementAndGet();
	}

}
