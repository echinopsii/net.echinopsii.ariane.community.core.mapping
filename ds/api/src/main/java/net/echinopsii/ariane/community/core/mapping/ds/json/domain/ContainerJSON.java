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

package net.echinopsii.ariane.community.core.mapping.ds.json.domain;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ContainerJSON {

    private final static Logger log = LoggerFactory.getLogger(ContainerJSON.class);

    public final static String CT_ID_TOKEN = MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"ID";
    public final static String CT_NAME_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_NAME_KEY;
    public final static String CT_COMPANY_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY;
    public final static String CT_PRODUCT_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY;
    public final static String CT_TYPE_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY;
    public final static String CT_PAGTID_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY+"ID";
    public final static String CT_GATE_URI = MappingDSGraphPropertyNames.DD_CONTAINER_GATEURI_KEY;
    public final static String CT_CLUSTER_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY+"ID";
    public final static String CT_PCID_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY+"ID";
    public final static String CT_CCID_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY+"ID";
    public final static String CT_NID_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY+"ID";
    public final static String CT_GID_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY+"ID";
    public final static String CT_PRP_TOKEN = MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY;

    private final static void containerProps2JSON(HashMap<String, Object> props, JsonGenerator jgenerator,
                                                  boolean writeOPropsFieldStart, boolean writeOPropsFieldEnd)
    throws JsonGenerationException, IOException {
        if (writeOPropsFieldStart) {
            jgenerator.writeObjectFieldStart(CT_PRP_TOKEN);
        }
        PropertiesJSON.propertiesToJSON(props, jgenerator);
        if (writeOPropsFieldEnd) {
            jgenerator.writeEndObject();
        }
    }

    public final static void container2MapJSON(Container cont, HashMap<String, Object> props, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(CT_ID_TOKEN, cont.getContainerID());
        jgenerator.writeStringField(CT_NAME_TOKEN, cont.getContainerName());
        jgenerator.writeStringField(CT_COMPANY_TOKEN, cont.getContainerCompany());
        jgenerator.writeStringField(CT_PRODUCT_TOKEN, cont.getContainerProduct());
        jgenerator.writeStringField(CT_TYPE_TOKEN, cont.getContainerType());
        jgenerator.writeStringField(CT_GATE_URI, cont.getContainerPrimaryAdminGate().getNodePrimaryAdminEndpoint().getEndpointURL());
        if (cont.getContainerParentContainer()!=null)
            jgenerator.writeNumberField(CT_PCID_TOKEN, cont.getContainerParentContainer().getContainerID());
        boolean isPropsBeginWritted = false;
        if (cont.getContainerProperties() != null) {
            containerProps2JSON(cont.getContainerProperties(), jgenerator, !isPropsBeginWritted, false);
            isPropsBeginWritted = true;
        }
        containerProps2JSON(props, jgenerator, (!isPropsBeginWritted && (props != null)), (isPropsBeginWritted || props != null));
        jgenerator.writeEndObject();
    }

    public final static void container2JSON(Container cont, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(CT_ID_TOKEN, cont.getContainerID());
        jgenerator.writeStringField(CT_NAME_TOKEN, cont.getContainerName());
        jgenerator.writeStringField(CT_COMPANY_TOKEN, cont.getContainerCompany());
        jgenerator.writeStringField(CT_PRODUCT_TOKEN, cont.getContainerProduct());
        jgenerator.writeStringField(CT_TYPE_TOKEN, cont.getContainerType());
        jgenerator.writeStringField(CT_GATE_URI, cont.getContainerPrimaryAdminGateURL());
        if (cont.getContainerParentContainer()!=null)
            jgenerator.writeNumberField(CT_PCID_TOKEN, cont.getContainerParentContainer().getContainerID());
        jgenerator.writeNumberField(CT_PAGTID_TOKEN, cont.getContainerPrimaryAdminGate().getNodeID());
        if (cont.getContainerCluster()!=null)
            jgenerator.writeNumberField(CT_CLUSTER_TOKEN, cont.getContainerCluster().getClusterID());
        if (cont.getContainerParentContainer()!=null)
            jgenerator.writeNumberField(CT_PCID_TOKEN, cont.getContainerParentContainer().getContainerID());

        jgenerator.writeArrayFieldStart(CT_CCID_TOKEN);
        for (Container container : cont.getContainerChildContainers())
            jgenerator.writeNumber(container.getContainerID());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(CT_GID_TOKEN);
        for (Gate gate : cont.getContainerGates())
            jgenerator.writeNumber(gate.getNodeID());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(CT_NID_TOKEN);
        for (Node node : cont.getContainerNodes(0))
            jgenerator.writeNumber(node.getNodeID());
        jgenerator.writeEndArray();

        if (cont.getContainerProperties() != null) {
            log.debug("Read container properties {}", new Object[]{cont.getContainerProperties().toString()});
            containerProps2JSON(cont.getContainerProperties(), jgenerator, true, true);
        }

        jgenerator.writeEndObject();
    }

    public final static void oneContainer2JSON(Container cont, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        ContainerJSON.container2JSON(cont, jgenerator);
        jgenerator.close();
    }

    public final static void manyContainers2JSON(HashSet<Container> conts, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("containers");
        Iterator<Container> iterC = conts.iterator();
        while (iterC.hasNext()) {
            Container current = iterC.next();
            ContainerJSON.container2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedContainer {
        private long containerID;
        private String containerName;
        private String containerCompany;
        private String containerProduct;
        private String containerType;
        private long containerPrimaryAdminGateID;
        private String containerGateURI;
        private String containerGateName;
        private long containerClusterID;
        private long containerParentContainerID;
        private List<Long> containerChildContainersID;
        private List<Long> containerNodesID;
        private List<Long> containerGatesID;
        private List<PropertiesJSON.JSONDeserializedProperty> containerProperties;


        public long getContainerID() {
            return containerID;
        }

        public void setContainerID(long containerID) {
            this.containerID = containerID;
        }

        public String getContainerName() {
            return containerName;
        }

        public void setContainerName(String containerName) {
            this.containerName = containerName;
        }

        public String getContainerCompany() {
            return containerCompany;
        }

        public void setContainerCompany(String containerCompany) {
            this.containerCompany = containerCompany;
        }

        public String getContainerProduct() {
            return containerProduct;
        }

        public void setContainerProduct(String containerProduct) {
            this.containerProduct = containerProduct;
        }

        public String getContainerType() {
            return containerType;
        }

        public void setContainerType(String containerType) {
            this.containerType = containerType;
        }

        public long getContainerPrimaryAdminGateID() {
            return containerPrimaryAdminGateID;
        }

        public void setContainerPrimaryAdminGateID(long containerPrimaryAdminGateID) {
            this.containerPrimaryAdminGateID = containerPrimaryAdminGateID;
        }

        public String getContainerGateURI() {
            return containerGateURI;
        }

        public void setContainerGateURI(String containerGateURI) {
            this.containerGateURI = containerGateURI;
        }

        public String getContainerGateName() {
            return containerGateName;
        }

        public void setContainerGateName(String containerGateName) {
            this.containerGateName = containerGateName;
        }

        public long getContainerClusterID() {
            return containerClusterID;
        }

        public void setContainerClusterID(long containerClusterID) {
            this.containerClusterID = containerClusterID;
        }

        public long getContainerParentContainerID() {
            return containerParentContainerID;
        }

        public void setContainerParentContainerID(long containerParentContainerID) {
            this.containerParentContainerID = containerParentContainerID;
        }

        public List<Long> getContainerChildContainersID() {
            return containerChildContainersID;
        }

        public void setContainerChildContainersID(List<Long> containerChildContainersID) {
            this.containerChildContainersID = containerChildContainersID;
        }

        public List<Long> getContainerNodesID() {
            return containerNodesID;
        }

        public void setContainerNodesID(List<Long> containerNodesID) {
            this.containerNodesID = containerNodesID;
        }

        public List<Long> getContainerGatesID() {
            return containerGatesID;
        }

        public void setContainerGatesID(List<Long> containerGatesID) {
            this.containerGatesID = containerGatesID;
        }

        public List<PropertiesJSON.JSONDeserializedProperty> getContainerProperties() {
            return containerProperties;
        }

        public void setContainerProperties(List<PropertiesJSON.JSONDeserializedProperty> containerProperties) {
            this.containerProperties = containerProperties;
        }
    }

    public static JSONDeserializedContainer JSON2Container(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedContainer.class);
    }
}