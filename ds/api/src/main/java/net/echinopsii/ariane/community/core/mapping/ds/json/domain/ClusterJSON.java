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

package net.echinopsii.ariane.community.core.mapping.ds.json.domain;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusterJSON {

    public static void cluster2JSON(Cluster cluster, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(Cluster.TOKEN_CL_ID, cluster.getClusterID());
        jgenerator.writeStringField(Cluster.TOKEN_CL_NAME, cluster.getClusterName());

        jgenerator.writeArrayFieldStart(Cluster.TOKEN_CL_CONT);
        HashSet<Container> clusterContainers = new HashSet<>(cluster.getClusterContainers());
        for (Container container : clusterContainers) jgenerator.writeString(container.getContainerID());
        jgenerator.writeEndArray();

        jgenerator.writeEndObject();
    }

    public static void oneCluster2JSON(Cluster cluster, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        ClusterJSON.cluster2JSON(cluster, jgenerator);
        jgenerator.close();
    }

    public static void manyClusters2JSON(HashSet<Cluster> clusters, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = ToolBox.jFactory.createJsonGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("clusters");
        for (Cluster current : clusters) ClusterJSON.cluster2JSON(current, jgenerator);
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONDeserializedCluster {
        private String clusterID;
        private String clusterName;
        private List<String> clusterContainersID;

        public String getClusterID() {
            return clusterID;
        }

        public void setClusterID(String clusterID) {
            this.clusterID = clusterID;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public List<String> getClusterContainersID() {
            return clusterContainersID;
        }

        public void setClusterContainersID(List<String> clusterContainersID) {
            this.clusterContainersID = clusterContainersID;
        }
    }

    public static class JSONDeserializedClusters {
        JSONDeserializedCluster[] clusters;

        public JSONDeserializedCluster[] getClusters() {
            return clusters;
        }

        public void setClusters(JSONDeserializedCluster[] clusters) {
            this.clusters = clusters;
        }

        public Set<JSONDeserializedCluster> toSet() {
            HashSet<JSONDeserializedCluster> ret = new HashSet<>();
            if (clusters!=null)
                Collections.addAll(ret, clusters);
            return ret;
        }
    }

    public static JSONDeserializedCluster JSON2Cluster(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedCluster.class);
    }

    public static Set<JSONDeserializedCluster> JSON2Clusters(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONDeserializedClusters.class).toSet();
    }
}