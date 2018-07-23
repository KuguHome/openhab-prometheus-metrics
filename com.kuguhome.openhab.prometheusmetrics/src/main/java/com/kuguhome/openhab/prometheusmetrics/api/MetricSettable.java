package com.kuguhome.openhab.prometheusmetrics.api;

import java.util.Map;

public interface MetricSettable extends RESTExposable {

    void set(String metricName, double metrciValue, Map<String, Double> labelsValues);

}
