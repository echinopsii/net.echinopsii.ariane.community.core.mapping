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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesException;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class TransportJSON {
    //private final static Logger log   = LoggerFactory.getLogger(TransportJSON.class);

    private static void transportProps2JSON(Transport transport, JsonGenerator jgenerator)
            throws IOException {
        if (transport.getTransportProperties() != null && transport.getTransportProperties().size()!=0) {
            jgenerator.writeObjectFieldStart(Transport.TOKEN_TP_PRP);
            PropertiesJSON.propertiesToJSON(transport.getTransportProperties(), jgenerator);
            jgenerator.writeEndObject();
        }
    }

    private static void transportProps2JSONWithTypedProps(Transport transport, JsonGenerator jgenerator) throws IOException, PropertiesException {
        if (transport.getTransportProperties() != null && transport.getTransportProperties().size()!=0) {
            jgenerator.writeArrayFieldStart(Transport.TOKEN_TP_PRP);
            for (PropertiesJSON.TypedPropertyField field : PropertiesJSON.propertiesToTypedPropertiesList(transport.getTransportProperties()))
                field.toJSON(jgenerator);
            jgenerator.writeEndArray();
        }
    }


    public static void transport2JSON(Transport transport, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Transport.TOKEN_TP_ID, transport.getTransportID());
        jgenerator.writeStringField(Transport.TOKEN_TP_NAME, transport.getTransportName());
        transportProps2JSON(transport, jgenerator);
        jgenerator.writeEndObject();
    }

    public static void transport2JSONWithTypedProps(Transport transport, JsonGenerator jgenerator) throws IOException, PropertiesException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Transport.TOKEN_TP_ID, transport.getTransportID());
        jgenerator.writeStringField(Transport.TOKEN_TP_NAME, transport.getTransportName());
        transportProps2JSONWithTypedProps(transport, jgenerator);
        jgenerator.writeEndObject();
    }

    public static void oneTransport2JSON(Transport transport, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        transport2JSON(transport, jgenerator);
        jgenerator.close();
    }

    public static void oneTransport2JSONWithTypedProps(Transport transport, ByteArrayOutputStream outStream) throws IOException, PropertiesException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        transport2JSONWithTypedProps(transport, jgenerator);
        jgenerator.close();
    }

    public static void manyTransports2JSON(HashSet<Transport> transports, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("transports");
        for (Transport current : transports) TransportJSON.transport2JSON(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static void manyTransports2JSONWithTypedProps(HashSet<Transport> transports, ByteArrayOutputStream outStream) throws IOException, PropertiesException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("transports");
        for (Transport current : transports) TransportJSON.transport2JSONWithTypedProps(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedTransport {
        private String transportID;
        private String transportName;
        private List<PropertiesJSON.TypedPropertyField> transportProperties;

        public String getTransportID() {
            return transportID;
        }

        public void setTransportID(String transportID) {
            this.transportID = transportID;
        }

        public String getTransportName() {
            return transportName;
        }

        public void setTransportName(String transportName) {
            this.transportName = transportName;
        }

        public List<PropertiesJSON.TypedPropertyField> getTransportProperties() {
            return transportProperties;
        }

        public void setTransportProperties(List<PropertiesJSON.TypedPropertyField> transportProperties) {
            this.transportProperties = transportProperties;
        }
    }

    public static JSONDeserializedTransport JSON2Transport(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedTransport.class);
    }

    public static class JSONDeserializedTransports {
        JSONDeserializedTransport[] transports;

        public JSONDeserializedTransport[] getTransports() {
            return transports;
        }

        public void setTransports(JSONDeserializedTransport[] transports) {
            this.transports = transports;
        }

        public Set<JSONDeserializedTransport> toSet() {
            HashSet<JSONDeserializedTransport> ret = new HashSet<>();
            if (transports!=null) Collections.addAll(ret, transports);
            return ret;
        }
    }

    public static Set<JSONDeserializedTransport> JSON2Transports(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedTransports.class).toSet();
    }
}