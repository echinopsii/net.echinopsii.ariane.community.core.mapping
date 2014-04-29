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
import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.spectral.cc.core.mapping.wat.MappingBootstrap;
import com.spectral.cc.core.mapping.wat.json.PropertiesJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class NodeJSON {

    private final static Logger log = LoggerFactory.getLogger(NodeJSON.class);

    public final static String ND_ID_TOKEN = MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"ID";
    public final static String ND_NAME_TOKEN = MappingDSGraphPropertyNames.DD_NODE_NAME_KEY;
    public final static String ND_CONID_TOKEN = MappingDSGraphPropertyNames.DD_NODE_CONT_KEY+"ID";
    public final static String ND_TWNID_TOKEN = MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY+"ID";
    public final static String ND_EPSID_TOKEN = MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY+"ID";
    public final static String ND_PRP_TOKEN = MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY;

    private final static void nodeProps2JSON(Node node, JsonGenerator jgenerator) throws JsonGenerationException, IOException {
        if (node.getNodeProperties()!=null && node.getNodeProperties().size()!=0) {
            jgenerator.writeObjectFieldStart(ND_PRP_TOKEN);
            PropertiesJSON.propertiesToJSON(node.getNodeProperties(),jgenerator);
            jgenerator.writeEndObject();
        }
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

        jgenerator.writeArrayFieldStart(ND_TWNID_TOKEN);
        Iterator<? extends Node> iterT = node.getTwinNodes().iterator();
        while (iterT.hasNext()) {
            Node twin = iterT.next();
            jgenerator.writeNumber(twin.getNodeID());
        }
        jgenerator.writeEndArray();

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
        JsonGenerator jgenerator = MappingBootstrap.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        node2JSON(node, jgenerator);
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public final static void manyNodes2JSON(HashSet<Node> nodes, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = MappingBootstrap.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
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