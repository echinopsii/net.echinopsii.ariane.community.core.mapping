/**
 * Mapping Registry Endpoint
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
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.json.MappingDSLRegistryDirectoryJSON;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.wat.helper.MDSLRegistryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;

@Path("/mapping/registry")
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
    public Response getRoot() throws JsonProcessingException
    {
        MDSLRegistryHelper md = new MDSLRegistryHelper();
        Response ret = mappingDSLRegistryDirToJSON(md.getRootD());
        return  ret;
    }
}
