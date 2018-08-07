package com.kuguhome.openhab.prometheusmetrics.api;

import io.prometheus.client.CollectorRegistry;

/**
 * This interface describes exposure of metric to REST service
 *
 * @author Roman Malyugin
 *
 */

public interface RESTExposable {

    static final CollectorRegistry restRegistry = CollectorRegistry.defaultRegistry;

    void expose();

}