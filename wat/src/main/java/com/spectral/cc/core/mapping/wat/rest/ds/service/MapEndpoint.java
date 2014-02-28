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
package com.spectral.cc.core.mapping.wat.rest.ds.service;

import com.spectral.cc.core.mapping.ds.domain.*;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.service.MapJSON;
import com.spectral.cc.core.mapping.wat.rest.ToolBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/service/map")
public class MapEndpoint {

    private static final Logger log = LoggerFactory.getLogger(MapEndpoint.class);

    @SuppressWarnings("unchecked")
    @GET
    @Path("/all")
    public Response printAllMapJSON() {
        MappingSce mapping = MappingBootstrap.getMappingSce();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            MapJSON.allMap2JSON((HashSet<Container>) mapping.getContainerSce().getContainers(null),
                                       (HashSet<Node>) mapping.getNodeSce().getNodes(null),
                                       (HashSet<Endpoint>) mapping.getEndpointSce().getEndpoints(null),
                                       (HashSet<Link>) mapping.getLinkSce().getLinks(null),
                                       (HashSet<Transport>) mapping.getTransportSce().getTransports(null), outStream);
            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }
}