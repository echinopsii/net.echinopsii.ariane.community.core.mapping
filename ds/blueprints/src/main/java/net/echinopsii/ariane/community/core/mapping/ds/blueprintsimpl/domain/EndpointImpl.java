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
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBObjectProps;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import com.tinkerpop.blueprints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EndpointImpl implements Endpoint, MappingDSCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(EndpointImpl.class);

    private long endpointID = 0;
    private String endpointURL = null;
    private NodeImpl endpointParentNode = null;
    private HashMap<String, Object> endpointProperties = null;
    private Set<EndpointImpl> endpointTwinEndpoints = new HashSet<EndpointImpl>();

    private Vertex endpointVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public long getEndpointID() {
        return this.endpointID;
    }

    @Override
    public String getEndpointURL() {
        return this.endpointURL;
    }

    @Override
    public void setEndpointURL(String url) {
        if (this.endpointURL == null || this.endpointURL.equals(url)) {
            this.endpointURL = url;
            synchronizeURLToDB();
        }
    }

    @Override
    public NodeImpl getEndpointParentNode() {
        return this.endpointParentNode;
    }

    @Override
    public void setEndpointParentNode(Node node) {
        if (this.endpointParentNode == null || !this.endpointParentNode.equals(node)) {
            if (node instanceof NodeImpl || node instanceof GateImpl) {
                this.endpointParentNode = (NodeImpl) node;
                synchronizeParentNodeToDB();
                log.debug("Add endpoint parent node {} to endpoint {}", new Object[]{this.endpointParentNode.getNodeID(),
                                                                                            this.endpointID});
            }
        }
    }

    @Override
    public HashMap<String, Object> getEndpointProperties() {
        return endpointProperties;
    }

    @Override
    public void addEndpointProperty(String propertyKey, Object value) {
        if (propertyKey != null && value != null) {
            if (endpointProperties == null) {
                endpointProperties = new HashMap<String, Object>();
            }
            endpointProperties.put(propertyKey, value);
            synchronizePropertyToDB(propertyKey, value);
            log.debug("Set endpoint {} property : ({},{})", new Object[]{this.endpointID,
                                                                                propertyKey,
                                                                                this.endpointProperties.get(propertyKey)});
        }
    }

    @Override
    public void removeEndpointProperty(String propertyKey) {
        if (endpointProperties!=null) {
            endpointProperties.remove(propertyKey);
            removePropertyFromDB(propertyKey);
        }
    }

    @Override
    public Set<? extends Endpoint> getTwinEndpoints() {
        return this.endpointTwinEndpoints;
    }

    @Override
    public boolean addTwinEndpoint(Endpoint endpoint) {
        if (endpoint instanceof EndpointImpl) {
            boolean ret = false;
            try {
                ret = this.endpointTwinEndpoints.add((EndpointImpl) endpoint);
                if (ret) {
                    synchronizeTwinEndpointToDB((EndpointImpl) endpoint);
                }
            } catch (MappingDSGraphDBException E) {
                E.printStackTrace();
                log.error("Exception while adding twin node {}...", new Object[]{endpoint.getEndpointID()});
                this.endpointTwinEndpoints.remove((EndpointImpl) endpoint);
                MappingDSGraphDB.autorollback();
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeTwinEndpoint(Endpoint endpoint) {
        if (endpoint instanceof EndpointImpl) {
            boolean ret = this.endpointTwinEndpoints.remove((EndpointImpl) endpoint);
            if (ret) {
                removeTwinEndpointFromDB((EndpointImpl) endpoint);
            }
            return ret;
        } else {
            return false;
        }
    }

    public Vertex getElement() {
        return endpointVertex;
    }

    public void setElement(Element endpointVertex) {
        this.endpointVertex = (Vertex) endpointVertex;
        if (MappingDSGraphDB.isBlueprintsNeo4j() && this.endpointVertex instanceof Neo4j2Vertex)
            ((Neo4j2Vertex) this.endpointVertex).addLabel(MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE);
        this.endpointVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE);
        this.endpointID = this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        log.debug("Endpoint vertex has been initialized ({},{}).", new Object[]{this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                       this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    public void synchronizeToDB() throws MappingDSGraphDBException {
        synchronizeURLToDB();
        synchronizePropertiesToDB();
        synchronizeParentNodeToDB();
        synchronizeTwinEndpointsToDB();
    }

    private void synchronizeURLToDB() {
        if (endpointVertex != null && this.endpointURL != null) {
            log.debug("Synchronize endpoint URL {}...", new Object[]{this.endpointURL});
            endpointVertex.setProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, this.endpointURL);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizePropertiesToDB() {
        if (endpointProperties != null) {
            Iterator<String> iterK = this.endpointProperties.keySet().iterator();
            while (iterK.hasNext()) {
                String key = iterK.next();
                Object value = endpointProperties.get(key);
                synchronizePropertyToDB(key, value);
            }
        }
    }

    private void synchronizePropertyToDB(String key, Object value) {
        if (endpointVertex != null && key != null && value != null) {
            log.debug("Synchronize property {}...", new Object[]{key});
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyToDB(endpointVertex, key, value, MappingDSGraphPropertyNames.DD_ENDPOINT_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeParentNodeToDB() {
        if (endpointVertex != null && endpointParentNode != null && endpointParentNode.getElement() != null) {
            log.debug("Synchronize parent node {}...", new Object[]{endpointParentNode.getNodeID()});
            endpointVertex.setProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY,
                                              endpointParentNode.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTwinEndpointsToDB() throws MappingDSGraphDBException {
        if (this.endpointVertex != null) {
            Iterator<EndpointImpl> iterTE = this.endpointTwinEndpoints.iterator();
            while (iterTE.hasNext()) {
                EndpointImpl aTwin = iterTE.next();
                synchronizeTwinEndpointToDB(aTwin);
            }
        }
    }

    private void synchronizeTwinEndpointToDB(EndpointImpl twin) throws MappingDSGraphDBException {
        if (this.endpointVertex != null && twin.getElement() != null) {
            if (log.isTraceEnabled()) {
                for (String propKey : endpointVertex.getPropertyKeys()) {
                    log.trace("Vertex {} property {}: {}", new Object[]{endpointVertex.toString(),propKey,endpointVertex.getProperty(propKey).toString()});
                }
            }
            VertexQuery query = this.endpointVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Vertex vertex : query.vertices()) {
                if ((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID) == twin.getEndpointID()) {
                    return;
                }
            }
            log.debug("Synchronize endpoint twin endpoint {} to db...", new Object[]{twin.getEndpointID()});
            MappingDSGraphDB.createEdge(this.endpointVertex, twin.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            if (log.isTraceEnabled()) {
                for (String propKey : endpointVertex.getPropertyKeys()) {
                    log.trace("Vertex {} property {}: {}", new Object[]{endpointVertex.toString(),propKey,endpointVertex.getProperty(propKey).toString()});
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }

    public void synchronizeFromDB() {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            synchronizeIDFromDB();
            synchronizeURLFromDB();
            synchronizePropertiesFromDB();
            synchronizeParentNodeFromDB();
            synchronizeTwinEndpointsFromDB();
            isBeingSyncFromDB = false;
        }
    }

    private void synchronizeIDFromDB() {
        if (this.endpointVertex != null) {
            this.endpointID = this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        }
    }

    private void synchronizeURLFromDB() {
        if (endpointVertex != null) {
            endpointURL = endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY);
        }
    }

    private void synchronizePropertiesFromDB() {
        if (endpointVertex != null) {
            if (endpointProperties == null) {
                endpointProperties = new HashMap<String, Object>();
            } else {
                endpointProperties.clear();
            }
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(endpointVertex, endpointProperties, MappingDSGraphPropertyNames.DD_ENDPOINT_PROPS_KEY);
        }
    }

    private void removePropertyFromDB(String key) {
        if (endpointVertex != null) {
            log.debug("Remove endpoint property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(endpointVertex, key, MappingDSGraphPropertyNames.DD_ENDPOINT_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeParentNodeFromDB() {
        if (endpointVertex != null) {
            Object parentNodeID = endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY);
            if (parentNodeID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) parentNodeID);
                if (entity != null) {
                    if (entity instanceof NodeImpl || entity instanceof GateImpl) {
                        endpointParentNode = (NodeImpl) entity;
                    } else {
                        log.error("CACHE CONSISTENCY PROBLEM : entity {} is not a node.", new Object[]{parentNodeID});
                    }
                }
            }
        }
    }

    private void synchronizeTwinEndpointsFromDB() {
        if (this.endpointVertex != null) {
            VertexQuery query = endpointVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            this.endpointTwinEndpoints.clear();
            for (Vertex vertex : query.vertices()) {
                EndpointImpl twin = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof EndpointImpl) {
                        twin = (EndpointImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a node.", endpointID);
                    }
                }
                if (entity != null) {
                    this.endpointTwinEndpoints.add(twin);
                }
            }
        }
    }

    private void removeTwinEndpointFromDB(EndpointImpl endpoint) {
        if (this.endpointVertex != null && endpoint.getElement() != null) {
            VertexQuery query = this.endpointVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.OUT).equals(endpoint.getElement())) {
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

        EndpointImpl tmp = (EndpointImpl) o;
        if (this.endpointID == 0) {
            return super.equals(o);
        }
        return (this.endpointID == tmp.getEndpointID());
    }

    @Override
    public int hashCode() {
        return endpointVertex != null ? new Long(endpointID).hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Endpoint{ID='%d', URL='%s'}", this.endpointID, this.endpointURL);
    }
}