/**
 * Mapping Registry Request Endpoint
 * Copyright (C) 2015 echinopsii
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

package net.echinopsii.ariane.community.core.mapping.reqregistry.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.mapping.reqregistry.model.MappingDSLRegistryRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class MappingDSLRegistryRequestJSON {

    public final static String MDSLREQ_ID = "mappingDSLRequestID";
    public final static String MDSLREQ_VERSION = "mappingDSLRequestVersion";
    public final static String MDSLREQ_NAME = "mappingDSLRequestName";
    public final static String MDSLREQ_DESCRIPTION = "mappingDSLRequestDescription";
    public final static String MDSLREQ_REQUEST = "mappingDSLRequestData";
    public final static String MDSLREQ_ROOTDIR_ID = "mappingDSLRequestRootDirID";
    public final static String MDSLREQ_USER_ID = "mappingDSLRequestUserID";
    public final static String MDSLREQ_GROUP_ID = "mappingDSLRequestGroupID";
    public final static String MDSLREQ_UXPERMISSIONS_ID = "mappingDSLRequestUxPermissionsID";

    public final static void mappingDSLRegistryRequest2JSON(MappingDSLRegistryRequest mappingDSLRegistryRequest, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(MDSLREQ_ID, mappingDSLRegistryRequest.getId());
        jgenerator.writeNumberField(MDSLREQ_VERSION, mappingDSLRegistryRequest.getVersion());
        jgenerator.writeStringField(MDSLREQ_NAME, mappingDSLRegistryRequest.getName());
        jgenerator.writeStringField(MDSLREQ_DESCRIPTION, mappingDSLRegistryRequest.getDescription());
        jgenerator.writeArrayFieldStart(MDSLREQ_UXPERMISSIONS_ID);
        for (UXPermission uxPermission : mappingDSLRegistryRequest.getUxPermissions())
            jgenerator.writeNumber(uxPermission.getId());
        jgenerator.writeEndArray();
        jgenerator.writeStringField(MDSLREQ_REQUEST, mappingDSLRegistryRequest.getRequest());
        jgenerator.writeNumberField(MDSLREQ_ROOTDIR_ID, ((mappingDSLRegistryRequest.getRootDirectory() != null) ? mappingDSLRegistryRequest.getRootDirectory().getId() : -1));
        jgenerator.writeNumberField(MDSLREQ_USER_ID, ((mappingDSLRegistryRequest.getUser() != null) ? mappingDSLRegistryRequest.getUser().getId() : -1));
        jgenerator.writeNumberField(MDSLREQ_GROUP_ID, ((mappingDSLRegistryRequest.getGroup() != null) ? mappingDSLRegistryRequest.getGroup().getId() : -1));
        jgenerator.writeEndObject();
    }

    public final static void oneMappingDSLRegistryRequest2JSON(MappingDSLRegistryRequest mappingDSLRegistryRequest, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = new JsonFactory().createGenerator(outStream, JsonEncoding.UTF8);
        MappingDSLRegistryRequestJSON.mappingDSLRegistryRequest2JSON(mappingDSLRegistryRequest, jgenerator);
        jgenerator.close();
    }

    public final static void manyMappingDSLRegistryRequests2JSON(HashSet<MappingDSLRegistryRequest> mappingDSLRegistryRequests, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = new JsonFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("mappingDSLRegistryRequests");
        Iterator<MappingDSLRegistryRequest> iter = mappingDSLRegistryRequests.iterator();
        while (iter.hasNext()) {
            MappingDSLRegistryRequest current = iter.next();
            MappingDSLRegistryRequestJSON.mappingDSLRegistryRequest2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}
