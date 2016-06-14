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
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesException;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class EndpointJSON {

    private final static Logger log = LoggerFactory.getLogger(EndpointJSON.class);

    private static void endpointProps2JSON(Endpoint endpoint, JsonGenerator jgenerator)
            throws IOException {
        HashMap<String, Object> props = endpoint.getEndpointProperties();
        if (props != null && props.size()!=0) {
            jgenerator.writeObjectFieldStart(Endpoint.TOKEN_EP_PRP);
            PropertiesJSON.propertiesToJSON(props,jgenerator);
            jgenerator.writeEndObject();
        }
    }

    private static void endpointProps2JSONWithTypedProps(Endpoint endpoint, JsonGenerator jgenerator) throws IOException, PropertiesException {
        HashMap<String, Object> props = endpoint.getEndpointProperties();
        if (props != null && props.size()!=0) {
            jgenerator.writeArrayFieldStart(Endpoint.TOKEN_EP_PRP);
            for (PropertiesJSON.TypedPropertyField field : PropertiesJSON.propertiesToTypedPropertiesList(props))
                field.toJSON(jgenerator);
            jgenerator.writeEndArray();
        }
    }

    private static void commonEndpoint2JSON(Endpoint endpoint, JsonGenerator jgenerator) throws IOException, MappingDSException {
        jgenerator.writeStringField(Endpoint.TOKEN_EP_ID, endpoint.getEndpointID());
        jgenerator.writeStringField(Endpoint.TOKEN_EP_URL, endpoint.getEndpointURL());
        if (endpoint.getEndpointParentNode()!=null)
            jgenerator.writeStringField(Endpoint.TOKEN_EP_PNODEID, endpoint.getEndpointParentNode().getNodeID());
        else
            throw new MappingDSException("Endpoint (" + endpoint.getEndpointID() +
                    ":" + endpoint.getEndpointURL() + ")");

        jgenerator.writeArrayFieldStart(Endpoint.TOKEN_EP_TWNEPID);
        for (Endpoint tep : endpoint.getTwinEndpoints()) jgenerator.writeString(tep.getEndpointID());
        jgenerator.writeEndArray();
    }

    public static void endpoint2JSON(Endpoint endpoint, JsonGenerator jgenerator)
            throws IOException, MappingDSException {
        jgenerator.writeStartObject();
        log.debug("Ep JSON :endpoint {}", new Object[]{endpoint.getEndpointID()});
        commonEndpoint2JSON(endpoint, jgenerator);
        EndpointJSON.endpointProps2JSON(endpoint, jgenerator);
        jgenerator.writeEndObject();
    }

    public static void endpoint2JSONWithTypedProps(Endpoint endpoint, JsonGenerator jgenerator)
            throws IOException, MappingDSException, PropertiesException {
        jgenerator.writeStartObject();
        log.debug("Ep JSON :endpoint {}", new Object[]{endpoint.getEndpointID()});
        commonEndpoint2JSON(endpoint, jgenerator);
        EndpointJSON.endpointProps2JSONWithTypedProps(endpoint, jgenerator);
        jgenerator.writeEndObject();
    }

    public static void oneEndpoint2JSON(Endpoint endpoint, ByteArrayOutputStream outStream)
            throws IOException, MappingDSException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        endpoint2JSON(endpoint, jgenerator);
        jgenerator.close();
    }

    public static void oneEndpoint2JSONWithTypedProps(Endpoint endpoint, ByteArrayOutputStream outStream)
            throws IOException, MappingDSException, PropertiesException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        endpoint2JSONWithTypedProps(endpoint, jgenerator);
        jgenerator.close();
    }

    public static void manyEndpoints2JSON(HashSet<Endpoint> endpoints, ByteArrayOutputStream outStream)
            throws IOException, MappingDSException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("endpoints");
        for (Endpoint current : endpoints) EndpointJSON.endpoint2JSON(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static void manyEndpoints2JSONWithTypedProps(HashSet<Endpoint> endpoints, ByteArrayOutputStream outStream)
            throws IOException, MappingDSException, PropertiesException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("endpoints");
        for (Endpoint current : endpoints) EndpointJSON.endpoint2JSONWithTypedProps(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedEndpoint {
        private String endpointID;
        private String endpointURL;
        private String endpointParentNodeID;
        private List<String> endpointTwinEndpointsID;
        private List<PropertiesJSON.TypedPropertyField> endpointProperties;

        public String getEndpointID() {
            return endpointID;
        }

        public void setEndpointID(String endpointID) {
            this.endpointID = endpointID;
        }

        public String getEndpointURL() {
            return endpointURL;
        }

        public void setEndpointURL(String endpointURL) {
            this.endpointURL = endpointURL;
        }

        public String getEndpointParentNodeID() {
            return endpointParentNodeID;
        }

        public void setEndpointParentNodeID(String endpointParentNodeID) {
            this.endpointParentNodeID = endpointParentNodeID;
        }

        public List<String> getEndpointTwinEndpointsID() {
            return endpointTwinEndpointsID;
        }

        public void setEndpointTwinEndpointsID(List<String> endpointTwinEndpointsID) {
            this.endpointTwinEndpointsID = endpointTwinEndpointsID;
        }

        public List<PropertiesJSON.TypedPropertyField> getEndpointProperties() {
            return endpointProperties;
        }

        public void setEndpointProperties(List<PropertiesJSON.TypedPropertyField> endpointProperties) {
            this.endpointProperties = endpointProperties;
        }
    }

    public static JSONDeserializedEndpoint JSON2Endpoint(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedEndpoint.class);
    }

    public static class JSONDeserializedEndpoints {
        JSONDeserializedEndpoint[] endpoints;

        public JSONDeserializedEndpoint[] getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(JSONDeserializedEndpoint[] endpoints) {
            this.endpoints = endpoints;
        }

        public Set<JSONDeserializedEndpoint> toSet() {
            HashSet<JSONDeserializedEndpoint> ret = new HashSet<>();
            if (endpoints!=null)
                Collections.addAll(ret, endpoints);
            return ret;
        }
    }

    public static Set<JSONDeserializedEndpoint> JSON2Endpoints(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedEndpoints.class).toSet();
    }
}