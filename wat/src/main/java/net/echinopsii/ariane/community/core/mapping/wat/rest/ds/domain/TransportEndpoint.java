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
package net.echinopsii.ariane.community.core.mapping.wat.rest.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.wat.rest.ds.JSONDeserializationResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Path("/mapping/domain/transports")
public class TransportEndpoint {
    private static final Logger log = LoggerFactory.getLogger(GateEndpoint.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(TransportJSON.JSONDeserializedTransport jsonDeserializedTransport) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedTransport.getTransportProperties()!=null && jsonDeserializedTransport.getTransportProperties().size() > 0) {
            for (PropertiesJSON.JSONDeserializedProperty deserializedProperty : jsonDeserializedTransport.getTransportProperties()) {
                try {
                    Object oValue = ToolBox.extractPropertyObjectValueFromString(deserializedProperty.getPropertyValue(), deserializedProperty.getPropertyType());
                    reqProperties.put(deserializedProperty.getPropertyName(), oValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    ret.setErrorMessage("Request Error : invalid property " + deserializedProperty.getPropertyName() + ".");
                    break;
                }
            }
        }

        // LOOK IF TRANSPORT MAYBE UPDATED OR CREATED
        Transport deserializedTransport = null;
        if (ret.getErrorMessage() == null && jsonDeserializedTransport.getTransportID()!=0) {
            deserializedTransport = (Transport) MappingBootstrap.getMappingSce().getTransportSce().getTransport(jsonDeserializedTransport.getTransportID());
            if (deserializedTransport==null) ret.setErrorMessage("Request Error : transport with provided ID " + jsonDeserializedTransport.getTransportID() + " was not found.");
        }

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            if (deserializedTransport == null) deserializedTransport = MappingBootstrap.getMappingSce().getTransportSce().createTransport(jsonDeserializedTransport.getTransportName());
            else { if (jsonDeserializedTransport.getTransportName()!=null) deserializedTransport.setTransportName(jsonDeserializedTransport.getTransportName()); }

            if (jsonDeserializedTransport.getTransportProperties()!=null) {
                if (deserializedTransport.getTransportProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedTransport.getTransportProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        deserializedTransport.removeTransportProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    deserializedTransport.addTransportProperty(propertyKey, reqProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedTransport);
        }
        return ret;
    }

    @GET
    @Path("/{param}")
    public Response displayTransport(@PathParam("param") long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get transport : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            Transport transport = (Transport) mapping.getTransportSce().getTransport(id);
            if (transport != null) {
                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    TransportJSON.oneTransport2JSON(transport, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Transport with id " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllTransports() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get transports", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            String result = "";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                TransportJSON.manyTransports2JSON((HashSet<Transport>) mapping.getTransportSce().getTransports(null), outStream);
                result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getTransport(@QueryParam("ID")long transportID) {
        return displayTransport(transportID);
    }

    @GET
    @Path("/create")
    public Response createTransport(@QueryParam("name")String transportName) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create transport : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), transportName});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            Transport transport = (Transport) mapping.getTransportSce().createTransport(transportName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                TransportJSON.oneTransport2JSON(transport, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @POST
    public Response postTransport(@QueryParam("payload") String payload) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create or update node : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Response ret;
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(TransportJSON.JSON2Transport(payload));
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        TransportJSON.oneTransport2JSON((Transport)deserializationResponse.getDeserializedObject(), outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        ret = Response.status(Status.OK).entity(result).build();
                    } else {
                        String result = "ERROR while deserializing !";
                        ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                    return ret ;
                } catch (MappingDSException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.BAD_REQUEST).entity("No payload attached to this POST").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteTransport(@QueryParam("ID")long transportID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete transport : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), transportID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            try {
                mapping.getTransportSce().deleteTransport(transportID);
                return Response.status(Status.OK).entity("Transport (" + transportID + ") has been successfully deleted !").build();
            } catch (MappingDSException e) {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting transport with id " + transportID).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response setTransportName(@QueryParam("ID")long id, @QueryParam("name")String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update transport name: ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(id);
            if (transport != null) {
                transport.setTransportName(name);
                return Response.status(Status.OK).entity("Transport (" + id + ") name successfully updated to " + name + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating transport (" + id + ") name " + name + " : link " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addTransportProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value,
                                         @DefaultValue("String") @QueryParam("propertyType") String type) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update transport by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name, value, type});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(id);
            if (transport != null) {
                Object oValue;
                try {
                    oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
                transport.addTransportProperty(name, oValue);
                return Response.status(Status.OK).entity("Property (" + name + "," + value + ") successfully added to transport " + id + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding property " + name + " to transport " + id + " : transport " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteTransportProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update transport by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(id);
            if (transport != null) {
                transport.removeTransportProperty(name);
                return Response.status(Status.OK).entity("Property (" + name + ") successfully deleted from transport " + id + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting property " + name + " from transport " + id + " : transport " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }
}