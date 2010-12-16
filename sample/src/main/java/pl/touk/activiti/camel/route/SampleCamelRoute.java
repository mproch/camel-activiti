/*
 * Copyright TouK (c) 2010.
 */

package pl.touk.activiti.camel.route;

import org.apache.camel.builder.RouteBuilder;
import pl.touk.activiti.camel.ActivitiEndpoint;
import pl.touk.activiti.camel.ActivitiProducer;

public class SampleCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("activiti:camelProcess:serviceTask").setBody().property("var1").
                to("mock:service1");

        from("direct:start").to("activiti:camelProcess");

        from("direct:receive").to("activiti:camelProcess:receive");

    }
}
