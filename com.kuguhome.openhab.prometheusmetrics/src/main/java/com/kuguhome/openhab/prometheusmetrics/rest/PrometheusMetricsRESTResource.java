/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.kuguhome.openhab.prometheusmetrics.rest;

import java.io.StringWriter;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuguhome.openhab.prometheusmetrics.api.MetricManager;
import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;
import com.kuguhome.openhab.prometheusmetrics.exposable.InboxCountMetric;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class describes the /metrics resource of the REST API
 *
 * @author Roman Malyugin - Initial contribution
 *
 */
@Path(PrometheusMetricsRESTResource.PATH_HABMETRICS)
@Api(PrometheusMetricsRESTResource.PATH_HABMETRICS)
@Component(service = { RESTResource.class, PrometheusMetricsRESTResource.class })
public class PrometheusMetricsRESTResource implements RESTResource {

    private final Logger logger = LoggerFactory.getLogger(PrometheusMetricsRESTResource.class);

    public static final String METRICS_ALIAS = "/metrics";
    public static final String COUNTER_NAME = "logback_appender_total";

    private final static Counter logCounter = Counter
            .build("openhab_logmessages_total", "Logback log statements at various log levels").labelNames("level")
            .register(CollectorRegistry.defaultRegistry);
    private final static Gauge logErrorCounter = Gauge
            .build("openhab_logmessages_error", "Logback log statements at various log levels").labelNames("type")
            .register(CollectorRegistry.defaultRegistry);

    public static final Counter.Child TRACE_LABEL = logCounter.labels("trace");
    public static final Counter.Child DEBUG_LABEL = logCounter.labels("debug");
    public static final Counter.Child INFO_LABEL = logCounter.labels("info");
    public static final Counter.Child WARN_LABEL = logCounter.labels("warn");
    public static final Counter.Child ERROR_LABEL = logCounter.labels("error");

    public static final String PATH_HABMETRICS = "metrics";

    private MetricManager metricManager;

    protected HttpService httpService;

    @GET
    @RolesAllowed({ Role.USER, Role.ADMIN })
    @Path("/prometheus")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Gets metrics info as for Prometheus")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 404, message = "Unknown page") })
    public Response getThingsMetricsPrometheus(@Context HttpServletRequest request,
            @Context HttpServletResponse response) throws Exception {

        metricManager.getExposables().forEach(e -> {
            e.expose();
        });

        /*
         * restExposables.forEach(r -> {
         * r.
         * });
         */

        final StringWriter writer = new StringWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        return Response.ok(writer.toString()).build();

    }

    @Activate
    protected void activate() {
        metricManager.addExposable(new RESTExposable() {

            @Override
            public void expose() {
                // TODO Auto-generated method stub

            }

        });

        metricManager.addExposable(new InboxCountMetric());

        try {
            httpService.registerResources(METRICS_ALIAS, "web", null);
            logger.info("Started Metrics at " + METRICS_ALIAS);
        } catch (NamespaceException e) {
            logger.error("Error during Metrics startup: {}", e.getMessage());
        }
    }

    @Deactivate
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
