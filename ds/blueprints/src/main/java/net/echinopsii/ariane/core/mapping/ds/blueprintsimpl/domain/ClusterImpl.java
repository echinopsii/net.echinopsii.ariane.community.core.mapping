/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
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

package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain;

import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBException;
import net.echinopsii.ariane.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.core.mapping.ds.domain.Container;
import com.tinkerpop.blueprints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ClusterImpl implements Cluster, MappingDSCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);

    private long clusterID = 0;
    private String clusterName = null;
    private Set<ContainerImpl> clusterContainers = new HashSet<ContainerImpl>();

    private Vertex clusterVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public long getClusterID() {
        return this.clusterID;
    }

    @Override
    public String getClusterName() {
        return this.clusterName;
    }

    @Override
    public void setClusterName(String name) {
        if (this.clusterName == null || !this.clusterName.equals(name)) {
            this.clusterName = name;
            synchronizeNameToDB();
        }
    }

    @Override
    public Set<ContainerImpl> getClusterContainers() {
        return this.clusterContainers;
    }

    @Override
    public boolean addClusterContainer(Container container) {
        if (container instanceof ContainerImpl) {
            boolean ret = false;
            try {
                ret = this.clusterContainers.add((ContainerImpl) container);
                if (ret) {
                    synchronizeContainerToDB((ContainerImpl) container);
                    container.setContainerCluster(this);
                }
            } catch (MappingDSGraphDBException E) {
                E.printStackTrace();
                log.error("Exception while adding container {}...", new Object[]{container.getContainerID()});
                this.clusterContainers.remove((ContainerImpl) container);
                MappingDSGraphDB.autorollback();
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeClusterContainer(Container container) {
        if (container instanceof ContainerImpl) {
            boolean ret = this.clusterContainers.remove(container);
            if (ret) removeContainerFromDB((ContainerImpl)container);
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public Element getElement() {
        return this.clusterVertex;
    }

    @Override
    public void setElement(Element vertex) {
        this.clusterVertex = (Vertex) vertex;
        this.clusterVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE);
        this.clusterID = this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        log.debug("Cluster vertex has been initialized ({},{}).", new Object[]{this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                               this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    @Override
    public void synchronizeToDB() throws MappingDSGraphDBException {
        synchronizeNameToDB();
        synchronizeContainersToDB();
    }

    private void synchronizeNameToDB() {
        if (this.clusterVertex != null && this.clusterName != null) {
            this.clusterVertex.setProperty(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, this.clusterName);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeContainersToDB() throws MappingDSGraphDBException {
        for (ContainerImpl cont : this.clusterContainers) {
            synchronizeContainerToDB(cont);
        }
    }

    private void synchronizeContainerToDB(ContainerImpl cont) throws MappingDSGraphDBException {
        if (this.clusterVertex != null && cont.getContainerID() != 0) {
            VertexQuery query = this.clusterVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            for (Vertex vertex : query.vertices()) {
                if ((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID) == cont.getContainerID()) {
                    return;
                }
            }
            Edge owns = MappingDSGraphDB.createEdge(this.clusterVertex, cont.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            owns.setProperty(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            MappingDSGraphDB.autocommit();
        }
    }

    @Override
    public void synchronizeFromDB() {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            synchronizeIDFromDB();
            synchronizeNameFromDB();
            synchronizeContainersFromDB();
        }
    }

    private void synchronizeIDFromDB() {
        if (this.clusterVertex != null) {
            this.clusterID = this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        }
    }

    private void synchronizeNameFromDB() {
        if (this.clusterVertex != null) {
            this.clusterName = this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY);
        }
    }

    private void synchronizeContainersFromDB() {
        if (this.clusterVertex != null) {
            VertexQuery query = this.clusterVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            this.clusterContainers.clear();
            for (Vertex vertex : query.vertices()) {
                ContainerImpl cont = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof ContainerImpl) {
                        cont = (ContainerImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a container.", entity.getElement().getId());
                    }
                }
                if (cont != null) {
                    this.clusterContainers.add(cont);
                }
            }
        }
    }

    private void removeContainerFromDB(ContainerImpl container) {
        if (this.clusterVertex!=null && container.getElement()!=null) {
            VertexQuery query = this.clusterVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.OUT).equals(container.getElement())) {
                    MappingDSGraphDB.getDDgraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClusterImpl tmp = (ClusterImpl) o;
        if (this.clusterName == null || this.clusterID == 0) {
            return super.equals(o);
        }
        return (clusterName.equals(tmp.getClusterName()) && clusterID == tmp.getClusterID());
    }

    @Override
    public int hashCode() {
        return clusterID != 0 ? new Long(clusterID).hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Cluster{ID='%d', name='%s'}", this.clusterID, this.clusterName);
    }
}