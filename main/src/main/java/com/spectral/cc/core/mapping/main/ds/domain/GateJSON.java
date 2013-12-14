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
import com.fasterxml.jackson.core.JsonGenerator;
import com.spectral.cc.core.mapping.ds.domain.Gate;
import com.spectral.cc.core.mapping.main.runtime.TopoWSRuntime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class GateJSON {
    //private final static Logger  log   = LoggerFactory.getLogger(GateJSON.class);

    private final static String GT_ADMPEP_TOKEN = "gateAdminPrimEPID";
    private final static String GT_NODE_TOKEN   = "node";

    public final static void gate2JSON(Gate gate, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeObjectFieldStart(GT_NODE_TOKEN);
        NodeJSON.node2JSON(gate, jgenerator);
        jgenerator.writeEndObject();
        jgenerator.writeNumberField(GT_ADMPEP_TOKEN, (gate.getNodePrimaryAdminEndpoint() != null) ? gate.getNodePrimaryAdminEndpoint().getEndpointID() : 0);
        jgenerator.writeEndObject();
    }

    public final static void oneGate2JSON(Gate gate, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = TopoWSRuntime.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        gate2JSON(gate, jgenerator);
        jgenerator.close();
    }

    public final static void manyGates2JSON(HashSet<Gate> gates, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = TopoWSRuntime.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("gates");
        Iterator<Gate> iterC = gates.iterator();
        while (iterC.hasNext()) {
            Gate current = iterC.next();
            GateJSON.gate2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}