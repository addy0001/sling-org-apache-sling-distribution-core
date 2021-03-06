package org.apache.sling.distribution.agent.impl;

import org.apache.sling.distribution.log.impl.DefaultDistributionLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by adisharm on 9/8/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ForwardDistribtuionAgentFactoryTest {

    @Spy
    @InjectMocks
    ForwardDistributionAgentFactory forwardDistributionAgentFactory = new ForwardDistributionAgentFactory();

    @Captor
    ArgumentCaptor<String> serviceNameCaptor;

    @Captor
    ArgumentCaptor<Boolean> queueProcessingEnabledCaptor;

    @Captor
    ArgumentCaptor<String[]> passiveQueuesCaptor;

    @Captor
    ArgumentCaptor<String> queueProviderCaptor;

    @Captor
    ArgumentCaptor<Boolean> asyncDeliveryCaptor;

    @Captor
    ArgumentCaptor<String> retryStrategyCaptor;

    @Captor
    ArgumentCaptor<Integer> timeoutCaptor;

    @Captor
    ArgumentCaptor<Integer> retryAttemptsCaptor;

    @Captor
    ArgumentCaptor<String[]> allowedRootsCaptor;

    BundleContext bundleContext;
    DefaultDistributionLog distributionLog;
    SimpleDistributionAgent distributionAgent = mock(SimpleDistributionAgent.class);

    @Test
    public void testConfigsBeingRead() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ForwardDistributionAgentFactory.HTTP, 20);
        String[] allowedRoots = {"/home"};
        String[] passiveQueues = {"queue1","queue2"};
        config.put("allowed.roots",allowedRoots);
        config.put("queue.processing.enabled",true);
        config.put("serviceName","serviceName");
        config.put("passiveQueues",passiveQueues);
        config.put("queue.provider","simple");
        config.put("async.delivery",true);
        config.put("retry.strategy","retryStrategy");
        config.put("retry.attempts",19);

        doReturn(distributionAgent).when(forwardDistributionAgentFactory).createAgent(any(String.class), any(BundleContext.class), any(DefaultDistributionLog.class), any(String.class), allowedRootsCaptor.capture(), any(boolean.class), any(String[].class),
                any(Map.class), any(Integer.class), any(String.class), any(boolean.class), any(String.class), any(int.class), any(Map.class));
        forwardDistributionAgentFactory.createAgent("test", bundleContext, config, distributionLog);

        verify(forwardDistributionAgentFactory).createAgent(any(String.class), any(BundleContext.class), any(DefaultDistributionLog.class), serviceNameCaptor.capture(), allowedRootsCaptor.capture(), queueProcessingEnabledCaptor.capture(), passiveQueuesCaptor.capture(),
                any(Map.class), timeoutCaptor.capture(), queueProviderCaptor.capture(), asyncDeliveryCaptor.capture(), retryStrategyCaptor.capture(), retryAttemptsCaptor.capture(), any(Map.class));

        assertEquals("serviceName",serviceNameCaptor.getValue().toString());
        assertEquals(allowedRoots.length, allowedRootsCaptor.getValue().length);
        assertEquals(true,queueProcessingEnabledCaptor.getValue());
        assertEquals(passiveQueues.length,passiveQueuesCaptor.getValue().length);
        assertEquals(20000,timeoutCaptor.getValue().intValue());
        assertEquals("simple",queueProviderCaptor.getValue().toString());
        assertEquals(true, asyncDeliveryCaptor.getValue());
        assertEquals("retryStrategy", retryStrategyCaptor.getValue());
        assertEquals(19, retryAttemptsCaptor.getValue().intValue());
    }
}
