package edu.isu.umls.REST;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.Term;
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
		
		List<AbstractConcept> concepts = query.searchByString(term);
		
		query.closeConnection();
		
		return RESTUtils.buildSuccessResponse(RESTUtils.getJSON(concepts));
	}
	
	@GET
	@Path("/searchwithcui")
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response searchCUI(@QueryParam("cui") String cui){
		
		DBQuery query = new DBQuery();
		AbstractConcept concept = new Term();
		query.getHierarchyInfomationByCUI(cui,0,concept);
		query.getAdjacencyInfomationByCUI(cui, concept);
		query.closeConnection();
		
		return RESTUtils.buildSuccessResponse(RESTUtils.getJSON(concept));
	}
	
	@GET
	@Path("/searchsynonyms")
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response searchSynonyms(@QueryParam("cui") String cui){
		
		DBQuery query = new DBQuery();
		Map<String,List<String>> synonyms = query.getSynonyms(cui);
		query.closeConnection();
		return RESTUtils.buildSuccessResponse(RESTUtils.getJSON(synonyms));
	}
	
	
	@GET
	@Path("/searchdef")
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response searchDefinition(@QueryParam("cui") String cui){
		
		DBQuery query = new DBQuery();
		List<String> definitions = query.getConceptDefinitons(cui);
		query.closeConnection();
		return RESTUtils.buildSuccessResponse(RESTUtils.getJSON(definitions));
	}
	
	@GET
	@Produces({MediaType.APPLICATION_FORM_URLENCODED})
	public Response test(){
		return RESTUtils.buildSuccessResponse("Test Good");
	}

}
