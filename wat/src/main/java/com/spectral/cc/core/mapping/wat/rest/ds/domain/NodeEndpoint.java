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
        MappingSce mapping = MappingBootstrap.getMappingSce();
        Node node = (Node) mapping.getNodeSce().getNode(id);
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
        MappingSce mapping = MappingBootstrap.getMappingSce();
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            NodeJSON.manyNodes2JSON((HashSet<Node>) mapping.getNodeSce().getNodes(null), outStream);
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
        MappingSce mapping = MappingBootstrap.getMappingSce();
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Node node = mapping.getNodeSce().createNode(nodeName, containerID, parentNodeID);
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
        return null;
    }

    @GET
    @Path("/update/container")
    public Response setNodeContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID) {
        return null;
    }

    @GET
    @Path("/update/parentNode")
    public Response setNodeParentNode(@QueryParam("ID")long id, @QueryParam("parentNode")long parentNodeID) {
        return null;
    }

    @GET
    @Path("/update/property")
    public Response setNodeProperty(@QueryParam("ID")long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value) {
        return null;
    }

    @GET
    @Path("/update/childNodes/add")
    public Response addNodeChildNode(@QueryParam("ID")long id, @QueryParam("childNodeID") long childNodeID) {
        return null;
    }

    @GET
    @Path("/update/childNodes/delete")
    public Response deleteNodeChildNode(@QueryParam("ID")long id, @QueryParam("childNodeID") long childNodeID) {
        return null;
    }

    @GET
    @Path("/update/twinNodes/add")
    public Response addNodeTwinNode(@QueryParam("ID")long id, @QueryParam("twinNodeID") long twinNodeID) {
        return null;
    }

    @GET
    @Path("/update/twinNodes/delete")
    public Response deleteNodeTwinNode(@QueryParam("ID")long id, @QueryParam("twinNodeID") long twinNodeID) {
        return null;
    }

    @GET
    @Path("/update/endpoints/add")
    public Response addNodeEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID") long endpointID) {
        return null;
    }

    @GET
    @Path("/update/endpoints/delete")
    public Response deleteNodeEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID") long endpointID) {
        return null;
    }
}