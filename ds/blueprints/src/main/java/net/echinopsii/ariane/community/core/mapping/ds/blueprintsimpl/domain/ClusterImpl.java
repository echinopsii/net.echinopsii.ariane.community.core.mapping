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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain;

import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Vertex;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import com.tinkerpop.blueprints.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ClusterImpl implements Cluster, MappingDSBlueprintsCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);

    private String clusterID = null;
    private String clusterName = null;
    private Set<ContainerImpl> clusterContainers = new HashSet<ContainerImpl>();

    private transient Vertex clusterVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public String getClusterID() {
        return this.clusterID;
    }

    @Override
    public String getClusterName() {
        return this.clusterName;
    }

    final static String SET_CLUSTER_NAME = "setClusterName";

    @Override
    public void setClusterName(Session session, String name) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CLUSTER_NAME, new Object[]{name});
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

    final static String ADD_CLUSTER_CONTAINER = "addClusterContainer";

    @Override
    public boolean addClusterContainer(Session session, Container container) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean)session.execute(this, ADD_CLUSTER_CONTAINER, new Object[]{container});
        return ret;
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
            } catch (MappingDSException E) {
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

    static final String REMOVE_CLUSTER_CONTAINER = "removeClusterContainer";

    @Override
    public boolean removeClusterContainer(Session session, Container container) throws MappingDSException {
        boolean ret = false;
        if (session != null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_CLUSTER_CONTAINER, new Object[]{container});
        return ret;
    }

    @Override
    public boolean removeClusterContainer(Container container) {
        if (container instanceof ContainerImpl) {
            boolean ret = this.clusterContainers.remove(container);
            if (ret) {
                removeContainerFromDB((ContainerImpl)container);
                container.setContainerCluster(null);
            }
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
        if (MappingDSGraphDB.isBlueprintsNeo4j() && this.clusterVertex instanceof Neo4j2Vertex)
            ((Neo4j2Vertex) this.clusterVertex).addLabel(MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE);
        this.clusterVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE);
        this.clusterID = this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        log.debug("Cluster vertex has been initialized ({},{}).", new Object[]{this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                               this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    @Override
    public String getEntityCacheID() {
        return "V" + this.clusterID;
    }

    @Override
    public void synchronizeToDB() throws MappingDSException {
        synchronizeNameToDB();
        synchronizeContainersToDB();
    }

    private void synchronizeNameToDB() {
        if (this.clusterVertex != null && this.clusterName != null) {
            this.clusterVertex.setProperty(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, this.clusterName);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeContainersToDB() throws MappingDSException {
        for (ContainerImpl cont : this.clusterContainers) {
            synchronizeContainerToDB(cont);
        }
    }

    private void synchronizeContainerToDB(ContainerImpl cont) throws MappingDSException {
        if (this.clusterVertex != null && cont.getContainerID() != null && !cont.getContainerID().equals("")) {
            VertexQuery query = this.clusterVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_COMPOSEDBY_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            for (Vertex vertex : query.vertices())
                if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(cont.getContainerID()))
                    return;
            Edge owns = MappingDSGraphDB.createEdge(this.clusterVertex, cont.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_COMPOSEDBY_LABEL_KEY);
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
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
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
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_COMPOSEDBY_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.OUT).equals(container.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
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
        if (this.clusterName == null || this.clusterID == null) {
            return super.equals(o);
        }
        return (clusterName.equals(tmp.getClusterName()) && clusterID.equals(tmp.getClusterID()));
    }

    @Override
    public int hashCode() {
        return (clusterID != null && !clusterID.equals("")) ? clusterID.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Cluster{ID='%s', name='%s'}", this.clusterID, this.clusterName);
    }
}