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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesException;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GateJSON {
    //private final static Logger  log   = LoggerFactory.getLogger(GateJSON.class);

    public static void gate2JSON(Gate gate, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeObjectFieldStart(Gate.TOKEN_GT_NODE);
        NodeJSON.node2JSON(gate, jgenerator);
        jgenerator.writeEndObject();
        jgenerator.writeBooleanField(Gate.TOKEN_GT_ISADMPRIM, gate.isAdminPrimary());
        if (gate.isAdminPrimary()) {
            jgenerator.writeStringField(Gate.TOKEN_GT_ADMPEP, (gate.getNodePrimaryAdminEndpoint() != null) ? gate.getNodePrimaryAdminEndpoint().getEndpointID() : "");
            jgenerator.writeStringField(Gate.TOKEN_GT_URL, (gate.getNodePrimaryAdminEndpoint() != null) ? gate.getNodePrimaryAdminEndpoint().getEndpointURL() : "");
        }
        jgenerator.writeEndObject();
    }

    public static void gate2JSONWithTypedProps(Gate gate, JsonGenerator jgenerator) throws IOException, PropertiesException {
        jgenerator.writeStartObject();
        jgenerator.writeObjectFieldStart(Gate.TOKEN_GT_NODE);
        NodeJSON.node2JSONWithTypedProps(gate, jgenerator);
        jgenerator.writeEndObject();
        jgenerator.writeStringField(Gate.TOKEN_GT_ADMPEP, (gate.getNodePrimaryAdminEndpoint() != null) ? gate.getNodePrimaryAdminEndpoint().getEndpointID() : "");
        jgenerator.writeEndObject();
    }

    public static void oneGate2JSON(Gate gate, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        gate2JSON(gate, jgenerator);
        jgenerator.close();
    }

    public static void oneGate2JSONWithTypedProps(Gate gate, ByteArrayOutputStream outStream) throws IOException, PropertiesException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        gate2JSONWithTypedProps(gate, jgenerator);
        jgenerator.close();
    }

    public static void manyGates2JSON(HashSet<Gate> gates, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("gates");
        for (Gate current : gates) GateJSON.gate2JSON(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static void manyGates2JSONWithTypedProps(HashSet<Gate> gates, ByteArrayOutputStream outStream) throws IOException, PropertiesException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("gates");
        for (Gate current : gates) GateJSON.gate2JSONWithTypedProps(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedGate {
        private NodeJSON.JSONDeserializedNode node = null;
        private boolean gateIsPrimaryAdmin = false;
        private String gatePrimaryAdminEndpointID = null;
        private String gatePrimaryAdminEndpointURL = null;

        public NodeJSON.JSONDeserializedNode getNode() {
            return node;
        }

        public void setNode(NodeJSON.JSONDeserializedNode node) {
            this.node = node;
        }

        public String getGatePrimaryAdminEndpointURL() {
            return gatePrimaryAdminEndpointURL;
        }

        public void setGatePrimaryAdminEndpointURL(String gatePrimaryAdminEndpointURL) {
            this.gatePrimaryAdminEndpointURL = gatePrimaryAdminEndpointURL;
        }

        public boolean isGateIsPrimaryAdmin() {
            return gateIsPrimaryAdmin;
        }

        public void setGateIsPrimaryAdmin(boolean gateIsPrimaryAdmin) {
            this.gateIsPrimaryAdmin = gateIsPrimaryAdmin;
        }

        public String getGatePrimaryAdminEndpointID() {
            return gatePrimaryAdminEndpointID;
        }

        public void setGatePrimaryAdminEndpointID(String gatePrimaryAdminEndpointID) {
            this.gatePrimaryAdminEndpointID = gatePrimaryAdminEndpointID;
        }
    }

    public static JSONDeserializedGate JSON2Gate(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedGate.class);
    }

    public static class JSONDeserializedGates {
        JSONDeserializedGate[] gates;

        public JSONDeserializedGate[] getGates() {
            return gates;
        }

        public void setGates(JSONDeserializedGate[] gates) {
            this.gates = gates;
        }

        public Set<JSONDeserializedGate> toSet() {
            HashSet<JSONDeserializedGate> ret = new HashSet<>();
            if (gates!=null)
                Collections.addAll(ret, gates);
            return ret;
        }
    }

    public static Set<JSONDeserializedGate> JSON2Gates(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedGates.class).toSet();
    }
}