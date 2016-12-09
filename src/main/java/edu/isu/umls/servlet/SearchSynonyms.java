package edu.isu.umls.servlet;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.isu.umls.database.DBQuery;
import edu.isu.umls.database.DBStatements;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;


@WebServlet(description="Search concept synonyms",displayName="Search Synonyms",value="/searchsynonyms")
public class SearchSynonyms extends HttpServlet {

	private static final long serialVersionUID = 6333034216749213108L;

	private final static Logger logger = LogManager.getLogger(SearchSynonyms.class.getName());
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			
			request.getSession();
			SessionFactory factory = (SessionFactory)getServletContext().getAttribute(DBStatements.HIBERNATE_SESSION_FACTORY);
			Session session = factory.openSession();
			
			DBQuery query = new DBQuery(session);
			Map<String,List<String>> synonyms = query.getSynonyms(request.getParameter("cui"));
			
			session.close();
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(synonyms));
			synonyms.clear();
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
    }
	
}
