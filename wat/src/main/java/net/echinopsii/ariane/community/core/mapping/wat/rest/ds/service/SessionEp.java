/**
 * Session Web Service :
 * provide session Web Service and REST Service
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
package net.echinopsii.ariane.community.core.mapping.wat.rest.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.service.SessionJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Path("/mapping/service/session")
public class SessionEp {

    private static final Logger log = LoggerFactory.getLogger(SessionEp.class);

    @GET
    @Path("/open")
    public Response openSession(@QueryParam(MappingSce.SESSION_MGR_OP_CLIENT_ID) String clientID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] openSession : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), clientID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Response ret = null;
            if (clientID!=null && !clientID.equals("")) {
                Session session = MappingBootstrap.getMappingSce().openSession(clientID, true);
                if (session != null) {
                    try {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        SessionJSON.oneSession2JSON(session, outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        ret = Response.status(Response.Status.OK).entity(result).build();
                    } catch (IOException e) {
                        ret = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
                    }
                } else ret = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failure when creating new session. Check server logs.").build();
            } else  ret = Response.status(Response.Status.BAD_REQUEST).entity("ClientID can not be null or empty").build();
            return ret ;
        } else return Response.status(Response.Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/close")
    public Response closeSession(@QueryParam(MappingSce.SESSION_MGR_OP_SESSION_ID) String sessionID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] closeSession : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), sessionID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Response ret = null;
            if (sessionID!=null && !sessionID.equals("")) {
                Session session = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                if (session!=null) {
                    MappingBootstrap.getMappingSce().closeSession(session);
                    ret = Response.status(Response.Status.OK).entity("Session with ID " + sessionID + " has been closed.").build();
                } else ret = Response.status(Response.Status.NOT_FOUND).entity("Session with ID " + sessionID + " not found !").build();
            } else ret = Response.status(Response.Status.BAD_REQUEST).entity("SessionID can not be null or empty").build();
            return ret ;
        } else return Response.status(Response.Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/commit")
    public Response commit(@QueryParam(MappingSce.SESSION_MGR_OP_SESSION_ID)  String sessionID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] commit : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), sessionID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Response ret = null;
            if (sessionID!=null && !sessionID.equals("")) {
                Session session = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                if (session!=null) {
                    try {
                        session.commit();
                        ret = Response.status(Response.Status.OK).entity("Commit on session with ID " + sessionID + " done.").build();
                    } catch (MappingDSException e) {
                        ret = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
                    }
                } else ret = Response.status(Response.Status.NOT_FOUND).entity("Session with ID " + sessionID + " not found !").build();
            } else  ret = Response.status(Response.Status.BAD_REQUEST).entity("SessionID can not be null or empty").build();
            return ret ;
        } else return Response.status(Response.Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }

    @GET
    @Path("/rollback")
    public Response rollback(@QueryParam(MappingSce.SESSION_MGR_OP_SESSION_ID)  String sessionID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] rollback : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), sessionID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Response ret = null;
            if (sessionID!=null && !sessionID.equals("")) {
                Session session = MappingBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                if (session!=null) {
                    try {
                        session.rollback();
                        ret = Response.status(Response.Status.OK).entity("Rollback on session with ID " + sessionID + " done.").build();
                    } catch (MappingDSException e) {
                        ret = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
                    }
                } else ret = Response.status(Response.Status.NOT_FOUND).entity("Session with ID " + sessionID + " not found !").build();
            } else ret = Response.status(Response.Status.BAD_REQUEST).entity("SessionID can not be null or empty").build();
            return ret ;
        } else return Response.status(Response.Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
    }
}