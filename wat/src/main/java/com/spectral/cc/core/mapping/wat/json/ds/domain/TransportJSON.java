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

package com.spectral.cc.core.mapping.wat.json.ds.domain;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.spectral.cc.core.mapping.ds.domain.Transport;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.PropertiesJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class TransportJSON {
    private final static Logger log   = LoggerFactory.getLogger(TransportJSON.class);

    public final static String TP_ID_TOKEN   = "transportID";
    public final static String TP_NAME_TOKEN = "transportName";
    public final static String TP_PRP_TOKEN  = "transportProperties";

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
        JsonGenerator jgenerator = MappingBootstrap.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        transport2JSON(transport, jgenerator);
        jgenerator.close();
    }

    public final static void manyTransports2JSON(HashSet<Transport> transports, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = MappingBootstrap.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
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
}