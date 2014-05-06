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
import com.spectral.cc.core.mapping.ds.domain.Container;
import com.spectral.cc.core.mapping.ds.domain.Transport;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.ContainerJSON;
import com.spectral.cc.core.mapping.wat.json.ds.domain.TransportJSON;
import com.spectral.cc.core.mapping.wat.rest.ToolBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/mapping/domain/transports")
public class TransportEndpoint {
    private static final Logger log = LoggerFactory.getLogger(GateEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayTransport(@PathParam("param") long id) {
        log.debug("[{}] get transport : {}", new Object[]{Thread.currentThread().getId(), id});
        MappingSce mapping = MappingBootstrap.getMappingSce();
        Transport transport = (Transport) mapping.getTransportSce().getTransport(id);
        if (transport != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                TransportJSON.oneTransport2JSON(transport, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("Transport with id " + id + " not found.").build();
        }
    }

    @GET
    public Response displayAllTransports() {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        log.debug("[{}] get transports", new Object[]{Thread.currentThread().getId()});
        try {
            TransportJSON.manyTransports2JSON((HashSet<Transport>) mapping.getTransportSce().getTransports(null), outStream);
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
    public Response createTransport(@QueryParam("name")String transportName) {
        log.debug("[{}] create transport : {}", new Object[]{Thread.currentThread().getId(), transportName});
        MappingSce mapping  = MappingBootstrap.getMappingSce();
        Transport transport = (Transport) mapping.getTransportSce().createTransport(transportName);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            TransportJSON.oneTransport2JSON(transport, outStream);
            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteTransport(@QueryParam("ID")long transportID) {
        log.debug("[{}] delete transport : {}", new Object[]{Thread.currentThread().getId(), transportID});
        MappingSce mapping = MappingBootstrap.getMappingSce();
        try {
            mapping.getTransportSce().deleteTransport(transportID);
            return Response.status(200).entity("Transport (" + transportID + ") has been successfully deleted !").build();
        } catch (MappingDSException e) {
            return Response.status(404).entity("Error while deleting transport with id " + transportID).build();
        }
    }

    @GET
    @Path("/get")
    public Response getTransport(@QueryParam("ID")long transportID) {
        return displayTransport(transportID);
    }

    @GET
    @Path("/update/name")
    public Response setTransportName(@QueryParam("ID")long id, @QueryParam("name")String name) {
        log.debug("[{}] update transport name: ({},{})", new Object[]{Thread.currentThread().getId(), id, name});
        Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(id);
        if (transport != null) {
            transport.setTransportName(name);
            return Response.status(200).entity("Transport ("+id+") name successfully updated to " + name + ".").build();
        } else {
            return Response.status(404).entity("Error while updating transport (" + id + ") name " + name + " : link " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addTransportProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value,
                                         @DefaultValue("String") @QueryParam("propertyType") String type) {
        log.debug("[{}] update transport by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), id, name, value, type});
        Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(id);
        if (transport != null) {
            Object oValue;
            try {
                oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
            transport.addTransportProperty(name, oValue);
            return Response.status(200).entity("Property ("+name+","+value+") successfully added to transport "+id+".").build();
        } else {
            return Response.status(404).entity("Error while adding property "+name+" to transport "+id+" : transport " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteTransportProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name) {
        log.debug("[{}] update transport by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), id, name});
        Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(id);
        if (transport != null) {
            transport.removeTransportProperty(name);
            return Response.status(200).entity("Property ("+name+") successfully deleted from transport "+id+".").build();
        } else {
            return Response.status(404).entity("Error while deleting property "+name+" from transport "+id+" : transport " + id + " not found.").build();
        }
    }
}