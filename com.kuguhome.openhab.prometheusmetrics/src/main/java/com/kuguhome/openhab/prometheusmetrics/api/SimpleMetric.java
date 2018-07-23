package com.kuguhome.openhab.prometheusmetrics.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

@Component(service = { SimpleMetric.class, MetricSettable.class }, scope = ServiceScope.SINGLETON)
public class SimpleMetric implements MetricSettable {

    private final Logger logger = LoggerFactory.getLogger(SimpleMetric.class);

    private static Gauge simpleMetricGauge;

    private String metricName;
    private double metricValue;
    private Map<String, Double> labelsValues;

    @Override
    public void expose() {
        // simpleMetricGauge.set(metricValue); TODO: fix this
        this.labelsValues.forEach((l, v) -> {
            Child child = new Child();
            child.set(v);
            simpleMetricGauge.setChild(child, l);
        });
    }

    @Activate
    protected void activate() {
        logger.info(SimpleMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(SimpleMetric.class.getName() + " deactivated.");
    }

    @Override
    public void set(String metricName, double metricValue, Map<String, Double> labelsValues) {
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.labelsValues = labelsValues;
        if (simpleMetricGauge == null) {
            simpleMetricGauge = Gauge.build(metricName, metricName).labelNames(metricName).register(restRegistry);
        }
    }

}
