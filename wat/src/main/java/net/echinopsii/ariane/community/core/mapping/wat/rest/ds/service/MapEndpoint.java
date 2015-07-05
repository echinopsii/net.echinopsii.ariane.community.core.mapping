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
package net.echinopsii.ariane.community.core.mapping.wat.rest.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.Map;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.service.MapJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/mapping/service/map")
public class MapEndpoint {

    private static final Logger log = LoggerFactory.getLogger(MapEndpoint.class);

    @SuppressWarnings("unchecked")
    @GET
    @Path("/all")
    public Response printAllMapJSON() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                MapJSON.allMap2JSON((HashSet<Container>) mapping.getContainerSce().getContainers(null),
                                           (HashSet<Node>) mapping.getNodeSce().getNodes(null),
                                           (HashSet<Endpoint>) mapping.getEndpointSce().getEndpoints(null),
                                           (HashSet<Link>) mapping.getLinkSce().getLinks(null),
                                           (HashSet<Transport>) mapping.getTransportSce().getTransports(null), outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/query")
    public Response printMapFromQueryJSON(@QueryParam("mdsl") String query) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            MappingSce mapping = MappingBootstrap.getMappingSce();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                Map map = mapping.getMapSce().getMap(query);
                MapJSON.allMap2JSON((HashSet<Container>) map.getContainers(),
                                           (HashSet<Node>) map.getNodes(),
                                           (HashSet<Endpoint>) map.getEndpoints(),
                                           (HashSet<Link>) map.getLinks(),
                                           (HashSet<Transport>) map.getTransports(), outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error("Original query is : " + query);
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }
}