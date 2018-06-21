package org.openhab.binding.openhabprometheusmetrics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openhab.binding.openhabprometheusmetrics.data.NodePromFileMetricsProvider;

public class NodePromFileMetricsProviderTest {

    @Test
    public void readFileTest() throws Exception {
        NodePromFileMetricsProvider node = new NodePromFileMetricsProvider();
        assertEquals(2162, node.getMetrics().count());
    }

}
