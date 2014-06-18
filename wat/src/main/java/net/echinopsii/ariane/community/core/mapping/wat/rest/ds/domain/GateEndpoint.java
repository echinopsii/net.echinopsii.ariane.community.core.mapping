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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.wat.json.ds.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.wat.rest.ToolBox;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/mapping/domain/gates")
public class GateEndpoint {
    private static final Logger log = LoggerFactory.getLogger(GateEndpoint.class);

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayGate(@PathParam("param") long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get gate : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Gate gate = (Gate) MappingBootstrap.getMappingSce().getGateSce().getGate(id);
            if (gate != null) {
                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    GateJSON.oneGate2JSON(gate, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
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
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllGates() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get gates", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result = "";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                GateJSON.manyGates2JSON((HashSet<Gate>) MappingBootstrap.getMappingSce().getGateSce().getGates(null), outStream);
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
    public Response getGate(@QueryParam("ID")long id) {
        return displayGate(id);
    }

    @GET
    @Path("/create")
    public Response createGate(@QueryParam("URL")String url, @QueryParam("name")String name,
                               @QueryParam("containerID")long containerID, @QueryParam("isPrimaryAdmin")boolean isPrimaryAdmin) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create gate : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), url, name, containerID, isPrimaryAdmin});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                Gate gate = MappingBootstrap.getMappingSce().getGateSce().createGate(url, name, containerID, isPrimaryAdmin);
                try {
                    GateJSON.oneGate2JSON(gate, outStream);
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

    @GET
    @Path("/delete")
    public Response deleteGate(@QueryParam("ID")long nodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete gate : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), nodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                MappingBootstrap.getMappingSce().getGateSce().deleteGate(nodeID);
                return Response.status(Status.OK).entity("Gate (" + nodeID + ") successfully deleted.").build();
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
    @Path("/update/primaryEndpoint")
    public Response setPrimaryEndpoint(@QueryParam("ID")long id, @QueryParam("endpointID")long endpointID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update primary admin endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), id, endpointID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(id);
            if (gate != null) {
                Endpoint endpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(endpointID);
                if (endpoint != null) {
                    gate.setNodePrimaryAdminEnpoint(endpoint);
                    return Response.status(Status.OK).entity("Gate (" + id + ") primary endpoint successfully updated to " + endpointID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating gate (" + id + ") primary endpoint " + endpointID + " : gate " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating gate (" + id + ") primary endpoint " + endpointID + " : gate " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }
}