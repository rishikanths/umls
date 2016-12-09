package edu.isu.umls.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.Term;
import edu.isu.umls.database.DBQuery;
import edu.isu.umls.database.DBStatements;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search with CUI",displayName="SearchWithCUI",value="/searchwithcui")
public class SearchByCUI extends HttpServlet {

	private static final long serialVersionUID = 2576576107023471288L;

	private final static Logger logger = LogManager.getLogger(SearchByCUI.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			SessionFactory factory = (SessionFactory)getServletContext().getAttribute(DBStatements.HIBERNATE_SESSION_FACTORY);
			Session session = factory.openSession();
			
			DBQuery query = new DBQuery(session);
			AbstractConcept concept = new Term();
			query.getHierarchyInfomationByCUI(request.getParameter("cui"),0,concept);
			query.getAdjacencyInfomationByCUI(request.getParameter("cui"), concept);
			
			session.close();
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(concept));
			concept.clear();
			concept = null;
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
    }
	
}
