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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON.TypedPropertyField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class NodeJSON {

    private final static Logger log = LoggerFactory.getLogger(NodeJSON.class);

    private static void nodeProps2JSON(Node node, JsonGenerator jgenerator) throws JsonGenerationException, IOException {
        if (node.getNodeProperties()!=null && node.getNodeProperties().size()!=0) {
            jgenerator.writeObjectFieldStart(Node.TOKEN_ND_PRP);
            PropertiesJSON.propertiesToJSON(node.getNodeProperties(), jgenerator);
            jgenerator.writeEndObject();
        }
    }

    public static void node2MapJSON(Node node, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Node.TOKEN_ND_ID, node.getNodeID());
        jgenerator.writeStringField(Node.TOKEN_ND_NAME, node.getNodeName());
        jgenerator.writeNumberField(Node.TOKEN_ND_DEPTH, node.getNodeDepth());
        jgenerator.writeStringField(Node.TOKEN_ND_CONID, node.getNodeContainer().getContainerID());
        if (node.getNodeParentNode()!=null)
            jgenerator.writeStringField(Node.TOKEN_ND_PNDID, node.getNodeParentNode().getNodeID());
        if (node.getNodeProperties() != null) {
            nodeProps2JSON(node, jgenerator);
        }
        jgenerator.writeEndObject();
    }

    public static void node2JSON(Node node, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStringField(Node.TOKEN_ND_ID, node.getNodeID());
        jgenerator.writeStringField(Node.TOKEN_ND_NAME, node.getNodeName());
        jgenerator.writeNumberField(Node.TOKEN_ND_DEPTH, node.getNodeDepth());
        jgenerator.writeStringField(Node.TOKEN_ND_CONID, node.getNodeContainer().getContainerID());
        if (node.getNodeParentNode()!=null)
            jgenerator.writeStringField(Node.TOKEN_ND_PNDID, node.getNodeParentNode().getNodeID());

        jgenerator.writeArrayFieldStart(Node.TOKEN_ND_CNDID);
        for (Node child : node.getNodeChildNodes())
            jgenerator.writeString(child.getNodeID());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(Node.TOKEN_ND_TWNID);
        for (Node twin : node.getTwinNodes()) jgenerator.writeString(twin.getNodeID());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(Node.TOKEN_ND_EPSID);
        for (Endpoint ep : node.getNodeEndpoints()) jgenerator.writeString(ep.getEndpointID());
        jgenerator.writeEndArray();

        if (node.getNodeProperties() != null) {
            nodeProps2JSON(node, jgenerator);
        }
    }

    public static void oneNode2JSON(Node node, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        node2JSON(node, jgenerator);
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static void manyNodes2JSON(HashSet<Node> nodes, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("nodes");
        for (Node current : nodes) {
            jgenerator.writeStartObject();
            NodeJSON.node2JSON(current, jgenerator);
            jgenerator.writeEndObject();
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedNode {
        private String nodeID;
        private String nodeName;
        private long nodeDepth;
        private String nodeContainerID;
        private String nodeParentNodeID;
        private List<String> nodeChildNodesID;
        private List<String> nodeTwinNodesID;
        private List<String> nodeEndpointsID;
        private List<TypedPropertyField> nodeProperties;

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public long getNodeDepth() {
            return nodeDepth;
        }

        public void setNodeDepth(long nodeDepth) {
            this.nodeDepth = nodeDepth;
        }

        public String getNodeContainerID() {
            return nodeContainerID;
        }

        public void setNodeContainerID(String nodeContainerID) {
            this.nodeContainerID = nodeContainerID;
        }

        public String getNodeParentNodeID() {
            return nodeParentNodeID;
        }

        public void setNodeParentNodeID(String nodeParentNodeID) {
            this.nodeParentNodeID = nodeParentNodeID;
        }

        public List<String> getNodeChildNodesID() {
            return nodeChildNodesID;
        }

        public void setNodeChildNodesID(List<String> nodeChildNodesID) {
            this.nodeChildNodesID = nodeChildNodesID;
        }

        public List<String> getNodeTwinNodesID() {
            return nodeTwinNodesID;
        }

        public void setNodeTwinNodesID(List<String> nodeTwinNodesID) {
            this.nodeTwinNodesID = nodeTwinNodesID;
        }

        public List<String> getNodeEndpointsID() {
            return nodeEndpointsID;
        }

        public void setNodeEndpointsID(List<String> nodeEndpointsID) {
            this.nodeEndpointsID = nodeEndpointsID;
        }

        public List<TypedPropertyField> getNodeProperties() {
            return nodeProperties;
        }

        public void setNodeProperties(List<TypedPropertyField> nodeProperties) {
            this.nodeProperties = nodeProperties;
        }
    }

    public static JSONDeserializedNode JSON2Node(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedNode.class);
    }
}