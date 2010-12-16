/*
 * Copyright TouK (c) 2010.
 */

package pl.touk.activiti.camel;

import org.activiti.engine.RuntimeService;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;

public class ActivitiEndpoint extends DefaultEndpoint {

    private RuntimeService runtimeService;

    private ActivitiConsumer activitiConsumer;

    public ActivitiEndpoint(String uri, CamelContext camelContext, RuntimeService runtimeService) {
        super(uri, camelContext);
        this.runtimeService = runtimeService;
    }

    void addConsumer(ActivitiConsumer consumer) {
        if (activitiConsumer != null) {
            throw new RuntimeException("Activit consumer already defined for "+getEndpointUri()+"!");
        }
        activitiConsumer = consumer;
    }

    public void process(Exchange ex) throws Exception {
        if (activitiConsumer == null) {
            throw new RuntimeException("Activiti consumer not defined"+getEndpointUri()+"!");
        }
        activitiConsumer.getProcessor().process(ex);

    }


    @Override
    public Producer createProducer() throws Exception {
        return new ActivitiProducer(this, runtimeService);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new ActivitiConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
