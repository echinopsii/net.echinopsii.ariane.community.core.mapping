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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
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

public class TransportJSON {
    private final static Logger log   = LoggerFactory.getLogger(TransportJSON.class);

    public final static String TP_ID_TOKEN   = MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"ID";
    public final static String TP_NAME_TOKEN = MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY;
    public final static String TP_PRP_TOKEN  = MappingDSGraphPropertyNames.DD_TRANSPORT_PROPS_KEY;

    private final static void transportProps2JSON(Transport transport, JsonGenerator jgenerator)
            throws JsonGenerationException, IOException {
        HashMap<String, Object> props = transport.getTransportProperties();
        if (props != null && props.size()!=0) {
            jgenerator.writeObjectFieldStart(TP_PRP_TOKEN);
            PropertiesJSON.propertiesToJSON(props, jgenerator);
            jgenerator.writeEndObject();
        }
    }

    public final static void transport2JSON(Transport transport, JsonGenerator jgenerator) throws JsonGenerationException, IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(TP_ID_TOKEN, transport.getTransportID());
        jgenerator.writeStringField(TP_NAME_TOKEN, transport.getTransportName());
        transportProps2JSON(transport, jgenerator);
        jgenerator.writeEndObject();
    }

    public final static void oneTransport2JSON(Transport transport, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        transport2JSON(transport, jgenerator);
        jgenerator.close();
    }

    public final static void manyTransports2JSON(HashSet<Transport> transports, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("transports");
        Iterator<Transport> iterC = transports.iterator();
        while (iterC.hasNext()) {
            Transport current = iterC.next();
            TransportJSON.transport2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedTransport {
        private long transportID;
        private String transportName;
        private List<PropertiesJSON.JSONDeserializedProperty> transportProperties;

        public long getTransportID() {
            return transportID;
        }

        public void setTransportID(long transportID) {
            this.transportID = transportID;
        }

        public String getTransportName() {
            return transportName;
        }

        public void setTransportName(String transportName) {
            this.transportName = transportName;
        }

        public List<PropertiesJSON.JSONDeserializedProperty> getTransportProperties() {
            return transportProperties;
        }

        public void setTransportProperties(List<PropertiesJSON.JSONDeserializedProperty> transportProperties) {
            this.transportProperties = transportProperties;
        }
    }

    public static JSONDeserializedTransport JSON2Transport(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedTransport.class);
    }

}