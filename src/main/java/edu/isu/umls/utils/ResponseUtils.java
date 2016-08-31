/**
 * 
 *
 */
package edu.isu.umls.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.gson.Gson;

/**
 * @author rsaripa
 * @date Sep 30, 2015
 * @time 4:15:05 PM
 *
 * RESTUtils
 *
 */
public class ResponseUtils {
	
	public enum Status{
		OK, Accepted,Bad_Request,Server_Error, Not_Found,Service_Unavailable
	}
	
	
	public static Response buildSuccessResponse(String content){
		
		ResponseBuilder response = Response.ok(content);
		return response.build();
	}
	
	public static Response buildErrorResponse(String content){
		
		ResponseBuilder response = Response.serverError();
		response.entity(content);
		return response.build();
	}
	
	public static String getJSON(Object o){
		return new Gson().toJson(o);
	}

}
