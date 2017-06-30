package com.expansel.server.camel.thymeleaf;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ThymeleafTest extends CamelTestSupport {

    @Test
    public void testWithHeaders() throws Exception {
        final DataHandler dataHandler = new DataHandler("attachment", "text/plain");

        Exchange response = template.request("direct:a", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().addAttachment("attach", dataHandler);
                exchange.getIn().setBody("Some text");
                exchange.getIn().setHeader("header", "Some header");
                exchange.setProperty("property", "Some property");
            }
        });
        assertCommonResult(response);
        // is copying attachments
        assertSame(dataHandler, response.getOut().getAttachment("attach"));
        // is copying headers
        assertEquals("Some header", response.getOut().getHeader("header"));
    }

    private void assertCommonResult(Exchange response) {
        // is processing template
        assertEquals("<h1>Some header</h1><p>Some text</p><p>Some property</p>", response.getOut().getBody());
        // template is expected resource
        assertEquals("com/expansel/server/camel/thymeleaf/template.xhtml",
                response.getOut().getHeader(ThymeleafConstants.THYMELEAF_RESOURCE_URI));
    }

    @Test
    public void testWithVariableMap() throws Exception {
        // test using variable map rather than headers
        Exchange response = template.request("direct:a", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("");
                exchange.getIn().setHeader("header", "Not using this");
                Map<String, Object> variableMap = new HashMap<String, Object>();
                Map<String, Object> headersMap = new HashMap<String, Object>();
                headersMap.put("header", "Some header");
                variableMap.put("headers", headersMap);
                variableMap.put("body", "Some text");
                variableMap.put("exchange", exchange);
                exchange.getIn().setHeader(ThymeleafConstants.THYMELEAF_VARIABLE_MAP, variableMap);
                // this property is accessed through
                // exchange.properties.property
                exchange.setProperty("property", "Some property");
            }
        });

        assertCommonResult(response);
        // is copying headers
        assertEquals("Not using this", response.getOut().getHeader("header"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:a").to("thymeleaf:com/expansel/server/camel/thymeleaf/template.xhtml");
            }
        };
    }
}
