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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
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
import java.util.HashSet;
import java.util.List;

@Path("/mapping/domain/clusters")
public class ClusterEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ContainerEndpoint.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster,
                                                                            Session mappingSession) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        List<Container> reqContainers = new ArrayList<>();
        if (jsonDeserializedCluster.getClusterContainersID()!=null && jsonDeserializedCluster.getClusterContainersID().size()>0) {
            for (long id : jsonDeserializedCluster.getClusterContainersID()) {
                Container container;

                if (mappingSession!=null)
                    container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) reqContainers.add(container);
                else {
                    ret.setErrorMessage("Request Error : container with provided ID " + id + " was not found.");
                    break;
                }
            }
        }

        // LOOK IF CLUSTER MAYBE UPDATED OR CREATED
        Cluster deserializedCluster = null;
        if (ret.getErrorMessage()!=null && jsonDeserializedCluster.getClusterID()!=0) {
            if (mappingSession!=null)
                deserializedCluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(mappingSession, jsonDeserializedCluster.getClusterID());
            else deserializedCluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(jsonDeserializedCluster.getClusterID());
            if (deserializedCluster == null) ret.setErrorMessage("Request Error : cluster with provided ID " + jsonDeserializedCluster.getClusterID() + " was not found.");
        }

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage()==null) {
            if (deserializedCluster==null)
                if (mappingSession!=null) deserializedCluster = MappingBootstrap.getMappingSce().getClusterSce().createCluster(mappingSession, jsonDeserializedCluster.getClusterName());
                else deserializedCluster = MappingBootstrap.getMappingSce().getClusterSce().createCluster(jsonDeserializedCluster.getClusterName());
            else if (jsonDeserializedCluster.getClusterName()!=null)
                if (mappingSession!=null) deserializedCluster.setClusterName(mappingSession, jsonDeserializedCluster.getClusterName());
                else deserializedCluster.setClusterName(jsonDeserializedCluster.getClusterName());

            if (jsonDeserializedCluster.getClusterContainersID() != null) {
                List<Container> containersToDelete = new ArrayList<>();
                for (Container containerToDel : deserializedCluster.getClusterContainers())
                    if (!reqContainers.contains(containerToDel))
                        containersToDelete.add(containerToDel);
                for (Container containerToDel : containersToDelete)
                    if (mappingSession!=null) deserializedCluster.removeClusterContainer(mappingSession, containerToDel);
                    else deserializedCluster.removeClusterContainer(containerToDel);
                for (Container containerToAdd : reqContainers)
                    if (mappingSession!=null) deserializedCluster.addClusterContainer(mappingSession, containerToAdd);
                    else deserializedCluster.addClusterContainer(containerToAdd);
            }

            ret.setDeserializedObject(deserializedCluster);
        }

        return ret;
    }

    private Response _displayCluster(long id, String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get cluster : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(),id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Session mappingSession = null;
            if (sessionId!=null && !sessionId.equals("")) {
                mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                if (mappingSession == null)
                    return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
            }

            Cluster cluster;
            if (mappingSession != null)
                try {
                    cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(mappingSession, id);
                } catch (MappingDSException e) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
                }
            else cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);

            if (cluster != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                try {
                    ClusterJSON.oneCluster2JSON(cluster, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Cluster with id " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }


    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayCluster(@PathParam("param") long id) {
        return _displayCluster(id, null);
    }

    @GET
    public Response displayAllClusters() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get clusters", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ClusterJSON.manyClusters2JSON((HashSet<Cluster>) MappingBootstrap.getMappingSce().getClusterSce().getClusters(null), outStream);
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
    public Response getCluster(@QueryParam("ID") long id, @QueryParam("sessionID") String sessionId) {
        return _displayCluster(id, sessionId);
    }

    @GET
    @Path("/create")
    public Response createCluster(@QueryParam("name") String name, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create cluster : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
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

                Cluster cluster;
                if (mappingSession != null) cluster = MappingBootstrap.getMappingSce().getClusterSce().createCluster(mappingSession, name);
                else cluster = MappingBootstrap.getMappingSce().getClusterSce().createCluster(name);

                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    ClusterJSON.oneCluster2JSON(cluster, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } catch (MappingDSException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @POST
    public Response postCluster(@QueryParam("payload") String payload, @QueryParam("sessionID") String sessionId) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create container", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Response ret;
                    Session mappingSession = null;
                    if (sessionId!=null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(ClusterJSON.JSON2Cluster(payload), mappingSession);
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        ClusterJSON.oneCluster2JSON((Cluster) deserializationResponse.getDeserializedObject(), outStream);
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
            } else return Response.status(Status.BAD_REQUEST).entity("No payload attached to this POST").build();
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/delete")
    public Response deleteCluster(@QueryParam("name") String name, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete cluster : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
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
                if (mappingSession!=null) MappingBootstrap.getMappingSce().getClusterSce().deleteCluster(mappingSession, name);
                else MappingBootstrap.getMappingSce().getClusterSce().deleteCluster(name);

                return Response.status(Status.OK).entity("Cluster (" + name + ") has been successfully deleted !").build();
            } catch (MappingDSException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error while deleting cluster with name " + name).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/name")
    public Response setClusterName(@QueryParam("ID")long id, @QueryParam("name")String name, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update cluster name: ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
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

                Cluster cluster;
                if (mappingSession!=null) cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(mappingSession, id);
                else cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);

                if (cluster != null) {
                    if (mappingSession!=null) cluster.setClusterName(mappingSession, name);
                    else cluster.setClusterName(name);
                    return Response.status(Status.OK).entity("Cluster (" + id + ") name successfully updated to " + name + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating cluster (" + id + ") name " + name + " : cluster " + id + " not found.").build();
            } catch (MappingDSException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/containers/add")
    public Response addClusterContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] add container to cluster : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, containerID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {

            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                Cluster cluster;
                if (mappingSession != null) cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(mappingSession, id);
                else cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);

                if (cluster != null) {
                    Container container;
                    if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, containerID);
                    else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);

                    if (container != null) {
                        if (mappingSession != null) cluster.addClusterContainer(mappingSession, container);
                        else cluster.addClusterContainer(container);
                        return Response.status(Status.OK).entity("Container " + containerID + " successfully added to cluster " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding container " + id + " to cluster (" + id + ") : container " + containerID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding container " + id + " to cluster (" + id + ") : cluster " + id + " not found.").build();
            } catch (MappingDSException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/containers/delete")
    public Response deleteClusterContainer(@QueryParam("ID")long id, @QueryParam("containerID")long containerID, @QueryParam("sessionID") String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete container from cluster : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, containerID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                Cluster cluster;
                if (mappingSession != null) cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(mappingSession, id);
                else cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(id);

                if (cluster != null) {
                    Container container;
                    if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, containerID);
                    else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(containerID);

                    if (container != null) {
                        if (mappingSession != null) cluster.removeClusterContainer(mappingSession, container);
                        else cluster.removeClusterContainer(container);
                        return Response.status(Status.OK).entity("Container " + containerID + " successfully deleted from cluster " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting container " + id + " from cluster (" + id + ") : container " + containerID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding container " + id + " to cluster (" + id + ") : cluster " + id + " not found.").build();
            } catch (MappingDSException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}