package edu.isu.umls.servlet;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search by String input",displayName="Search",value="/search")
public class SearchByString extends HttpServlet {

	private static final long serialVersionUID = 3530369857344461267L;

	private static Logger log = Logger.getLogger(SearchByString.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			
			DBQuery query = new DBQuery();
			List<AbstractConcept> concepts = query.searchByString(request.getParameter("term"));
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(concepts));
			
		}catch(Exception e){
			LoggerUtil.logError(log, e);
		}
    }
	
}
