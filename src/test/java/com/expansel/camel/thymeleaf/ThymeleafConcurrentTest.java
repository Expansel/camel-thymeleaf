package com.expansel.camel.thymeleaf;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ThymeleafConcurrentTest extends CamelTestSupport {

    @Test
    public void testNoConcurrentProducers() throws Exception {
        doSendMessages(1, 1);
    }

    @Test
    public void testConcurrentProducers() throws Exception {
        doSendMessages(10, 5);
    }

    private void doSendMessages(int files, int poolSize) throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(files);
        getMockEndpoint("mock:result").assertNoDuplicates(body());
        getMockEndpoint("mock:result").message(0).body().contains("<body>");

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < files; i++) {
            final int index = i;
            executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    template.sendBody("direct:start", "Body thread " + index);
                    return null;
                }
            });
        }

        assertMockEndpointsSatisfied();
        executor.shutdownNow();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start").to("thymeleaf:com/expansel/camel/thymeleaf/concurrent.xhtml").to("mock:result");
            }
        };
    }

}
