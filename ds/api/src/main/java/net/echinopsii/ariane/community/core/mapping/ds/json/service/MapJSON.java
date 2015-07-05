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
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class MapJSON {

    public final static String MAP_CONTAINERS_ARRAY = "containers";
    public final static String MAP_NODES_ARRAY = "nodes";
    public final static String MAP_ENDPOINTS_ARRAY = "endpoints";
    public final static String MAP_LINKS_ARRAY = "links";
    public final static String MAP_TRANSPORTS_ARRAY = "transports";

    private static interface propsToInjectHolder {
        public HashMap<String, Object> getPropsMap();
    }

    private final static void genericMap2JSON(HashSet<Container> conts, propsToInjectHolder cprps2Inject,
                                              HashSet<Node> nodes, propsToInjectHolder nprps2Inject,
                                              HashSet<Endpoint> eps, propsToInjectHolder eprps2Inject,
                                              HashSet<Link> links, propsToInjectHolder lprps2Inject,
                                              HashSet<Transport> transports, propsToInjectHolder tprps2Inject,
                                              propsToInjectHolder mprps2Inject, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();

        jgenerator.writeArrayFieldStart(MAP_CONTAINERS_ARRAY);
        Iterator<Container> iterC = conts.iterator();
        while (iterC.hasNext()) {
            Container current = iterC.next();
            ContainerJSON.container2MapJSON(current, ((cprps2Inject != null) ? cprps2Inject.getPropsMap() : null), jgenerator);
        }
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_NODES_ARRAY);
        Iterator<Node> iterN = nodes.iterator();
        while (iterN.hasNext()) {
            Node current = iterN.next();
            if (!(current instanceof Gate && !current.getNodeName().contains("cluster"))) {
                NodeJSON.node2MapJSON(current, jgenerator);
            }
        }
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_ENDPOINTS_ARRAY);
        Iterator<Endpoint> iterE = eps.iterator();
        while (iterE.hasNext()) {
            Endpoint current = iterE.next();
            EndpointJSON.endpoint2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_LINKS_ARRAY);
        Iterator<Link> iterL = links.iterator();
        while (iterL.hasNext()) {
            Link current = iterL.next();
            LinkJSON.link2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(MAP_TRANSPORTS_ARRAY);
        Iterator<Transport> iterT = transports.iterator();
        while (iterT.hasNext()) {
            Transport current = iterT.next();
            TransportJSON.transport2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();

        if (mprps2Inject != null) {
            Iterator<Entry<String, Object>> iterP = mprps2Inject.getPropsMap().entrySet().iterator();
            while (iterP.hasNext()) {
                Entry<String, Object> current = iterP.next();
                String objectName = current.getKey();
                Object obj = current.getValue();
                if (obj instanceof String) {
                    jgenerator.writeStringField(objectName, (String) obj);
                }
            }
        }

        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public final static void allMap2JSON(HashSet<Container> conts, HashSet<Node> nodes, HashSet<Endpoint> eps,
                                         HashSet<Link> links, HashSet<Transport> transports, ByteArrayOutputStream outStream) throws IOException {
        genericMap2JSON(conts, null, nodes, null, eps, null, links, null, transports, null, null, outStream);
    }
}