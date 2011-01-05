/*
 * Copyright TouK (c) 2011.
 */

package pl.touk.activiti.camel;

import org.apache.camel.CamelContext;


public interface ContextProvider {

    CamelContext getContext(String processName); 


}
