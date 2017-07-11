package edu.isu.umls.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.Term;
import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;

/**
 * @author Rishi Saripalle
 * Service point to search the UMLS database for a given CUI
 */

@WebServlet(description="Search with CUI",displayName="SearchWithCUI",value="/searchwithcui")
public class SearchByCUI extends HttpServlet {

	private static final long serialVersionUID = 2576576107023471288L;

	private final static Logger logger = LogManager.getLogger(SearchByCUI.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			DBQuery query = new DBQuery();
			AbstractConcept concept = new Term();
			query.getHierarchyInfomationByCUI(request.getParameter("cui"),concept);
			//query.getHierarchyInfomationByCUI(request.getParameter("cui"),0,concept);
			query.getAdjacencyInfomationByCUI(request.getParameter("cui"), concept);
			
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(concept));
			concept.clear();
			concept = null;
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
    }
	
}
