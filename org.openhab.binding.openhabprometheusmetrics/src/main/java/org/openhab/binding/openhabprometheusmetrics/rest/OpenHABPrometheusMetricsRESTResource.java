/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openhabprometheusmetrics.rest;

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
import org.eclipse.smarthome.io.rest.RESTResource;
import org.eclipse.smarthome.io.rest.Stream2JSONInputStream;
import org.openhab.binding.openhabprometheusmetrics.data.MetricItem;
import org.openhab.binding.openhabprometheusmetrics.data.MetricsProvider;
import org.openhab.binding.openhabprometheusmetrics.data.NodePromFileMetricsProvider;
import org.openhab.binding.openhabprometheusmetrics.util.NodeFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class OpenHABPrometheusMetricsRESTResource implements RESTResource {

    private final Logger logger = LoggerFactory.getLogger(OpenHABPrometheusMetricsRESTResource.class);

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
        /*
         * try (Writer writer = response.getWriter()) {
         * writer.write("openhab_bundle_state{bundle=\"org.openhab.binding.openhabprometheusmetrics\"} Resolved");
         * writer.flush();
         * }
         */
        return Response.ok("openhab_bundle_state{bundle=\"org.openhab.binding.openhabprometheusmetrics\"} Resolved")
                .build();
    }

}
