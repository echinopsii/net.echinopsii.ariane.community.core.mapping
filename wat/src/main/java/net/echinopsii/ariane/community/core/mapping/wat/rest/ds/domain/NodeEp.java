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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON.JSONDeserializedNode;
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

@Path("/mapping/domain/nodes")
public class NodeEp {
    private static final Logger log = LoggerFactory.getLogger(NodeEp.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(JSONDeserializedNode jsonDeserializedNode, Session mappingSession) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Container reqNodeContainer = null;
        Node reqNodeParentNode = null;
        List<Node> reqNodeChildNodes = new ArrayList<>();
        List<Node> reqNodeTwinNodes = new ArrayList<>();
        List<Endpoint> reqNodeEndpoints = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedNode.getNodeContainerID()!=null) {
            if (mappingSession!=null) reqNodeContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, jsonDeserializedNode.getNodeContainerID());
            else reqNodeContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(jsonDeserializedNode.getNodeContainerID());
            if (reqNodeContainer == null) ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedNode.getNodeContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeParentNodeID()!=null) {
            if (mappingSession!=null) reqNodeParentNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, jsonDeserializedNode.getNodeParentNodeID());
            else reqNodeParentNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(jsonDeserializedNode.getNodeParentNodeID());
            if (reqNodeParentNode == null) ret.setErrorMessage("Request Error : parent node with provided ID " + jsonDeserializedNode.getNodeParentNodeID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeChildNodesID()!=null && jsonDeserializedNode.getNodeChildNodesID().size() > 0 ) {
            for (String id : jsonDeserializedNode.getNodeChildNodesID()) {
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
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeTwinNodesID()!=null && jsonDeserializedNode.getNodeTwinNodesID().size() > 0 ) {
            for (String id : jsonDeserializedNode.getNodeTwinNodesID()) {
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
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeEndpointsID()!=null && jsonDeserializedNode.getNodeEndpointsID().size() > 0) {
            for (String id : jsonDeserializedNode.getNodeTwinNodesID()) {
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
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeProperties()!=null && jsonDeserializedNode.getNodeProperties().size() > 0) {
            for (PropertiesJSON.JSONDeserializedProperty deserializedProperty : jsonDeserializedNode.getNodeProperties()) {
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
        Node deserializedNode = null;
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeID() != null) {
            if (mappingSession!=null) deserializedNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, jsonDeserializedNode.getNodeID());
            else deserializedNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(jsonDeserializedNode.getNodeID());
            if (deserializedNode == null)
                ret.setErrorMessage("Request Error : node with provided ID " + jsonDeserializedNode.getNodeID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedNode == null && reqNodeContainer != null && jsonDeserializedNode.getNodeName() != null)
            if (mappingSession!=null) deserializedNode = MappingBootstrap.getMappingSce().getNodeByName(mappingSession, reqNodeContainer, jsonDeserializedNode.getNodeName());
            else deserializedNode = MappingBootstrap.getMappingSce().getNodeByName(reqNodeContainer, jsonDeserializedNode.getNodeName());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqNodeName = jsonDeserializedNode.getNodeName();
            String reqContainerID = jsonDeserializedNode.getNodeContainerID();
            String reqParentNodeID = jsonDeserializedNode.getNodeParentNodeID();
            if (deserializedNode == null)
                if (mappingSession!=null) deserializedNode = MappingBootstrap.getMappingSce().getNodeSce().createNode(mappingSession, reqNodeName, reqContainerID, reqParentNodeID);
                else deserializedNode = MappingBootstrap.getMappingSce().getNodeSce().createNode(reqNodeName, reqContainerID, reqParentNodeID);
            else {
                if (reqNodeName != null)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).setNodeName(mappingSession, reqNodeName);
                    else deserializedNode.setNodeName(reqNodeName);
                if (reqNodeContainer != null)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).setNodeContainer(mappingSession, reqNodeContainer);
                    else deserializedNode.setNodeContainer(reqNodeContainer);
                if (reqNodeParentNode != null)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).setNodeParentNode(mappingSession, reqNodeParentNode);
                    else deserializedNode.setNodeParentNode(reqNodeParentNode);
            }

            if (jsonDeserializedNode.getNodeChildNodesID()!=null) {
                List<Node> childNodesToDelete = new ArrayList<>();
                for (Node existingChildNode : deserializedNode.getNodeChildNodes())
                    if (!reqNodeChildNodes.contains(existingChildNode))
                        childNodesToDelete.add(existingChildNode);
                for (Node childNodeToDelete : childNodesToDelete)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).removeNodeChildNode(mappingSession, childNodeToDelete);
                    else deserializedNode.removeNodeChildNode(childNodeToDelete);

                for (Node childNodeReq : reqNodeChildNodes)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).addNodeChildNode(mappingSession, childNodeReq);
                    else deserializedNode.addNodeChildNode(childNodeReq);
            }

            if (jsonDeserializedNode.getNodeTwinNodesID()!=null) {
                List<Node> twinNodesToDelete = new ArrayList<>();
                for (Node existingTwinNode : deserializedNode.getTwinNodes())
                    if (!reqNodeTwinNodes.contains(existingTwinNode))
                        twinNodesToDelete.add(existingTwinNode);
                for (Node twinNodeToDelete : twinNodesToDelete) {
                    if (mappingSession!=null) {
                        ((SProxNode)deserializedNode).removeTwinNode(mappingSession, twinNodeToDelete);
                        ((SProxNode)twinNodeToDelete).removeTwinNode(mappingSession, deserializedNode);
                    } else {
                        deserializedNode.removeTwinNode(twinNodeToDelete);
                        twinNodeToDelete.removeTwinNode(deserializedNode);
                    }
                }

                for (Node twinNodeReq : reqNodeTwinNodes) {
                    if (mappingSession!=null) {
                        ((SProxNode)deserializedNode).addTwinNode(mappingSession, twinNodeReq);
                        ((SProxNode)twinNodeReq).addTwinNode(mappingSession, deserializedNode);
                    } else {
                        deserializedNode.addTwinNode(twinNodeReq);
                        twinNodeReq.addTwinNode(deserializedNode);
                    }
                }
            }

            if (jsonDeserializedNode.getNodeEndpointsID()!=null) {
                List<Endpoint> endpointsToDelete = new ArrayList<>();
                for (Endpoint existingEndpoint : deserializedNode.getNodeEndpoints())
                    if (!reqNodeEndpoints.contains(existingEndpoint))
                        endpointsToDelete.add(existingEndpoint);
                for (Endpoint endpointToDelete : endpointsToDelete)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).removeEndpoint(mappingSession, endpointToDelete);
                    else deserializedNode.removeEndpoint(endpointToDelete);

                for (Endpoint endpointReq : reqNodeEndpoints)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).addEndpoint(mappingSession, endpointReq);
                    else deserializedNode.addEndpoint(endpointReq);
            }

            if (jsonDeserializedNode.getNodeProperties()!=null) {
                if (deserializedNode.getNodeProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedNode.getNodeProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxNode)deserializedNode).removeNodeProperty(mappingSession, propertyToDelete);
                        else deserializedNode.removeNodeProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    if (mappingSession!=null) ((SProxNode)deserializedNode).addNodeProperty(mappingSession, propertyKey, reqProperties.get(propertyKey));
                    else deserializedNode.addNodeProperty(propertyKey, reqProperties.get(propertyKey));
            }
            ret.setDeserializedObject(deserializedNode);
        }

        return ret;
    }

    private Response _displayNode(String id, String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get node : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    try {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        NodeJSON.oneNode2JSON(node, outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        return Response.status(Status.OK).entity(result).build();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                } else return Response.status(Status.NOT_FOUND).entity("Node with id " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayNode(@PathParam("param")String id) {
        return _displayNode(id, null);
    }

    @GET
    public Response displayAllNodes(@QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get nodes", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
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

                HashSet<Node> nodes;
                if (mappingSession!=null) nodes = (HashSet<Node>) MappingBootstrap.getMappingSce().getNodeSce().getNodes(mappingSession, null);
                else nodes = (HashSet<Node>) MappingBootstrap.getMappingSce().getNodeSce().getNodes(null);
                NodeJSON.manyNodes2JSON(nodes, outStream);
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
    public Response getNode(@QueryParam(Endpoint.TOKEN_EP_URL) String endpointURL,
                            @QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                            @QueryParam(MappingSce.GLOBAL_PARAM_SELECTOR) String selector,
                            @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        try {
            if (id != null) {
                return _displayNode(id, sessionId);
            } else if (endpointURL != null) {
                Subject subject = SecurityUtils.getSubject();
                log.debug("[{}-{}] get node: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), endpointURL});
                if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    Node node;
                    if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNodeByEndpointURL(mappingSession, endpointURL);
                    else node = MappingBootstrap.getMappingSce().getNodeSce().getNodeByEndpointURL(endpointURL);

                    if (node != null) {
                        try {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            NodeJSON.oneNode2JSON(node, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            return Response.status(Status.OK).entity(result).build();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            String result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                    } else
                        return Response.status(Status.NOT_FOUND).entity("Node with endpoint URL " + endpointURL + " not found.").build();
                } else
                    return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
            } else if (selector != null) {
                Subject subject = SecurityUtils.getSubject();
                log.debug("[{}-{}] find node: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), selector});
                if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    HashSet<Node> nodes;
                    if (mappingSession != null) nodes = (HashSet<Node>) MappingBootstrap.getMappingSce().getNodeSce().getNodes(mappingSession, selector);
                    else nodes = (HashSet<Node>) MappingBootstrap.getMappingSce().getNodeSce().getNodes(selector);

                    if (nodes != null && nodes.size() > 0) {
                        String result;
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        try {
                            NodeJSON.manyNodes2JSON(nodes, outStream);
                            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            return Response.status(Status.OK).entity(result).build();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                    } else return Response.status(Status.NOT_FOUND).entity("No node matching with selector ('" + selector + "') found.").build();
                } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
            } else return Response.status(Status.INTERNAL_SERVER_ERROR).entity("MappingDSLRegistryRequest error: name and id are not defined. You must define one of these parameters").build();
        } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
    }

    @GET
    @Path("/create")
    public Response createNode(@QueryParam(NodeSce.PARAM_NODE_NAME)String nodeName,
                               @QueryParam(Container.TOKEN_CT_ID)String containerID,
                               @QueryParam(NodeSce.PARAM_NODE_PNID)String parentNodeID,
                               @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create node : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), nodeName, containerID, parentNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                Session mappingSession = null;
                if (sessionId!=null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null) {
                        log.debug("[{}-{}]Response error: no session found", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }
                }

                Node node ;
                if (mappingSession!=null) node = MappingBootstrap.getMappingSce().getNodeSce().createNode(mappingSession, nodeName, containerID, parentNodeID);
                else node = MappingBootstrap.getMappingSce().getNodeSce().createNode(nodeName, containerID, parentNodeID);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                try {
                    NodeJSON.oneNode2JSON(node, outStream);
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
    public Response postNode(@QueryParam(MappingSce.GLOBAL_PARAM_PAYLOAD) String payload,
                             @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create or update node : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
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
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(NodeJSON.JSON2Node(payload), mappingSession);
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        NodeJSON.oneNode2JSON((Node)deserializationResponse.getDeserializedObject(), outStream);
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
    public Response deleteNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String nodeID,
                               @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), nodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                Session mappingSession = null;
                if (sessionId!=null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                if (mappingSession!=null) MappingBootstrap.getMappingSce().getNodeSce().deleteNode(mappingSession, nodeID);
                else MappingBootstrap.getMappingSce().getNodeSce().deleteNode(nodeID);
                return Response.status(Status.OK).entity("Node (" + nodeID + ") successfully deleted.").build();
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/name")
    public Response setNodeName(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                @QueryParam(NodeSce.PARAM_NODE_NAME)String name,
                                @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node name : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    if (mappingSession != null) ((SProxNode)node).setNodeName(mappingSession, name);
                    else node.setNodeName(name);
                    return Response.status(Status.OK).entity("Node (" + id + ") name successfully updated to " + name + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") name " + name + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/container")
    public Response setNodeContainer(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                     @QueryParam(Container.TOKEN_CT_ID)String containerID,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, containerID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Container container;
                    if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, containerID);
                    else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);
                    if (container != null) {
                        if (mappingSession != null) ((SProxNode)node).setNodeContainer(mappingSession, container);
                        else node.setNodeContainer(container);
                        return Response.status(Status.OK).entity("Node (" + id + ") container successfully updated to " + containerID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") container " + containerID + " : container " + containerID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") container " + containerID + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/parentNode")
    public Response setNodeParentNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                      @QueryParam(NodeSce.PARAM_NODE_PNID)String parentNodeID,
                                      @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node parent node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, parentNodeID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Node parentNode;
                    if (mappingSession != null) parentNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                    else parentNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                    if (parentNode != null) {
                        if (mappingSession != null) ((SProxNode)node).setNodeParentNode(mappingSession, parentNode);
                        else node.setNodeParentNode(parentNode);
                        return Response.status(Status.OK).entity("Node (" + parentNodeID + ") parent node successfully updated to " + parentNodeID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") parent node " + parentNodeID + " : parent node " + parentNodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") parent node " + parentNodeID + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/childNodes/add")
    public Response addNodeChildNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                     @QueryParam(NodeSce.PARAM_NODE_CNID)String childNodeID,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add node child node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childNodeID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Node childNode;
                    if (mappingSession != null) childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, childNodeID);
                    else childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(childNodeID);

                    if (childNode != null) {
                        if (mappingSession != null) ((SProxNode)node).addNodeChildNode(mappingSession, childNode);
                        else node.addNodeChildNode(childNode);
                        return Response.status(Status.OK).entity("Child node (" + childNodeID + ") successfully added to node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding child node " + childNodeID + " to node " + id + " : child node " + childNodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding child node " + childNodeID + " to node " + id + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/childNodes/delete")
    public Response deleteNodeChildNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                        @QueryParam(NodeSce.PARAM_NODE_CNID)String childNodeID,
                                        @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node child node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childNodeID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Node childNode;
                    if (mappingSession != null) childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, childNodeID);
                    else childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(childNodeID);

                    if (childNode != null) {
                        if (mappingSession != null) ((SProxNode)node).removeNodeChildNode(mappingSession, childNode);
                        else node.removeNodeChildNode(childNode);
                        return Response.status(Status.OK).entity("Child node (" + childNodeID + ") successfully deleted from node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting child node " + childNodeID + " from node " + id + " : child node " + childNodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting child node " + childNodeID + " from node " + id + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/twinNodes/add")
    public Response addNodeTwinNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                    @QueryParam(NodeSce.PARAM_NODE_TNID)String twinNodeID,
                                    @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add node twin node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, twinNodeID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Node twinNode;
                    if (mappingSession != null) twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, twinNodeID);
                    else twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(twinNodeID);

                    if (twinNode != null) {
                        if (mappingSession != null) {
                            ((SProxNode)node).addTwinNode(mappingSession, twinNode);
                            ((SProxNode)twinNode).addTwinNode(mappingSession, node);
                        } else {
                            node.addTwinNode(twinNode);
                            twinNode.addTwinNode(node);
                        }
                        return Response.status(Status.OK).entity("Twin node (" + twinNodeID + ") successfully added to node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding twin node " + twinNodeID + " to node " + id + " : twin node " + twinNodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding twin node " + twinNodeID + " to node " + id + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/twinNodes/delete")
    public Response deleteNodeTwinNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                       @QueryParam(NodeSce.PARAM_NODE_TNID)String twinNodeID,
                                       @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node twin node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, twinNodeID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Node twinNode;
                    if (mappingSession != null) twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, twinNodeID);
                    else twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(twinNodeID);

                    if (twinNode != null) {
                        if (mappingSession != null) {
                            ((SProxNode)node).removeTwinNode(mappingSession, twinNode);
                            ((SProxNode)twinNode).removeTwinNode(mappingSession, node);
                        } else {
                            node.removeTwinNode(twinNode);
                            twinNode.removeTwinNode(node);
                        }
                        return Response.status(Status.OK).entity("Twin node (" + twinNodeID + ") successfully deleted from node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting twin node " + twinNodeID + " from node " + id + " : twin node " + twinNodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting twin node " + twinNodeID + " from node " + id + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/endpoints/add")
    public Response addNodeEndpoint(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                    @QueryParam(Endpoint.TOKEN_EP_ID)String endpointID,
                                    @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add node endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, endpointID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Endpoint endpoint ;
                    if (mappingSession != null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, endpointID);
                    else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
                    if (endpoint != null) {
                        if (mappingSession!=null) ((SProxNode)node).addEndpoint(mappingSession, endpoint);
                        else node.addEndpoint(endpoint);
                        return Response.status(Status.OK).entity("Endpoint (" + endpointID + ") successfully added to node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding endpoint " + endpointID + " to node " + id + " : node " + id + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding endpoint " + endpointID + " to node " + id + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/endpoints/delete")
    public Response deleteNodeEndpoint(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                       @QueryParam(Endpoint.TOKEN_EP_ID)String endpointID,
                                       @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, endpointID});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    Endpoint endpoint ;
                    if (mappingSession!=null) endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, endpointID);
                    else endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
                    if (endpoint != null) {
                        if (mappingSession!=null) ((SProxNode)node).removeEndpoint(mappingSession, endpoint);
                        else node.removeEndpoint(endpoint);
                        return Response.status(Status.OK).entity("Endpoint (" + endpointID + ") successfully deleted from node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting endpoint " + endpointID + " from node " + id + " : node " + id + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting endpoint " + endpointID + " from node : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/properties/add")
    public Response addNodeProperty(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                    @QueryParam(MappingSce.GLOBAL_PARAM_PROP_NAME) String name,
                                    @QueryParam(MappingSce.GLOBAL_PARAM_PROP_VALUE) String value,
                                    @DefaultValue("String") @QueryParam(MappingSce.GLOBAL_PARAM_PROP_TYPE) String type,
                                    @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name, value, type});
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

                    Node node;
                    if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                    else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                    if (node != null) {
                        Object oValue;
                        try {
                            oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            String result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                        if (mappingSession!=null) ((SProxNode)node).addNodeProperty(mappingSession, name, oValue);
                        else node.addNodeProperty(name, oValue);
                        return Response.status(Status.OK).entity("Property (" + name + "," + value + ") successfully added to node " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding property " + name + " to node " + id + " : node " + id + " not found.").build();
                } else {
                    log.warn("Property is not defined correctly : {name: " + name + ", type: " + type + ", value: " + value + "}.");
                    return Response.status(Status.BAD_REQUEST).entity("Property is not defined correctly : {name: " + name + ", type: " + type + ", value: " + value + "}.").build();
                }
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteNodeProperty(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                       @QueryParam(MappingSce.GLOBAL_PARAM_PROP_NAME) String name,
                                       @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
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

                Node node;
                if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, id);
                else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);

                if (node != null) {
                    if (mappingSession!=null) ((SProxNode)node).removeNodeProperty(mappingSession, name);
                    else node.removeNodeProperty(name);
                    return Response.status(Status.OK).entity("Property (" + name + ") successfully deleted from node " + id + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding property " + name + " from node " + id + " : node " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}