package edu.isu.umls.servlet;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.database.DBQuery;
import edu.isu.umls.utils.LoggerUtil;
import edu.isu.umls.utils.ResponseUtils;

/**
 * @author Rishi Saripalle
 * 
 * Service point to search the UMLS database for a given user string. 
 * For example, if the user enter "Mala", the database will be searched
 * for all concepts starting with the given word.
 */

@WebServlet(description = "Search by String input", displayName = "Search", value = "/search")
public class SearchByString extends HttpServlet {

	private static final long serialVersionUID = 3530369857344461267L;

	private final static Logger logger = LogManager.getLogger(SearchByString.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {

			request.getSession();

			List<AbstractConcept> concepts = new DBQuery().searchByString(request.getParameter("term"));
			response.setContentType("application/text");
			response.getWriter().write(ResponseUtils.getJSON(concepts));
			concepts.clear();

		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

}
