package edu.isu.umls.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.Term;
import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search by String input",displayName="Search",value="/searchwithcui",
			initParams={@WebInitParam(name="cui", value="cui")})
public class SearchByCUI extends HttpServlet {

	private static final long serialVersionUID = 2576576107023471288L;

	private static Logger log = Logger.getLogger(SearchByCUI.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			
			DBQuery query = new DBQuery();
			AbstractConcept concept = new Term();
			query.getHierarchyInfomationByCUI(request.getParameter("cui"),0,concept);
			query.getAdjacencyInfomationByCUI(request.getParameter("cui"), concept);
			
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(concept));
			
		}catch(Exception e){
			LoggerUtil.logError(log, e);
		}
    }
	
}
