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
package net.echinopsii.ariane.community.core.mapping.ds.json.service;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class MapJSON {

    public final static String MAP_CONTAINERS_ARRAY = "containers";
    public final static String MAP_NODES_ARRAY = "nodes";
    public final static String MAP_ENDPOINTS_ARRAY = "endpoints";
    public final static String MAP_LINKS_ARRAY = "links";
    public final static String MAP_TRANSPORTS_ARRAY = "transports";

    private interface propsToInjectHolder {
        HashMap<String, Object> getPropsMap();
    }

    private static void genericMap2JSON(HashSet<Container> conts, propsToInjectHolder cprps2Inject,
                                              HashSet<Node> nodes, propsToInjectHolder nprps2Inject,
                                              HashSet<Endpoint> eps, propsToInjectHolder eprps2Inject,
                                              HashSet<Link> links, propsToInjectHolder lprps2Inject,
                                              HashSet<Transport> transports, propsToInjectHolder tprps2Inject,
                                              propsToInjectHolder mprps2Inject, ByteArrayOutputStream outStream) throws IOException, MappingDSException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();

        jgenerator.writeArrayFieldStart(MAP_CONTAINERS_ARRAY);
        for (Container current : conts) ContainerJSON.container2MapJSON(current, ((cprps2Inject != null) ? cprps2Inject.getPropsMap() : null), jgenerator);
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_NODES_ARRAY);
        for (Node current : nodes)
            if (!(current instanceof Gate && !current.getNodeName().contains("cluster")))
                NodeJSON.node2MapJSON(current, jgenerator);
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_ENDPOINTS_ARRAY);
        for (Endpoint current : eps) EndpointJSON.endpoint2JSON(current, jgenerator);
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_LINKS_ARRAY);
        for (Link current : links) LinkJSON.link2JSON(current, jgenerator);
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_TRANSPORTS_ARRAY);
        for (Transport current : transports) TransportJSON.transport2JSON(current, jgenerator);
        jgenerator.writeEndArray();

        if (mprps2Inject != null) {
            for (Entry<String, Object> current : mprps2Inject.getPropsMap().entrySet()) {
                String objectName = current.getKey();
                Object obj = current.getValue();
                if (obj instanceof String) jgenerator.writeStringField(objectName, (String) obj);
            }
        }

        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static void allMap2JSON(HashSet<Container> conts, HashSet<Node> nodes, HashSet<Endpoint> eps,
                                         HashSet<Link> links, HashSet<Transport> transports, ByteArrayOutputStream outStream) throws IOException, MappingDSException {
        genericMap2JSON(conts, null, nodes, null, eps, null, links, null, transports, null, null, outStream);
    }
}