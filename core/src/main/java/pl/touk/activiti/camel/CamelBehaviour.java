/*
 * Copyright TouK (c) 2010.
 */

package pl.touk.activiti.camel;

import org.activiti.engine.impl.bpmn.AbstractBpmnActivity;
import org.activiti.engine.impl.bpmn.BpmnActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;

import java.util.Map;

public class CamelBehaviour extends BpmnActivityBehavior implements ActivityBehavior {

    private CamelContext camelContext;

    public CamelBehaviour(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        ActivitiEndpoint ae = createEndpoint(execution);
        ae.process(createExchange(execution));
        performDefaultOutgoingBehavior(execution);

    }


    private ActivitiEndpoint createEndpoint(ActivityExecution execution) {
        String uri = "activiti:"+getProcessName(execution)+":"+execution.getActivity().getId();
        ActivitiEndpoint ae = (ActivitiEndpoint) camelContext.getEndpoint(uri);
        if (ae == null) {
            throw new RuntimeException("Activiti endpoint not defined for "+uri);
        }
        return ae;
    }

    private Exchange createExchange(ActivityExecution activityExecution) {
        Exchange ex = new DefaultExchange(camelContext);
        for (Map.Entry<String, Object> var : activityExecution.getVariables().entrySet()) {
            ex.setProperty(var.getKey(), var.getValue());
        }
        return ex;
    }

    private String getProcessName(ActivityExecution execution) {
        String id = execution.getActivity().getProcessDefinition().getId();
        return id.substring(0, id.indexOf(":"));

    }

}
