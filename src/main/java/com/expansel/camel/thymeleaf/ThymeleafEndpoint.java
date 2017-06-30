package com.expansel.camel.thymeleaf;

import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.ResourceEndpoint;
import org.apache.camel.util.ExchangeHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class ThymeleafEndpoint extends ResourceEndpoint {

    public ThymeleafEndpoint(String endpointURI, Component component, String resourceURI) {
        super(endpointURI, component, resourceURI);
    }

    @Override
    protected void onExchange(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> variableMap = exchange.getIn().getHeader(ThymeleafConstants.THYMELEAF_VARIABLE_MAP,
                Map.class);
        if (variableMap == null) {
            variableMap = ExchangeHelper.createVariableMap(exchange);
        }

        // getResourceAsInputStream also considers the content cache
        String text = exchange.getContext().getTypeConverter().mandatoryConvertTo(String.class,
                getResourceAsInputStream());

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        Context ctx = new Context();
        ctx.setVariables(variableMap);

        String htmlContent = templateEngine.process(text, ctx);

        Message out = exchange.getOut();
        out.setBody(htmlContent);
        out.setHeaders(exchange.getIn().getHeaders());
        out.setHeader(ThymeleafConstants.THYMELEAF_RESOURCE_URI, getResourceUri());
        out.setAttachments(exchange.getIn().getAttachments());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
