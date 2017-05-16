package edu.isu.umls.servlet;

import java.util.List;
import java.util.Map;

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
 * Service point to search for all the synonyms of a UMLS term.
 */

@WebServlet(description="Search concept synonyms",displayName="Search Synonyms",value="/searchsynonyms")
public class SearchSynonyms extends HttpServlet {

	private static final long serialVersionUID = 6333034216749213108L;

	private final static Logger logger = LogManager.getLogger(SearchSynonyms.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			
			DBQuery query = new DBQuery();
			Map<String,List<String>> synonyms = query.getSynonyms(request.getParameter("cui"));
			
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(synonyms));
			synonyms.clear();
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
    }
	
}
