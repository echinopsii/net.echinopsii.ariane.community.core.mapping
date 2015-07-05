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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
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

@Path("/mapping/domain/links")
public class LinkEndpoint {
    private static final Logger log = LoggerFactory.getLogger(GateEndpoint.class);

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayLink(@PathParam("param") long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get link : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Link link = (Link) MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
            if (link != null) {
                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    LinkJSON.oneLink2JSON(link, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Link with id " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllLinks() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get links", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result = "";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                LinkJSON.manyLinks2JSON((HashSet<Link>) MappingBootstrap.getMappingSce().getLinkSce().getLinks(null), outStream);
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
    public Response getLink(@QueryParam("ID")long id) {
        return displayLink(id);
    }

    @GET
    @Path("/create")
    public Response createLink(@QueryParam("SEPID")long sourceEndpointID, @QueryParam("TEPID")long targetEndpointID,
                               @QueryParam("transportID")long transportID, @QueryParam("UPLID") long upLinkID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create link : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), sourceEndpointID, targetEndpointID, transportID, upLinkID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                Link link = MappingBootstrap.getMappingSce().getLinkSce().createLink(sourceEndpointID, targetEndpointID, transportID, upLinkID);
                try {
                    LinkJSON.oneLink2JSON(link, outStream);
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
    public Response deleteLink(@QueryParam("ID")long linkID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete link : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), linkID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            try {
                MappingBootstrap.getMappingSce().getLinkSce().deleteLink(linkID);
                return Response.status(Status.OK).entity("Link (" + linkID + ") successfully deleted.").build();
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
    @Path("/update/transport")
    public Response setLinkTransport(@QueryParam("ID")long id, @QueryParam("transportID") long transportID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update link transport : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, transportID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Link link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
            if (link != null) {
                Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(transportID);
                if (transport != null) {
                    link.setLinkTransport(transport);
                    return Response.status(Status.OK).entity("Link (" + id + ") transport successfully updated to " + transportID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") transport " + transportID + " : transport " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") transport " + transportID + " : link " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/sourceEP")
    public Response setLinkEndpointSource(@QueryParam("ID")long id, @QueryParam("SEPID") long SEPID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update link source endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, SEPID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Link link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
            if (link != null) {
                Endpoint sourceEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(SEPID);
                if (sourceEP != null) {
                    link.setLinkEndpointSource(sourceEP);
                    return Response.status(Status.OK).entity("Link (" + id + ") source endpoint successfully updated to " + SEPID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") source endpoint " + SEPID + " : link " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") source endpoint " + SEPID + " : link " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/targetEP")
    public Response setLinkEndpointTarget(@QueryParam("ID")long id, @QueryParam("TEPID") long TEPID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update link target endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(),  id, TEPID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Link link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
            if (link != null) {
                Endpoint targetEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(TEPID);
                if (targetEP != null) {
                    link.setLinkEndpointTarget(targetEP);
                    return Response.status(Status.OK).entity("Link (" + id + ") target endpoint successfully updated to " + TEPID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") target endpoint " + TEPID + " : link " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") target endpoint " + TEPID + " : link " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }
}