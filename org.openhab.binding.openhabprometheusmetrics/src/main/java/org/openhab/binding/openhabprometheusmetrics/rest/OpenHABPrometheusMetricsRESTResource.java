/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openhabprometheusmetrics.rest;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.eclipse.smarthome.io.rest.Stream2JSONInputStream;
import org.openhab.binding.openhabprometheusmetrics.data.MetricItem;
import org.openhab.binding.openhabprometheusmetrics.data.MetricsProvider;
import org.openhab.binding.openhabprometheusmetrics.data.NodePromFileMetricsProvider;
import org.openhab.binding.openhabprometheusmetrics.util.NodeFileReader;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.CollectorRegistry;
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
@Path(OpenHABPrometheusMetricsRESTResource.PATH_HABMETRICS)
@Api(OpenHABPrometheusMetricsRESTResource.PATH_HABMETRICS)
@Component
public class OpenHABPrometheusMetricsRESTResource implements RESTResource {

    private final Logger logger = LoggerFactory.getLogger(OpenHABPrometheusMetricsRESTResource.class);

    // private OpenHABPrometheusMetricsThingManager thingManager;
    private ThingRegistry thingRegistry;

    private final static CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;

    public static final String PATH_HABMETRICS = "metrics";

    @GET
    @RolesAllowed({ Role.USER, Role.ADMIN })
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets metrics info as JSON")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 404, message = "Unknown page") })
    public Response getMetricsJSON() throws Exception {

        logger.info("METRICS GET STARTED.");

        MetricsProvider<MetricItem> metricsProvider = new NodePromFileMetricsProvider(); // TODO: change it to factory
                                                                                         // after it will be more than
                                                                                         // one
        Stream<MetricItem> stream = metricsProvider.getMetrics();

        return Response.ok(new Stream2JSONInputStream(stream)).build();
    }

    @GET
    @RolesAllowed({ Role.USER, Role.ADMIN })
    @Path("/text")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Gets metrics info as TXT")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 404, message = "Unknown page") })
    public Response getMetricsText() throws Exception {

        logger.info("METRICS GET STARTED.");
        String fileName = "/home/ronx/Projects/kugu/openhab-prometheus-metrics/org.openhab.binding.openhabprometheusmetrics/src/main/resources/node.prom.txt";
        NodeFileReader reader = new NodeFileReader();

        return Response.ok(reader.read(fileName).collect(Collectors.joining("\n"))).build();
    }

    @GET
    @RolesAllowed({ Role.USER, Role.ADMIN })
    @Path("/prometheus")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Gets metrics info as for Prometheus")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 404, message = "Unknown page") })
    public Response getMetricsPrometheus(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        logger.info("Prometheus scrape started.");

        Collection<Thing> things = thingRegistry.getAll();
        String[] thingsLabelNames = things.stream().map(x -> x.getUID().getAsString()).toArray(String[]::new);
        final Gauge openhabThingState = Gauge.build("openhab_thing_state", "openHAB OSGi bundles state")
                .labelNames(thingsLabelNames).register(collectorRegistry);
        for (Thing thing : things) {
            switch (thing.getStatus()) {
                case ONLINE:
                    openhabThingState.set(ThingStatus.ONLINE.ordinal());
                    break;
                case UNINITIALIZED:
                    openhabThingState.set(ThingStatus.UNINITIALIZED.ordinal());
                    break;
                case UNKNOWN:
                    openhabThingState.set(ThingStatus.UNKNOWN.ordinal());
                    break;
                case OFFLINE:
                    openhabThingState.set(ThingStatus.OFFLINE.ordinal());
                    break;
                case REMOVING:
                    openhabThingState.set(ThingStatus.REMOVING.ordinal());
                    break;
                case REMOVED:
                    openhabThingState.set(ThingStatus.REMOVED.ordinal());
                    break;
                case INITIALIZING:
                    openhabThingState.set(ThingStatus.INITIALIZING.ordinal());
                    break;

            }
            // openhabBundleState.set(val);
            // logger.debug("Thing '{}' with status '{}'", thing.getUID().getAsString(), thing.getStatus().name());
        }

        Bundle[] bundles = FrameworkUtil.getBundle(OpenHABPrometheusMetricsRESTResource.class).getBundleContext()
                .getBundles();
        String[] bundlesLabelNames = Arrays.stream(bundles).map(x -> x.getSymbolicName()).toArray(String[]::new);
        final Gauge openhabBundleState = Gauge.build("openhab_bundle_state", "openHAB OSGi bundles state")
                .labelNames(bundlesLabelNames).register(collectorRegistry);

        for (Bundle bundle : bundles) {
            switch (bundle.getState()) {
                case Bundle.ACTIVE:
                    openhabBundleState.set(Bundle.ACTIVE);
                    break;
                case Bundle.INSTALLED:
                    openhabBundleState.set(Bundle.INSTALLED);
                    break;
                case Bundle.RESOLVED:
                    openhabBundleState.set(Bundle.RESOLVED);
                    break;
                case Bundle.STARTING:
                    openhabBundleState.set(Bundle.STARTING);
                    break;
                case Bundle.STOPPING:
                    openhabBundleState.set(Bundle.STOPPING);
                    break;
                case Bundle.UNINSTALLED:
                    openhabBundleState.set(Bundle.UNINSTALLED);
                    break;
            }

        }

        // OpenHABPrometheusMetricsHandler.getInstance().getBundleContext()

        // try (Writer writer = response.getWriter()) {
        // writer.write();
        // writer.flush();
        // }
        // writer.write("openhab_bundle_state{bundle=\"org.openhab.binding.openhabprometheusmetrics\"} Resolved");
        // writer.flush();
        // }
        //
        final StringWriter writer = new StringWriter();
        TextFormat.write004(writer, collectorRegistry.metricFamilySamples());
        return Response.ok(writer.toString()).build();
    }

    // private String exposePrometheusMetrics(Request request, Response response) throws IOException {
    // Response.status(200);
    // response.type(TextFormat.CONTENT_TYPE_004);
    // final StringWriter writer = new StringWriter();
    // TextFormat.write004(writer, registry.metricFamilySamples());
    // return writer.toString();
    // }

    @Reference
    protected void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    protected void unsetThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = null;
    }

}
