/*
 * Copyright TouK (c) 2011.
 */

package pl.touk.activiti.camel;

import org.apache.camel.CamelContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SimpleContextProvider implements ContextProvider {

    private Map<String, CamelContext> contexts = new HashMap<String, CamelContext>();

    public SimpleContextProvider(Map<String, CamelContext> contexts) {
        this.contexts = contexts;
    }

    public SimpleContextProvider(String processName, CamelContext ctx) {
        this(Collections.singletonMap(processName, ctx));
    }

    @Override
    public CamelContext getContext(String processName) {
        return contexts.get(processName);
    }
}
