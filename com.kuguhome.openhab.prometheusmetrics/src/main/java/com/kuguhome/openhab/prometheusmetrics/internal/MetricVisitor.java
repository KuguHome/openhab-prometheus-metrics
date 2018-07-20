package com.kuguhome.openhab.prometheusmetrics.internal;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

public interface MetricVisitor {

    public String visitGauge(Gauge gauge);

    public String visitCounter(Counter counter);

}
