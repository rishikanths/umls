package edu.isu.umls.servlet;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;

/**
 * @author Rishi Saripalle
 * 
 * Service point to search for a definition(s) of a UMLS term.
 */

@WebServlet(description="Search definition of the term",displayName="Search Definition",value="/searchdef")
public class SearchDefinition extends HttpServlet {

	private static final long serialVersionUID = 2747526324366947009L;

	private final static Logger logger = LogManager.getLogger(SearchDefinition.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			DBQuery query = new DBQuery();
			List<String> definitions = query.getConceptDefinitons(request.getParameter("cui"));
			
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(definitions));
			definitions.clear();
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
    }
	
}
