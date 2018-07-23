package com.kuguhome.openhab.prometheusmetrics.api;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class describes the default implementation of MetricManager
 *
 * @author Roman Malyugin
 *
 */

@Component(service = { DefaultMetricManager.class, MetricManager.class })
public class DefaultMetricManager implements MetricManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultMetricManager.class);

    private List<RESTExposable> exposables = new CopyOnWriteArrayList<>();

    @Override
    public List<RESTExposable> getExposables() {
        return Collections.unmodifiableList(exposables);
    }

    @Activate
    protected void activate() {
        logger.info(DefaultMetricManager.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(DefaultMetricManager.class.getName() + " deactivated.");
    }

    @Override
    public void registerMetric(RESTExposable exposable) {
        exposables.add(exposable);
    }

}
