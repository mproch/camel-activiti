/*
 * Copyright TouK (c) 2010.
 */

package pl.touk.activiti.camel;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class ActivitiConsumer extends DefaultConsumer {

    public ActivitiConsumer(ActivitiEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ((ActivitiEndpoint) getEndpoint()).addConsumer(this);
    }
}
