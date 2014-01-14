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
package com.spectral.cc.core.mapping.main.ds.domain;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.main.ds.PropertiesJSON;
import com.spectral.cc.core.mapping.main.runtime.TopoWSRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class EndpointJSON {

    private final static Logger log = LoggerFactory.getLogger(EndpointJSON.class);

    public final static String EP_ID_TOKEN = "endpointID";
    public final static String EP_URL_TOKEN = "endpointURL";
    public final static String EP_PNODEID_TOKEN = "endpointParentNodeID";
    public final static String EP_PRP_TOKEN = "endpointProperties";

    private final static void endpointProps2JSON(Endpoint endpoint, JsonGenerator jgenerator)
            throws JsonGenerationException, IOException {
        HashMap<String, Object> props = endpoint.getEndpointProperties();
        if (props != null && props.size()!=0) {
            jgenerator.writeObjectFieldStart(EP_PRP_TOKEN);
            PropertiesJSON.propertiesToJSON(props,jgenerator);
            jgenerator.writeEndObject();
        }
    }

    public final static void endpoint2JSON(Endpoint endpoint, JsonGenerator jgenerator) throws JsonGenerationException, IOException {
        jgenerator.writeStartObject();
        log.debug("Ep JSON :endpoint {}", new Object[]{endpoint.getEndpointID()});
        jgenerator.writeNumberField(EP_ID_TOKEN, endpoint.getEndpointID());
        jgenerator.writeStringField(EP_URL_TOKEN, endpoint.getEndpointURL());
        jgenerator.writeNumberField(EP_PNODEID_TOKEN, endpoint.getEndpointParentNode().getNodeID());
        EndpointJSON.endpointProps2JSON(endpoint,jgenerator);
        jgenerator.writeEndObject();
    }

    public final static void oneEndpoint2JSON(Endpoint endpoint, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = TopoWSRuntime.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        endpoint2JSON(endpoint, jgenerator);
        jgenerator.close();
    }

    public final static void manyEndpoints2JSON(HashSet<Endpoint> endpoints, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = TopoWSRuntime.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("endpoints");
        Iterator<Endpoint> iterC = endpoints.iterator();
        while (iterC.hasNext()) {
            Endpoint current = iterC.next();
            EndpointJSON.endpoint2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}