package com.expansel.server.camel.thymeleaf;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.ResourceHelper;

public class ThymeleafComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ThymeleafEndpoint endpoint = new ThymeleafEndpoint(uri, this, remaining);
        setProperties(endpoint, parameters);

        // if its a http resource then append any remaining parameters and
        // update the resource uri
        if (ResourceHelper.isHttpUri(remaining)) {
            remaining = ResourceHelper.appendParameters(remaining, parameters);
            endpoint.setResourceUri(remaining);
        }
        return endpoint;
    }

}
