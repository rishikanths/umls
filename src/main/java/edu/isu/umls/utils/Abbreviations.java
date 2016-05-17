/**
 * 
 *
 */
package edu.isu.umls.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rsaripa
 * @date Sep 28, 2015
 * @time 6:34:50 PM
 *
 * Abbreviations
 *
 */

public final class Abbreviations {


	private static Map<String,String> RelationType = new HashMap<String, String>();
	
	
	public static void setRelationType(){
		
	}
	
	public static String getRelationType(String key){
		
		return RelationType.get(key);
	}
	
}

