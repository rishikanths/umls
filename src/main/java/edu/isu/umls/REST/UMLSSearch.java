package edu.isu.umls.REST;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.REST.Utils.RESTUtils;
import edu.isu.umls.database.DBQuery;

@Path("/umls")
public class UMLSSearch {
	
	@GET
	@Path("/id")
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public String searchTermById(@QueryParam("id") String id){
		return "";
	}
	
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response searchTerm(@QueryParam("term") String term){
		
		DBQuery query = new DBQuery();
		
		List<AbstractConcept> concepts = query.searchByString(term,30);
		
		return RESTUtils.buildSuccessResponse(RESTUtils.getJSON(concepts));
	}
	
	@GET
	@Path("/searchwithcui")
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response searchCUI(@QueryParam("cui") String cui){
		
		DBQuery query = new DBQuery();
		
		AbstractConcept concept = query.getInfomationByCUI(cui,0);
		
		return RESTUtils.buildSuccessResponse(RESTUtils.getJSON(concept));
	}
	
	@GET
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response test(){
		return RESTUtils.buildSuccessResponse("Test Good");
	}

}
