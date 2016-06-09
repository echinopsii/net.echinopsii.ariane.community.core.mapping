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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxCluster;
import com.tinkerpop.blueprints.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxClusterAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterImpl extends SProxClusterAbs implements MappingDSBlueprintsCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);

    private transient Vertex clusterVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public void setClusterName(String name) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setClusterName(session, name);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            // check change needs before requesting db operation
            if (super.getClusterName() == null || !super.getClusterName().equals(name)) {
                super.setClusterName(name);
                synchronizeNameToDB();
            }
        }
    }

    @Override
    public boolean addClusterContainer(Container container) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addClusterContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (container instanceof ContainerImpl) {
                try {
                    ret = super.addClusterContainer(container);
                    if (ret) {
                        container.setContainerCluster(this);
                        synchronizeContainerToDB((ContainerImpl) container);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding container {}...", new Object[]{container.getContainerID()});
                    super.removeClusterContainer(container);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
        return ret;
    }

    @Override
    public boolean removeClusterContainer(Container container) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeClusterContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (container instanceof ContainerImpl) {
                ret = super.removeClusterContainer(container);
                if (ret) {
                    container.setContainerCluster(null);
                    removeContainerFromDB((ContainerImpl) container);
                }
            }
        }
        return ret;
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
        super.setClusterID((String) this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        log.debug("Cluster vertex has been initialized ({},{}).", new Object[]{this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                               this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    @Override
    public String getEntityCacheID() {
        return "V" + super.getClusterID();
    }

    @Override
    public void synchronizeToDB() throws MappingDSException {
        synchronizeNameToDB();
        synchronizeContainersToDB();
    }

    private void synchronizeNameToDB() {
        if (this.clusterVertex != null && super.getClusterName() != null) {
            this.clusterVertex.setProperty(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, super.getClusterName());
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeContainersToDB() throws MappingDSException {
        for (Container cont : super.getClusterContainers()) {
            synchronizeContainerToDB((ContainerImpl)cont);
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
    public void synchronizeFromDB() throws MappingDSException {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            synchronizeIDFromDB();
            synchronizeNameFromDB();
            synchronizeContainersFromDB();
        }
    }

    private void synchronizeIDFromDB() {
        if (this.clusterVertex != null) {
            super.setClusterID((String)this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        }
    }

    private void synchronizeNameFromDB() throws MappingDSException {
        if (this.clusterVertex != null) {
            super.setClusterName((String)this.clusterVertex.getProperty(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY));
        }
    }

    private void synchronizeContainersFromDB() throws MappingDSException {
        if (this.clusterVertex != null) {
            VertexQuery query = this.clusterVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, true);
            super.getClusterContainers().clear();
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
                    super.addClusterContainer(cont);
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
                if (edge.getVertex(Direction.IN).equals(container.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }
}