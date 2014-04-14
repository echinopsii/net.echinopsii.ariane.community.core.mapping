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
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.NodeJSON;
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

@Path("/domain/node")
public class NodeEndpoint {
    private static final Logger log = LoggerFactory.getLogger(NodeEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayNode(@PathParam("param") long id) {
        Node node = (Node) MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                NodeJSON.oneNode2JSON(node, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No node with id " + id + " in the repository").build();
        }
    }

    @GET
    public Response displayAllNodes() {
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            NodeJSON.manyNodes2JSON((HashSet<Node>) MappingBootstrap.getMappingSce().getNodeSce().getNodes(null), outStream);
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
    public Response createNode(@QueryParam("name")String nodeName, @QueryParam("containerID")long containerID, @QueryParam("parentNodeID")long parentNodeID) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Node node = MappingBootstrap.getMappingSce().getNodeSce().createNode(nodeName, containerID, parentNodeID);
            try {
                NodeJSON.oneNode2JSON(node, outStream);
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
    public Response deleteNode(@QueryParam("ID")long nodeID) {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        try {
            mapping.getNodeSce().deleteNode(nodeID);
            return Response.status(200).entity("Node (" + nodeID + ") successfully deleted.").build();
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/get")
    public Response getNode(@QueryParam("ID")long id) {
        return displayNode(id);
    }

    @GET
    @Path("/update/name")
    public Response setNodeName(@QueryParam("ID")long id, @QueryParam("name")String name) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            node.setNodeName(name);
            return Response.status(200).entity("Node ("+id+") name successfully updated to " + name + ".").build();
        } else {
            return Response.status(500).entity("Error while updating node (" + id + ") name " + name + " : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/container")
    public Response setNodeContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);
            if (container != null) {
                node.setNodeContainer(container);
                return Response.status(200).entity("Node ("+id+") container successfully updated to " + containerID + ".").build();
            } else {
                return Response.status(500).entity("Error while updating node (" + id + ") container " + containerID + " : container " + containerID + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while updating node (" + id + ") container " + containerID + " : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/parentNode")
    public Response setNodeParentNode(@QueryParam("ID")long id, @QueryParam("parentNodeID")long parentNodeID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Node parentNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
            if (parentNode!=null) {
                node.setNodeParentNode(parentNode);
                return Response.status(200).entity("Node ("+parentNodeID+") parent node successfully updated to " + parentNodeID + ".").build();
            } else {
                return Response.status(500).entity("Error while updating node (" + id + ") parent node " + parentNodeID + " : parent node " + parentNodeID + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while updating node (" + id + ") parent node " + parentNodeID + " : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/childNodes/add")
    public Response addNodeChildNode(@QueryParam("ID")long id, @QueryParam("childNodeID") long childNodeID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Node childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(childNodeID);
            if (childNode!=null) {
                node.addNodeChildNode(childNode);
                return Response.status(200).entity("Child node ("+childNodeID+") successfully added to node " + id + ".").build();
            } else {
                return Response.status(500).entity("Error while adding child node " + childNodeID + " to node " + id + " : child node " + childNodeID + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while adding child node " + childNodeID + " to node " + id + " : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/childNodes/delete")
    public Response deleteNodeChildNode(@QueryParam("ID")long id, @QueryParam("childNodeID") long childNodeID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Node childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(childNodeID);
            if (childNode!=null) {
                node.removeNodeChildNode(childNode);
                return Response.status(200).entity("Child node ("+childNodeID+") successfully deleted from node " + id + ".").build();
            } else {
                return Response.status(500).entity("Error while deleting child node " + childNodeID + " from node " + id + " : child node " + childNodeID + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while deleting child node " + childNodeID + " from node " + id + " : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/twinNodes/add")
    public Response addNodeTwinNode(@QueryParam("ID")long id, @QueryParam("twinNodeID") long twinNodeID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Node twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(twinNodeID);
            if (twinNode!=null) {
                node.addTwinNode(twinNode);
                return Response.status(200).entity("Twin node ("+twinNodeID+") successfully added to node " + id + ".").build();
            } else {
                return Response.status(500).entity("Error while adding twin node " + twinNodeID + " to node " + id + " : twin node " + twinNodeID + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while adding twin node " + twinNodeID + " to node " + id + " : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/twinNodes/delete")
    public Response deleteNodeTwinNode(@QueryParam("ID")long id, @QueryParam("twinNodeID") long twinNodeID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Node twinNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(twinNodeID);
            if (twinNode!=null) {
                node.removeTwinNode(twinNode);
                return Response.status(200).entity("Twin node ("+twinNodeID+") successfully deleted from node " + id + ".").build();
            } else {
                return Response.status(500).entity("Error while deleting twin node " + twinNodeID + " from node " + id + " : twin node " + twinNodeID + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while deleting twin node "+twinNodeID+" from node "+id+" : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/endpoints/add")
    public Response addNodeEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID") long endpointID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
            if (endpoint != null) {
                node.addEnpoint(endpoint);
                return Response.status(200).entity("Endpoint ("+endpointID+") successfully added to node " + id + ".").build();
            } else {
                return Response.status(500).entity("Error while adding endpoint "+endpointID+" to node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while adding endpoint "+endpointID+" to node "+id+" : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/endpoints/delete")
    public Response deleteNodeEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID") long endpointID) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
            if (endpoint != null) {
                node.removeEndpoint(endpoint);
                return Response.status(200).entity("Endpoint ("+endpointID+") successfully deleted from node " + id + ".").build();
            } else {
                return Response.status(500).entity("Error while deleting endpoint "+endpointID+" from node " + id + " : node " + id + " not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while deleting endpoint "+endpointID+" from node : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addNodeProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            node.addNodeProperty(name,value);
            return Response.status(200).entity("Property ("+name+","+value+") successfully added to node "+id+".").build();
        } else {
            return Response.status(500).entity("Error while adding property "+name+" to node "+id+" : node " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteNodeProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name) {
        Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
        if (node != null) {
            node.removeNodeProperty(name);
            return Response.status(200).entity("Property ("+name+") successfully deleted from node "+id+".").build();
        } else {
            return Response.status(500).entity("Error while adding property "+name+" from node "+id+" : node " + id + " not found.").build();
        }
    }
}