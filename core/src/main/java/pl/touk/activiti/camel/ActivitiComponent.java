package pl.touk.activiti.camel;

import org.activiti.engine.RuntimeService;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class ActivitiComponent extends DefaultComponent {

    private RuntimeService runtimeService;

    public ActivitiComponent(CamelContext context) {
        super(context);
        runtimeService = getByType(context, RuntimeService.class);
    }

    private <T> T getByType(CamelContext ctx, Class<T> kls) {
        Map<String, T> looked = ctx.getRegistry().lookupByType(kls);
        if (looked.isEmpty()) {
            return null;
        }
        return looked.values().iterator().next();

    }

    @Override
    protected Endpoint createEndpoint(String s, String s1, Map<String, Object> stringObjectMap) throws Exception {

        return new ActivitiEndpoint(s, getCamelContext(), runtimeService);
    }
}
