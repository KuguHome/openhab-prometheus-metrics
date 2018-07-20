package com.kuguhome.openhab.prometheusmetrics.api;

import io.prometheus.client.CollectorRegistry;

public interface RESTExposable {

    static final CollectorRegistry restRegistry = CollectorRegistry.defaultRegistry;

    void expose();

}