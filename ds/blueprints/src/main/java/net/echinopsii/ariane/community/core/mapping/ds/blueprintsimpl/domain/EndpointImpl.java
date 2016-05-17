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
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBObjectProps;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import com.tinkerpop.blueprints.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EndpointImpl implements Endpoint, MappingDSBlueprintsCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(EndpointImpl.class);

    private String endpointID = null;
    private String endpointURL = null;
    private NodeImpl endpointParentNode = null;
    private HashMap<String, Object> endpointProperties = null;
    private Set<EndpointImpl> endpointTwinEndpoints = new HashSet<EndpointImpl>();

    private transient Vertex endpointVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public String getEndpointID() {
        return this.endpointID;
    }

    @Override
    public String getEndpointURL() {
        return this.endpointURL;
    }

    static final String SET_ENDPOINT_URL = "setEndpointURL";

    @Override
    public void setEndpointURL(Session session, String url) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_ENDPOINT_URL, new Object[]{url});
    }

    @Override
    public void setEndpointURL(String url) {
        if (this.endpointURL == null || !this.endpointURL.equals(url)) {
            this.endpointURL = url;
            synchronizeURLToDB();
        }
    }

    @Override
    public NodeImpl getEndpointParentNode() {
        return this.endpointParentNode;
    }

    static final String SET_ENDPOINT_PARENT_NODE = "setEndpointParentNode";

    @Override
    public void setEndpointParentNode(Session session, Node node) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_ENDPOINT_PARENT_NODE, new Object[]{node});
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

    static final String ADD_ENDPOINT_PROPERTY = "addEndpointProperty";

    @Override
    public void addEndpointProperty(Session session, String propertyKey, Object value) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ADD_ENDPOINT_PROPERTY, new Object[]{propertyKey, value});
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

    static final String REMOVE_ENDPOINT_PROPERTY = "removeEndpointProperty";

    @Override
    public void removeEndpointProperty(Session session, String propertyKey) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, REMOVE_ENDPOINT_PROPERTY, new Object[]{propertyKey});
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

    static final String ADD_TWIN_ENDPOINT = "addTwinEndpoint";

    @Override
    public boolean addTwinEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_TWIN_ENDPOINT, new Object[]{endpoint});
        return ret;
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
            } catch (MappingDSException E) {
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

    static final String REMOVE_TWIN_ENDPOINT = "removeTwinEndpoint";

    @Override
    public boolean removeTwinEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_TWIN_ENDPOINT, new Object[]{endpoint});
        return ret;
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

    @Override
    public String getEntityCacheID() {
        return "V" + this.endpointID;
    }

    public void synchronizeToDB() throws MappingDSException {
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

    private void synchronizeTwinEndpointsToDB() throws MappingDSException {
        if (this.endpointVertex != null) {
            Iterator<EndpointImpl> iterTE = this.endpointTwinEndpoints.iterator();
            while (iterTE.hasNext()) {
                EndpointImpl aTwin = iterTE.next();
                synchronizeTwinEndpointToDB(aTwin);
            }
        }
    }

    private void synchronizeTwinEndpointToDB(EndpointImpl twin) throws MappingDSException {
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
                if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(twin.getEndpointID())) {
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
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) parentNodeID);
                if (entity != null) {
                    if (entity instanceof NodeImpl || entity instanceof GateImpl) {
                        endpointParentNode = (NodeImpl) entity;
                    } else {
                        log.error("CACHE CONSISTENCY PROBLEM : entity {} is not a node.", new Object[]{parentNodeID});
                        log.error(entity.getClass().toString());
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
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof EndpointImpl) {
                        twin = (EndpointImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a endpoint.", endpointID);
                        log.error(entity.getClass().toString());
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
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Edge edge : query.edges()) {
                Vertex vo = edge.getVertex(Direction.OUT);
                Vertex vi = edge.getVertex(Direction.IN);
                if (vo != null && vo.equals(endpoint.getElement()))
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                if (vi != null && vi.equals(endpoint.getElement()))
                    MappingDSGraphDB.getGraph().removeEdge(edge);
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
        if (this.endpointID ==null) {
            return super.equals(o);
        }
        return (this.endpointID.equals(tmp.getEndpointID()));
    }

    @Override
    public int hashCode() {
        return (endpointID != null && !endpointID.equals("")) ? endpointID.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Endpoint{ID='%s', URL='%s'}", this.endpointID, this.endpointURL);
    }
}