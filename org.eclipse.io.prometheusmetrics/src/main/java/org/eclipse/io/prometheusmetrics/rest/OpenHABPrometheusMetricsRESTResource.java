/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.io.prometheusmetrics.rest;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.inbox.Inbox;
import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;
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
@Component(service = { RESTResource.class, OpenHABPrometheusMetricsRESTResource.class })
public class OpenHABPrometheusMetricsRESTResource implements RESTResource {

    private final Logger logger = LoggerFactory.getLogger(OpenHABPrometheusMetricsRESTResource.class);

    public static final String METRICS_ALIAS = "/metrics";

    // private OpenHABPrometheusMetricsThingManager thingManager;
    private ThingRegistry thingRegistry;

    private final Gauge openhabThingState = Gauge.build("openhab_thing_state", "openHAB Things state")
            .labelNames("thing").register(CollectorRegistry.defaultRegistry);
    private final Gauge openhabBundleState = Gauge.build("openhab_bundle_state", "openHAB OSGi bundles state")
            .labelNames("bundle").register(CollectorRegistry.defaultRegistry);
    private final Gauge openhabInboxState = Gauge.build("openhab_inbox_count", "openHAB inbox count")
            .register(CollectorRegistry.defaultRegistry);

    public static final String PATH_HABMETRICS = "metrics";

    private Inbox inbox;
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

        List<DiscoveryResult> inboxList = inbox.getAll();
        {
            Child child = new Child();
            child.set(inboxList.size());
            openhabInboxState.setChild(child);
        }

        /*
         * for (DiscoveryResult discoveryResult : inboxList) {
         * Child child = new Child();
         * child.set(discoveryResult.ge().ordinal());
         * discoveryResult.getLabel();
         * openhabThingState.setChild(child, thing.getUID().getAsString());
         * }
         */

        Collection<Thing> things = thingRegistry.getAll();
        for (Thing thing : things) {
            Child child = new Child();
            child.set(thing.getStatus().ordinal());
            openhabThingState.setChild(child, thing.getUID().getAsString());
        }
        Bundle[] bundles = FrameworkUtil.getBundle(OpenHABPrometheusMetricsRESTResource.class).getBundleContext()
                .getBundles();
        for (Bundle bundle : bundles) {
            Child child = new Child();
            child.set(bundle.getState());
            openhabBundleState.setChild(child, bundle.getSymbolicName());
        }

        final StringWriter writer = new StringWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        return Response.ok(writer.toString()).build();

    }

    @Reference
    protected void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    protected void unsetThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = null;
    }

    @Activate
    protected void activate() {
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

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    protected void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }

    protected void unsetInbox(Inbox inbox) {
        this.inbox = null;
    }

}
