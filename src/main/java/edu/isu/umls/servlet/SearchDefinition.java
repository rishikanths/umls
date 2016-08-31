package edu.isu.umls.servlet;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search definition of the term",displayName="Search Definition",value="/searchdef")
public class SearchDefinition extends HttpServlet {

	private static final long serialVersionUID = 2747526324366947009L;

	private static Logger log = Logger.getLogger(SearchDefinition.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			
			DBQuery query = new DBQuery();
			List<String> definitions = query.getConceptDefinitons(request.getParameter("cui"));
			
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(definitions));
			
		}catch(Exception e){
			LoggerUtil.logError(log, e);
		}
    }
	
}
