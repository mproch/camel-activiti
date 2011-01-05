/*
 * Copyright TouK (c) 2010.
 */

package pl.touk.activiti.camel;

import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

@ContextConfiguration("classpath:camel-activiti-context.xml")
public class SimpleProcessTest extends SpringActivitiTestCase {

    public void setUp() {
        CamelContext ctx = applicationContext.getBean(CamelContext.class);
        MockEndpoint me = (MockEndpoint) ctx.getEndpoint("mock:service1");
        me.reset();
    }

    @Deployment(resources = {"process/example.bpmn20.xml"})
    public void testRunProcess() throws Exception {
        CamelContext ctx = applicationContext.getBean(CamelContext.class);
        ProducerTemplate tpl = ctx.createProducerTemplate();
        MockEndpoint me = (MockEndpoint) ctx.getEndpoint("mock:service1");
        me.expectedBodiesReceived("ala");


        String instanceId = (String) tpl.requestBody("direct:start", Collections.singletonMap("var1","ala"));

        tpl.sendBodyAndProperty("direct:receive", null, ActivitiProducer.PROCESS_ID_PROPERTY, instanceId);

        assertProcessEnded(instanceId);

        me.assertIsSatisfied();
    }


    @Deployment(resources = {"process/example.bpmn20.xml"})
    public void testRunProcessByKey() throws Exception {
        CamelContext ctx = applicationContext.getBean(CamelContext.class);
        ProducerTemplate tpl = ctx.createProducerTemplate();
        MockEndpoint me = (MockEndpoint) ctx.getEndpoint("mock:service1");
        me.expectedBodiesReceived("ala");



        tpl.sendBodyAndProperty("direct:start", Collections.singletonMap("var1","ala"), ActivitiProducer.PROCESS_KEY_PROPERTY, "key1");

        String instanceId = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey("key1")
                                .singleResult().getProcessInstanceId();


        tpl.sendBodyAndProperty("direct:receive", null, ActivitiProducer.PROCESS_KEY_PROPERTY, "key1");

        assertProcessEnded(instanceId);

        me.assertIsSatisfied();
    }

}
