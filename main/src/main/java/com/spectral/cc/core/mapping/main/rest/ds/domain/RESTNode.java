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
package com.spectral.cc.core.mapping.main.rest.ds.domain;

import com.spectral.cc.core.mapping.ds.domain.Node;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.main.ds.domain.NodeJSON;
import com.spectral.cc.core.mapping.main.rest.ToolBox;
import com.spectral.cc.core.mapping.main.runtime.TopoWSRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

//import java.io.File;

@Path("/domain/node")
public class RESTNode {
    private static final Logger log = LoggerFactory.getLogger(RESTNode.class);

    @GET
    @Path("/{param}")
    public Response printNodeJSON(@PathParam("param") long id) {
        MappingSce topo = TopoWSRuntime.getMappingSce();
        Node node = (Node) topo.getNodeSce().getNode(id);
        if (node != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                NodeJSON.oneNode2JSON(node, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No node with id " + id + " in the repository").build();
        }
    }

    @GET
    public Response printAllNodeJSON() {
        MappingSce topo = TopoWSRuntime.getMappingSce();
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            NodeJSON.manyNodes2JSON((HashSet<Node>) topo.getNodeSce().getNodes(null), outStream);
            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }
}
