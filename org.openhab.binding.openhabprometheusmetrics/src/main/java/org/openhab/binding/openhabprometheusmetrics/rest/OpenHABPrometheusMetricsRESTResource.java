/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openhabprometheusmetrics.rest;

import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.eclipse.smarthome.io.rest.Stream2JSONInputStream;
import org.openhab.binding.openhabprometheusmetrics.data.MetricItem;
import org.openhab.binding.openhabprometheusmetrics.data.MetricsProvider;
import org.openhab.binding.openhabprometheusmetrics.data.NodePromFileMetricsProvider;
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
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets metrics info")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 404, message = "Unknown page") })
    public Response getMetrics() throws Exception {

        MetricsProvider<MetricItem> metricsProvider = new NodePromFileMetricsProvider(); // TODO: change it to factory
                                                                                         // after it will be more than
                                                                                         // one
        Stream<MetricItem> stream = metricsProvider.getMetrics();

        return Response.ok(new Stream2JSONInputStream(stream)).build();
    }

}
