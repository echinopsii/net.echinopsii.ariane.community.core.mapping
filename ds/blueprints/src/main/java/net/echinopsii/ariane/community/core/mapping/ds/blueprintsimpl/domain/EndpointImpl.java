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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBObjectProps;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import com.tinkerpop.blueprints.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpointAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointImpl extends SProxEndpointAbs implements SProxEndpoint, MappingDSBlueprintsCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(EndpointImpl.class);

    private transient Vertex endpointVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public void setEndpointURL(String url) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setEndpointURL(session, url);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getEndpointURL() == null || !super.getEndpointURL().equals(url)) {
                super.setEndpointURL(url);
                synchronizeURLToDB();
            }
        }
    }

    @Override
    public void setEndpointParentNode(Node node) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setEndpointParentNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getEndpointParentNode() == null || !super.getEndpointParentNode().equals(node)) {
                if (node instanceof NodeImpl) {
                    Node previousParentNode = super.getEndpointParentNode();
                    super.setEndpointParentNode(node);
                    node.addEndpoint(this);
                    if (previousParentNode!=null && previousParentNode.getNodeEndpoints().contains(this))
                        previousParentNode.removeEndpoint(this);
                    synchronizeParentNodeToDB();
                    log.debug("Add endpoint parent node {} to endpoint {}", new Object[]{super.getEndpointParentNode().getNodeID(),
                            super.getEndpointID()});
                } else if (node == null) {
                    Node previousParentNode = super.getEndpointParentNode();
                    super.setEndpointParentNode(null);
                    synchronizeParentNodeToDB();
                    if (previousParentNode!=null && previousParentNode.getNodeEndpoints().contains(this))
                        previousParentNode.removeEndpoint(this);
                    if ((previousParentNode!=null && !((NodeImpl)previousParentNode).isBeingDeleted())) {
                        log.info("Endpoint " + this.toString() + " has no more parent node. This state should be avoided.");
                        log.info("Activate debug logs if you want to investigate on this unstable state...");
                        log.debug("trace last calls : \n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}\n\t{}",
                                new Object[]{
                                        (Thread.currentThread().getStackTrace().length > 0) ? Thread.currentThread().getStackTrace()[0].getClassName() + "." + Thread.currentThread().getStackTrace()[0].getMethodName() + " - " + Thread.currentThread().getStackTrace()[0].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 1) ? Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + " - " + Thread.currentThread().getStackTrace()[1].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 2) ? Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + Thread.currentThread().getStackTrace()[2].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 3) ? Thread.currentThread().getStackTrace()[3].getClassName() + "." + Thread.currentThread().getStackTrace()[3].getMethodName() + " - " + Thread.currentThread().getStackTrace()[3].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 4) ? Thread.currentThread().getStackTrace()[4].getClassName() + "." + Thread.currentThread().getStackTrace()[4].getMethodName() + " - " + Thread.currentThread().getStackTrace()[4].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 5) ? Thread.currentThread().getStackTrace()[5].getClassName() + "." + Thread.currentThread().getStackTrace()[5].getMethodName() + " - " + Thread.currentThread().getStackTrace()[5].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 6) ? Thread.currentThread().getStackTrace()[6].getClassName() + "." + Thread.currentThread().getStackTrace()[6].getMethodName() + " - " + Thread.currentThread().getStackTrace()[6].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 7) ? Thread.currentThread().getStackTrace()[7].getClassName() + "." + Thread.currentThread().getStackTrace()[7].getMethodName() + " - " + Thread.currentThread().getStackTrace()[7].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 8) ? Thread.currentThread().getStackTrace()[8].getClassName() + "." + Thread.currentThread().getStackTrace()[8].getMethodName() + " - " + Thread.currentThread().getStackTrace()[8].getLineNumber() : "",
                                        (Thread.currentThread().getStackTrace().length > 9) ? Thread.currentThread().getStackTrace()[9].getClassName() + "." + Thread.currentThread().getStackTrace()[9].getMethodName() + " - " + Thread.currentThread().getStackTrace()[9].getLineNumber() : "",
                                });
                    }
                }
            }
        }
    }

    @Override
    public void addEndpointProperty(String propertyKey, Object value) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.addEndpointProperty(session, propertyKey, value);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (propertyKey != null && value != null) {
                super.addEndpointProperty(propertyKey, value);
                synchronizePropertyToDB(propertyKey, value);
                log.debug("Set endpoint {} property : ({},{})", new Object[]{super.getEndpointID(),
                        propertyKey,
                        super.getEndpointProperties().get(propertyKey)});
            }
        }
    }

    @Override
    public void removeEndpointProperty(String propertyKey) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.removeEndpointProperty(session, propertyKey);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            super.removeEndpointProperty(propertyKey);
            removePropertyFromDB(propertyKey);
        }
    }

    @Override
    public boolean addTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addTwinEndpoint(session, endpoint);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (endpoint instanceof EndpointImpl) {
                try {
                    ret = super.addTwinEndpoint(endpoint);
                    if (ret) {
                        if (!endpoint.getTwinEndpoints().contains(this)) endpoint.addTwinEndpoint(this);
                        synchronizeTwinEndpointToDB((EndpointImpl) endpoint);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding twin node {}...", new Object[]{endpoint.getEndpointID()});
                    super.removeTwinEndpoint(endpoint);
                    MappingDSGraphDB.autorollback();
                }
                return ret;
            }
        }
        return ret;
    }

    @Override
    public boolean removeTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeTwinEndpoint(session, endpoint);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        }
        if (endpoint instanceof EndpointImpl) {
            ret = super.removeTwinEndpoint(endpoint);
            if (ret) {
                if (endpoint.getTwinEndpoints().contains(this)) endpoint.removeTwinEndpoint(this);
                removeTwinEndpointFromDB((EndpointImpl) endpoint);
            }
        }
        return ret;
    }

    public Vertex getElement() {
        return endpointVertex;
    }

    public void setElement(Element endpointVertex) {
        this.endpointVertex = (Vertex) endpointVertex;
        if (MappingDSGraphDB.isBlueprintsNeo4j() && this.endpointVertex instanceof Neo4j2Vertex)
            ((Neo4j2Vertex) this.endpointVertex).addLabel(MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE);
        this.endpointVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE);
        super.setEndpointID((String) this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        log.debug("Endpoint vertex has been initialized ({},{}).", new Object[]{this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                       this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    @Override
    public String getEntityCacheID() {
        return "V" + super.getEndpointID();
    }

    public void synchronizeToDB() throws MappingDSException {
        synchronizeURLToDB();
        synchronizePropertiesToDB();
        synchronizeParentNodeToDB();
        synchronizeTwinEndpointsToDB();
    }

    private void synchronizeURLToDB() {
        if (endpointVertex != null && super.getEndpointURL() != null) {
            log.debug("Synchronize endpoint URL {}...", new Object[]{super.getEndpointURL()});
            endpointVertex.setProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, super.getEndpointURL());
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizePropertiesToDB() {
        if (super.getEndpointProperties() != null)
            for (String key : super.getEndpointProperties().keySet()) {
                Object value = super.getEndpointProperties().get(key);
                synchronizePropertyToDB(key, value);
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
        if (endpointVertex != null && super.getEndpointParentNode() != null && ((NodeImpl)super.getEndpointParentNode()).getElement() != null) {
            log.debug("Synchronize parent node {}...", new Object[]{super.getEndpointParentNode().getNodeID()});
            endpointVertex.setProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY,
                    ((NodeImpl) super.getEndpointParentNode()).getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            MappingDSGraphDB.autocommit();
        } else if (endpointVertex != null && super.getEndpointParentNode() == null && endpointVertex.getPropertyKeys().contains(MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY)) {
            endpointVertex.removeProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTwinEndpointsToDB() throws MappingDSException {
        if (this.endpointVertex != null)
            for (Endpoint aTwin : super.getTwinEndpoints()) synchronizeTwinEndpointToDB((EndpointImpl) aTwin);
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

    public void synchronizeFromDB() throws MappingDSException {
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
        if (this.endpointVertex != null) super.setEndpointID((String) this.endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
    }

    private void synchronizeURLFromDB() throws MappingDSException {
        if (endpointVertex != null) super.setEndpointURL((String) endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY));
    }

    private void synchronizePropertiesFromDB() {
        if (endpointVertex != null) {
            super.getEndpointProperties().clear();
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(endpointVertex, super.getEndpointProperties(), MappingDSGraphPropertyNames.DD_ENDPOINT_PROPS_KEY);
        }
    }

    private void removePropertyFromDB(String key) {
        if (endpointVertex != null) {
            log.debug("Remove endpoint property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(endpointVertex, key, MappingDSGraphPropertyNames.DD_ENDPOINT_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeParentNodeFromDB() throws MappingDSException {
        if (endpointVertex != null) {
            Object parentNodeID = endpointVertex.getProperty(MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY);
            if (parentNodeID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) parentNodeID);
                if (entity != null) {
                    if (entity instanceof NodeImpl) super.setEndpointParentNode((NodeImpl) entity);
                    else {
                        log.error("CACHE CONSISTENCY PROBLEM : entity {} is not a node.", new Object[]{parentNodeID});
                        log.error(entity.getClass().toString());
                    }
                }
            }
        }
    }

    private void synchronizeTwinEndpointsFromDB() throws MappingDSException {
        if (this.endpointVertex != null) {
            VertexQuery query = endpointVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            super.getTwinEndpoints().clear();
            for (Vertex vertex : query.vertices()) {
                EndpointImpl twin = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof EndpointImpl) {
                        twin = (EndpointImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a endpoint.", super.getEndpointID());
                        log.error(entity.getClass().toString());
                    }
                }
                if (entity != null) super.addTwinEndpoint(twin);
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
                if ((vo != null && vo.equals(endpoint.getElement())) || (vi != null && vi.equals(endpoint.getElement())))
                    MappingDSGraphDB.getGraph().removeEdge(edge);
            }
            MappingDSGraphDB.autocommit();
        }
    }
}