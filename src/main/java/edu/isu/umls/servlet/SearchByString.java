package edu.isu.umls.servlet;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.database.DBQuery;
import edu.isu.umls.database.DBStatements;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search by String input",displayName="Search",value="/search")
public class SearchByString extends HttpServlet {

	private static final long serialVersionUID = 3530369857344461267L;

	private final static Logger logger = LogManager.getLogger(SearchByString.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			SessionFactory factory = (SessionFactory)getServletContext().getAttribute(DBStatements.HIBERNATE_SESSION_FACTORY);
			Session session = factory.openSession();
			
			DBQuery query = new DBQuery(session);
			List<AbstractConcept> concepts = query.searchByString(request.getParameter("term"));
			
			session.close();
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(concepts));
			concepts.clear();
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
    }
	
}
