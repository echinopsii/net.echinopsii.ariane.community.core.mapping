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
import com.spectral.cc.core.mapping.ds.domain.Node;
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

public class NodeJSON {

    private final static Logger log = LoggerFactory.getLogger(NodeJSON.class);

    public final static String ND_ID_TOKEN = "nodeID";
    public final static String ND_NAME_TOKEN = "nodeName";
    public final static String ND_CONID_TOKEN = "nodeContainerID";
    public final static String ND_EPSID_TOKEN = "nodeEndpointsID";
    public final static String ND_PRP_TOKEN = "nodeProperties";

    private final static void nodeProps2JSON(Node node, JsonGenerator jgenerator)
    throws JsonGenerationException, IOException {
        jgenerator.writeObjectFieldStart(ND_PRP_TOKEN);
        Iterator<Entry<String, Object>> iterP = node.getNodeProperties().entrySet().iterator();
        while (iterP.hasNext()) {
            Entry<String, Object> current = iterP.next();
            String objectName = current.getKey();
            Object obj = current.getValue();
            log.debug("node property {}", new Object[]{objectName});

            if (obj instanceof String) {
                jgenerator.writeStringField(objectName, (String) obj);
            } else if (obj instanceof Boolean) {
                jgenerator.writeBooleanField(objectName, (Boolean) obj);
            } else if (obj instanceof Long) {
                jgenerator.writeNumberField(objectName, (Long) obj);
            } else if (obj instanceof Integer) {
                jgenerator.writeNumberField(objectName, (Integer) obj);
            } else if (obj instanceof HashMap<?, ?>) {
                log.debug("node property {} value is an object", new Object[]{objectName});
                jgenerator.writeObjectFieldStart(objectName);
                @SuppressWarnings("unchecked")
                HashMap<String, Object> hobj = (HashMap<String, Object>) obj;
                Iterator<String> iterHK = hobj.keySet().iterator();
                while (iterHK.hasNext()) {
                    String key = iterHK.next();
                    Object value = hobj.get(key);
                    if (value instanceof String) {
                        log.debug("Container property Object {} value {}:{}", new Object[]{objectName, key, (String) value});
                        jgenerator.writeStringField(key, (String) value);
                    } else if (value instanceof Long) {
                        log.debug("Container property Object {} value {}:{}", new Object[]{objectName, key, (Long) value});
                        jgenerator.writeNumberField(key, (Long) value);
                    }
                }
                jgenerator.writeEndObject();
            } else if (obj instanceof ArrayList<?>) {
                jgenerator.writeArrayFieldStart(objectName);
                @SuppressWarnings("unchecked")
                ArrayList<Object> aobj = (ArrayList<Object>) obj;
                Iterator<Object> iterAK = aobj.iterator();
                while (iterAK.hasNext()) {
                    Object value = iterAK.next();
                    if (value instanceof String) {
                        jgenerator.writeString((String) value);
                    } else if (value instanceof Long) {
                        jgenerator.writeNumber((Long) value);
                    }
                }
                jgenerator.writeEndArray();
            } else {
                log.error("Node property {} type is not managed...", new Object[]{objectName});
            }
        }
        jgenerator.writeEndObject();
    }

    public final static void node2MapJSON(Node node, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(ND_ID_TOKEN, node.getNodeID());
        jgenerator.writeStringField(ND_NAME_TOKEN, node.getNodeName());
        jgenerator.writeNumberField(ND_CONID_TOKEN, node.getNodeContainer().getContainerID());
        if (node.getNodeProperties() != null) {
            nodeProps2JSON(node, jgenerator);
        }
        jgenerator.writeEndObject();
    }

    public final static void node2JSON(Node node, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeNumberField(ND_ID_TOKEN, node.getNodeID());
        jgenerator.writeStringField(ND_NAME_TOKEN, node.getNodeName());
        jgenerator.writeNumberField(ND_CONID_TOKEN, node.getNodeContainer().getContainerID());

        jgenerator.writeArrayFieldStart(ND_EPSID_TOKEN);
        Iterator<? extends Endpoint> iterE = node.getNodeEndpoints().iterator();
        while (iterE.hasNext()) {
            Endpoint ep = iterE.next();
            jgenerator.writeNumber(ep.getEndpointID());
        }
        jgenerator.writeEndArray();
        if (node.getNodeProperties() != null) {
            nodeProps2JSON(node, jgenerator);
        }
    }

    public final static void oneNode2JSON(Node node, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = TopoWSRuntime.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        node2JSON(node, jgenerator);
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public final static void manyNodes2JSON(HashSet<Node> nodes, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = TopoWSRuntime.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("nodes");
        Iterator<Node> iterC = nodes.iterator();
        while (iterC.hasNext()) {
            Node current = iterC.next();
            jgenerator.writeStartObject();
            NodeJSON.node2JSON(current, jgenerator);
            jgenerator.writeEndObject();
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}