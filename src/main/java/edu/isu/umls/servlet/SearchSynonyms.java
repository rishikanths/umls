package edu.isu.umls.servlet;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search by String input",displayName="Search",value="/searchsynonyms",
			initParams={@WebInitParam(name="cui", value="cui")})
public class SearchSynonyms extends HttpServlet {

	private static final long serialVersionUID = 6333034216749213108L;

	private static Logger log = Logger.getLogger(SearchSynonyms.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			
			DBQuery query = new DBQuery();
			Map<String,List<String>> synonyms = query.getSynonyms(request.getParameter("cui"));
			
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(synonyms));
			
		}catch(Exception e){
			LoggerUtil.logError(log, e);
		}
    }
	
}
