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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
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
import java.util.*;

@Path("/mapping/domain/endpoints")
public class EndpointEndpoint {
    private static final Logger log = LoggerFactory.getLogger(EndpointEndpoint.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint,
                                                                            Session mappingSession) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Node reqEndpointParentNode = null;
        List<Endpoint> reqEndpointTwinEndpoints = new ArrayList<>();
        HashMap<String, Object> reqEndpointProperties = new HashMap<>();

        if (jsonDeserializedEndpoint.getEndpointParentNodeID() != null) {
            if (mappingSession!=null) reqEndpointParentNode =  MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, jsonDeserializedEndpoint.getEndpointParentNodeID());
            else reqEndpointParentNode =  MappingBootstrap.getMappingSce().getNodeSce().getNode(jsonDeserializedEndpoint.getEndpointParentNodeID());
            if (reqEndpointParentNode==null) ret.setErrorMessage("Request Error : node with provided ID " + jsonDeserializedEndpoint.getEndpointParentNodeID() + " was not found.");
        } else ret.setErrorMessage("Request Error : no parent node ID provided...");

        if (ret.getErrorMessage()==null && jsonDeserializedEndpoint.getEndpointTwinEndpointsID()!=null && jsonDeserializedEndpoint.getEndpointTwinEndpointsID().size() > 0) {
            for (String id : jsonDeserializedEndpoint.getEndpointTwinEndpointsID()) {
                Endpoint twinEndpoint ;
                if (mappingSession!=null) twinEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else twinEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (twinEndpoint != null)
                    reqEndpointTwinEndpoints.add(twinEndpoint);
                else {
                    ret.setErrorMessage("Request Error : twin endpoint with provided ID " + id + " was not found.");
                    break;
                }
            }
        }

        if (ret.getErrorMessage()==null && jsonDeserializedEndpoint.getEndpointProperties()!=null && jsonDeserializedEndpoint.getEndpointProperties().size() > 0) {
            for (PropertiesJSON.JSONDeserializedProperty deserializedProperty : jsonDeserializedEndpoint.getEndpointProperties()) {
                try {
                    Object oValue = ToolBox.extractPropertyObjectValueFromString(deserializedProperty.getPropertyValue(), deserializedProperty.getPropertyType());
                    reqEndpointProperties.put(deserializedProperty.getPropertyName(), oValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    ret.setErrorMessage("Request Error : invalid property " + deserializedProperty.getPropertyName() + ".");
                    break;
                }
            }
        }

        // LOOK IF NODE MAYBE UPDATED OR CREATED
        Endpoint deserializedEndpoint = null;
        if (ret.getErrorMessage() == null && jsonDeserializedEndpoint.getEndpointID()!=null) {
            if (mappingSession!=null) deserializedEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, jsonDeserializedEndpoint.getEndpointID());
            else deserializedEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(jsonDeserializedEndpoint.getEndpointID());
            if (deserializedEndpoint==null)
                ret.setErrorMessage("Request Error : endpoint with provided ID " + jsonDeserializedEndpoint.getEndpointID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedEndpoint==null && jsonDeserializedEndpoint.getEndpointURL() != null)
            if (mappingSession!=null) deserializedEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, jsonDeserializedEndpoint.getEndpointID());
            else deserializedEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(jsonDeserializedEndpoint.getEndpointID());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqEndpointURL = jsonDeserializedEndpoint.getEndpointURL();
            String reqEndpointParentNodeID = jsonDeserializedEndpoint.getEndpointParentNodeID();
            if (deserializedEndpoint == null)
                if (mappingSession!=null) deserializedEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(mappingSession, reqEndpointURL, reqEndpointParentNodeID);
                else deserializedEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(reqEndpointURL, reqEndpointParentNodeID);
            else {
                if (reqEndpointURL!=null)
                    if (mappingSession!=null) ((SProxEndpoint)deserializedEndpoint).setEndpointURL(mappingSession, reqEndpointURL);
                    else deserializedEndpoint.setEndpointURL(reqEndpointURL);
                if (reqEndpointParentNode!=null)
                    if (mappingSession!=null) ((SProxEndpoint)deserializedEndpoint).setEndpointParentNode(mappingSession, reqEndpointParentNode);
                    else deserializedEndpoint.setEndpointParentNode(reqEndpointParentNode);
            }

            if (jsonDeserializedEndpoint.getEndpointTwinEndpointsID()!=null) {
                List<Endpoint> twinEndpointsToDelete = new ArrayList<>();
                for (Endpoint existingTwinEndpoint : deserializedEndpoint.getTwinEndpoints())
                    if (!reqEndpointTwinEndpoints.contains(existingTwinEndpoint))
                        twinEndpointsToDelete.add(existingTwinEndpoint);
                for (Endpoint twinEndpointToDelete : twinEndpointsToDelete) {
                    if (mappingSession!=null) {
                        ((SProxEndpoint)deserializedEndpoint).removeTwinEndpoint(mappingSession, twinEndpointToDelete);
                        ((SProxEndpoint)twinEndpointToDelete).removeTwinEndpoint(mappingSession, deserializedEndpoint);
                    } else {
                        deserializedEndpoint.removeTwinEndpoint(twinEndpointToDelete);
                        twinEndpointToDelete.removeTwinEndpoint(deserializedEndpoint);
                    }
                }

                for (Endpoint twinEndpointToAdd : reqEndpointTwinEndpoints) {
                    if (mappingSession!=null) {
                        ((SProxEndpoint)deserializedEndpoint).addTwinEndpoint(mappingSession, twinEndpointToAdd);
                        ((SProxEndpoint)twinEndpointToAdd).addTwinEndpoint(mappingSession, deserializedEndpoint);
                    } else {
                        deserializedEndpoint.addTwinEndpoint(twinEndpointToAdd);
                        twinEndpointToAdd.addTwinEndpoint(deserializedEndpoint);
                    }
                }
            }

            if (jsonDeserializedEndpoint.getEndpointProperties()!=null) {
                if (deserializedEndpoint.getEndpointProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedEndpoint.getEndpointProperties().keySet())
                        if (!reqEndpointProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyKeyToDelete : propertiesToDelete)
                        if (mappingSession!=null) deserializedEndpoint.removeEndpointProperty(mappingSession, propertyKeyToDelete);
                        else deserializedEndpoint.removeEndpointProperty(propertyKeyToDelete);
                }

                for (String propertyKey : reqEndpointProperties.keySet())
                    if (mappingSession!=null) deserializedEndpoint.addEndpointProperty(mappingSession, propertyKey, reqEndpointProperties.get(propertyKey));
                    else deserializedEndpoint.addEndpointProperty(propertyKey, reqEndpointProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedEndpoint);
        }

        return ret;
    }

    private Response _displayEndpoint(String id, String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get endpoint : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                Endpoint endpoint;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (endpoint != null) {
                    try {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        EndpointJSON.oneEndpoint2JSON(endpoint, outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        return Response.status(Status.OK).entity(result).build();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                } else return Response.status(Status.NOT_FOUND).entity("Endpoint with id " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayEndpoint(@PathParam("param") String id) {
        return _displayEndpoint(id, null);
    }

    @GET
    public Response displayAllEndpoints(@QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get endpoints", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                HashSet<Endpoint> endpoints ;
                if (mappingSession!=null) endpoints = (HashSet<Endpoint>) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoints(mappingSession, null);
                else endpoints = (HashSet<Endpoint>) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoints(null);

                EndpointJSON.manyEndpoints2JSON(endpoints, outStream);
                result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/get")
    public Response getEndpoint(@QueryParam("URL")String URL, @QueryParam("ID")String id, @QueryParam("selector") String selector, @QueryParam("sessionID") String sessionId) {
        if (id!=null) {
            return _displayEndpoint(id, sessionId);
        } else if (URL!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get endpoint : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), URL});
            if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                try {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    Endpoint endpoint;
                    if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpointByURL(mappingSession, URL);
                    else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpointByURL(URL);

                    if (endpoint != null) {
                        try {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            EndpointJSON.oneEndpoint2JSON(endpoint, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            return Response.status(Status.OK).entity(result).build();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            String result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                    } else return Response.status(Status.NOT_FOUND).entity("Endpoint with URL " + URL + " not found.").build();
                } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
            } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        } else if (selector != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get endpoint : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), selector});
            if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                try {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    HashSet<Endpoint> ret;
                    if (mappingSession!=null) ret = (HashSet<Endpoint>) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoints(mappingSession, selector);
                    else ret = (HashSet<Endpoint>) MappingBootstrap.getMappingSce().getEndpointSce().getEndpoints(selector);

                    if (ret != null && ret.size() > 0) {
                        String result;
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        try {
                            EndpointJSON.manyEndpoints2JSON(ret, outStream);
                            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            return Response.status(Status.OK).entity(result).build();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                    } else
                        return Response.status(Status.NOT_FOUND).entity("No endpoints matching selector " + selector + " ...").build();
                } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
            } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        } else return Response.status(Status.INTERNAL_SERVER_ERROR).entity("MappingDSLRegistryRequest error: URL and id are not defined. You must define one of thes parameters").build();
    }

    @GET
    @Path("/create")
    public Response createEndpoint(@QueryParam("endpointURL")String url, @QueryParam("parentNodeID")String parentNodeID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), url, parentNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                Endpoint endpoint;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(mappingSession, url, parentNodeID);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(url, parentNodeID);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                try {
                    EndpointJSON.oneEndpoint2JSON(endpoint, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @POST
    public Response postEndpoint(@QueryParam("payload") String payload, @QueryParam("sessionID") String sessionId) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create or update endpoint : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    Response ret;
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(EndpointJSON.JSON2Endpoint(payload), mappingSession);
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        EndpointJSON.oneEndpoint2JSON((Endpoint) deserializationResponse.getDeserializedObject(), outStream);
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
            } return Response.status(Status.BAD_REQUEST).entity("No payload attached to this POST").build();
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/delete")
    public Response deleteEndpoint(@QueryParam("ID")String endpointID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete endpoint : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), endpointID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }
                if (mappingSession!=null) MappingBootstrap.getMappingSce().getEndpointSce().deleteEndpoint(mappingSession, endpointID);
                else MappingBootstrap.getMappingSce().getEndpointSce().deleteEndpoint(endpointID);
                return Response.status(Status.OK).entity("Endpoint (" + endpointID + ") successfully deleted.").build();
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/url")
    public Response setEndpointURL(@QueryParam("ID")String id, @QueryParam("URL") String url, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update endpoint url: ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, url});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }
                Endpoint endpoint;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (endpoint != null) {
                    if (mappingSession!=null) ((SProxEndpoint)endpoint).setEndpointURL(mappingSession, url);
                    else endpoint.setEndpointURL(url);
                    return Response.status(Status.OK).entity("Endpoint (" + id + ") URL successfully updated to " + url + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating endpoint (" + id + ") URL " + url + " : endpoint " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/parentNode")
    public Response setEndpointParentNode(@QueryParam("ID")String id, @QueryParam("parentNodeID")String parentNodeID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update endpoint parent node: ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, parentNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (endpoint != null) {
                    Node node;
                    if (mappingSession!=null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, parentNodeID);
                    else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(parentNodeID);
                    if (node != null) {
                        if (mappingSession!=null) ((SProxNode)node).setNodeParentNode(mappingSession, node);
                        else node.setNodeParentNode(node);
                        return Response.status(Status.OK).entity("Endpoint (" + id + ") parent node successfully updated to " + parentNodeID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating endpoint (" + id + ") parent node " + parentNodeID + " : node " + parentNodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating endpoint (" + id + ") parent node " + parentNodeID + " : endpoint " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/twinEndpoints/add")
    public Response addTwinEndpoint(@QueryParam("ID")String id, @QueryParam("twinEndpointID") String twinEndpointID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update endpoint by adding twin endpoint: ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, twinEndpointID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (endpoint != null) {
                    Endpoint twinEP;
                    if (mappingSession!=null) twinEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, twinEndpointID);
                    else twinEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(twinEndpointID);
                    if (twinEP != null) {
                        if (mappingSession!=null) {
                            ((SProxEndpoint)endpoint).addTwinEndpoint(mappingSession, twinEP);
                            ((SProxEndpoint)twinEP).addTwinEndpoint(mappingSession, endpoint);
                        } else {
                            endpoint.addTwinEndpoint(twinEP);
                            twinEP.addTwinEndpoint(endpoint);
                        }
                        return Response.status(Status.OK).entity("Twin endpoint (" + twinEndpointID + ") successfully added to endpoint " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding twin endpoint " + twinEndpointID + " to endpoint (" + id + ") : endpoint " + twinEndpointID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding twin endpoint " + twinEndpointID + " to endpoint (" + id + ") : endpoint " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/twinEndpoints/delete")
    public Response deleteTwinEndpoint(@QueryParam("ID")String id, @QueryParam("twinEndpointID")String twinEndpointID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update endpoint by deleting twin endpoint: ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, twinEndpointID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);

                if (endpoint != null) {
                    Endpoint twinEP;
                    if (mappingSession!=null) twinEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, twinEndpointID);
                    else twinEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(twinEndpointID);
                    if (twinEP != null) {
                        if (mappingSession!=null) {
                            ((SProxEndpoint)endpoint).removeTwinEndpoint(mappingSession, twinEP);
                            ((SProxEndpoint)twinEP).removeTwinEndpoint(mappingSession, endpoint);
                        } else {
                            endpoint.removeTwinEndpoint(twinEP);
                            twinEP.removeTwinEndpoint(endpoint);
                        }
                        return Response.status(Status.OK).entity("Twin endpoint (" + twinEndpointID + ") successfully deleted from endpoint " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting twin endpoint " + twinEndpointID + " from endpoint (" + id + ") : endpoint " + twinEndpointID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting twin endpoint " + twinEndpointID + " from endpoint (" + id + ") : endpoint " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/properties/add")
    public Response addEndpointProperty(@QueryParam("ID")String id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value,
                                        @DefaultValue("String") @QueryParam("propertyType") String type, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update endpoint by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name, value, type});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                if (name != null && value != null && type != null) {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }
                    Endpoint endpoint ;
                    if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                    else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);

                    if (endpoint != null) {
                        Object oValue;
                        try {
                            oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            String result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                        if (mappingSession!=null) endpoint.addEndpointProperty(mappingSession, name, oValue);
                        else endpoint.addEndpointProperty(name, oValue);
                        return Response.status(Status.OK).entity("Property (" + name + "," + value + ") successfully added to endpoint " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding property " + name + " to endpoint (" + id + ") : endpoint " + id + " not found.").build();
                } else {
                    log.warn("Property is not defined correctly : {name: " + name + ", type: " + type + ", value: " + value + "}.");
                    return Response.status(Status.BAD_REQUEST).entity("Property is not defined correctly : {name: " + name + ", type: " + type + ", value: " + value + "}.").build();
                }
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteEndpointProperty(@QueryParam("ID")String id, @QueryParam("propertyName") String name, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update endpoint by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);

                if (endpoint != null) {
                    if (mappingSession!=null) endpoint.removeEndpointProperty(mappingSession, name);
                    else endpoint.removeEndpointProperty(name);
                    return Response.status(Status.OK).entity("Property (" + name + ") successfully deleted from endpoint " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting property " + name + " from endpoint (" + id + ") : endpoint " + id + " not found.").build();
                }
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}