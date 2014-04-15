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
package com.spectral.cc.core.mapping.wat.rest.ds.domain;

import com.spectral.cc.core.mapping.ds.MappingDSException;
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.domain.Link;
import com.spectral.cc.core.mapping.ds.domain.Transport;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.ds.domain.LinkJSON;
import com.spectral.cc.core.mapping.wat.rest.ToolBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

@Path("/domain/link")
public class LinkEndpoint {
    private static final Logger log = LoggerFactory.getLogger(GateEndpoint.class);

    @GET
    @Path("/{param}")
    public Response displayLink(@PathParam("param") long id) {
        Link link = (Link) MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
        if (link != null) {
            try {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                LinkJSON.oneLink2JSON(link, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } else {
            return Response.status(404).entity("Link with id " + id + " not found.").build();
        }
    }

    @GET
    public Response displayAllLinks() {
        String  result = "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            LinkJSON.manyLinks2JSON((HashSet<Link>) MappingBootstrap.getMappingSce().getLinkSce().getLinks(null), outStream);
            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/create")
    public Response createLink(long sourceEndpointID, long targetEndpointID,
                               long transportID, long upLinkID) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Link link = MappingBootstrap.getMappingSce().getLinkSce().createLink(sourceEndpointID, targetEndpointID, transportID, upLinkID);
            try {
                LinkJSON.oneLink2JSON(link, outStream);
                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(200).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                String result = e.getMessage();
                return Response.status(500).entity(result).build();
            }
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteLink(long linkID) {
        try {
            MappingBootstrap.getMappingSce().getLinkSce().deleteLink(linkID);
            return Response.status(200).entity("Link (" + linkID + ") successfully deleted.").build();
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(500).entity(result).build();
        }
    }

    @GET
    @Path("/get")
    public Response getLink(@QueryParam("ID")long id) {
        return displayLink(id);
    }

    @GET
    @Path("/update/transport")
    public Response setLinkTransport(@QueryParam("ID")long id, @QueryParam("transportID") long transportID) {
        Link link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
        if (link!=null) {
            Transport transport = MappingBootstrap.getMappingSce().getTransportSce().getTransport(transportID);
            if (transport!=null) {
                link.setLinkTransport(transport);
                return Response.status(200).entity("Link ("+id+") transport successfully updated to " + transportID + ".").build();
            } else {
                return Response.status(404).entity("Error while updating link (" + id + ") transport " + transportID + " : transport " + id + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while updating link (" + id + ") transport " + transportID + " : link " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/sourceEP")
    public Response setLinkEndpointSource(@QueryParam("ID")long id, @QueryParam("SEPID") long SEPID) {
        Link link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
        if (link!=null) {
            Endpoint sourceEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(SEPID);
            if (sourceEP!=null) {
                link.setLinkEndpointSource(sourceEP);
                return Response.status(200).entity("Link ("+id+") source endpoint successfully updated to " + SEPID + ".").build();
            } else {
                return Response.status(404).entity("Error while updating link (" + id + ") source endpoint " + SEPID + " : link " + id + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while updating link (" + id + ") source endpoint " + SEPID + " : link " + id + " not found.").build();
        }
    }

    @GET
    @Path("/update/targetEP")
    public Response setLinkEndpointTarget(@QueryParam("ID")long id, @QueryParam("TEPID") long TEPID) {
        Link link = MappingBootstrap.getMappingSce().getLinkSce().getLink(id);
        if (link!=null) {
            Endpoint targetEP = MappingBootstrap.getMappingSce().getEndpointSce().getEndpoint(TEPID);
            if (targetEP!=null) {
                link.setLinkEndpointTarget(targetEP);
                return Response.status(200).entity("Link ("+id+") target endpoint successfully updated to " + TEPID + ".").build();
            } else {
                return Response.status(404).entity("Error while updating link (" + id + ") target endpoint " + TEPID + " : link " + id + " not found.").build();
            }
        } else {
            return Response.status(404).entity("Error while updating link (" + id + ") target endpoint " + TEPID + " : link " + id + " not found.").build();
        }
    }
}