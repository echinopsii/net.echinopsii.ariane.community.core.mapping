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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

@Path("/mapping/domain/containers")
public class ContainerEp {
    private static final Logger log = LoggerFactory.getLogger(ContainerEp.class);

    private Response _displayContainer(String id, String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get container : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
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

                Container cont;
                if (mappingSession != null) cont = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else cont = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (cont != null) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    try {
                        ContainerJSON.oneContainer2JSON(cont, outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        log.debug("[{}-{}]Response returned: success", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.OK).entity(result).build();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                } else return Response.status(Status.NOT_FOUND).entity("Container with id " + id + " not found").build();
            } catch (MappingDSException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayContainer(@PathParam("param") String id) {
        return _displayContainer(id, null);
    }

    @GET
    public Response displayAllContainers(@QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get containers", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
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
                HashSet<Container> containers ;
                if (mappingSession!=null) containers = (HashSet<Container>) MappingBootstrap.getMappingSce().getContainerSce().getContainers(mappingSession, null);
                else containers = (HashSet<Container>) MappingBootstrap.getMappingSce().getContainerSce().getContainers(null);
                ContainerJSON.manyContainers2JSON(containers, outStream);
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
    public Response getContainer(@QueryParam(ContainerSce.PARAM_CONTAINER_PAG_URL) String primaryAdminURL,
                                 @QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                 @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        if (id!=null) {
            return _displayContainer(id, sessionId);
        } else if (primaryAdminURL != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get container: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), primaryAdminURL});
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

                    Container cont;
                    if (mappingSession != null) cont = MappingBootstrap.getMappingSce().getContainerSce().getContainerByPrimaryAdminURL(mappingSession, primaryAdminURL);
                    else cont = MappingBootstrap.getMappingSce().getContainerSce().getContainerByPrimaryAdminURL(primaryAdminURL);

                    if (cont != null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        try {
                            ContainerJSON.oneContainer2JSON(cont, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            log.debug("[{}-{}]Response returned: success", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                            return Response.status(Status.OK).entity(result).build();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            String result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                    } else return Response.status(Status.NOT_FOUND).entity("Container with primary admin url " + primaryAdminURL + " not found").build();
                } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
            } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        } else return Response.status(Status.INTERNAL_SERVER_ERROR).entity("MappingDSLRegistryRequest error: primaryAdminURL and id are not defined. You must define one of these parameters").build();
    }

    @GET
    @Path("/create")
    public Response createContainer(@QueryParam(ContainerSce.PARAM_CONTAINER_NAME) String name,
                                    @QueryParam(ContainerSce.PARAM_CONTAINER_PAG_URL) String primaryAdminURL,
                                    @QueryParam(ContainerSce.PARAM_CONTAINER_PAG_NAME) String primaryAdminGateName,
                                    @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        try {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), primaryAdminURL, primaryAdminGateName});
            if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                Session mappingSession = null;
                if (sessionId!=null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null) {
                        log.debug("[{}-{}]Response error: no session found", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }
                }

                Container cont;
                if (name != null)
                    if (mappingSession!=null) cont = MappingBootstrap.getMappingSce().getContainerSce().createContainer(mappingSession, name, primaryAdminURL, primaryAdminGateName);
                    else cont = MappingBootstrap.getMappingSce().getContainerSce().createContainer(name, primaryAdminURL, primaryAdminGateName);
                else
                    if (mappingSession!=null) cont = MappingBootstrap.getMappingSce().getContainerSce().createContainer(mappingSession, primaryAdminURL, primaryAdminGateName);
                    else cont = MappingBootstrap.getMappingSce().getContainerSce().createContainer(primaryAdminURL, primaryAdminGateName);

                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    ContainerJSON.oneContainer2JSON(cont, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    log.debug("[{}-{}]Response returned: success", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }

    @POST
    public Response postContainer(@QueryParam(MappingSce.GLOBAL_PARAM_PAYLOAD) String payload,
                                  @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create container", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
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
                    DeserializedPushResponse deserializationResponse = SProxContainerSceAbs.pushDeserializedContainer(
                            ContainerJSON.JSON2Container(payload),
                            mappingSession,
                            MappingBootstrap.getMappingSce()
                    );
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        ContainerJSON.oneContainer2JSON((Container) deserializationResponse.getDeserializedObject(), outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        ret = Response.status(Status.OK).entity(result).build();
                    } else {
                        String result = "ERROR while deserializing !";
                        ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                    log.debug("[{}-{}]Response returned: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(),ret.getStatus()});
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
    public Response deleteContainer(@QueryParam(ContainerSce.PARAM_CONTAINER_PAG_URL) String primaryAdminURL,
                                    @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        try {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete container : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), primaryAdminURL});
            if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                Session mappingSession = null;
                if (sessionId!=null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                if (mappingSession != null) MappingBootstrap.getMappingSce().getContainerSce().deleteContainer(mappingSession, primaryAdminURL);
                else MappingBootstrap.getMappingSce().getContainerSce().deleteContainer(primaryAdminURL);

                return Response.status(Status.OK).entity("Container (" + primaryAdminURL + ") has been successfully deleted !").build();
            } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error while deleting container with primary admin URL " + primaryAdminURL).build(); }
    }

    @GET
    @Path("/update/name")
    public Response setContainerName(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                     @QueryParam(ContainerSce.PARAM_CONTAINER_NAME) String name,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container name : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    if (mappingSession != null) ((SProxContainer)container).setContainerName(mappingSession, name);
                    else container.setContainerName(name);
                    return Response.status(Status.OK).entity("Container (" + id + ") company successfully updated to " + name + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") company " + name + " : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/company")
    public Response setContainerCompany(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                        @QueryParam(ContainerSce.PARAM_CONTAINER_COMPANY) String company,
                                        @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container company : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, company});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    if (mappingSession != null) ((SProxContainer)container).setContainerCompany(mappingSession, company);
                    else container.setContainerCompany(company);
                    return Response.status(Status.OK).entity("Container (" + id + ") company successfully updated to " + company + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") company " + company + " : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/product")
    public Response setContainerProduct(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                        @QueryParam(ContainerSce.PARAM_CONTAINER_PRODUCT) String product,
                                        @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container product : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, product});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    if (mappingSession != null) ((SProxContainer)container).setContainerProduct(mappingSession, product);
                    else container.setContainerProduct(product);
                    return Response.status(Status.OK).entity("Container (" + id + ") product successfully updated to " + product + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") product " + product + " : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/type")
    public Response setContainerType(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                     @QueryParam(ContainerSce.PARAM_CONTAINER_TYPE) String type,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container type : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, type});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    if (mappingSession != null) ((SProxContainer)container).setContainerType(mappingSession, type);
                    else container.setContainerType(type);
                    return Response.status(Status.OK).entity("Container (" + id + ") type successfully updated to " + type + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") type " + type + " : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/primaryAdminGate")
    public Response setContainerPrimaryAdminGate(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                                 @QueryParam(ContainerSce.PARAM_CONTAINER_PAG_ID) String paGateID,
                                                 @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container primary admin gate : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, paGateID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Gate gate;
                    if (mappingSession != null) gate = MappingBootstrap.getMappingSce().getGateSce().getGate(mappingSession, paGateID);
                    else gate = MappingBootstrap.getMappingSce().getGateSce().getGate(paGateID);

                    if (gate != null) {
                        if (mappingSession != null) ((SProxContainer)container).setContainerPrimaryAdminGate(mappingSession, gate);
                        else container.setContainerPrimaryAdminGate(gate);
                        return Response.status(Status.OK).entity("Container (" + id + ") primary admin gate successfully updated to " + gate.getNodeName() + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") primary admin gate : gate " + paGateID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") primary admin gate " + paGateID + " : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/cluster")
    public Response setContainerCluster(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                        @QueryParam(Cluster.TOKEN_CL_ID) String clusterID,
                                        @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container cluster : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, clusterID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Cluster cluster;
                    if (mappingSession != null) cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(mappingSession, clusterID);
                    else cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(clusterID);

                    if (cluster != null) {
                        if (mappingSession != null) ((SProxContainer)container).setContainerCluster(mappingSession, cluster);
                        else container.setContainerCluster(cluster);
                        return Response.status(Status.OK).entity("Container (" + id + ") cluster successfully updated to " + clusterID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating container " + id + " cluster : cluster " + clusterID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") cluster " + clusterID + ": container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/parentContainer")
    public Response setContainerParentContainer(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                                @QueryParam(ContainerSce.PARAM_CONTAINER_PCO_ID) String parentContainerID,
                                                @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container parent container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, parentContainerID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Container parentContainer;
                    if (mappingSession != null)  parentContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, parentContainerID);
                    else parentContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(parentContainerID);
                    if (parentContainer != null) {
                        if (mappingSession != null) ((SProxContainer)parentContainer).setContainerParentContainer(mappingSession, parentContainer);
                        else parentContainer.setContainerParentContainer(parentContainer);
                        return Response.status(Status.OK).entity("Container (" + id + ") parent container successfully updated to " + parentContainerID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") parent container " + parentContainerID + ": parent container " + parentContainerID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") parent container " + parentContainerID + ": container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/childContainers/add")
    public Response addContainerChildContainer(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                               @QueryParam(ContainerSce.PARAM_CONTAINER_CCO_ID) String childContainerID,
                                               @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by adding child container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childContainerID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Container childContainer;
                    if (mappingSession != null) childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, childContainerID);
                    else childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(childContainerID);

                    if (childContainer != null) {
                        if (mappingSession != null) ((SProxContainer)container).addContainerChildContainer(mappingSession, childContainer);
                        else container.addContainerChildContainer(childContainer);
                        return Response.status(Status.OK).entity("Child container " + childContainerID + " successfully added to container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding child container into container " + id + " : child container " + childContainerID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding child container (" + childContainerID + ") to container " + id + ": container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/childContainers/delete")
    public Response deleteContainerChildContainer(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                                  @QueryParam(ContainerSce.PARAM_CONTAINER_CCO_ID) String childContainerID,
                                                  @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by removing child container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childContainerID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Container childContainer;
                    if (mappingSession != null) childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, childContainerID);
                    else childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(childContainerID);
                    if (childContainer != null) {
                        if (mappingSession!=null) ((SProxContainer)container).removeContainerChildContainer(mappingSession, childContainer);
                        else container.removeContainerChildContainer(childContainer);
                        return Response.status(Status.OK).entity("Child container " + childContainerID + " successfully deleted from container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting child container from container " + id + " : child container " + childContainerID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting child container (" + childContainerID + ") from container " + id + ": container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/nodes/add")
    public Response addContainerNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                     @QueryParam(Node.TOKEN_ND_ID) String nodeID,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by adding node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, nodeID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Node node;
                    if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, nodeID);
                    else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(nodeID);

                    if (node != null) {
                        if (mappingSession != null) ((SProxContainer)container).addContainerNode(mappingSession, node);
                        else container.addContainerNode(node);
                        return Response.status(Status.OK).entity("Node " + nodeID + " successfully added to container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding node into container " + id + " : node " + nodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding node " + nodeID + " into container (" + id + ") : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/nodes/delete")
    public Response deleteContainerNode(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                        @QueryParam(Node.TOKEN_ND_ID) String nodeID,
                                        @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by removing node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, nodeID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Node node;
                    if (mappingSession != null) node = MappingBootstrap.getMappingSce().getNodeSce().getNode(mappingSession, nodeID);
                    else node = MappingBootstrap.getMappingSce().getNodeSce().getNode(nodeID);

                    if (node != null) {
                        if (mappingSession != null) ((SProxContainer)container).removeContainerNode(mappingSession, node);
                        else container.removeContainerNode(node);
                        return Response.status(Status.OK).entity("Node " + nodeID + " successfully deleted from container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting node from container " + id + " : node " + nodeID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deletinging node " + nodeID + " into container : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/gates/add")
    public Response addContainerGate(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                     @QueryParam(Node.TOKEN_ND_ID) String gateID,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by adding gate : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, gateID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Gate gate;
                    if (mappingSession != null) gate = MappingBootstrap.getMappingSce().getGateSce().getGate(mappingSession, gateID);
                    else gate = MappingBootstrap.getMappingSce().getGateSce().getGate(gateID);

                    if (gate != null) {
                        if (mappingSession != null) ((SProxContainer)container).addContainerGate(mappingSession, gate);
                        else container.addContainerGate(gate);
                        return Response.status(Status.OK).entity("Gate " + gateID + " successfully added to container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding gate into container " + id + " : gate " + gateID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while adding gate " + gateID + " into container (" + id + ") : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/gates/delete")
    public Response deleteContainerGate(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                        @QueryParam(Node.TOKEN_ND_ID) String gateID,
                                        @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update container by removing gate : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, gateID});
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

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    Gate gate;
                    if (mappingSession != null) gate = MappingBootstrap.getMappingSce().getGateSce().getGate(mappingSession, gateID);
                    else gate = MappingBootstrap.getMappingSce().getGateSce().getGate(gateID);

                    if (gate != null) {
                        if (mappingSession != null) ((SProxContainer)container).removeContainerGate(mappingSession, gate);
                        else container.removeContainerGate(gate);
                        return Response.status(Status.OK).entity("Gate " + gateID + " successfully deleted from container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while deleting gate from container " + id + " : gate " + gateID + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting gate " + gateID + " from container (" + id + ") : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/properties/add")
    public Response addContainerProperty(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                         @QueryParam(MappingSce.GLOBAL_PARAM_PROP_NAME) String name,
                                         @QueryParam(MappingSce.GLOBAL_PARAM_PROP_VALUE) String value,
                                         @DefaultValue("string") @QueryParam(MappingSce.GLOBAL_PARAM_PROP_TYPE) String type,
                                         @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update container by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name, value, type});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            if (name != null && type != null && value != null) {
                try {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    Container container;
                    if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                    else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                    if (container != null) {
                        Object oValue;
                        try {
                            oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                            String result = e.getMessage();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                        }
                        if (mappingSession != null) ((SProxContainer)container).addContainerProperty(mappingSession, name, oValue);
                        else container.addContainerProperty(name, oValue);
                        return Response.status(Status.OK).entity("Property (" + name + "," + value + ") successfully added to container " + id + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while adding property (" + name + "," + value + ") into container : container " + id + " not found.").build();
                } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
            } else {
                log.warn("Property is not defined correctly : {name: " + name + ", type: " + type + ", value: " + value + "}.");
                return Response.status(Status.BAD_REQUEST).entity("Property is not defined correctly : {name: " + name + ", type: " + type + ", value: " + value + "}.").build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteContainerProperty(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID) String id,
                                            @QueryParam(MappingSce.GLOBAL_PARAM_PROP_NAME) String name,
                                            @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                Container container;
                if (mappingSession != null) container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(mappingSession, id);
                else container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);

                if (container != null) {
                    if (mappingSession != null) ((SProxContainer)container).removeContainerProperty(mappingSession, name);
                    else container.removeContainerProperty(name);
                    return Response.status(Status.OK).entity("Property (" + name + ") successfully deleted from container " + id + ".").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while deleting property (" + name + ") from container : container " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}