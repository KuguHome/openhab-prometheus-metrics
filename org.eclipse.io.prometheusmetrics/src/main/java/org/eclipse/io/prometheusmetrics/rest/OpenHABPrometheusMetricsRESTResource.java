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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.inbox.Inbox;
import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.core.events.EventFilter;
import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
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
@Component(service = { EventSubscriber.class, EventHandler.class, RESTResource.class,
        OpenHABPrometheusMetricsRESTResource.class })
public class OpenHABPrometheusMetricsRESTResource /* extends EventBridge */
        implements RESTResource, EventHandler, EventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(OpenHABPrometheusMetricsRESTResource.class);

    public static final String METRICS_ALIAS = "/metrics";

    private final Gauge openhabThingState = Gauge.build("openhab_thing_state", "openHAB Things state")
            .labelNames("thing").register(CollectorRegistry.defaultRegistry);
    private final Gauge openhabBundleState = Gauge.build("openhab_bundle_state", "openHAB OSGi bundles state")
            .labelNames("bundle").register(CollectorRegistry.defaultRegistry);
    private final Gauge openhabInboxCount = Gauge.build("openhab_inbox_count", "openHAB inbox count")
            .register(CollectorRegistry.defaultRegistry);
    private final Gauge smarthomeEventCount = Gauge.build("smarthome_event_count", "openHAB event count")
            .labelNames("source").register(CollectorRegistry.defaultRegistry);

    public static final String PATH_HABMETRICS = "metrics";

    private ThingRegistry thingRegistry;
    private Inbox inbox;
    protected HttpService httpService;
    // private EventAdmin eventAdmin;
    private EventHandler eventHandler;
    private EventSubscriber eventSubscriber;

    private Map<String, Queue<org.eclipse.smarthome.core.events.@NonNull Event>> smarthomeEventCache = new ConcurrentHashMap<>();

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
            openhabInboxCount.setChild(child);
        }

        smarthomeEventCache.forEach((s, q) -> {
            Child child = new Child();
            child.set(q.size());
            smarthomeEventCount.setChild(child, s);
        });

        /*
         * for (DiscoveryResult discoveryResult : inboxList) {
         * Child child = new Child();
         * child.set(discoveryResult.ge().ordinal());
         * discoveryResult.getLabel();
         * openhabThingState.setChild(child, thing.getUID().getAsString());
         * }
         */

        thingRegistry.getAll().parallelStream().forEach(t -> {
            Child child = new Child();
            child.set(t.getStatus().ordinal());
            openhabThingState.setChild(child, t.getUID().getAsString());
        });

        Bundle[] bundles = FrameworkUtil.getBundle(OpenHABPrometheusMetricsRESTResource.class).getBundleContext()
                .getBundles();
        Stream.of(bundles).parallel().forEach(b -> {
            Child child = new Child();
            child.set(b.getState());
            openhabBundleState.setChild(child, b.getSymbolicName());
        });

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

    // @Reference
    // public void setEventAdmin(EventAdmin eventAdmin) {
    // this.eventAdmin = eventAdmin;
    // }
    //
    // public void unsetEventAdmin(EventAdmin eventAdmin) {
    // this.eventAdmin = null;
    // }
    //

    public void unsetEventSubscriber() {
        this.eventSubscriber = null;
    }

    public void unsetEventHandler() {
        this.eventHandler = null;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Reference
    public void setEventSubscriber(EventSubscriber eventSubscriber) {
        this.eventSubscriber = eventSubscriber;
    }

    @Override
    public void handleEvent(Event event) {
        logger.debug("event!");
    }

    @Override
    public void receive(org.eclipse.smarthome.core.events.@NonNull Event event) {
        if (event.getTopic().startsWith("smarthome/")) {
            String source = event.getSource();
            if (source == null) {
                logger.debug("Event source equals to null.");
                String[] tmp = event.getTopic().split("/");
                source = tmp[tmp.length - 2];
            }
            smarthomeEventCache.putIfAbsent(source, new ConcurrentLinkedQueue<>());
            smarthomeEventCache.get(source).add(event);
        } else {
            logger.debug("Received event not from smarthome");
        }
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        Set<String> types = new HashSet<>(2);
        types.add(ItemCommandEvent.TYPE);
        types.add(ItemStateEvent.TYPE);
        return types;
    }

    @Override
    public EventFilter getEventFilter() {
        return null;
    }

}
