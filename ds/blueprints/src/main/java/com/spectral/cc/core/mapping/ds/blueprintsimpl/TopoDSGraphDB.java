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

package com.spectral.cc.core.mapping.ds.blueprintsimpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.cfg.TopoDSCfgLoader;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.*;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TopoDSGraphDB {

    private final static String BLUEPRINTS_IMPL_ODB = "OrientGraph";

    private final static Logger log = LoggerFactory.getLogger(TopoDSGraphDB.class);

    private static Graph ddgraph = null;
    private static Vertex idmanager = null;

    private static HashMap<Long, Boolean> autocommit = new HashMap<Long, Boolean>();

    public static boolean init(Dictionary<Object, Object> properties) throws JsonParseException, JsonMappingException, IOException {
        if (properties != null) {
            return TopoDSCfgLoader.load(properties);
        } else {
            return TopoDSCfgLoader.load();
        }
    }

    public static boolean start() {
        if (TopoDSCfgLoader.getDefaultCfgEntity() != null && TopoDSCfgLoader.getDefaultCfgEntity().getBlueprintsURL() != null) {
            String impl = TopoDSCfgLoader.getDefaultCfgEntity().getBlueprintsImplementation();
            switch (impl) {
                case BLUEPRINTS_IMPL_ODB:
                    String url = TopoDSCfgLoader.getDefaultCfgEntity().getBlueprintsURL();
                    String user = TopoDSCfgLoader.getDefaultCfgEntity().getBlueprintsUser();
                    String pwd = TopoDSCfgLoader.getDefaultCfgEntity().getBlueprintsPassword();
                    if (user != null && pwd != null) {
                        ddgraph = new OrientGraph(url, user, pwd);
                        log.debug("Connected to OrientDB ({}@{})", new Object[]{user, url});
                    } else {
                        ddgraph = new OrientGraph(url);
                        log.debug("Connected to OrientDB ({})", new Object[]{url});
                    }
                    OGlobalConfiguration.CACHE_LEVEL1_ENABLED.setValue(false);
                    log.info("{} is started!", new Object[]{ddgraph.toString()});
                    log.info(ddgraph.getFeatures().toString());
                    break;
                default:
                    log.error("This target MappingDS blueprints implementation {} is not managed by MappingDS Blueprints !", new Object[]{impl});
                    log.error("List of valid target MappingDS blueprints implementation : {}", new Object[]{BLUEPRINTS_IMPL_ODB});
                    return false;
            }

            if (ddgraph instanceof KeyIndexableGraph) {
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_NODE_NAME_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_GATE_PAEP_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, Vertex.class);
                ((KeyIndexableGraph) ddgraph).createKeyIndex(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID, Edge.class);
            }
            idmanager = ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, (long) 0).iterator().hasNext() ?
                                ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, (long) 0).iterator().next() : null;
            if (idmanager == null) {
                log.info("Initialize CC Mapping Blueprints DB...");
                idmanager = ddgraph.addVertex(null);
                idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, (long) 0);
                idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY, (long) 0);
                idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY, (long) 0);
                autocommit();
            }
            return true;
        } else {
            return false;
        }
    }

    public static void stop() {
        try {
            TopoDSCache.synchronize();
        } catch (TopoDSGraphDBException E) {
            String msg = "Exception while synchronizing TopoDSCache...";
            E.printStackTrace();
            log.error(msg);
        } finally {
            String ddgraphinfo = ddgraph.toString();
            ddgraph.shutdown();
            log.info("{} is stopped!", new Object[]{ddgraphinfo});
        }
    }

    public static synchronized void setAutocommit(boolean autocommit) {
        Long threadID = Thread.currentThread().getId();
        log.debug("Autocommit mode is {} for thread {}", new Object[]{(autocommit ? "activated" : "deactivated"), Thread.currentThread().getName()});
        TopoDSGraphDB.autocommit.put(threadID, autocommit);
    }

    public static void autocommit() {
        if (ddgraph instanceof TransactionalGraph) {
            Long threadID = Thread.currentThread().getId();
            boolean isThreadWithAutoCommitMode = true;
            if (autocommit.containsKey(threadID)) {
                isThreadWithAutoCommitMode = autocommit.get(threadID);
            }
            if (isThreadWithAutoCommitMode) {
                log.debug("Auto commit operation...");
                ((TransactionalGraph) ddgraph).commit();
            }
        }
    }

    public static void commit() {
        if (ddgraph instanceof TransactionalGraph) {
            log.debug("Commit operation...");
            ((TransactionalGraph) ddgraph).commit();
        }
    }

    public static void autorollback() {
        if (ddgraph instanceof TransactionalGraph) {
            Long threadID = Thread.currentThread().getId();
            boolean isThreadWithAutoCommitMode = true;
            if (autocommit.containsKey(threadID)) {
                isThreadWithAutoCommitMode = autocommit.get(threadID);
            }
            if (isThreadWithAutoCommitMode) {
                log.error("Auto rollback operation...");
                ((TransactionalGraph) ddgraph).rollback();
            }
        }
    }

    public static void rollback() {
        if (ddgraph instanceof TransactionalGraph) {
            log.error("Rollback operation...");
            ((TransactionalGraph) ddgraph).rollback();
        }
    }

    public static Graph getDDgraph() {
        return ddgraph;
    }

    private static synchronized long incrementVertexMaxCursor() throws TopoDSGraphDBException {
        try {
            long countProp = idmanager.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
            countProp++;
            idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY, countProp++);
        } catch (Exception E) {
            String msg = "Exception while incrementing vertex max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
        return getVertexMaxCursor();
    }

    private static synchronized void decrementVertexMaxCursor() throws TopoDSGraphDBException {
        try {
            long countProp = idmanager.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
            countProp--;
            idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY, countProp--);
        } catch (Exception E) {
            String msg = "Exception catched while decrementing vertex max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
    }

    public static synchronized long getVertexMaxCursor() {
        return idmanager.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
    }

    private static synchronized long incrementEdgeMaxCursor() throws TopoDSGraphDBException {
        try {
            long countProp = idmanager.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
            countProp++;
            idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY, countProp);
        } catch (Exception E) {
            String msg = "Exception catched while incrementing edge max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
        return getEdgeMaxCursor();
    }

    private static synchronized void decrementEdgeMaxCursor() throws TopoDSGraphDBException {
        try {
            long countProp = idmanager.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
            countProp--;
            idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY, countProp);
        } catch (Exception E) {
            String msg = "Exception catched while decrementing edge max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
    }

    public static synchronized long getEdgeMaxCursor() {
        return idmanager.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
    }

    private static synchronized void addVertexFreeID(long id) throws TopoDSGraphDBException {
        try {
            idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_FREE_IDS_KEY + id, id);
        } catch (Exception E) {
            String msg = "Exception catched while adding vertex free ID " + id + "...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
    }

    private static synchronized boolean hasVertexFreeID() {
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_FREE_IDS_KEY)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized long consumeVertexFreeID() {
        long ret = 0;
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_FREE_IDS_KEY)) {
                ret = idmanager.getProperty(key);
                idmanager.removeProperty(key);
                break;
            }
        }
        return ret;
    }

    private static synchronized void addEdgeFreeID(long id) throws TopoDSGraphDBException {
        try {
            idmanager.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_FREE_IDS_KEY + id, id);
        } catch (Exception E) {
            String msg = "Exception catched while adding edge free ID " + id + "...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
    }

    private static synchronized boolean hasEdgeFreeID() {
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_FREE_IDS_KEY)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized long consumeEdgeFreeID() {
        long ret = 0;
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_FREE_IDS_KEY)) {
                ret = idmanager.getProperty(key);
                idmanager.removeProperty(key);
                break;
            }
        }
        return ret;
    }

    public static TopoDSCacheEntity saveVertexEntity(TopoDSCacheEntity entity) {
        Vertex entityV = null;
        long id = 0;
        try {
            if (!hasVertexFreeID()) {
                id = incrementVertexMaxCursor();
            } else {
                id = consumeVertexFreeID();
            }
            entityV = ddgraph.addVertex(null);
            entityV.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id);
            entity.setElement(entityV);
            TopoDSCache.putEntityToCache(entity);
            entity.synchronizeToDB();
            autocommit();
            log.debug("Vertex {} has been saved on graph {}", new Object[]{id, ddgraph.toString() + "(" + ddgraph.hashCode() + ")"});
        } catch (Exception E) {
            log.error("Exception catched while saving vertex " + id + ".");
            E.printStackTrace();
            autorollback();
        }
        return entity;
    }

    public static Edge createEdge(Vertex source, Vertex destination, String label) throws TopoDSGraphDBException {
        Edge edge = null;
        long id = 0;
        try {
            if (!hasEdgeFreeID()) {
                id = incrementEdgeMaxCursor();
            } else {
                id = consumeEdgeFreeID();
            }
            edge = ddgraph.addEdge(null, source, destination, label);
            edge.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id);
            autocommit();
            log.debug("Edge {} has been saved on graph {}", new Object[]{id, ddgraph.toString() + "(" + ddgraph.hashCode() + ")"});
        } catch (Exception E) {
            String msg = "Exception catched while saving edge " + id + ".";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new TopoDSGraphDBException(msg);
        }
        return edge;
    }

    public static TopoDSCacheEntity saveEdgeEntity(TopoDSCacheEntity entity, Vertex source, Vertex destination, String label) {
        try {
            Edge entityE = createEdge(source, destination, label);
            entity.setElement(entityE);
            TopoDSCache.putEntityToCache(entity);
            entity.synchronizeToDB();
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while saving edge...");
            E.printStackTrace();
            autorollback();
        }
        return entity;
    }

    public static TopoDSCacheEntity getVertexEntity(long id) {
        log.debug("Get cache entity {} if exists ...", new Object[]{"V"+id});
        TopoDSCacheEntity ret = TopoDSCache.getCachedEntity("V" + id);
        if (ret == null) {
            log.debug("Get vertex {} from graph {}...", new Object[]{id, ddgraph.toString() + "(" + ddgraph.hashCode() + ")"});
            Vertex vertex = (ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id).iterator().hasNext() ?
                                     ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id).iterator().next() : null);
            if (vertex != null) {
                String vertexType = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                if (vertexType != null) {
                    switch (vertexType) {
                        case TopoDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE:
                            ret = new ClusterImpl();
                            break;
                        case TopoDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE:
                            ret = new ContainerImpl();
                            break;
                        case TopoDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                            ret = new NodeImpl();
                            break;
                        case TopoDSGraphPropertyNames.DD_TYPE_GATE_VALUE:
                            ret = new GateImpl();
                            break;
                        case TopoDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                            ret = new EndpointImpl();
                            break;
                        case TopoDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                            ret = new TransportImpl();
                            break;
                        default:
                            break;
                    }
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    TopoDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
        } else {
            log.debug("Entity returned from cache {}", new Object[]{ret.toString()});
        }
        return ret;
    }

    public static Set<ContainerImpl> getContainers() {
        Set<ContainerImpl> ret = new HashSet<ContainerImpl>();
        log.debug("Get all containers from graph {}...", new Object[]{ddgraph.toString() + "(" + ddgraph.hashCode() + ")"});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            ContainerImpl tmp = (ContainerImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new ContainerImpl();
                tmp.setElement(vertex);
                TopoDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add container {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        return ret;
    }

    public static Set<NodeImpl> getNodes() {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        log.debug("Get all nodes from graph {}...", new Object[]{ddgraph.toString()});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            NodeImpl tmp = (NodeImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new NodeImpl();
                tmp.setElement(vertex);
                TopoDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add node {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        return ret;
    }

    public static Set<NodeImpl> getNodes(String key, Object value) {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        log.debug("Get all nodes from graph {}...", new Object[]{ddgraph.toString()});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            NodeImpl tmp = (NodeImpl) getVertexEntity(id);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value)) {
                log.debug("Add node {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        return ret;
    }

    public static Set<GateImpl> getGates() {
        Set<GateImpl> ret = new HashSet<GateImpl>();
        log.debug("Get all gates from graph {}...", new Object[]{ddgraph.toString()});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            GateImpl tmp = (GateImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new GateImpl();
                tmp.setElement(vertex);
                TopoDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add gate {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        return ret;
    }

    public static Set<GateImpl> getGates(String key, Object value) {
        Set<GateImpl> ret = new HashSet<GateImpl>();
        log.debug("Get all gates from graph {}...", new Object[]{ddgraph.toString()});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            GateImpl tmp = (GateImpl) getVertexEntity(id);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value)) {
                log.debug("Add gate {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        return ret;
    }

    public static Set<EndpointImpl> getEndpoints() {
        Set<EndpointImpl> ret = new HashSet<EndpointImpl>();
        log.debug("Get all endpoints from graph {}...", new Object[]{ddgraph.toString()});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            EndpointImpl tmp = (EndpointImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new EndpointImpl();
                tmp.setElement(vertex);
                TopoDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add endpoint {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        return ret;
    }

    public static Set<EndpointImpl> getEndpoints(String key, Object value) {
        Set<EndpointImpl> ret = new HashSet<EndpointImpl>();
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            log.debug("Test vertex {}...", new Object[]{id});
            EndpointImpl tmp = (EndpointImpl) getVertexEntity(id);
            Object tmpValue = (tmp.getEndpointProperties() != null) ? tmp.getEndpointProperties().get(key) : null;
            if (tmpValue != null && tmpValue.equals(value)) {
                log.debug("Add endpoint {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        return ret;
    }

    public static Set<TransportImpl> getTransports() {
        Set<TransportImpl> ret = new HashSet<TransportImpl>();
        log.debug("Get all transports from graph {}...", new Object[]{ddgraph.toString()});
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        TopoDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)) {
            long id = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            TransportImpl tmp = (TransportImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new TransportImpl();
                tmp.setElement(vertex);
                TopoDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add transport {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        return ret;
    }

    public static ClusterImpl getIndexedCluster(String clusterName) {
        TopoDSCacheEntity ret = TopoDSCache.getClusterFromCache(clusterName);
        if (ret == null) {
            Vertex vertex = ddgraph.getVertices(TopoDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, clusterName).iterator().hasNext() ?
                                    ddgraph.getVertices(TopoDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, clusterName).iterator().next() : null;
            if (vertex != null) {
                String vertexType = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                switch (vertexType) {
                    case TopoDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE:
                        ret = new ClusterImpl();
                        break;
                    default:
                        break;
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    TopoDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
        }
        return (ClusterImpl) ret;
    }

    public static Set<NodeImpl> getIndexedNodes(String name) {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        for (Vertex vertex : ddgraph.getVertices(TopoDSGraphPropertyNames.DD_NODE_NAME_KEY, name)) {
            String vertexType = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
            NodeImpl tmp = null;
            switch (vertexType) {
                case TopoDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                    tmp = new NodeImpl();
                    break;
                default:
                    break;
            }
            if (tmp != null) {
                tmp.setElement(vertex);
                TopoDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
                ret.add(tmp);
            }
        }
        return ret;
    }

    public static EndpointImpl getIndexedEndpoint(String url) {
        TopoDSCacheEntity ret = TopoDSCache.getEndpointFromCache(url);
        if (ret == null && ddgraph != null) {
            Vertex vertex = ddgraph.getVertices(TopoDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, url).iterator().hasNext() ?
                                    ddgraph.getVertices(TopoDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, url).iterator().next() : null;
            if (vertex != null) {
                String vertexType = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                switch (vertexType) {
                    case TopoDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                        ret = new EndpointImpl();
                    default:
                        break;
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    TopoDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
        }
        return (EndpointImpl) ret;
    }

    public static TransportImpl getIndexedTransport(String transportName) {
        TopoDSCacheEntity ret = TopoDSCache.getTransportFromCache(transportName);
        if (ret == null && ddgraph != null) {
            Vertex vertex = ddgraph.getVertices(TopoDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, transportName).iterator().hasNext() ?
                                    ddgraph.getVertices(TopoDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, transportName).iterator().next() : null;
            if (vertex != null) {
                String vertexType = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                switch (vertexType) {
                    case TopoDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                        ret = new TransportImpl();
                    default:
                        break;
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    TopoDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
        }
        return (TransportImpl) ret;
    }

    public static TopoDSCacheEntity getLink(long id) {
        TopoDSCacheEntity ret = TopoDSCache.getCachedEntity("E" + id);
        if (ret == null && ddgraph != null) {
            Edge edge = ddgraph.getEdges(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id).iterator().hasNext() ?
                                ddgraph.getEdges(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id).iterator().next() : null;
            if (edge != null && edge.getLabel().equals(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                TopoDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        return ret;
    }

    public static Set<LinkImpl> getLinks() {
        Set<LinkImpl> ret = new HashSet<LinkImpl>();
        for (Edge edge : ddgraph.getEdges()) {
            if (edge.getLabel().equals(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                long id = edge.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
                LinkImpl tmp = (LinkImpl) getLink(id);
                if (tmp == null) {
                    tmp = new LinkImpl();
                    tmp.setElement(edge);
                    TopoDSCache.putEntityToCache(tmp);
                    tmp.synchronizeFromDB();
                }
                ret.add(tmp);
            }
        }
        return ret;
    }

    public static void deleteEntity(TopoDSCacheEntity entity) {
        Element elem = entity.getElement();
        try {
            if (elem != null) {
                if (elem instanceof Vertex) {
                    Vertex vertex = (Vertex) elem;
                    for (Edge edge : vertex.getEdges(Direction.BOTH, TopoDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                        long edgeID = (long) edge.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
                        ddgraph.removeEdge(edge);
                        if (edgeID == getEdgeMaxCursor()) {
                            decrementEdgeMaxCursor();
                        } else {
                            addEdgeFreeID(edgeID);
                        }
                    }
                    long vertexID = (long) vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
                    ddgraph.removeVertex(vertex);
                    if (vertexID == getVertexMaxCursor()) {
                        decrementVertexMaxCursor();
                    } else {
                        addVertexFreeID(vertexID);
                    }
                } else if (elem instanceof Edge) {
                    Edge edge = (Edge) elem;
                    long edgeID = (long) edge.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
                    ddgraph.removeEdge(edge);
                    if (edgeID == getEdgeMaxCursor()) {
                        decrementEdgeMaxCursor();
                    } else {
                        addEdgeFreeID(edgeID);
                    }
                }
            }
            TopoDSCache.removeEntityFromCache(entity);
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while deleting entity " + entity.getElement().getId() + "...");
            E.printStackTrace();
            autorollback();
        }
    }

    public static void clear() {
        try {
            for (Edge edge : ddgraph.getEdges()) {
                ddgraph.removeEdge(edge);
            }
            for (Vertex vertex : ddgraph.getVertices()) {
                ddgraph.removeVertex(vertex);
            }
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while clearing DB Graph...");
            E.printStackTrace();
            autorollback();
        }
    }
}