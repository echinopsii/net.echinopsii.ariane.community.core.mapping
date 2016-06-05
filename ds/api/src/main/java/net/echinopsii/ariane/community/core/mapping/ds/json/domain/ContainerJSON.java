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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class ContainerJSON {

    private final static Logger log = LoggerFactory.getLogger(ContainerJSON.class);

    private static void containerProps2JSON(HashMap<String, Object> props, JsonGenerator jgenerator,
                                                  boolean writeOPropsFieldStart, boolean writeOPropsFieldEnd) throws IOException {
        if (writeOPropsFieldStart)jgenerator.writeObjectFieldStart(Container.TOKEN_CT_PRP);
        PropertiesJSON.propertiesToJSON(props, jgenerator);
        if (writeOPropsFieldEnd) jgenerator.writeEndObject();
    }

    private static void containerProps2JSONWithTypedProps(HashMap<String, Object> props, JsonGenerator jgenerator,
                                                          boolean writeOPropsFieldStart, boolean writeOPropsFieldEnd) throws IOException {
        if (writeOPropsFieldStart) jgenerator.writeArrayFieldStart(Container.TOKEN_CT_PRP);
        for (PropertiesJSON.TypedPropertyField field : PropertiesJSON.propertiesToTypedPropertiesList(props))
            field.toJSON(jgenerator);
        if (writeOPropsFieldEnd) jgenerator.writeEndArray();
    }

    public static void container2MapJSON(Container cont, HashMap<String, Object> props, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Container.TOKEN_CT_ID, cont.getContainerID());
        jgenerator.writeStringField(Container.TOKEN_CT_NAME, cont.getContainerName());
        jgenerator.writeStringField(Container.TOKEN_CT_COMPANY, cont.getContainerCompany());
        jgenerator.writeStringField(Container.TOKEN_CT_PRODUCT, cont.getContainerProduct());
        jgenerator.writeStringField(Container.TOKEN_CT_TYPE, cont.getContainerType());
        jgenerator.writeStringField(Container.TOKEN_CT_GATE_URI, cont.getContainerPrimaryAdminGate().getNodePrimaryAdminEndpoint().getEndpointURL());
        if (cont.getContainerParentContainer()!=null)
            jgenerator.writeStringField(Container.TOKEN_CT_PCID, cont.getContainerParentContainer().getContainerID());
        boolean isPropsBeginWritted = false;
        if (cont.getContainerProperties() != null) {
            containerProps2JSON(cont.getContainerProperties(), jgenerator, true, false);
            isPropsBeginWritted = true;
        }
        containerProps2JSON(props, jgenerator, (!isPropsBeginWritted && (props != null)), (isPropsBeginWritted || props != null));
        jgenerator.writeEndObject();
    }

    private static void commonContainer2JSON(Container cont, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Container.TOKEN_CT_ID, cont.getContainerID());
        jgenerator.writeStringField(Container.TOKEN_CT_NAME, cont.getContainerName());
        jgenerator.writeStringField(Container.TOKEN_CT_COMPANY, cont.getContainerCompany());
        jgenerator.writeStringField(Container.TOKEN_CT_PRODUCT, cont.getContainerProduct());
        jgenerator.writeStringField(Container.TOKEN_CT_TYPE, cont.getContainerType());
        jgenerator.writeStringField(Container.TOKEN_CT_GATE_URI, cont.getContainerPrimaryAdminGateURL());
        if (cont.getContainerParentContainer()!=null)
            jgenerator.writeStringField(Container.TOKEN_CT_PCID, cont.getContainerParentContainer().getContainerID());
        if (cont.getContainerPrimaryAdminGate()!=null)
            jgenerator.writeStringField(Container.TOKEN_CT_PAGTID, cont.getContainerPrimaryAdminGate().getNodeID());
        else
            log.error("Container " + cont.getContainerName() + " has no primary admin gate !?");
        if (cont.getContainerCluster()!=null)
            jgenerator.writeStringField(Container.TOKEN_CT_CLUSTER, cont.getContainerCluster().getClusterID());
        if (cont.getContainerParentContainer()!=null)
            jgenerator.writeStringField(Container.TOKEN_CT_PCID, cont.getContainerParentContainer().getContainerID());

        jgenerator.writeArrayFieldStart(Container.TOKEN_CT_CCID);
        for (Container container : cont.getContainerChildContainers())
            jgenerator.writeString(container.getContainerID());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(Container.TOKEN_CT_GID);
        for (Gate gate : cont.getContainerGates())
            jgenerator.writeString(gate.getNodeID());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(Container.TOKEN_CT_NID);
        for (Node node : cont.getContainerNodes(0))
            jgenerator.writeString(node.getNodeID());
        jgenerator.writeEndArray();
    }

    public static void container2JSON(Container cont, JsonGenerator jgenerator) throws IOException {
        commonContainer2JSON(cont, jgenerator);
        if (cont.getContainerProperties() != null) containerProps2JSON(cont.getContainerProperties(), jgenerator, true, true);
        jgenerator.writeEndObject();
    }

    public static void container2JSONWithTypedProps(Container cont, JsonGenerator jgenerator) throws IOException {
        commonContainer2JSON(cont, jgenerator);
        if (cont.getContainerProperties() != null) containerProps2JSONWithTypedProps(cont.getContainerProperties(), jgenerator, true, true);
        jgenerator.writeEndObject();
    }

    public static void oneContainer2JSON(Container cont, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        ContainerJSON.container2JSON(cont, jgenerator);
        jgenerator.close();
    }

    public static void oneContainer2JSONWithTypedProps(Container cont, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        ContainerJSON.container2JSONWithTypedProps(cont, jgenerator);
        jgenerator.close();
    }

    public static void manyContainers2JSON(HashSet<Container> conts, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("containers");
        for (Container current : conts) ContainerJSON.container2JSON(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static void manyContainers2JSONWithTypedProps(HashSet<Container> conts, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("containers");
        for (Container current : conts) ContainerJSON.container2JSONWithTypedProps(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedContainer {
        private String containerID;
        private String containerName;
        private String containerCompany;
        private String containerProduct;
        private String containerType;
        private String containerPrimaryAdminGateID;
        private String containerGateURI;
        private String containerGateName;
        private String containerClusterID;
        private String containerParentContainerID;
        private List<String> containerChildContainersID;
        private List<String> containerNodesID;
        private List<String> containerGatesID;
        private List<PropertiesJSON.TypedPropertyField> containerProperties;


        public String getContainerID() {
            return containerID;
        }

        public void setContainerID(String containerID) {
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

        public String getContainerPrimaryAdminGateID() {
            return containerPrimaryAdminGateID;
        }

        public void setContainerPrimaryAdminGateID(String containerPrimaryAdminGateID) {
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

        public String getContainerClusterID() {
            return containerClusterID;
        }

        public void setContainerClusterID(String containerClusterID) {
            this.containerClusterID = containerClusterID;
        }

        public String getContainerParentContainerID() {
            return containerParentContainerID;
        }

        public void setContainerParentContainerID(String containerParentContainerID) {
            this.containerParentContainerID = containerParentContainerID;
        }

        public List<String> getContainerChildContainersID() {
            return containerChildContainersID;
        }

        public void setContainerChildContainersID(List<String> containerChildContainersID) {
            this.containerChildContainersID = containerChildContainersID;
        }

        public List<String> getContainerNodesID() {
            return containerNodesID;
        }

        public void setContainerNodesID(List<String> containerNodesID) {
            this.containerNodesID = containerNodesID;
        }

        public List<String> getContainerGatesID() {
            return containerGatesID;
        }

        public void setContainerGatesID(List<String> containerGatesID) {
            this.containerGatesID = containerGatesID;
        }

        public List<PropertiesJSON.TypedPropertyField> getContainerProperties() {
            return containerProperties;
        }

        public void setContainerProperties(List<PropertiesJSON.TypedPropertyField> containerProperties) {
            this.containerProperties = containerProperties;
        }
    }

    public static JSONDeserializedContainer JSON2Container(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedContainer.class);
    }

    public static class JSONDeserializedContainers {
        JSONDeserializedContainer[] containers;

        public JSONDeserializedContainer[] getContainers() {
            return containers;
        }

        public void setContainers(JSONDeserializedContainer[] containers) {
            this.containers = containers;
        }

        public Set<JSONDeserializedContainer> toSet() {
            HashSet<JSONDeserializedContainer> ret = new HashSet<>();
            if (containers!=null)
                Collections.addAll(ret, containers);
            return ret;
        }
    }

    public static Set<JSONDeserializedContainer> JSON2Containers(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedContainers.class).toSet();
    }
}