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
import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuguhome.openhab.prometheusmetrics.api.DefaultMetricManager;
import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;
import com.kuguhome.openhab.prometheusmetrics.exposable.InboxCountMetric;
import com.kuguhome.openhab.prometheusmetrics.exposable.JVMMetric;
import com.kuguhome.openhab.prometheusmetrics.exposable.OpenHABBundleStateMetric;
import com.kuguhome.openhab.prometheusmetrics.exposable.OpenHABThingStateMetric;
import com.kuguhome.openhab.prometheusmetrics.exposable.SmarthomeEventCountMetric;
import com.kuguhome.openhab.prometheusmetrics.exposable.ThreadPoolMetric;
import com.kuguhome.openhab.prometheusmetrics.internal.PrometheusMetricsActivator;

import io.prometheus.client.CollectorRegistry;
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

    private static final Logger logger = LogManager.getLogger(PrometheusMetricsActivator.class);

    public static final String PATH_HABMETRICS = "metrics";
    public static final String METRICS_ALIAS = "/" + PATH_HABMETRICS;

    @Reference
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

        metricManager.getExposables().parallelStream().filter(Objects::nonNull).forEach(RESTExposable::expose);

        final StringWriter writer = new StringWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        return Response.ok(writer.toString()).build();

    }

    @Activate
    protected void activate() {

        JVMMetric.initialize();

        metricManager.registerMetric(openHABBundleStateMetric);
        metricManager.registerMetric(threadPoolMetric);
        metricManager.registerMetric(smarthomeEventCountMetric);
        metricManager.registerMetric(openHABThingStateMetric);
        metricManager.registerMetric(inboxCountMetric);

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
    protected DefaultMetricManager metricManager;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    volatile protected InboxCountMetric inboxCountMetric;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    volatile protected OpenHABBundleStateMetric openHABBundleStateMetric;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    volatile protected OpenHABThingStateMetric openHABThingStateMetric;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    volatile protected SmarthomeEventCountMetric smarthomeEventCountMetric;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    volatile protected ThreadPoolMetric threadPoolMetric;

}
