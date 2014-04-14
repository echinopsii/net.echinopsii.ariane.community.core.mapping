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
import com.spectral.cc.core.mapping.ds.domain.Cluster;
import com.spectral.cc.core.mapping.ds.domain.Container;
import com.spectral.cc.core.mapping.ds.domain.Gate;
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.ContainerJSON;
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

@Path("/domain/container")
public class ContainerEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ContainerEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayContainer(@PathParam("param") long id) {
        Container cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (cont != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ContainerJSON.oneContainer2JSON(cont, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No container with id " + id + " in the repository").build();
        }
    }

    @GET
    public Response displayAllContainers() {
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ContainerJSON.manyContainers2JSON((HashSet<Container>) MappingBootstrap.getMappingSce().getContainerSce().getContainers(null), outStream);
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
    @Path("/get")
    public Response getContainer(@QueryParam("containerPrimaryAdminURL") String primaryAdminURL) {
        Container cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(primaryAdminURL);
        if (cont != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ContainerJSON.oneContainer2JSON(cont, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No container with primary admin url " + primaryAdminURL + " in the repository").build();
        }
    }

    @GET
    @Path("/get")
    public Response getContainer(@QueryParam("ID") long id) {
        return displayContainer(id);
    }

    @GET
    @Path("/create")
    public Response createContainer(@QueryParam("primaryAdminURL") String primaryAdminURL, @QueryParam("primaryAdminGateName") String primaryAdminGateName) {
        try {
            Container cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(primaryAdminURL, primaryAdminGateName);
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ContainerJSON.oneContainer2JSON(cont, outStream);
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
    public Response deleteContainer(@QueryParam("primaryAdminURL") String primaryAdminURL) {
        try {
            MappingBootstrap.getMappingSce().getContainerSce().deleteContainer(primaryAdminURL);
            return Response.status(200).entity("Container (" + primaryAdminURL + ") has been successfully deleted !").build();
        } catch (MappingDSException e) {
            return Response.status(500).entity("Error while deleting container with primary admin URL " + primaryAdminURL).build();
        }
    }

    @GET
    @Path("/update/company")
    public Response setContainerCompany(@QueryParam("ID") long id, @QueryParam("company") String company) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            container.setContainerCompany(company);
            return Response.status(200).entity("Container ("+id+") company successfully updated to " + company + ".").build();
        } else {
            return Response.status(500).entity("Error while updating container ("+id+") company "+company+" : container " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/product")
    public Response setContainerProduct(@QueryParam("ID") long id, @QueryParam("product") String product) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            container.setContainerProduct(product);
            return Response.status(200).entity("Container ("+id+")product successfully updated to "+product+".").build();
        } else {
            return Response.status(500).entity("Error while updating container ("+id+") product "+product+" : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/type")
    public Response setContainerType(@QueryParam("ID") long id, @QueryParam("type") String type) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            container.setContainerType(type);
            return Response.status(200).entity("Container ("+id+") type successfully updated to "+type+".").build();
        } else {
            return Response.status(500).entity("Error while updating container ("+id+") type "+type+" : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/primaryAdminGate")
    public Response setContainerPrimaryAdminGate(@QueryParam("ID") long id, @QueryParam("paGateID") long paGateID) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(paGateID);
            if (gate!=null) {
                container.setContainerPrimaryAdminGate(gate);
                return Response.status(200).entity("Container ("+id+") primary admin gate successfully updated to "+gate.getNodeName()+".").build();
            } else {
                return Response.status(500).entity("Error while updating container ("+id+") primary admin gate : gate "+paGateID+" not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while updating container ("+id+") primary admin gate "+paGateID+" : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/cluster")
    public Response setContainerCluster(@QueryParam("ID") long id, @QueryParam("clusterID") long clusterID) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(clusterID);
            if (cluster!=null) {
                container.setContainerCluster(cluster);
                return Response.status(200).entity("Container ("+id+") cluster successfully updated to "+clusterID+".").build();
            } else {
                return Response.status(500).entity("Error while updating container "+id+" cluster : cluster "+clusterID+" not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while updating container ("+id+") cluster "+clusterID+": container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/nodes/add")
    public Response addContainerNode(@QueryParam("ID") long id, @QueryParam("nodeID") long nodeID) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(nodeID);
            if (node!=null) {
                container.addContainerNode(node);
                return Response.status(200).entity("Node "+ nodeID +" successfully added to container "+id+".").build();
            } else {
                return Response.status(500).entity("Error while adding node into container " +id+ " : node "+nodeID+" not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while adding node "+nodeID+" into container ("+id+") : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/nodes/delete")
    public Response deleteContainerNode(@QueryParam("ID") long id, @QueryParam("nodeID") long nodeID) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(nodeID);
            if (node!=null) {
                container.removeContainerNode(node);
                return Response.status(200).entity("Node "+ nodeID +" successfully deleted from container "+id+".").build();
            } else {
                return Response.status(500).entity("Error while deleting node from container " +id+ " : node "+nodeID+" not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while deletinging node "+nodeID+" into container : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/gates/add")
    public Response addContainerGate(@QueryParam("ID") long id, @QueryParam("gateID") long gateID) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(gateID);
            if (gate!=null) {
                container.addContainerGate(gate);
                return Response.status(200).entity("Gate "+ gateID +" successfully added to container "+id+".").build();
            } else {
                return Response.status(500).entity("Error while adding gate into container " +id+ " : gate "+gateID+" not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while adding gate "+gateID+" into container ("+id+") : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/gates/delete")
    public Response deleteContainerGate(@QueryParam("ID") long id, @QueryParam("nodeID") long gateID) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(gateID);
            if (gate!=null) {
                container.removeContainerGate(gate);
                return Response.status(200).entity("Gate "+ gateID +" successfully deleted from container "+id+".").build();
            } else {
                return Response.status(500).entity("Error while deleting gate from container " +id+ " : gate "+gateID+" not found.").build();
            }
        } else {
            return Response.status(500).entity("Error while deleting gate "+gateID+" from container ("+id+") : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addContainerProperty(@QueryParam("ID") long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            container.addContainerProperty(name, value);
            return Response.status(200).entity("Property ("+name+","+value+") successfully added to container "+id+".").build();
        } else {
            return Response.status(500).entity("Error while adding property ("+name+","+value+") into container : container "+id+" not found.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteContainerProperty(@QueryParam("ID") long id, @QueryParam("propertyName") String name) {
        Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
        if (container!=null) {
            container.removeContainerProperty(name);
            return Response.status(200).entity("Property ("+name+") successfully deleted from container "+id+".").build();
        } else {
            return Response.status(500).entity("Error while deleting property ("+name+") from container : container "+id+" not found.").build();
        }
    }
}