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
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import net.echinopsii.ariane.community.core.mapping.ds.service.LinkSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
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
import java.util.HashSet;

@Path("/mapping/domain/links")
public class LinkEp {
    private static final Logger log = LoggerFactory.getLogger(LinkEp.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(LinkJSON.JSONDeserializedLink jsonDeserializedLink,
                                                                            Session mappingSession) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Endpoint reqSourceEndpoint=null;
        Endpoint reqTargetEndpoint=null;
        Transport reqTransport=null;

        if (jsonDeserializedLink.getLinkSEPID()!=null) {
            if (mappingSession!=null) reqSourceEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, jsonDeserializedLink.getLinkSEPID());
            else reqSourceEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(jsonDeserializedLink.getLinkSEPID());
            if (reqSourceEndpoint==null) ret.setErrorMessage("Request Error : source endpoint with provided ID " + jsonDeserializedLink.getLinkSEPID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedLink.getLinkTEPID()!=null) {
            if (mappingSession!=null) reqTargetEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, jsonDeserializedLink.getLinkTEPID());
            else reqTargetEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(jsonDeserializedLink.getLinkTEPID());
            if (reqTargetEndpoint==null) ret.setErrorMessage("Request Error : target endpoint with provided ID " + jsonDeserializedLink.getLinkTEPID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedLink.getLinkTRPID()!=null) {
            if (mappingSession!=null) reqTransport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(mappingSession, jsonDeserializedLink.getLinkTRPID());
            else reqTransport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(jsonDeserializedLink.getLinkTRPID());
            if (reqTransport == null) ret.setErrorMessage("Request Error : transport with provided ID " + jsonDeserializedLink.getLinkTRPID() + " was not found.");
        }

        // LOOK IF LINK MAYBE UPDATED OR CREATED
        Link deserializedLink = null;
        if (ret.getErrorMessage() == null && jsonDeserializedLink.getLinkID()!=null) {
            if (mappingSession!=null) deserializedLink = MappingBootstrap.getMappingSce().getLinkSce().getLink(mappingSession, jsonDeserializedLink.getLinkID());
            else deserializedLink = MappingBootstrap.getMappingSce().getLinkSce().getLink(jsonDeserializedLink.getLinkID());
            if (deserializedLink==null) ret.setErrorMessage("Request Error : link with provided ID " + jsonDeserializedLink.getLinkID() + " was not found.");
        }

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            if (deserializedLink==null) {
                if (mappingSession!=null) deserializedLink = MappingBootstrap.getMappingSce().getLinkSce().createLink(
                        mappingSession,
                        jsonDeserializedLink.getLinkSEPID(),
                        jsonDeserializedLink.getLinkTEPID(),
                        jsonDeserializedLink.getLinkTRPID());
                else deserializedLink = MappingBootstrap.getMappingSce().getLinkSce().createLink(
                        jsonDeserializedLink.getLinkSEPID(),
                        jsonDeserializedLink.getLinkTEPID(),
                        jsonDeserializedLink.getLinkTRPID());
            } else {
                if (reqSourceEndpoint!=null)
                    if (mappingSession!=null) ((SProxLink) deserializedLink).setLinkEndpointSource(mappingSession, reqSourceEndpoint);
                    else deserializedLink.setLinkEndpointSource(reqSourceEndpoint);
                if (mappingSession!=null) ((SProxLink)deserializedLink).setLinkEndpointTarget(mappingSession, reqTargetEndpoint);
                else deserializedLink.setLinkEndpointTarget(reqTargetEndpoint);
                if (reqTransport!=null)
                    if (mappingSession!=null) ((SProxLink)deserializedLink).setLinkTransport(mappingSession, reqTransport);
                    else deserializedLink.setLinkTransport(reqTransport);
            }
            ret.setDeserializedObject(deserializedLink);
        }

        return ret;
    }

    private Response _displayLink(String id, String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get link : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
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

                Link link ;
                if (mappingSession!=null) link = MappingBootstrap.getMappingSce().getLinkSce().getLink(mappingSession, id);
                else link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
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
                } else return Response.status(Status.NOT_FOUND).entity("Link with id " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayLink(@PathParam("param") String id) {
        return _displayLink(id, null);
    }

    @GET
    public Response displayAllLinks(@QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get links", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result = "";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                HashSet<Link> links ;
                if (mappingSession!=null) links = (HashSet<Link>) MappingBootstrap.getMappingSce().getLinkSce().getLinks(mappingSession, null);
                else links = (HashSet<Link>) MappingBootstrap.getMappingSce().getLinkSce().getLinks(null);
                LinkJSON.manyLinks2JSON(links, outStream);
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
    public Response getLink(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                            @QueryParam(LinkSce.PARAM_LINK_SEPID) String sepid,
                            @QueryParam(LinkSce.PARAM_LINK_TEPID) String tepid,
                            @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        if (id!=null)
            return _displayLink(id, sessionId);
        else {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get links ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), sepid, tepid});
            if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                Session mappingSession = null;
                if (sessionId != null && !sessionId.equals("")) {
                    mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                    if (mappingSession == null)
                        return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                }

                String result;
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                try {
                    Endpoint sourceEndpoint = null;
                    Endpoint targetEndpoint = null;
                    if (sepid != null) {
                        if (mappingSession!=null) sourceEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, sepid);
                        else sourceEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(sepid);
                        if (sourceEndpoint == null) {
                            result = "Unable to find source endpoint !";
                            return Response.status(Status.BAD_REQUEST).entity(result).build();
                        }
                    }
                    if (tepid != null) {
                        if (mappingSession!=null) targetEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, tepid);
                        else targetEndpoint = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(tepid);
                        if (targetEndpoint == null) {
                            result = "Unable to find target endpoint !";
                            return Response.status(Status.BAD_REQUEST).entity(result).build();
                        }
                    }

                    if (sourceEndpoint != null && targetEndpoint != null) {
                        Link toPrint;
                        if (mappingSession!=null)  toPrint = MappingBootstrap.getMappingSce().getLinkBySourceEPandDestinationEP(mappingSession, sourceEndpoint, targetEndpoint);
                        else toPrint = MappingBootstrap.getMappingSce().getLinkBySourceEPandDestinationEP(sourceEndpoint, targetEndpoint);
                        LinkJSON.oneLink2JSON(toPrint, outStream);
                        result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        return Response.status(Status.OK).entity(result).build();
                    } else if (sourceEndpoint != null) {
                        HashSet<Link> toPrint;
                        if (mappingSession!=null) toPrint = (HashSet<Link>)MappingBootstrap.getMappingSce().getLinksBySourceEP(mappingSession, sourceEndpoint);
                        else toPrint = (HashSet<Link>)MappingBootstrap.getMappingSce().getLinksBySourceEP(sourceEndpoint);
                        LinkJSON.manyLinks2JSON(toPrint, outStream);
                        result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        return Response.status(Status.OK).entity(result).build();
                    } else {
                        HashSet<Link> toPrint;
                        if (mappingSession!=null) toPrint = (HashSet<Link>) MappingBootstrap.getMappingSce().getLinksByDestinationEP(mappingSession, targetEndpoint);
                        else toPrint = (HashSet<Link>) MappingBootstrap.getMappingSce().getLinksByDestinationEP(targetEndpoint);
                        LinkJSON.manyLinks2JSON(toPrint, outStream);
                        result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        return Response.status(Status.OK).entity(result).build();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createLink(@QueryParam(LinkSce.PARAM_LINK_SEPID)String sourceEndpointID,
                               @QueryParam(LinkSce.PARAM_LINK_TEPID)String targetEndpointID,
                               @QueryParam(Transport.TOKEN_TP_ID)String transportID,
                               @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create link : ({},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), sourceEndpointID, targetEndpointID, transportID});
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

                Link link ;
                if (mappingSession!=null) link = MappingBootstrap.getMappingSce().getLinkSce().createLink(mappingSession, sourceEndpointID, targetEndpointID, transportID);
                else link = MappingBootstrap.getMappingSce().getLinkSce().createLink(sourceEndpointID, targetEndpointID, transportID);

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
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @POST
    public Response postLink(@QueryParam(MappingSce.GLOBAL_PARAM_PAYLOAD) String payload,
                             @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create or update link : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Session mappingSession = null;
                    if (sessionId != null && !sessionId.equals("")) {
                        mappingSession = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionId);
                        if (mappingSession == null)
                            return Response.status(Status.BAD_REQUEST).entity("No session found for ID " + sessionId).build();
                    }

                    Response ret;
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(LinkJSON.JSON2Link(payload), mappingSession);
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        LinkJSON.oneLink2JSON((Link)deserializationResponse.getDeserializedObject(), outStream);
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
    public Response deleteLink(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String linkID,
                               @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] delete link : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), linkID});
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
                if (mappingSession!=null) MappingBootstrap.getMappingSce().getLinkSce().deleteLink(mappingSession, linkID);
                else MappingBootstrap.getMappingSce().getLinkSce().deleteLink(linkID);
                return Response.status(Status.OK).entity("Link (" + linkID + ") successfully deleted.").build();
            } catch (MappingDSException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/transport")
    public Response setLinkTransport(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                     @QueryParam(Transport.TOKEN_TP_ID)String transportID,
                                     @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update link transport : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, transportID});
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
                Link link ;
                if (mappingSession!=null) link = MappingBootstrap.getMappingSce().getLinkSce().getLink(mappingSession, id);
                else link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
                if (link != null) {
                    Transport transport;
                    if (mappingSession!=null) transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(mappingSession, transportID);
                    else transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(transportID);
                    if (transport != null) {
                        if (mappingSession!=null) ((SProxLink)link).setLinkTransport(mappingSession, transport);
                        else link.setLinkTransport(transport);
                        return Response.status(Status.OK).entity("Link (" + id + ") transport successfully updated to " + transportID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") transport " + transportID + " : transport " + id + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") transport " + transportID + " : link " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/sourceEP")
    public Response setLinkEndpointSource(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                          @QueryParam(LinkSce.PARAM_LINK_SEPID)String SEPID,
                                          @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update link source endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, SEPID});
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
                Link link ;
                if (mappingSession!=null) link = MappingBootstrap.getMappingSce().getLinkSce().getLink(mappingSession, id);
                else link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
                if (link != null) {
                    Endpoint sourceEP;
                    if (mappingSession!=null) sourceEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, SEPID);
                    else sourceEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(SEPID);
                    if (sourceEP != null) {
                        link.setLinkEndpointSource(sourceEP);
                        return Response.status(Status.OK).entity("Link (" + id + ") source endpoint successfully updated to " + SEPID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") source endpoint " + SEPID + " : link " + id + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") source endpoint " + SEPID + " : link " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/update/targetEP")
    public Response setLinkEndpointTarget(@QueryParam(MappingSce.GLOBAL_PARAM_OBJ_ID)String id,
                                          @QueryParam(LinkSce.PARAM_LINK_TEPID)String TEPID,
                                          @QueryParam(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID) String sessionId) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update link target endpoint : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(),  id, TEPID});
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
                Link link ;
                if (mappingSession!=null) link = MappingBootstrap.getMappingSce().getLinkSce().getLink(mappingSession, id);
                else link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);

                if (link != null) {
                    Endpoint targetEP;
                    if (mappingSession!=null) targetEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(mappingSession, TEPID);
                    else targetEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(TEPID);
                    if (targetEP != null) {
                        if (mappingSession!=null) ((SProxLink)link).setLinkEndpointTarget(mappingSession, targetEP);
                        else link.setLinkEndpointTarget(targetEP);
                        return Response.status(Status.OK).entity("Link (" + id + ") target endpoint successfully updated to " + TEPID + ".").build();
                    } else return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") target endpoint " + TEPID + " : link " + id + " not found.").build();
                } else return Response.status(Status.NOT_FOUND).entity("Error while updating link (" + id + ") target endpoint " + TEPID + " : link " + id + " not found.").build();
            } catch (MappingDSException e) { return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); }
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}