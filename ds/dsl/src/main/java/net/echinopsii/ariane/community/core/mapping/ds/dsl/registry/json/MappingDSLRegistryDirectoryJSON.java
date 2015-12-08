package net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import org.neo4j.shell.util.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by sagar on 24/11/15.
 */
public class MappingDSLRegistryDirectoryJSON {

    public final static String MDSLDIR_ID = "mappingDSLDirectoryID";
    public final static String MDSL_VERSION = "mappingDSLDirectoryVersion";
    public final static String MDSL_NAME = "mappingDSLDirectoryName";
    public final static String MDSL_DESCRIPTION = "mappingDSLDirectoryDescription";
    public final static String MDSL_ROOT_DIR_ID = "mappingDSLDirectoryRootDirID";
    public final static String MDSL_SUB_DIRS_ID = "mappingDSLDirectorySubDirsID";
    public final static String MDSL_REQUESTS_ID = "mappingDSLDirectoryRequestsID";
    public final static String MDSL_USER_ID = "mappingDSLDirectoryUserID";
    public final static String MDSL_GROUP_ID = "mappingDSLDirectoryGroupID";
    public final static String MDSL_UXPERMISSIONS_ID = "mappingDSLDirectoryUxPermissionsID";
    public final static String MDSL_SUB_DIR_ID = "subDirectoryID";
    public final static String MDSL_SUB_DIR_NAME = "subDirectoryName";

    public final static void mappingDSLRegistryDirectory2JSON(MappingDSLRegistryDirectory mappingDSLRegistryDirectory, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(MDSLDIR_ID, mappingDSLRegistryDirectory.getId());
        jgenerator.writeNumberField(MDSL_VERSION, mappingDSLRegistryDirectory.getVersion());
        jgenerator.writeStringField(MDSL_NAME, mappingDSLRegistryDirectory.getName());
        jgenerator.writeStringField(MDSL_DESCRIPTION, mappingDSLRegistryDirectory.getDescription());
        jgenerator.writeArrayFieldStart(MDSL_UXPERMISSIONS_ID);
        for (UXPermission uxPermission : mappingDSLRegistryDirectory.getUxPermissions())
            jgenerator.writeNumber(uxPermission.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(MDSL_SUB_DIRS_ID);
        for (MappingDSLRegistryDirectory mappingDSLRegistryDirectory1 : mappingDSLRegistryDirectory.getSubDirectories()) {
            jgenerator.writeStartObject();
            jgenerator.writeNumberField(MDSL_SUB_DIR_ID, mappingDSLRegistryDirectory1.getId());
            jgenerator.writeStringField(MDSL_SUB_DIR_NAME, mappingDSLRegistryDirectory1.getName());
            jgenerator.writeEndObject();
        }
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(MDSL_REQUESTS_ID);
        for (MappingDSLRegistryRequest mappingDSLRegistryRequest : mappingDSLRegistryDirectory.getRequests())
            jgenerator.writeNumber(mappingDSLRegistryRequest.getId());
        jgenerator.writeEndArray();
        jgenerator.writeNumberField(MDSL_ROOT_DIR_ID, ((mappingDSLRegistryDirectory.getRootDirectory() != null) ? mappingDSLRegistryDirectory.getRootDirectory().getId() : -1));
        jgenerator.writeNumberField(MDSL_USER_ID, ((mappingDSLRegistryDirectory.getUser() != null) ? mappingDSLRegistryDirectory.getUser().getId() : -1));
        jgenerator.writeNumberField(MDSL_GROUP_ID, ((mappingDSLRegistryDirectory.getGroup() != null) ? mappingDSLRegistryDirectory.getGroup().getId() : -1));
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
