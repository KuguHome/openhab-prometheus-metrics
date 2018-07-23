package com.kuguhome.openhab.prometheusmetrics.api;

import java.util.List;

/**
 * This interface describes the interaction between metrics and REST service
 *
 * @author Roman Malyugin
 *
 */

public interface MetricManager {

    List<RESTExposable> getExposables();

    void registerMetric(RESTExposable exposable);
}
