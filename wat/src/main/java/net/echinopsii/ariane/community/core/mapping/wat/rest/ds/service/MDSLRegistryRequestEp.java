/**
 * Mapping Registry Request Endpoint
 * Copyright (C) 23/11/15 echinopsii
 *
 * author : Sagar Ghuge
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

import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.wat.helper.MDSLRegistryRequestHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Path("/mapping/registryRequest")
public class MDSLRegistryRequestEp {
    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryRequestEp.class);
    private EntityManager em;

    @POST
    @Path("/deleteRequest")
    public Response deleteRequest(@QueryParam("data") String params) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> postData = mapper.readValue(params, Map.class);
            long requestID = Long.valueOf((String) postData.get("requestID"));
            MDSLRegistryRequestHelper md = new MDSLRegistryRequestHelper();
            Boolean responseVal = md.deleteRequest(requestID);
            return Response.status(Response.Status.OK).entity("Request " + requestID + "has been successfully deleted").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You're not authorized to delete request. Contact your administrator.").build();
        }
    }

    @POST
    @Path("/saveRequest")
    public Response saveDirectory(@QueryParam("data") String params) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            MDSLRegistryRequestHelper md = new MDSLRegistryRequestHelper();
            long id = md.saveRequest(params, subject.getPrincipal().toString());
            if (id != 0)
                return Response.status(Response.Status.OK).entity(id).build();
            else
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to save Request.").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You're not authorized to save request. Contact your administrator.").build();
        }
    }
}
