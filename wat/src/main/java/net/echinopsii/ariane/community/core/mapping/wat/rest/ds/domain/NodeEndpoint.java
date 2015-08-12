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
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Path("/mapping/domain/nodes")
public class NodeEndpoint {
    private static final Logger log = LoggerFactory.getLogger(NodeEndpoint.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(JSONDeserializedNode jsonDeserializedNode) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Container reqNodeContainer = null;
        Node reqNodeParentNode = null;
        List<Node> reqNodeChildNodes = new ArrayList<>();
        List<Node> reqNodeTwinNodes = new ArrayList<>();
        List<Endpoint> reqNodeEndpoints = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedNode.getNodeContainerID()!=0) {
            reqNodeContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(jsonDeserializedNode.getNodeContainerID());
            if (reqNodeContainer == null) ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedNode.getNodeContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeParentNodeID()!=0) {
            reqNodeParentNode = (Node) MappingBootstrap.getMappingSce().getNodeSce().getNode(jsonDeserializedNode.getNodeParentNodeID());
            if (reqNodeParentNode == null) ret.setErrorMessage("Request Error : parent node with provided ID " + jsonDeserializedNode.getNodeParentNodeID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeChildNodesID()!=null && jsonDeserializedNode.getNodeChildNodesID().size() > 0 ) {
            for (long id : jsonDeserializedNode.getNodeChildNodesID()) {
                Node childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
                if (childNode != null)
                    reqNodeChildNodes.add(childNode);
                else {
                    ret.setErrorMessage("Request Error : child node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeTwinNodesID()!=null && jsonDeserializedNode.getNodeTwinNodesID().size() > 0 ) {
            for (long id : jsonDeserializedNode.getNodeTwinNodesID()) {
                Node twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
                if (twinNode != null)
                    reqNodeTwinNodes.add(twinNode);
                else {
                    ret.setErrorMessage("Request Error : twin node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeEndpointsID()!=null && jsonDeserializedNode.getNodeEndpointsID().size() > 0) {
            for (long id : jsonDeserializedNode.getNodeTwinNodesID()) {
                Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                if (endpoint != null)
                    reqNodeEndpoints.add(endpoint);
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
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeID() != 0) {
            deserializedNode = (Node) MappingBootstrap.getMappingSce().getNodeSce().getNode(jsonDeserializedNode.getNodeID());
            if (deserializedNode == null)
                ret.setErrorMessage("Request Error : node with provided ID " + jsonDeserializedNode.getNodeID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedNode == null && reqNodeContainer != null && jsonDeserializedNode.getNodeName() != null)
                deserializedNode = (Node) MappingBootstrap.getMappingSce().getNodeByName(reqNodeContainer, jsonDeserializedNode.getNodeName());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqNodeName = jsonDeserializedNode.getNodeName();
            long reqContainerID = jsonDeserializedNode.getNodeContainerID();
            long reqParentNodeID = jsonDeserializedNode.getNodeParentNodeID();
            if (deserializedNode == null)
                deserializedNode = MappingBootstrap.getMappingSce().getNodeSce().createNode(reqNodeName, reqContainerID, reqParentNodeID);
            else {
                if (reqNodeName != null) deserializedNode.setNodeName(reqNodeName);
                if (reqNodeContainer != null) deserializedNode.setNodeContainer(reqNodeContainer);
                if (reqNodeParentNode != null) deserializedNode.setNodeParentNode(reqNodeParentNode);
            }

            if (jsonDeserializedNode.getNodeChildNodesID()!=null) {
                List<Node> childNodesToDelete = new ArrayList<>();
                for (Node existingChildNode : deserializedNode.getNodeChildNodes())
                    if (!reqNodeChildNodes.contains(existingChildNode))
                        childNodesToDelete.add(existingChildNode);
                for (Node childNodeToDelete : childNodesToDelete)
                    deserializedNode.removeNodeChildNode(childNodeToDelete);

                for (Node childNodeReq : reqNodeChildNodes)
                    deserializedNode.addNodeChildNode(childNodeReq);
            }

            if (jsonDeserializedNode.getNodeTwinNodesID()!=null) {
                List<Node> twinNodesToDelete = new ArrayList<>();
                for (Node existingTwinNode : deserializedNode.getTwinNodes())
                    if (!reqNodeTwinNodes.contains(existingTwinNode))
                        twinNodesToDelete.add(existingTwinNode);
                for (Node twinNodeToDelete : twinNodesToDelete) {
                    deserializedNode.removeTwinNode(twinNodeToDelete);
                    twinNodeToDelete.removeTwinNode(deserializedNode);
                }

                for (Node twinNodeReq : reqNodeTwinNodes) {
                    deserializedNode.addTwinNode(twinNodeReq);
                    twinNodeReq.addTwinNode(deserializedNode);
                }
            }

            if (jsonDeserializedNode.getNodeEndpointsID()!=null) {
                List<Endpoint> endpointsToDelete = new ArrayList<>();
                for (Endpoint existingEndpoint : deserializedNode.getNodeEndpoints())
                    if (!reqNodeEndpoints.contains(existingEndpoint))
                        endpointsToDelete.add(existingEndpoint);
                for (Endpoint endpointToDelete : endpointsToDelete)
                        deserializedNode.removeEndpoint(endpointToDelete);

                for (Endpoint endpointReq : reqNodeEndpoints)
                    deserializedNode.addEnpoint(endpointReq);
            }

            if (jsonDeserializedNode.getNodeProperties()!=null) {
                if (deserializedNode.getNodeProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedNode.getNodeProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        deserializedNode.removeNodeProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    deserializedNode.addNodeProperty(propertyKey, reqProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedNode);
        }

        return ret;
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayNode(@PathParam("param") long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get node : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = (Node) MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
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
            } else {
                return Response.status(Status.NOT_FOUND).entity("Node with id " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllNodes() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get nodes", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result = "";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                NodeJSON.manyNodes2JSON((HashSet<Node>) MappingBootstrap.getMappingSce().getNodeSce().getNodes(null), outStream);
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
    public Response getNode(@QueryParam("endpointURL") String endpointURL, @QueryParam("ID")long id) {
        if (id != 0) {
            return displayNode(id);
        } else if (endpointURL!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get node: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), endpointURL});
            if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                Node node = (Node) MappingBootstrap.getMappingSce().getNodeSce().getNode(endpointURL);
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
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Node with id " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("MappingDSLRegistryRequest error: name and id are not defined. You must define one of these parameters").build();
        }
    }

    @GET
    @Path("/create")
    public Response createNode(@QueryParam("name")String nodeName, @QueryParam("containerID")long containerID, @QueryParam("parentNodeID")long parentNodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create node : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), nodeName, containerID, parentNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                Node node = MappingBootstrap.getMappingSce().getNodeSce().createNode(nodeName, containerID, parentNodeID);
                try {
                    NodeJSON.oneNode2JSON(node, outStream);
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
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @POST
    public Response postNode(@QueryParam("payload") String payload) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create or update node : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Response ret;
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(NodeJSON.JSON2Node(payload));
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
    public Response deleteNode(@QueryParam("ID")long nodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), nodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            try {
                mapping.getNodeSce().deleteNode(nodeID);
                return Response.status(Status.OK).entity("Node (" + nodeID + ") successfully deleted.").build();
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response setNodeName(@QueryParam("ID")long id, @QueryParam("name")String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node name : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                node.setNodeName(name);
                return Response.status(Status.OK).entity("Node (" + id + ") name successfully updated to " + name + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") name " + name + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/container")
    public Response setNodeContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, containerID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);
                if (container != null) {
                    node.setNodeContainer(container);
                    return Response.status(Status.OK).entity("Node (" + id + ") container successfully updated to " + containerID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") container " + containerID + " : container " + containerID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") container " + containerID + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/parentNode")
    public Response setNodeParentNode(@QueryParam("ID")long id, @QueryParam("parentNodeID")long parentNodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node parent node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, parentNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Node parentNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
                if (parentNode != null) {
                    node.setNodeParentNode(parentNode);
                    return Response.status(Status.OK).entity("Node (" + parentNodeID + ") parent node successfully updated to " + parentNodeID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") parent node " + parentNodeID + " : parent node " + parentNodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating node (" + id + ") parent node " + parentNodeID + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/childNodes/add")
    public Response addNodeChildNode(@QueryParam("ID")long id, @QueryParam("childNodeID") long childNodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add node child node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Node childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(childNodeID);
                if (childNode != null) {
                    node.addNodeChildNode(childNode);
                    return Response.status(Status.OK).entity("Child node (" + childNodeID + ") successfully added to node " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while adding child node " + childNodeID + " to node " + id + " : child node " + childNodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding child node " + childNodeID + " to node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/childNodes/delete")
    public Response deleteNodeChildNode(@QueryParam("ID")long id, @QueryParam("childNodeID") long childNodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node child node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Node childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(childNodeID);
                if (childNode != null) {
                    node.removeNodeChildNode(childNode);
                    return Response.status(Status.OK).entity("Child node (" + childNodeID + ") successfully deleted from node " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting child node " + childNodeID + " from node " + id + " : child node " + childNodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting child node " + childNodeID + " from node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/twinNodes/add")
    public Response addNodeTwinNode(@QueryParam("ID")long id, @QueryParam("twinNodeID") long twinNodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add node twin node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, twinNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Node twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(twinNodeID);
                if (twinNode != null) {
                    node.addTwinNode(twinNode);
                    twinNode.addTwinNode(node);
                    return Response.status(Status.OK).entity("Twin node (" + twinNodeID + ") successfully added to node " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while adding twin node " + twinNodeID + " to node " + id + " : twin node " + twinNodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding twin node " + twinNodeID + " to node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/twinNodes/delete")
    public Response deleteNodeTwinNode(@QueryParam("ID")long id, @QueryParam("twinNodeID") long twinNodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node twin node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, twinNodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Node twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(twinNodeID);
                if (twinNode != null) {
                    node.removeTwinNode(twinNode);
                    twinNode.removeTwinNode(node);
                    return Response.status(Status.OK).entity("Twin node (" + twinNodeID + ") successfully deleted from node " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting twin node " + twinNodeID + " from node " + id + " : twin node " + twinNodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting twin node " + twinNodeID + " from node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/endpoints/add")
    public Response addNodeEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID") long endpointID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add node endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, endpointID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
                if (endpoint != null) {
                    node.addEnpoint(endpoint);
                    return Response.status(Status.OK).entity("Endpoint (" + endpointID + ") successfully added to node " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while adding endpoint " + endpointID + " to node " + id + " : node " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding endpoint " + endpointID + " to node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/endpoints/delete")
    public Response deleteNodeEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID") long endpointID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete node endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, endpointID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
                if (endpoint != null) {
                    node.removeEndpoint(endpoint);
                    return Response.status(Status.OK).entity("Endpoint (" + endpointID + ") successfully deleted from node " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting endpoint " + endpointID + " from node " + id + " : node " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting endpoint " + endpointID + " from node : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addNodeProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value,
                                    @DefaultValue("String") @QueryParam("propertyType") String type) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name, value, type});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
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
                node.addNodeProperty(name, oValue);
                return Response.status(Status.OK).entity("Property (" + name + "," + value + ") successfully added to node " + id + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding property " + name + " to node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteNodeProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update node by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (node != null) {
                node.removeNodeProperty(name);
                return Response.status(Status.OK).entity("Property (" + name + ") successfully deleted from node " + id + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding property " + name + " from node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }
}