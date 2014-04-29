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
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.EndpointJSON;
import com.spectral.cc.core.mapping.wat.rest.ToolBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/domain/endpoint")
public class EndpointEndpoint {
    private static final Logger log = LoggerFactory.getLogger(EndpointEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayEndpoint(@PathParam("param") long id) {
        log.debug("[{}] get endpoint : {}", new Object[]{Thread.currentThread().getId(), id});
        Endpoint endpoint = (Endpoint) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
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
            return Response.status(404).entity("Endpoint with id " + id + " not found.").build();
        }
    }

    @GET
    public Response displayAllEndpoints() {
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        log.debug("[{}] get endpoints", new Object[]{Thread.currentThread().getId()});
        try {
            EndpointJSON.manyEndpoints2JSON((HashSet<Endpoint>) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoints(null), outStream);
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
        log.debug("[{}] create endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), url, parentNodeID});
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(url, parentNodeID);
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
        log.debug("[{}] delete endpoint : ({})", new Object[]{Thread.currentThread().getId(), endpointID});
        try {
            MappingBootstrap.getMappingSce().getEndpointSce().deleteEndpoint(endpointID);
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
    public Response getEndpoint(@QueryParam("URL")String URL, @QueryParam("ID")long id) {
        log.debug("[{}] get endpoint : {}|{}", new Object[]{Thread.currentThread().getId(), URL,id});
        if (id!=0) {
            return displayEndpoint(id);
        } else if (URL!=null) {
            Endpoint endpoint = (Endpoint) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(URL);
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
                return Response.status(404).entity("Endpoint with URL " + URL + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Request error: URL and id are not defined. You must define one of thes parameters").build();
        }
    }

    @GET
    @Path("/update/url")
    public Response setEndpointURL(@QueryParam("ID")long id, @QueryParam("URL") String url) {
        log.debug("[{}] update endpoint url: ({},{})", new Object[]{Thread.currentThread().getId(), id, url});
        Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
        if (endpoint!=null) {
            endpoint.setEndpointURL(url);
            return Response.status(200).entity("Endpoint ("+id+") URL successfully updated to " + url + ".").build();
        } else {
            return Response.status(404).entity("Error while updating endpoint (" + id + ") URL " + url + " : endpoint " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/parentNode")
    public Response setEndpointParentNode(@QueryParam("ID")long id, @QueryParam("parentNodeID") long parentNodeID) {
        log.debug("[{}] update endpoint parent node: ({},{})", new Object[]{Thread.currentThread().getId(), id, parentNodeID});
        Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
        if (endpoint!=null) {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(parentNodeID);
            if (node!=null) {
                node.setNodeParentNode(node);
                return Response.status(200).entity("Endpoint ("+id+") parent node successfully updated to " + parentNodeID + ".").build();
            } else {
                return Response.status(404).entity("Error while updating endpoint (" + id + ") parent node " + parentNodeID + " : node " + parentNodeID + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while updating endpoint (" + id + ") parent node " + parentNodeID + " : endpoint " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/twinEndpoints/add")
    public Response addTwinEndpoint(@QueryParam("ID")long id, @QueryParam("twinEndpointID") long twinEndpointID) {
        log.debug("[{}] update endpoint by adding twin endpoint: ({},{})", new Object[]{Thread.currentThread().getId(), id, twinEndpointID});
        Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
        if (endpoint!=null) {
            Endpoint twinEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(twinEndpointID);
            if (twinEP!=null) {
                endpoint.addTwinEndpoint(twinEP);
                return Response.status(200).entity("Twin endpoint ("+twinEndpointID+") successfully added to endpoint " + id + ".").build();
            } else {
                return Response.status(404).entity("Error while adding twin endpoint " + twinEndpointID + " to endpoint (" + id + ") : endpoint " + twinEndpointID + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while adding twin endpoint " + twinEndpointID + " to endpoint (" + id + ") : endpoint " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/twinEndpoints/delete")
    public Response deleteTwinEndpoint(@QueryParam("ID")long id, @QueryParam("twinEndpointID") long twinEndpointID) {
        log.debug("[{}] update endpoint by deleting twin endpoint: ({},{})", new Object[]{Thread.currentThread().getId(), id, twinEndpointID});
        Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
        if (endpoint!=null) {
            Endpoint twinEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(twinEndpointID);
            if (twinEP!=null) {
                endpoint.removeTwinEndpoint(twinEP);
                return Response.status(200).entity("Twin endpoint ("+twinEndpointID+") successfully deleted from endpoint " + id + ".").build();
            } else {
                return Response.status(404).entity("Error while deleting twin endpoint " + twinEndpointID + " from endpoint (" + id + ") : endpoint " + twinEndpointID + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while deleting twin endpoint " + twinEndpointID + " from endpoint (" + id + ") : endpoint " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addEndpointProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value,
                                        @DefaultValue("String") @QueryParam("propertyType") String type) {
        log.debug("[{}] update endpoint by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), id, name, value, type});
        Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
        if (endpoint!=null) {
            Object oValue;
            try {
                oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
            endpoint.addEndpointProperty(name, oValue);
            return Response.status(200).entity("Property ("+name+","+value+") successfully added to endpoint "+id+".").build();
        } else {
            return Response.status(404).entity("Error while adding property " + name + " to endpoint (" + id + ") : endpoint " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteEndpointProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name) {
        log.debug("[{}] update endpoint by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), id, name});
        Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
        if (endpoint!=null) {
            endpoint.removeEndpointProperty(name);
            return Response.status(200).entity("Property ("+name+") successfully deleted from endpoint "+id+".").build();
        } else {
            return Response.status(404).entity("Error while deleting property " + name + " from endpoint (" + id + ") : endpoint " + id + " not found.").build();
        }
    }
}