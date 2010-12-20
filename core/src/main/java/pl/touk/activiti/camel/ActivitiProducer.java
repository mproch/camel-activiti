/*
 * Copyright TouK (c) 2010.
 */

package pl.touk.activiti.camel;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import java.util.HashMap;
import java.util.Map;

public class ActivitiProducer extends DefaultProducer {

    private RuntimeService runtimeService;

    public static final String PROCESS_KEY_PROPERTY = "PROCESS_KEY_PROPERTY";

    public static final String PROCESS_ID_PROPERTY = "PROCESS_ID_PROPERTY";

    private String processKey = null;

    private String activity = null;

    public ActivitiProducer(ActivitiEndpoint endpoint, RuntimeService runtimeService) {
        super(endpoint);
        this.runtimeService = runtimeService;
        String[] path = endpoint.getEndpointUri().split(":");
        processKey = path[1].replace("//","");
        if (path.length > 2) {
            activity = path[2];
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (shouldStartProcess()) {
            ProcessInstance pi = startProcess(exchange);
            exchange.getOut().setBody(pi.getId());
        } else {
            signal(exchange);
        }
    }

    private boolean shouldStartProcess() {
        return activity == null;
    }

    private void signal(Exchange exchange) {
        String processInstanceId = findProcessInstanceId(exchange);
        Execution execution = runtimeService.createExecutionQuery()
                .processDefinitionKey(processKey)
                .processInstanceId(processInstanceId)
                .activityId(activity).singleResult();

        if (execution == null) {
            throw new RuntimeException("Couldn't find activity for processId "+processInstanceId);
        }
        runtimeService.setVariables(execution.getId(), prepareVariables(exchange));
        runtimeService.signal(execution.getId());

    }

    private String findProcessInstanceId(Exchange exchange) {
        String processInstanceId = exchange.getProperty(PROCESS_ID_PROPERTY, String.class);
        if (processInstanceId != null) {
            return processInstanceId;
        }
        String processInstanceKey = exchange.getProperty(PROCESS_KEY_PROPERTY, String.class);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(processInstanceKey).singleResult();

        if (processInstance == null) {
            throw new RuntimeException("Could not find activit with key "+processInstanceKey);
        }
        return processInstance.getId();
    }


    private ProcessInstance startProcess(Exchange exchange) {
        String key = exchange.getProperty(PROCESS_KEY_PROPERTY, String.class);
        if (key == null) {
            return runtimeService.startProcessInstanceByKey(processKey, prepareVariables(exchange));
        } else {
            return runtimeService.startProcessInstanceByKey(processKey, key, prepareVariables(exchange));
        }

    }

    private Map<String, Object> prepareVariables(Exchange exchange) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<?,?> m = exchange.getIn().getBody(Map.class);
        if (m != null) {
            for (Map.Entry e : m.entrySet()) {
                if (e.getKey() instanceof String) {
                    ret.put((String) e.getKey(), e.getValue());
                }
            }
        }
        return ret;
    }


}
