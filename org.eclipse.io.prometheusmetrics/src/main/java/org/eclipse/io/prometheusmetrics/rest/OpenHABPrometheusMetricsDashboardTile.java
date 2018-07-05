/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.io.prometheusmetrics.rest;

import org.openhab.ui.dashboard.DashboardTile;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The dashboard tile and resource registering for Metrics
 *
 * @author Roman Malyugin
 *
 */
@Component
public class OpenHABPrometheusMetricsDashboardTile implements DashboardTile {

    @Override
    public String getName() {
        return "Metrics";
    }

    @Override
    public String getUrl() {
        return "../metrics/index.html";
    }

    @Override
    public String getOverlay() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return "../metrics/metrics.png";
    }

    public static final String METRICS_ALIAS = "/metrics";

    private static final Logger logger = LoggerFactory.getLogger(OpenHABPrometheusMetricsDashboardTile.class);

    protected HttpService httpService;
    // private OpenHABPrometheusMetricsThingManager thingManager;

    protected void activate() {
        try {
            httpService.registerResources(METRICS_ALIAS, "web", null);
            logger.info("Started Metrics at " + METRICS_ALIAS);
        } catch (NamespaceException e) {
            logger.error("Error during Metrics startup: {}", e.getMessage());
        }
    }

    protected void deactivate() {
        httpService.unregister(METRICS_ALIAS);
        logger.info("Stopped Metrics");
    }

    @Reference
    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

}
