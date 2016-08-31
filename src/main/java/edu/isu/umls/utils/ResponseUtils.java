/**
 * 
 *
 */
package edu.isu.umls.utils;

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
	
	public static String getJSON(Object o){
		return new Gson().toJson(o);
	}

}
