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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxGate;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
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

@Path("/mapping/domain/gates")
public class GateEp {
    private static final Logger log = LoggerFactory.getLogger(GateEp.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(GateJSON.JSONDeserializedGate jsonDeserializedGate, Session mappingSession) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Container reqNodeContainer = null;
        List<Node> reqNodeChildNodes = new ArrayList<>();
        List<Node> reqNodeTwinNodes = new ArrayList<>();
        List<Endpoint> reqNodeEndpoints = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedGate.getNode().getNodeContainerID()!=null) {
            if (mappingSession!=null) reqNodeContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, jsonDeserializedGate.getNode().getNodeContainerID());
            else reqNodeContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(jsonDeserializedGate.getNode().getNodeContainerID());
            if (reqNodeContainer == null) ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedGate.getNode().getNodeContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeChildNodesID()!=null && jsonDeserializedGate.getNode().getNodeChildNodesID().size() > 0 ) {
            for (String id : jsonDeserializedGate.getNode().getNodeChildNodesID()) {
                Node childNode ;
                if (mappingSession!=null) childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
                if (childNode != null) reqNodeChildNodes.add(childNode);
                else {
                    ret.setErrorMessage("Request Error : child node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeTwinNodesID()!=null && jsonDeserializedGate.getNode().getNodeTwinNodesID().size() > 0 ) {
            for (String id : jsonDeserializedGate.getNode().getNodeTwinNodesID()) {
                Node twinNode ;
                if (mappingSession!=null) twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
                if (twinNode != null) reqNodeTwinNodes.add(twinNode);
                else {
                    ret.setErrorMessage("Request Error : twin node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeEndpointsID()!=null && jsonDeserializedGate.getNode().getNodeEndpointsID().size() > 0) {
            for (String id : jsonDeserializedGate.getNode().getNodeTwinNodesID()) {
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (endpoint != null) reqNodeEndpoints.add(endpoint);
                else {
                    ret.setErrorMessage("Request Error : endpoint with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeProperties()!=null && jsonDeserializedGate.getNode().getNodeProperties().size() > 0) {
            for (PropertiesJSON.JSONDeserializedProperty deserializedProperty : jsonDeserializedGate.getNode().getNodeProperties()) {
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

        // LOOK IF NODE MAYBE UPDATED OR CREATED
        Gate deserializedGate = null;
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeID() != null) {
            if (mappingSession!=null) deserializedGate = MappingBootstrap.getMappingSce().getGateSce().getGate(mappingSession, jsonDeserializedGate.getNode().getNodeID());
            else deserializedGate = MappingBootstrap.getMappingSce().getGateSce().getGate(jsonDeserializedGate.getNode().getNodeID());
            if (deserializedGate == null)
                ret.setErrorMessage("Request Error : gate with provided ID " + jsonDeserializedGate.getNode().getNodeID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedGate == null && reqNodeContainer != null && jsonDeserializedGate.getNode().getNodeName() != null)
            if (mappingSession!=null) deserializedGate = MappingBootstrap.getMappingSce().getGateByName(mappingSession, reqNodeContainer, jsonDeserializedGate.getNode().getNodeName());
            else deserializedGate = MappingBootstrap.getMappingSce().getGateByName(reqNodeContainer, jsonDeserializedGate.getNode().getNodeName());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqNodeName = jsonDeserializedGate.getNode().getNodeName();
            String reqGateURL = jsonDeserializedGate.getGateURL();
            boolean reqGateIsPrimaryAdmin = jsonDeserializedGate.isGateIsPrimaryAdmin();
            String reqContainerID = jsonDeserializedGate.getNode().getNodeContainerID();
            if (deserializedGate == null)
                if (mappingSession!=null) deserializedGate = MappingBootstrap.getMappingSce().getGateSce().createGate(mappingSession, reqGateURL, reqNodeName, reqContainerID, reqGateIsPrimaryAdmin);
                else deserializedGate = MappingBootstrap.getMappingSce().getGateSce().createGate(reqGateURL, reqNodeName, reqContainerID, reqGateIsPrimaryAdmin);
            else {
                if (reqNodeName != null)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).setNodeName(mappingSession, reqNodeName);
                    else deserializedGate.setNodeName(reqNodeName);
                if (reqNodeContainer != null)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).setNodeContainer(mappingSession, reqNodeContainer);
                    else deserializedGate.setNodeContainer(reqNodeContainer);
                if (reqGateIsPrimaryAdmin && deserializedGate.getNodePrimaryAdminEndpoint()==null && reqGateURL!=null) {
                    Endpoint primaryAdminEp ;
                    if (mappingSession!=null) primaryAdminEp = MappingBootstrap.getMappingSce().getEndpointSce().getEndpointByURL(mappingSession, reqGateURL);
                    else primaryAdminEp = MappingBootstrap.getMappingSce().getEndpointSce().getEndpointByURL(reqGateURL);

                    if (primaryAdminEp == null)
                        if (mappingSession!=null) primaryAdminEp = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(mappingSession, reqGateURL, deserializedGate.getNodeID());
                        else primaryAdminEp = MappingBootstrap.getMappingSce().getEndpointSce().createEndpoint(reqGateURL, deserializedGate.getNodeID());

                    if (mappingSession!=null) ((SProxGate)deserializedGate).setNodePrimaryAdminEnpoint(mappingSession, primaryAdminEp);
                    else deserializedGate.setNodePrimaryAdminEnpoint(primaryAdminEp);
                }
            }

            if (jsonDeserializedGate.getNode().getNodeChildNodesID()!=null) {
                List<Node> childNodesToDelete = new ArrayList<>();
                for (Node existingChildNode : deserializedGate.getNodeChildNodes())
                    if (!reqNodeChildNodes.contains(existingChildNode))
                        childNodesToDelete.add(existingChildNode);
                for (Node childNodeToDelete : childNodesToDelete)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).removeNodeChildNode(mappingSession, childNodeToDelete);
                    else deserializedGate.removeNodeChildNode(childNodeToDelete);

                for (Node childNodeReq : reqNodeChildNodes)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).addNodeChildNode(mappingSession, childNodeReq);
                    else deserializedGate.addNodeChildNode(childNodeReq);
            }

            if (jsonDeserializedGate.getNode().getNodeTwinNodesID()!=null) {
                List<Node> twinNodesToDelete = new ArrayList<>();
                for (Node existingTwinNode : deserializedGate.getTwinNodes())
                    if (!reqNodeTwinNodes.contains(existingTwinNode))
                        twinNodesToDelete.add(existingTwinNode);
                for (Node twinNodeToDelete : twinNodesToDelete) {
                    if (mappingSession!=null) {
                        ((SProxGate)deserializedGate).removeTwinNode(mappingSession, twinNodeToDelete);
                        ((SProxGate)twinNodeToDelete).removeTwinNode(mappingSession, deserializedGate);
                    } else {
                        deserializedGate.removeTwinNode(twinNodeToDelete);
                        twinNodeToDelete.removeTwinNode(deserializedGate);
                    }
                }

                for (Node twinNodeReq : reqNodeTwinNodes) {
                    if (mappingSession!=null) {
                        ((SProxGate)deserializedGate).addTwinNode(mappingSession, twinNodeReq);
                        ((SProxGate)twinNodeReq).addTwinNode(mappingSession, deserializedGate);
                    } else {
                        deserializedGate.addTwinNode(twinNodeReq);
                        twinNodeReq.addTwinNode(deserializedGate);
                    }
                }
            }

            if (jsonDeserializedGate.getNode().getNodeEndpointsID()!=null) {
                List<Endpoint> endpointsToDelete = new ArrayList<>();
                for (Endpoint existingEndpoint : deserializedGate.getNodeEndpoints())
                    if (!reqNodeEndpoints.contains(existingEndpoint))
                        endpointsToDelete.add(existingEndpoint);
                for (Endpoint endpointToDelete : endpointsToDelete)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).removeEndpoint(mappingSession, endpointToDelete);
                    else deserializedGate.removeEndpoint(endpointToDelete);

                for (Endpoint endpointReq : reqNodeEndpoints)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).addEndpoint(mappingSession, endpointReq);
                    else deserializedGate.addEndpoint(endpointReq);
            }

            if (jsonDeserializedGate.getNode().getNodeProperties()!=null) {
                if (deserializedGate.getNodeProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedGate.getNodeProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxGate)deserializedGate).removeNodeProperty(mappingSession, propertyToDelete);
                        else deserializedGate.removeNodeProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    if (mappingSession!=null) ((SProxGate)deserializedGate).addNodeProperty(mappingSession, propertyKey, reqProperties.get(propertyKey));
                    else deserializedGate.addNodeProperty(propertyKey, reqProperties.get(propertyKey));
            }
            ret.setDeserializedObject(deserializedGate);
        }

        return ret;
    }

    private Response _displayGate(String id, String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get gate : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null) {
                        log.debug("[{}-{}]Response error: no session found", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }
                }

                Gate gate;
                if (mappingSession!=null) gate = MappingBootstrap.getMappingSce().getGateSce().getGate(mappingSession, id);
                else gate = MappingBootstrap.getMappingSce().getGateSce().getGate(id);

                if (gate != null) {
                    try {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        GateJSON.oneGate2JSON(gate, outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        log.debug("[{}-{}]Response returned: success", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.OK).entity(result).build();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Gate with id " + id + " not found.").build();
                }
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayGate(@PathParam("param") String id) {
        return _displayGate(id, null);
    }

    @GET
    public Response displayAllGates(@QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get gates", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null) {
                        log.debug("[{}-{}]Response error: no session found", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }
                }

                HashSet<Gate> gates ;
                if (mappingSession!=null) gates = (HashSet<Gate>) MappingBootstrap.getMappingSce().getGateSce().getGates(mappingSession, null);
                else gates = (HashSet<Gate>) MappingBootstrap.getMappingSce().getGateSce().getGates(null);

                GateJSON.manyGates2JSON(gates, outStream);
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
    public Response getGate(@QueryParam("ID")String id, @QueryParam("sessionID") String sessionId) {
        return _displayGate(id, sessionId);
    }

    @GET
    @Path("/create")
    public Response createGate(@QueryParam("URL")String url, @QueryParam("name")String name,
                               @QueryParam("containerID")String containerID, @QueryParam("isPrimaryAdmin")boolean isPrimaryAdmin,
                               @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create gate : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), url, name, containerID, isPrimaryAdmin});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null) {
                        log.debug("[{}-{}]Response error: no session found", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }
                }

                Gate gate;
                if (mappingSession!=null) gate = MappingBootstrap.getMappingSce().getGateSce().createGate(mappingSession, url, name, containerID, isPrimaryAdmin);
                else gate = MappingBootstrap.getMappingSce().getGateSce().createGate(url, name, containerID, isPrimaryAdmin);

                try {
                    GateJSON.oneGate2JSON(gate, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    log.debug("[{}-{}]Response returned: success", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
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
    public Response postGate(@QueryParam("payload") String payload, @QueryParam("sessionID") String sessionId) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create or update gate : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Session mappingSession = null;
                    if (sessionId!=null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null) {
                            log.debug("[{}-{}]Response error: no session found", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                        }
                    }

                    Response ret;
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(GateJSON.JSON2Gate(payload), mappingSession);
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        GateJSON.oneGate2JSON((Gate) deserializationResponse.getDeserializedObject(), outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        ret = Response.status(Status.OK).entity(result).build();
                    } else {
                        String result = "ERROR while deserializing !";
                        ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                    log.debug("[{}-{}]Response returned: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), ret.getStatus()});
                    return ret ;
                } catch (MappingDSException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else return Response.status(Status.BAD_REQUEST).entity("No payload attached to this POST").build();
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/delete")
    public Response deleteGate(@QueryParam("ID")String nodeID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete gate : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), nodeID});
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

                if (mappingSession!=null) MappingBootstrap.getMappingSce().getGateSce().deleteGate(mappingSession, nodeID);
                else MappingBootstrap.getMappingSce().getGateSce().deleteGate(nodeID);
                return Response.status(Status.OK).entity("Gate (" + nodeID + ") successfully deleted.").build();
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/primaryEndpoint")
    public Response setPrimaryEndpoint(@QueryParam("ID")String id, @QueryParam("endpointID")String endpointID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update primary admin endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), id, endpointID});
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

                Gate gate;
                if (mappingSession!=null) gate = MappingBootstrap.getMappingSce().getGateSce().getGate(mappingSession, id);
                else gate = MappingBootstrap.getMappingSce().getGateSce().getGate(id);

                if (gate != null) {
                    Endpoint endpoint;
                    if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, endpointID);
                    else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
                    if (endpoint != null) {
                        gate.setNodePrimaryAdminEnpoint(endpoint);
                        return Response.status(Status.OK).entity("Gate (" + id + ") primary endpoint successfully updated to " + endpointID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating gate (" + id + ") primary endpoint " + endpointID + " : gate " + id + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating gate (" + id + ") primary endpoint " + endpointID + " : gate " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}