/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 14/04/14 echinopsii
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

package net.echinopsii.ariane.core.mapping.wat.json.ds.domain;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import net.echinopsii.ariane.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.core.mapping.wat.MappingBootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class ClusterJSON {

    public final static String CL_ID_TOKEN   = MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE+"ID";
    public final static String CL_NAME_TOKEN = MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY;
    public final static String CL_CONT_TOKEN = MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY+"ID";

    public final static void cluster2JSON(Cluster cluster, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(CL_ID_TOKEN, cluster.getClusterID());
        jgenerator.writeStringField(CL_NAME_TOKEN, cluster.getClusterName());

        jgenerator.writeArrayFieldStart(CL_CONT_TOKEN);
        Iterator<? extends Container> iterCo = cluster.getClusterContainers().iterator();
        while (iterCo.hasNext()) {
            Container container = iterCo.next();
            jgenerator.writeNumber(container.getContainerID());
        }
        jgenerator.writeEndArray();

        jgenerator.writeEndObject();
    }

    public final static void oneCluster2JSON(Cluster cluster, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = MappingBootstrap.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        ClusterJSON.cluster2JSON(cluster, jgenerator);
        jgenerator.close();
    }

    public final static void manyClusters2JSON(HashSet<Cluster> clusters, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = MappingBootstrap.getjFactory().createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("clusters");
        Iterator<Cluster> iterC = clusters.iterator();
        while (iterC.hasNext()) {
            Cluster current = iterC.next();
            ClusterJSON.cluster2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}