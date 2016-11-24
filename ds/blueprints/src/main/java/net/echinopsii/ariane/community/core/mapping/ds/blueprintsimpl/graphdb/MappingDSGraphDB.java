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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCache;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cfg.MappingBlueprintsDSCfgLoader;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.MapperExecutor;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.SelectorExecutor;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

public class MappingDSGraphDB {

    private final static String BLUEPRINTS_IMPL_N4J = "Neo4j";

    private final static Logger log = MomLoggerFactory.getLogger(MappingDSGraphDB.class);

    private static String blpImpl                                    = null;
    private static MappingDSGraphDBNeo4jBootstrapper neoBootstrapper = null;
    private static Graph  ccgraph                                    = null;
    private static MapperExecutor mexecutor                          = null;
    private static SelectorExecutor sexecutor                        = null;

    private static HashMap<Long, Session> threadSessionRegistry = new HashMap<>();

    public static boolean isBlueprintsNeo4j() {
        return (blpImpl.equals(BLUEPRINTS_IMPL_N4J));
    }

    public static boolean init(Dictionary<Object, Object> properties) throws IOException {
        return properties != null && MappingBlueprintsDSCfgLoader.load(properties);
    }

    public static boolean start() {
        if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity() != null && (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsURL() != null ||
            MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath()!=null ||
            MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsNeoConfigFile()!=null)) {
            blpImpl = MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsImplementation();
            switch (blpImpl) {
                case BLUEPRINTS_IMPL_N4J:
                    GraphDatabaseService graphDb   = null;
                    if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath() != null) {
                        String  graphPath = MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath();
                        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( graphPath );
                    } else if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsNeoConfigFile() != null) {
                        String neo4jConfigFilePath = MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsNeoConfigFile();
                        neoBootstrapper = new MappingDSGraphDBNeo4jBootstrapper().start(neo4jConfigFilePath);
                        graphDb = neoBootstrapper.getDatabase();
                    }
                    if (graphDb!=null) {
                        ccgraph = new Neo4j2Graph(graphDb);
                        mexecutor = new MapperExecutor(graphDb);
                        sexecutor = new SelectorExecutor(ccgraph);
                        log.debug("{} is started", new Object[]{ccgraph.toString()});
                        log.debug(ccgraph.getFeatures().toString());
                    } else {
                        log.error("Unable to init Neo4J graph DB !");
                        return false;
                    }
                    break;
                default:
                    log.error("This target MappingDS blueprints implementation {} is not managed by MappingDS Blueprints !", new Object[]{blpImpl});
                    log.error("List of valid target MappingDS blueprints implementation : {}, {}", new Object[]{BLUEPRINTS_IMPL_N4J});
                    return false;
            }

            if (blpImpl.equals(BLUEPRINTS_IMPL_N4J)) {
                mexecutor.execute("CYPHER create index on:cluster(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ")");
                mexecutor.execute("CYPHER create index on:cluster(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + ")");
                mexecutor.execute("CYPHER create index on:cluster(" + MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY + ")");

                mexecutor.execute("CYPHER create index on:container(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ")");
                mexecutor.execute("CYPHER create index on:container(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + ")");
                mexecutor.execute("CYPHER create index on:container(" + MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY + ")");

                mexecutor.execute("CYPHER create index on:node(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ")");
                mexecutor.execute("CYPHER create index on:node(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + ")");
                mexecutor.execute("CYPHER create index on:node(" + MappingDSGraphPropertyNames.DD_NODE_NAME_KEY + ")");

                mexecutor.execute("CYPHER create index on:gate(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ")");
                mexecutor.execute("CYPHER create index on:gate(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + ")");
                mexecutor.execute("CYPHER create index on:gate(" + MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY + ")");
                mexecutor.execute("CYPHER create index on:gate(" + MappingDSGraphPropertyNames.DD_NODE_NAME_KEY + ")");

                mexecutor.execute("CYPHER create index on:endpoint(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ")");
                mexecutor.execute("CYPHER create index on:endpoint(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + ")");
                mexecutor.execute("CYPHER create index on:endpoint(" + MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY + ")");

                mexecutor.execute("CYPHER create index on:transport(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ")");
                mexecutor.execute("CYPHER create index on:transport(" + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + ")");
                mexecutor.execute("CYPHER create index on:transport(" + MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY + ")");
            }

            if (ccgraph instanceof KeyIndexableGraph) {
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_NODE_NAME_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, Vertex.class);
                log.debug("Create index for {} ...", MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
                ((KeyIndexableGraph) ccgraph).createKeyIndex(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, Edge.class);
            }
            log.debug("Mapping blueprints DB is started !");
            return true;
        } else {
            return false;
        }
    }

    public static void stop() {
        try {
            MappingDSCache.synchronizeToDB();
        } catch (MappingDSException E) {
            String msg = "Exception while synchronizing MappingDSCache...";
            E.printStackTrace();
            log.error(msg);
        } finally {
            String ddgraphinfo = ccgraph.toString();
            if (blpImpl.equals(BLUEPRINTS_IMPL_N4J) && MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsNeoConfigFile() != null) {
                neoBootstrapper.stop();
            } else {
                ccgraph.shutdown();
            }
            log.debug("{} is stopped", new Object[]{ddgraphinfo});
        }
    }

    public static synchronized void putThreadedSession(Session session) {
        Long threadID = Thread.currentThread().getId();
        MappingDSGraphDB.threadSessionRegistry.put(threadID, session);
    }

    public static synchronized void removeThreadedSession() {
        Long threadID = Thread.currentThread().getId();
        MappingDSGraphDB.threadSessionRegistry.remove(threadID);
    }

    public static void autocommit() {
        if (ccgraph instanceof TransactionalGraph) {
            boolean isThreadWithAutoCommitMode = true;
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) isThreadWithAutoCommitMode = false;
            log.debug("Auto commit ({}) for thread {}", new Object[]{isThreadWithAutoCommitMode, Thread.currentThread().getName()});
            if (isThreadWithAutoCommitMode) {
                log.debug("Auto commit operation...");
                try {
                    ((TransactionalGraph) ccgraph).commit();
                } catch (Exception e) {
                    log.error("Error while commiting from thread (" + threadID + ":" + Thread.currentThread().getName() + ")");
                    log.debug("autocommit table size: " + threadSessionRegistry.size());
                }
            }
        }
    }

    public static void commit() {
        if (ccgraph instanceof TransactionalGraph) {
            boolean isThreadWithAutoCommitMode = true;
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) isThreadWithAutoCommitMode = false;
            if (!isThreadWithAutoCommitMode) {
                log.debug("Commit operation from thread {} ...", new Object[]{Thread.currentThread().getName()});
                ((TransactionalGraph) ccgraph).commit();
            }
            else log.error("Thread " + Thread.currentThread().getName() + " is registered as autocommit : manual commit forbidden !");
        }
    }

    public static void autorollback() {
        if (ccgraph instanceof TransactionalGraph) {
            boolean isThreadWithAutoCommitMode = true;
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) isThreadWithAutoCommitMode = false;
            if (isThreadWithAutoCommitMode) {
                log.error("Auto rollback operation...");
                try {
                    ((TransactionalGraph) ccgraph).rollback();
                } catch (Exception e) {
                    log.error("Error while commiting from thread (" + threadID + ":" + Thread.currentThread().getName() + ")");
                    log.debug("autocommit table size: " + threadSessionRegistry.size());
                }
            }
        }
    }

    public static void rollback() {
        if (ccgraph instanceof TransactionalGraph) {
            boolean isThreadWithAutoCommitMode = true;
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) isThreadWithAutoCommitMode = false;
            if (!isThreadWithAutoCommitMode) {
                log.debug("Rollback operation from thread {} ...", new Object[]{Thread.currentThread().getName()});
                ((TransactionalGraph) ccgraph).rollback();
            } else log.error("Thread " + Thread.currentThread().getName() + " is registered as autocommit : manual rollback forbidden !");
        }
    }

    public static Map<String, String> executeQuery(String query) {
        Map<String,String> ret = null;
        switch (blpImpl) {
            case BLUEPRINTS_IMPL_N4J:
                ret = mexecutor.execute(query);
                break;
            default:
                log.error("Mapper DSL is not implemented yet for this MappingDS blueprints implementation !", new Object[]{blpImpl});
                break;
        }
        return ret;
    }

    public static Graph getGraph() {
        return ccgraph;
    }

    public static synchronized MappingDSBlueprintsCacheEntity saveVertexEntity(MappingDSBlueprintsCacheEntity entity) {
        Vertex entityV;
        String id = UUID.randomUUID().toString();
        try {
            entityV = ccgraph.addVertex(null);
            entityV.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id);
            entity.setElement(entityV);
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(entity);
            else MappingDSCache.putEntityToCache(entity);
            entity.synchronizeToDB();
            autocommit();
            log.debug("Vertex {} ({}:{}) has been saved on graph {}", new Object[]{entityV.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id,
                    ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
            if (log.isTraceEnabled()) {
                for (String propKey : entityV.getPropertyKeys()) {
                    log.trace("Vertex {} property {}: {}", new Object[]{entityV.toString(),propKey,entityV.getProperty(propKey).toString()});
                }
            }
        } catch (Exception E) {
            log.error("Exception catched while saving vertex " + id + ".");
            E.printStackTrace();
            autorollback();
        }
        return entity;
    }

    public static synchronized Edge createEdge(Vertex source, Vertex destination, String label) throws MappingDSException {
        Edge edge;
        String id = UUID.randomUUID().toString();
        try {
            if (log.isTraceEnabled()) {
                for (String propKey : source.getPropertyKeys()) {
                    log.trace("Source vertex {} property {}: {}", new Object[]{source.toString(),propKey,source.getProperty(propKey).toString()});
                }
                for (String propKey : destination.getPropertyKeys()) {
                    log.trace("Destination vertex {} property {}: {}", new Object[]{destination.toString(),propKey,destination.getProperty(propKey).toString()});
                }
            }
            edge = ccgraph.addEdge(null, source, destination, label);
            edge.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id);
            autocommit();
            log.debug("Edge {} ({}:{}) has been saved on graph {}", new Object[]{edge.toString(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id,
                    ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
            if (log.isTraceEnabled()) {
                for (String propKey : edge.getPropertyKeys()) {
                    log.trace("Edge property {}: {}", new Object[]{edge.toString(),propKey,edge.getProperty(propKey).toString()});
                }
                for (String propKey : source.getPropertyKeys()) {
                    log.trace("Source vertex {} property {}: {}", new Object[]{source.toString(),propKey,source.getProperty(propKey).toString()});
                }
                for (String propKey : destination.getPropertyKeys()) {
                    log.trace("Destination vertex {} property {}: {}", new Object[]{destination.toString(),propKey,destination.getProperty(propKey).toString()});
                }
            }
        } catch (Exception E) {
            String msg = "Exception catched while saving edge " + id + ".";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
        return edge;
    }

    public static MappingDSBlueprintsCacheEntity saveEdgeEntity(MappingDSBlueprintsCacheEntity entity, Vertex source, Vertex destination, String label) {
        try {
            Edge entityE = createEdge(source, destination, label);
            entity.setElement(entityE);
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(entity);
            else MappingDSCache.putEntityToCache(entity);
            entity.synchronizeToDB();
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while saving edge...");
            E.printStackTrace();
            autorollback();
        }
        return entity;
    }

    private static MappingDSBlueprintsCacheEntity getEdgeEntity(Edge edge) throws MappingDSException {
        String id = edge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
        MappingDSBlueprintsCacheEntity ret;
        Long threadID = Thread.currentThread().getId();
        if (threadSessionRegistry.containsKey(threadID)) {
            ret = (MappingDSBlueprintsCacheEntity) ((SessionImpl) threadSessionRegistry.get(threadID)).getCachedEntity("E" + id);
            //if (ret == null) ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        } else ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        if (ret == null) {
            if (edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                else MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        return ret;
    }

    public static MappingDSBlueprintsCacheEntity getEdgeEntity(String id) throws MappingDSException {
        log.debug("Get cache entity {} if exists ...", new Object[]{"E"+id});
        MappingDSBlueprintsCacheEntity ret;
        Long threadID = Thread.currentThread().getId();
        if (threadSessionRegistry.containsKey(threadID)) {
            ret = (MappingDSBlueprintsCacheEntity) ((SessionImpl) threadSessionRegistry.get(threadID)).getCachedEntity("E" + id);
            //if (ret == null) ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        } else ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        if (ret == null) {
            if (ccgraph instanceof Neo4j2Graph)
                //Tinkerpop Blueprint 2.5 forget to start transaction on getEdges(final String key, final Object value)
                ((Neo4j2Graph)ccgraph).autoStartTransaction(false);
            Edge edge = (ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID,id).iterator().hasNext() ?
                                 ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID,id).iterator().next() : null);
            if (edge!=null && edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                else MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        return ret;
    }

    private static MappingDSBlueprintsCacheEntity getVertexEntity(Vertex vertex) throws MappingDSException {
        String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
        MappingDSBlueprintsCacheEntity ret;
        Long threadID = Thread.currentThread().getId();
        if (threadSessionRegistry.containsKey(threadID)) {
            ret = (MappingDSBlueprintsCacheEntity) ((SessionImpl) threadSessionRegistry.get(threadID)).getCachedEntity("V" + id);
            //if (ret == null) ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("V" + id);
        } else ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("V" + id);
        if (ret == null) {
            if (vertexType != null) {
                switch (vertexType) {
                    case MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE:
                        ret = new ClusterImpl();
                        break;
                    case MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE:
                        ret = new ContainerImpl();
                        break;
                    case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                        ret = new NodeImpl();
                        break;
                    case MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE:
                        ret = new GateImpl();
                        break;
                    case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                        ret = new EndpointImpl();
                        break;
                    case MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                        ret = new TransportImpl();
                        break;
                    default:
                        break;
                }
            }
            if (ret != null) {
                ret.setElement(vertex);
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                else MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        /*
        log.debug("{} : {}", new Object[]{vertexType, ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE)) ? ((ClusterImpl)ret).getClusterName() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)) ? ((ContainerImpl)ret).getContainerPrimaryAdminGateURL() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) ? ((NodeImpl)ret).getNodeName() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) ? ((GateImpl)ret).getNodeName() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) ? ((EndpointImpl)ret).getEndpointURL() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)) ? ((TransportImpl)ret).getTransportName() :"!!!!"))))))});
        */
        return ret;
    }

    public static MappingDSBlueprintsCacheEntity getVertexEntity(String id) throws MappingDSException {
        if (id == null)
            return null;
        log.debug("Get cache entity {} if exists ...", new Object[]{"V" + id});
        MappingDSBlueprintsCacheEntity ret;
        Long threadID = Thread.currentThread().getId();
        if (threadSessionRegistry.containsKey(threadID)) {
            ret = (MappingDSBlueprintsCacheEntity) ((SessionImpl) threadSessionRegistry.get(threadID)).getCachedEntity("V" + id);
            //if (ret == null) ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("V" + id);
        } else ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("V" + id);
        if (ret == null) {
            log.debug("Get vertex {} from graph {}...", new Object[]{id, ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
            Vertex vertex = (ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id).iterator().hasNext() ?
                             ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id).iterator().next() : null);
            if (vertex != null) {
                String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                if (vertexType != null) {
                    switch (vertexType) {
                        case MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE:
                            ret = new ClusterImpl();
                            break;
                        case MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE:
                            ret = new ContainerImpl();
                            break;
                        case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                            ret = new NodeImpl();
                            break;
                        case MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE:
                            ret = new GateImpl();
                            break;
                        case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                            ret = new EndpointImpl();
                            break;
                        case MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                            ret = new TransportImpl();
                            break;
                        default:
                            break;
                    }
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                    else MappingDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
            autocommit();
        } else {
            log.debug("Entity returned from cache {}", new Object[]{ret.toString()});
        }
        return ret;
    }

    public static Set<ClusterImpl> getClusters() throws MappingDSException {
        Set<ClusterImpl> ret = new HashSet<>();
        log.debug("Get all clusters from graph {}...", new Object[]{ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE)) {
            String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            ClusterImpl tmp = (ClusterImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new ClusterImpl();
                tmp.setElement(vertex);
                Long threadID = Thread.currentThread().getId();
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
                else MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add cluster {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<ContainerImpl> getContainers() throws MappingDSException {
        Set<ContainerImpl> ret = new HashSet<>();
        log.debug("Get all containers from graph {}...", new Object[]{ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)) {
            String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            ContainerImpl tmp = (ContainerImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new ContainerImpl();
                tmp.setElement(vertex);
                tmp.synchronizeFromDB();
                Long threadID = Thread.currentThread().getId();
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
                else MappingDSCache.putEntityToCache(tmp);
            }
            log.debug("Add container {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    private static NodeImpl getNodeFromVertex(Vertex vertex) throws MappingDSException {
        String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        NodeImpl tmp = (NodeImpl) getVertexEntity(id);
        if (tmp == null) {
            tmp = new NodeImpl();
            tmp.setElement(vertex);
            tmp.synchronizeFromDB();
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
            else MappingDSCache.putEntityToCache(tmp);
        }
        return tmp;
    }

    private static GateImpl getGateFromVertex(Vertex vertex) throws MappingDSException {
        String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        GateImpl tmp = (GateImpl) getVertexEntity(id);
        if (tmp == null) {
            tmp = new GateImpl();
            tmp.setElement(vertex);
            tmp.synchronizeFromDB();
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
            else MappingDSCache.putEntityToCache(tmp);
        }
        return tmp;
    }

    public static Set<NodeImpl> getNodes() throws MappingDSException {
        Set<NodeImpl> ret = new HashSet<>();
        log.debug("Get all nodes from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE))
            ret.add(getNodeFromVertex(vertex));
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE))
            ret.add(getGateFromVertex(vertex));
        autocommit();
        return ret;
    }

    public static Set<NodeImpl> getNodes(String selector) throws MappingDSException {
        Set<NodeImpl> ret = new HashSet<>();
        selector = selector.replace("nodeID", MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        Object query_try = sexecutor.execute(selector, MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE);
        if (query_try != null && query_try instanceof GraphQuery)
            for (Vertex vertex : ((GraphQuery) query_try).vertices())
                ret.add(getNodeFromVertex(vertex));
        query_try = sexecutor.execute(selector, MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE);
        if (query_try != null && query_try instanceof GraphQuery)
            for (Vertex vertex : ((GraphQuery) query_try).vertices())
                ret.add(getGateFromVertex(vertex));
        return ret;
    }

    public static Set<NodeImpl> getNodes(String key, Object value) throws MappingDSException {
        Set<NodeImpl> ret = new HashSet<>();
        log.debug("Get all nodes from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                 MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) {
            NodeImpl tmp = getNodeFromVertex(vertex);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value))
                ret.add(tmp);
        }
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                 MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            NodeImpl tmp = getGateFromVertex(vertex);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value))
                ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<GateImpl> getGates() throws MappingDSException {
        Set<GateImpl> ret = new HashSet<>();
        log.debug("Get all gates from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE))
            ret.add(getGateFromVertex(vertex));
        autocommit();
        return ret;
    }

    public static Set<GateImpl> getGates(String key, Object value) throws MappingDSException {
        Set<GateImpl> ret = new HashSet<>();
        log.debug("Get all gates from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            GateImpl tmp = getGateFromVertex(vertex);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value))
                ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    private static EndpointImpl getEndpointFromVertex(Vertex vertex) throws MappingDSException {
        String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        EndpointImpl tmp = (EndpointImpl) getVertexEntity(id);
        if (tmp == null) {
            tmp = new EndpointImpl();
            tmp.setElement(vertex);
            tmp.synchronizeFromDB();
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
            else MappingDSCache.putEntityToCache(tmp);
        }
        return tmp;
    }

    public static Set<EndpointImpl> getEndpoints() throws MappingDSException {
        Set<EndpointImpl> ret = new HashSet<>();
        log.debug("Get all endpoints from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE))
            ret.add(getEndpointFromVertex(vertex));
        autocommit();
        return ret;
    }

    public static Set<EndpointImpl> getEndpoints(String selector) throws MappingDSException {
        Set<EndpointImpl> ret = new HashSet<>();
        selector = selector.replace("endpointID", MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        Object query_try = sexecutor.execute(selector, MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE);
        if (query_try != null && query_try instanceof GraphQuery)
            for (Vertex vertex : ((GraphQuery) query_try).vertices())
                ret.add(getEndpointFromVertex(vertex));
        return ret;
    }

    public static Set<EndpointImpl> getEndpoints(String key, Object value) throws MappingDSException {
        Set<EndpointImpl> ret = new HashSet<>();
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) {
            String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            log.debug("Test vertex {}...", new Object[]{id});
            EndpointImpl tmp = (EndpointImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new EndpointImpl();
                tmp.setElement(vertex);
                tmp.synchronizeFromDB();
                Long threadID = Thread.currentThread().getId();
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
                else MappingDSCache.putEntityToCache(tmp);
            }
            Object tmpValue = (tmp.getEndpointProperties() != null) ? tmp.getEndpointProperties().get(key) : null;
            if (tmpValue != null && tmpValue.equals(value)) {
                log.debug("Add endpoint {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    public static Set<TransportImpl> getTransports() throws MappingDSException {
        Set<TransportImpl> ret = new HashSet<>();
        log.debug("Get all transports from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)) {
            String id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            TransportImpl tmp = (TransportImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new TransportImpl();
                tmp.setElement(vertex);
                tmp.synchronizeFromDB();
                Long threadID = Thread.currentThread().getId();
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
                else MappingDSCache.putEntityToCache(tmp);
            }
            log.debug("Add transport {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static ClusterImpl getIndexedCluster(String clusterName) throws MappingDSException {
        MappingDSBlueprintsCacheEntity ret = (ClusterImpl)MappingDSCache.getClusterFromCache(clusterName);
        if (ret == null) {
            Vertex vertex = ccgraph.getVertices(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, clusterName).iterator().hasNext() ?
                                 ccgraph.getVertices(MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY, clusterName).iterator().next() : null;
            if (vertex != null) {
                String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                switch (vertexType) {
                    case MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE:
                        ret = new ClusterImpl();
                        break;
                    default:
                        break;
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    ret.synchronizeFromDB();
                    Long threadID = Thread.currentThread().getId();
                    if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                    else MappingDSCache.putEntityToCache(ret);
                }
            }
            autocommit();
        }
        return (ClusterImpl) ret;
    }

    public static Set<NodeImpl> getIndexedNodes(String name) throws MappingDSException {
        Set<NodeImpl> ret = new HashSet<>();
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY, name)) {
            String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
            NodeImpl tmp = null;
            switch (vertexType) {
                case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                    tmp = new NodeImpl();
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE:
                    tmp = new GateImpl();
                    break;
                default:
                    break;
            }
            if (tmp != null) {
                tmp.setElement(vertex);
                tmp.synchronizeFromDB();
                Long threadID = Thread.currentThread().getId();
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
                else MappingDSCache.putEntityToCache(tmp);
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    public static EndpointImpl getIndexedEndpoint(String url) throws MappingDSException {
        MappingDSBlueprintsCacheEntity ret = (EndpointImpl) MappingDSCache.getEndpointFromCache(url);
        if (ret == null && ccgraph != null) {
            Vertex vertex = ccgraph.getVertices(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, url).iterator().hasNext() ?
                                    ccgraph.getVertices(MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY, url).iterator().next() : null;
            if (vertex != null) {
                String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                switch (vertexType) {
                    case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                        ret = new EndpointImpl();
                    default:
                        break;
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    ret.synchronizeFromDB();
                    Long threadID = Thread.currentThread().getId();
                    if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                    else MappingDSCache.putEntityToCache(ret);
                }
            }
            autocommit();
        }
        return (EndpointImpl) ret;
    }

    public static TransportImpl getIndexedTransport(String transportName) throws MappingDSException {
        MappingDSBlueprintsCacheEntity ret = (TransportImpl) MappingDSCache.getTransportFromCache(transportName);
        if (ret == null && ccgraph != null) {
            Vertex vertex = ccgraph.getVertices(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, transportName).iterator().hasNext() ?
                                    ccgraph.getVertices(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, transportName).iterator().next() : null;
            if (vertex != null) {
                String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
                switch (vertexType) {
                    case MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                        ret = new TransportImpl();
                    default:
                        break;
                }
                if (ret != null) {
                    ret.setElement(vertex);
                    ret.synchronizeFromDB();
                    Long threadID = Thread.currentThread().getId();
                    if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                    else MappingDSCache.putEntityToCache(ret);
                }
            }
            autocommit();
        }
        return (TransportImpl) ret;
    }

    public static MappingDSBlueprintsCacheEntity getLink(String id) throws MappingDSException {
        if (id == null)
            return null;
        MappingDSBlueprintsCacheEntity ret;
        Long threadID = Thread.currentThread().getId();
        if (threadSessionRegistry.containsKey(threadID)) {
            ret = (MappingDSBlueprintsCacheEntity) ((SessionImpl) threadSessionRegistry.get(threadID)).getCachedEntity("E" + id);
            //if (ret == null) ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        } else ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        if (ret == null && ccgraph != null) {
            if (ccgraph instanceof Neo4j2Graph)
                //Tinkerpop Blueprint 2.5 forget to start transaction on getEdges(final String key, final Object value)
                ((Neo4j2Graph)ccgraph).autoStartTransaction(false);
            Edge edge = ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id).iterator().hasNext() ?
                        ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id).iterator().next() : null;
            if (edge != null && edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                ret.synchronizeFromDB();
                if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(ret);
                else MappingDSCache.putEntityToCache(ret);
            }
            autocommit();
        }
        return ret;
    }

    private static LinkImpl getLinkFromEdge(Edge edge) throws MappingDSException {
        String id = edge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
        LinkImpl tmp = (LinkImpl) getLink(id);
        if (tmp == null) {
            tmp = new LinkImpl();
            tmp.setElement(edge);
            tmp.synchronizeFromDB();
            Long threadID = Thread.currentThread().getId();
            if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).putEntityToCache(tmp);
            else MappingDSCache.putEntityToCache(tmp);
        }
        return  tmp;
    }

    public static Set<LinkImpl> getLinks(EndpointImpl sourceEndpoint, EndpointImpl targetEndpoint, TransportImpl transport) throws MappingDSException {
        Vertex sourceEpVertex = (sourceEndpoint!=null) ? sourceEndpoint.getElement() : null;
        Vertex targetEpVertex = (targetEndpoint!=null) ? targetEndpoint.getElement() : null;
        Vertex transportVertex = (transport!=null) ? transport.getElement() : null;
        Set<LinkImpl> ret = new HashSet<>();

        if (sourceEpVertex!=null && targetEpVertex!=null) {
            for (Edge edge : sourceEpVertex.getEdges(Direction.OUT, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                if (edge.getVertex(Direction.IN).equals(targetEpVertex))
                    ret.add(getLinkFromEdge(edge));
        } else if (sourceEpVertex!=null && transportVertex!=null) {
            for (Edge edge : sourceEpVertex.getEdges(Direction.OUT, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                if (edge.getVertex(Direction.IN).equals(transportVertex))
                    ret.add(getLinkFromEdge(edge));
        } else if (sourceEpVertex!=null) {
            for (Edge edge : sourceEpVertex.getEdges(Direction.OUT, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                ret.add(getLinkFromEdge(edge));
        } else if (targetEpVertex!=null) {
            for (Edge edge : targetEpVertex.getEdges(Direction.IN, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                ret.add(getLinkFromEdge(edge));
        } else {
            for (Edge edge : ccgraph.getEdges())
                if (edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                    ret.add(getLinkFromEdge(edge));
        }
        autocommit();
        return ret;
    }

    private static void removeVertex(Vertex vertex) throws MappingDSException {
        ccgraph.removeVertex(vertex);
        autocommit();
    }

    private static void removeEdge(Edge edge) throws MappingDSException {
        ccgraph.removeEdge(edge);
        autocommit();
    }

    public static synchronized void deleteEntity(MappingDSBlueprintsCacheEntity entity) {
        Element elem = entity.getElement();
        try {
            if (elem != null) {
                if (elem instanceof Vertex) {
                    Vertex vertex = (Vertex) elem;
                    for (Edge edge : vertex.getEdges(Direction.OUT, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY)) {
                        Vertex ownedVertex = edge.getVertex(Direction.IN);
                        deleteEntity(getVertexEntity(ownedVertex));
                    }

                    for (Edge edge : vertex.getEdges(Direction.IN, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY)) {
                        MappingDSCacheEntity owningEntity = getVertexEntity(edge.getVertex(Direction.OUT));
                        removeEdge(edge);
                        owningEntity.synchronizeFromDB();
                    }

                    for (Edge edge : vertex.getEdges(Direction.BOTH, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY)) {
                        MappingDSCacheEntity twinEntity ;
                        Vertex v = edge.getVertex(Direction.OUT);
                        if (!v.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID)))
                            twinEntity = getVertexEntity(v);
                        else
                            twinEntity = getVertexEntity(edge.getVertex(Direction.IN));
                        removeEdge(edge);
                        twinEntity.synchronizeFromDB();
                    }

                    for (Edge edge : vertex.getEdges(Direction.BOTH, MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                        deleteEntity(getEdgeEntity(edge));
                    synchronized (ccgraph) {
                        Long threadID = Thread.currentThread().getId();
                        if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).removeEntityFromCache(entity);
                        else MappingDSCache.removeEntityFromCache(entity);
                        removeVertex(vertex);
                    }
                } else if (elem instanceof Edge) {
                    synchronized (ccgraph) {
                        Long threadID = Thread.currentThread().getId();
                        if (threadSessionRegistry.containsKey(threadID)) ((SessionImpl)threadSessionRegistry.get(threadID)).removeEntityFromCache(entity);
                        else MappingDSCache.removeEntityFromCache(entity);
                        removeEdge((Edge) elem);
                    }
                }
            }
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while deleting entity " + ((Element)entity.getElement()).getId() + "...");
            E.printStackTrace();
            autorollback();
        }
    }

    public static synchronized void clear() {
        try {
            for (Edge edge : ccgraph.getEdges()) ccgraph.removeEdge(edge);
            for (Vertex vertex : ccgraph.getVertices()) ccgraph.removeVertex(vertex);
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while clearing DB Graph...");
            E.printStackTrace();
            autorollback();
        }
    }
}