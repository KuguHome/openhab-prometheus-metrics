package org.openhab.binding.openhabprometheusmetrics.data;

import java.util.stream.Stream;

public class StubMetricsProvider implements MetricsProvider<MetricItem> {

    @Override
    public Stream<MetricItem> getMetrics() throws Exception {

        MetricItem metricItem = new MetricItem("FOO", "BAR");

        return Stream.of(metricItem);
    }

}
