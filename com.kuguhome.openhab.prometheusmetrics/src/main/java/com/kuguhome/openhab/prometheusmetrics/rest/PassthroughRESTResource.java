/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.kuguhome.openhab.prometheusmetrics.rest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

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
@Path(PassthroughRESTResource.PATH_HABMETRICS)
@Api(PassthroughRESTResource.PATH_HABMETRICS)
@Component(properties = "OSGI-INF/config.properties", service = { RESTResource.class, PassthroughRESTResource.class })
public class PassthroughRESTResource implements RESTResource {

    private final Logger logger = LoggerFactory.getLogger(PassthroughRESTResource.class);

    public static final String PATH_HABMETRICS = "passthrough/node/metrics";
    public static final String METRICS_ALIAS = "/" + PATH_HABMETRICS;

    private String url;

    @GET
    @RolesAllowed({ Role.USER, Role.ADMIN })
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Get Passthrough")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 404, message = "Unknown page") })
    public Response getPassthrough(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {

        URL urlObj = new URL(url);
        URLConnection urlConnection = urlObj.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        return Response.ok(CharStreams.toString(reader)).build();
    }

    @Activate
    protected void activate(Map<String, String> properties) {
        url = properties.get("passthrough.url");
    }

    @Deactivate
    protected void deactivate() {
    }

}
