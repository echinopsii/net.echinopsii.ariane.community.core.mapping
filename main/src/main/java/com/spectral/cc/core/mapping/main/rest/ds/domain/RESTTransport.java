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

import com.spectral.cc.core.mapping.ds.domain.Transport;
import com.spectral.cc.core.mapping.ds.service.TopoSce;
import com.spectral.cc.core.mapping.main.ds.domain.TransportJSON;
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

@Path("/domain/transport")
public class RESTTransport {
    private static final Logger log = LoggerFactory.getLogger(RESTGate.class);

    @GET
    @Path("/{param}")
    public Response printLinkJSON(@PathParam("param") long id) {
        TopoSce topo = TopoWSRuntime.getTopoSce();
        Transport transport = (Transport) topo.getTransportSce().getTransport(id);
        if (transport != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                TransportJSON.oneTransport2JSON(transport, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("NOT FOUND ! No transport with id " + id + " in the repository").build();
        }
    }

    @GET
    public Response printAllTransportJSON() {
        TopoSce topo = TopoWSRuntime.getTopoSce();
        String result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            TransportJSON.manyTransports2JSON((HashSet<Transport>) topo.getTransportSce().getTransports(null), outStream);
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