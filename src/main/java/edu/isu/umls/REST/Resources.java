/**
 * 
 *
 */
package edu.isu.umls.REST;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author rsaripa
 * @date Sep 30, 2015
 * @time 4:57:50 PM
 *
 * Resources
 *
 */
public class Resources extends Application{

	private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();
 
    public Resources() {
        // ADD YOUR RESTFUL RESOURCES HERE
        this.singletons.add(new UMLSSearch());
    }
 
    public Set<Class<?>> getClasses()
    {
        return this.empty;
    }
 
    public Set<Object> getSingletons()
    {
        return this.singletons;
    }
}
