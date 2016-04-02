/**
 * Mapping Registry Directory JSON
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

package net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.model.MappingDSLRegistryRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class MappingDSLRegistryDirectoryJSON {

    public final static String MDSLDIR_ID = "mappingDSLDirectoryID";
    public final static String MDSLDIR_VERSION = "mappingDSLDirectoryVersion";
    public final static String MDSLDIR_NAME = "mappingDSLDirectoryName";
    public final static String MDSLDIR_DESCRIPTION = "mappingDSLDirectoryDescription";
    public final static String MDSLDIR_ROOT_DIR_ID = "mappingDSLDirectoryRootDirID";
    public final static String MDSLDIR_SUB_DIRS_ID = "mappingDSLDirectorySubDirsID";
    public final static String MDSLDIR_REQUESTS_ID = "mappingDSLDirectoryRequestsID";
    public final static String MDSLDIR_REQUEST_ID = "dirRequestID";
    public final static String MDSLDIR_REQUEST_NAME = "dirRequestName";
    public final static String MDSLDIR_REQUEST_DESC = "dirRequestDescription";
    public final static String MDSLDIR_REQUEST_REQ = "dirRequestReq";
    public final static String MDSLDIR_USER_ID = "mappingDSLDirectoryUserID";
    public final static String MDSLDIR_GROUP_ID = "mappingDSLDirectoryGroupID";
    public final static String MDSLDIR_UXPERMISSIONS_ID = "mappingDSLDirectoryUxPermissionsID";
    public final static String MDSLDIR_SUB_DIR_ID = "subDirectoryID";
    public final static String MDSLDIR_SUB_DIR_NAME = "subDirectoryName";
    public final static String MDSLDIR_SUB_DIR_DESC = "subDirectoryDesc";

    public final static void mappingDSLRegistryDirectory2JSON(MappingDSLRegistryDirectory mappingDSLRegistryDirectory, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(MDSLDIR_ID, mappingDSLRegistryDirectory.getId());
        jgenerator.writeNumberField(MDSLDIR_VERSION, mappingDSLRegistryDirectory.getVersion());
        jgenerator.writeStringField(MDSLDIR_NAME, mappingDSLRegistryDirectory.getName());
        jgenerator.writeStringField(MDSLDIR_DESCRIPTION, mappingDSLRegistryDirectory.getDescription());
        jgenerator.writeArrayFieldStart(MDSLDIR_UXPERMISSIONS_ID);
        for (UXPermission uxPermission : mappingDSLRegistryDirectory.getUxPermissions())
            jgenerator.writeNumber(uxPermission.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(MDSLDIR_SUB_DIRS_ID);
        for (MappingDSLRegistryDirectory mappingDSLRegistryDirectory1 : mappingDSLRegistryDirectory.getSubDirectories()) {
            jgenerator.writeStartObject();
            jgenerator.writeNumberField(MDSLDIR_SUB_DIR_ID, mappingDSLRegistryDirectory1.getId());
            jgenerator.writeStringField(MDSLDIR_SUB_DIR_NAME, mappingDSLRegistryDirectory1.getName());
            jgenerator.writeStringField(MDSLDIR_SUB_DIR_DESC, mappingDSLRegistryDirectory1.getDescription());
            jgenerator.writeEndObject();
        }
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(MDSLDIR_REQUESTS_ID);
        for (MappingDSLRegistryRequest mappingDSLRegistryRequest : mappingDSLRegistryDirectory.getRequests()) {
            jgenerator.writeStartObject();
            jgenerator.writeNumberField(MDSLDIR_REQUEST_ID, mappingDSLRegistryRequest.getId());
            jgenerator.writeStringField(MDSLDIR_REQUEST_NAME, mappingDSLRegistryRequest.getName());
            jgenerator.writeStringField(MDSLDIR_REQUEST_DESC, mappingDSLRegistryRequest.getDescription());
            jgenerator.writeStringField(MDSLDIR_REQUEST_REQ, mappingDSLRegistryRequest.getRequest());
            jgenerator.writeEndObject();
        }
        jgenerator.writeEndArray();
        jgenerator.writeNumberField(MDSLDIR_ROOT_DIR_ID, ((mappingDSLRegistryDirectory.getRootDirectory() != null) ? mappingDSLRegistryDirectory.getRootDirectory().getId() : -1));
        jgenerator.writeNumberField(MDSLDIR_USER_ID, ((mappingDSLRegistryDirectory.getUser() != null) ? mappingDSLRegistryDirectory.getUser().getId() : -1));
        jgenerator.writeNumberField(MDSLDIR_GROUP_ID, ((mappingDSLRegistryDirectory.getGroup() != null) ? mappingDSLRegistryDirectory.getGroup().getId() : -1));
        jgenerator.writeEndObject();
    }

    public final static void oneMappingDSLRegistryDir2JSON(MappingDSLRegistryDirectory mappingDSLRegistryDirectory, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = new JsonFactory().createGenerator(outStream, JsonEncoding.UTF8);
        MappingDSLRegistryDirectoryJSON.mappingDSLRegistryDirectory2JSON(mappingDSLRegistryDirectory, jgenerator);
        jgenerator.close();
    }

    public final static void manyMappingDSLRegistryDirs2JSON(HashSet<MappingDSLRegistryDirectory> mappingDSLRegistryDirectories, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = new JsonFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("mappingDSLRegistryDirectories");
        Iterator<MappingDSLRegistryDirectory> iter = mappingDSLRegistryDirectories.iterator();
        while (iter.hasNext()) {
            MappingDSLRegistryDirectory current = iter.next();
            MappingDSLRegistryDirectoryJSON.mappingDSLRegistryDirectory2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}
