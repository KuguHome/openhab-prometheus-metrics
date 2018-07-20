package com.kuguhome.openhab.prometheusmetrics.api;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuguhome.openhab.prometheusmetrics.exposable.InboxCountMetric;

@Component
public class MetricManager {

    private final Logger logger = LoggerFactory.getLogger(InboxCountMetric.class);

    private List<RESTExposable> exposables = new CopyOnWriteArrayList<>();

    public void addExposable(RESTExposable exposable) {
        exposables.add(exposable);
    }

    public List<RESTExposable> getExposables() {
        return Collections.unmodifiableList(exposables);
    }

    @Activate
    protected void activate() {
        logger.info(InboxCountMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(InboxCountMetric.class.getName() + " deactivated.");
    }

}
