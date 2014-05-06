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
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.ClusterJSON;
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

@Path("/mapping/domain/clusters")
public class ClusterEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ContainerEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayCluster(@PathParam("param") long id) {
        log.debug("[{}] get cluster : {}", new Object[]{Thread.currentThread().getId(), id});
        Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);
        if (cluster != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ClusterJSON.oneCluster2JSON(cluster, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("Cluster with id " + id + " not found.").build();
        }
    }

    @GET
    public Response displayAllClusters() {
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        log.debug("[{}] get clusters : {}", new Object[]{Thread.currentThread().getId()});
        try {
            ClusterJSON.manyClusters2JSON((HashSet<Cluster>) MappingBootstrap.getMappingSce().getClusterSce().getClusters(null), outStream);
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
    public Response getContainer(@QueryParam("ID") long id) {
        return displayCluster(id);
    }

    @GET
    @Path("/create")
    public Response createCluster(@QueryParam("name") String name) {
        log.debug("[{}] create cluster : {}", new Object[]{Thread.currentThread().getId(), name});
        Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().createCluster(name);
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ClusterJSON.oneCluster2JSON(cluster, outStream);
            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            log.debug(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteCluster(@QueryParam("name") String name) {
        log.debug("[{}] delete cluster : {}", new Object[]{Thread.currentThread().getId(), name});
        try {
            MappingBootstrap.getMappingSce().getClusterSce().deleteCluster(name);
            return Response.status(200).entity("Cluster (" + name + ") has been successfully deleted !").build();
        } catch (MappingDSException e) {
            return Response.status(500).entity("Error while deleting cluster with name " + name).build();
        }
    }

    @GET
    @Path("/update/name")
    public Response setClusterName(@QueryParam("ID")long id, @QueryParam("name")String name) {
        log.debug("[{}] update cluster name: ({},{})", new Object[]{Thread.currentThread().getId(), id, name});
        Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);
        if (cluster != null) {
            cluster.setClusterName(name);
            return Response.status(200).entity("Cluster ("+id+") name successfully updated to " + name + ".").build();
        } else {
            return Response.status(404).entity("Error while updating cluster ("+id+") name "+name+" : cluster " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/containers/add")
    public Response addClusterContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID) {
        log.debug("[{}] add container to cluster : ({},{})", new Object[]{Thread.currentThread().getId(), id, containerID});
        Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);
        if (cluster != null) {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);
            if (container!=null) {
                cluster.addClusterContainer(container);
                return Response.status(200).entity("Container "+ containerID +" successfully added to cluster "+id+".").build();
            } else {
                return Response.status(404).entity("Error while adding container " + id + " to cluster (" + id + ") : container " + containerID + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while adding container " + id + " to cluster (" + id + ") : cluster " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/containers/delete")
    public Response deleteClusterContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID) {
        log.debug("[{}] delete container from cluster : ({},{})", new Object[]{Thread.currentThread().getId(), id, containerID});
        Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);
        if (cluster != null) {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);
            if (container!=null) {
                return Response.status(200).entity("Container "+ containerID +" successfully deleted from cluster "+id+".").build();
            } else {
                return Response.status(404).entity("Error while deleting container " + id + " from cluster (" + id + ") : container " + containerID + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while adding container "+id+" to cluster ("+id+") : cluster " + id + " not found.").build();
        }
    }
}
