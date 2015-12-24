/**
 * Mapping Registry Directory Endpoint
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.json.MappingDSLRegistryDirectoryJSON;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.wat.helper.MDSLRegistryDirectoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Path("/mapping/registryDirectory")
public class MDSLRegistryDirectoryEndpoint {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryDirectoryEndpoint.class);
    private EntityManager em;

    public static Response mappingDSLRegistryDirToJSON(MappingDSLRegistryDirectory mappingDSLRegistryDirectory) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            MappingDSLRegistryDirectoryJSON.oneMappingDSLRegistryDir2JSON(mappingDSLRegistryDirectory, outStream);
            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            ret = Response.status(Response.Status.OK).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
            ret = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return ret;
    }

    @GET
    @Path("/getRoot")
    public Response getRoot() throws JsonProcessingException {
        MDSLRegistryDirectoryHelper md = new MDSLRegistryDirectoryHelper();
        Response ret = mappingDSLRegistryDirToJSON(md.getRootD());
        return ret;
    }

    @POST
    @Path("/getChild")
    public Response getChild(@QueryParam("data") String params) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> postData = mapper.readValue(params, Map.class);
        int subDirID = Integer.valueOf((String) postData.get("subDirID"));
        MDSLRegistryDirectoryHelper md = new MDSLRegistryDirectoryHelper();
        Response ret = mappingDSLRegistryDirToJSON(md.getChild(subDirID));
        return ret;
    }

    @POST
    @Path("/deleteDirectory")
    public Response deleteDirectory(@QueryParam("data") String params) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> postData = mapper.readValue(params, Map.class);
        long directoryID = Long.valueOf((String) postData.get("directoryID"));
        MDSLRegistryDirectoryHelper md = new MDSLRegistryDirectoryHelper();
        Boolean responseVal = md.deleteDirectory(directoryID);
        return Response.status(Response.Status.OK).entity("Directory " + directoryID + "has been successfully deleted").build();
    }
}
