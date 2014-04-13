/**
 * Mapping Web Service :
 * provide a mapping DS Web Service and REST Service
 *
 * Copyright (C) 2013  Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.spectral.cc.core.mapping.wat.rest.ds.domain;

import com.spectral.cc.core.mapping.ds.MappingDSException;
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.EndpointJSON;
import com.spectral.cc.core.mapping.wat.rest.ToolBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/domain/endpoint")
public class EndpointEndpoint {
    private static final Logger log = LoggerFactory.getLogger(EndpointEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayEndpoint(@PathParam("param") long id) {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        Endpoint endpoint = (Endpoint) mapping.getEndpointSce().getEndpoint(id);
        if (endpoint != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                EndpointJSON.oneEndpoint2JSON(endpoint, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No endpoint with id " + id + " in the repository").build();
        }
    }

    @GET
    public Response displayAllEndpoints() {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            EndpointJSON.manyEndpoints2JSON((HashSet<Endpoint>) mapping.getEndpointSce().getEndpoints(null), outStream);
            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/create")
    public Response createEndpoint(@QueryParam("endpointURL")String url, @QueryParam("parentNodeID")long parentNodeID) {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Endpoint endpoint = mapping.getEndpointSce().createEndpoint(url, parentNodeID);
            try {
                EndpointJSON.oneEndpoint2JSON(endpoint, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteEndpoint(@QueryParam("ID")long endpointID) {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        try {
            mapping.getEndpointSce().deleteEndpoint(endpointID);
            return Response.status(200).entity("Endpoint (" + endpointID + ") successfully deleted.").build();
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/get")
    public Response getEndpoint(@QueryParam("ID")long id) {
        return displayEndpoint(id);
    }

    @GET
    @Path("/get")
    public Response getEndpoint(@QueryParam("URL")String URL) {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        Endpoint endpoint = (Endpoint) mapping.getEndpointSce().getEndpoint(URL);
        if (endpoint != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                EndpointJSON.oneEndpoint2JSON(endpoint, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No endpoint with URL " + URL + " in the repository").build();
        }
    }

    @GET
    @Path("/update/url")
    public Response setEndpointURL(@QueryParam("ID")long id, @QueryParam("URL") String url) {
        return null;
    }

    @GET
    @Path("/update/parentNode")
    public Response setEndpointParentNode(@QueryParam("ID")long id, @QueryParam("parentNodeID") long parentNodeID) {
        return null;
    }

    @GET
    @Path("/update/twinEndpoints/add")
    public Response addTwinEndpoint(@QueryParam("ID")long id, @QueryParam("twinEndpointID") long twinEndpointID) {
        return null;
    }

    @GET
    @Path("/update/twinEndpoints/delete")
    public Response deleteTwinEndpoint(@QueryParam("ID")long id, @QueryParam("twinEndpointID") long twinEndpointID) {
        return null;
    }

    @GET
    @Path("/update/property")
    public Response setEndpointProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value) {
        return null;
    }
}