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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCache;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cfg.MappingBlueprintsDSCfgLoader;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.MapperExecutor;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class MappingDSGraphDB {

    private final static String BLUEPRINTS_IMPL_N4J = "Neo4j";

    private final static Logger log = LoggerFactory.getLogger(MappingDSGraphDB.class);

    private static String blpImpl                                    = null;
    private static MappingDSGraphDBNeo4jBootstrapper neoBootstrapper = null;
    private static Graph  ccgraph                                    = null;
    private static MapperExecutor executor                           = null;
    private static Vertex idmanager                                  = null;

    private static HashMap<Long, Boolean> autocommit = new HashMap<Long, Boolean>();

    public static boolean isBlueprintsNeo4j() {
        return (blpImpl.equals(BLUEPRINTS_IMPL_N4J));
    }

    public static boolean init(Dictionary<Object, Object> properties) throws JsonParseException, JsonMappingException, IOException {
        if (properties != null) return MappingBlueprintsDSCfgLoader.load(properties);
        else return false;
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
                        executor = new MapperExecutor(graphDb);
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
                executor.execute("CYPHER create index on:cluster("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+")");
                executor.execute("CYPHER create index on:cluster("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+")");
                executor.execute("CYPHER create index on:cluster("+MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY+")");

                executor.execute("CYPHER create index on:container("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+")");
                executor.execute("CYPHER create index on:container("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+")");
                executor.execute("CYPHER create index on:container("+MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY+")");

                executor.execute("CYPHER create index on:node("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+")");
                executor.execute("CYPHER create index on:node("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+")");
                executor.execute("CYPHER create index on:node("+MappingDSGraphPropertyNames.DD_NODE_NAME_KEY+")");

                executor.execute("CYPHER create index on:gate("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+")");
                executor.execute("CYPHER create index on:gate("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+")");
                executor.execute("CYPHER create index on:gate("+MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY+")");
                executor.execute("CYPHER create index on:gate("+MappingDSGraphPropertyNames.DD_NODE_NAME_KEY+")");

                executor.execute("CYPHER create index on:endpoint("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+")");
                executor.execute("CYPHER create index on:endpoint("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+")");
                executor.execute("CYPHER create index on:endpoint("+MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY+")");

                executor.execute("CYPHER create index on:transport("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+")");
                executor.execute("CYPHER create index on:transport("+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+")");
                executor.execute("CYPHER create index on:transport("+MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY+")");
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
            log.debug("Retrieve Mapping ID manager vertex if exists...");
            idmanager = ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, (long) 0).iterator().hasNext() ?
                                ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, (long) 0).iterator().next() : null;
            if (idmanager == null) {
                log.debug("Initialize Mapping Blueprints DB...");
                idmanager = ccgraph.addVertex(null);
                idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, (long) 0);
                idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY, (long) 0);
                idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY, (long) 0);
                autocommit();
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

    public static synchronized void setAutocommit(boolean autocommit) {
        Long threadID = Thread.currentThread().getId();
        log.debug("Autocommit mode is {} for thread {}", new Object[]{(autocommit ? "activated" : "deactivated"), Thread.currentThread().getName()});
        MappingDSGraphDB.autocommit.put(threadID, autocommit);
    }

    public static void autocommit() {
        if (ccgraph instanceof TransactionalGraph) {
            Long threadID = Thread.currentThread().getId();
            boolean isThreadWithAutoCommitMode = true;
            if (autocommit.containsKey(threadID)) {
                isThreadWithAutoCommitMode = autocommit.get(threadID);
            }
            log.debug("Auto commit ({}) for thread {}", new Object[]{isThreadWithAutoCommitMode, threadID});
            if (isThreadWithAutoCommitMode) {
                log.debug("Auto commit operation...");
                ((TransactionalGraph) ccgraph).commit();
            }
        }
    }

    public static void commit() {
        if (ccgraph instanceof TransactionalGraph) {
            log.debug("Commit operation...");
            ((TransactionalGraph) ccgraph).commit();
        }
    }

    public static void autorollback() {
        if (ccgraph instanceof TransactionalGraph) {
            Long threadID = Thread.currentThread().getId();
            boolean isThreadWithAutoCommitMode = true;
            if (autocommit.containsKey(threadID)) {
                isThreadWithAutoCommitMode = autocommit.get(threadID);
            }
            if (isThreadWithAutoCommitMode) {
                log.error("Auto rollback operation...");
                ((TransactionalGraph) ccgraph).rollback();
            }
        }
    }

    public static void rollback() {
        if (ccgraph instanceof TransactionalGraph) {
            log.error("Rollback operation...");
            ((TransactionalGraph) ccgraph).rollback();
        }
    }

    public static Map<String, String> executeQuery(String query) {
        Map<String,String> ret = null;
        switch (blpImpl) {
            case BLUEPRINTS_IMPL_N4J:
                ret = executor.execute(query);
                break;
            default:
                log.error("Mapper DSL is not implemented yet for this MappingDS blueprints implementation !", new Object[]{blpImpl});
                break;
        }
        return ret;
    }

    public static Graph getDDgraph() {
        return ccgraph;
    }

    private static synchronized long incrementVertexMaxCursor() throws MappingDSException {
        try {
            long countProp = idmanager.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
            countProp++;
            idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY, countProp++);
        } catch (Exception E) {
            String msg = "Exception while incrementing vertex max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
        return getVertexMaxCursor();
    }

    private static synchronized void decrementVertexMaxCursor() throws MappingDSException {
        try {
            long countProp = idmanager.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
            countProp--;
            idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY, countProp--);
        } catch (Exception E) {
            String msg = "Exception catched while decrementing vertex max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
    }

    public static synchronized long getVertexMaxCursor() {
        return idmanager.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
    }

    private static synchronized long incrementEdgeMaxCursor() throws MappingDSException {
        try {
            long countProp = idmanager.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
            countProp++;
            idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY, countProp);
        } catch (Exception E) {
            String msg = "Exception catched while incrementing edge max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
        return getEdgeMaxCursor();
    }

    private static synchronized void decrementEdgeMaxCursor() throws MappingDSException {
        try {
            long countProp = idmanager.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
            countProp--;
            idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY, countProp);
        } catch (Exception E) {
            String msg = "Exception catched while decrementing edge max cursor count...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
    }

    public static synchronized long getEdgeMaxCursor() {
        return idmanager.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
    }

    private static synchronized void addVertexFreeID(long id) throws MappingDSException {
        try {
            idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_FREE_IDS_KEY + id, id);
        } catch (Exception E) {
            String msg = "Exception catched while adding vertex free ID " + id + "...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
    }

    private static synchronized boolean hasVertexFreeID() {
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_FREE_IDS_KEY)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized long consumeVertexFreeID() {
        long ret = 0;
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_FREE_IDS_KEY)) {
                ret = idmanager.getProperty(key);
                idmanager.removeProperty(key);
                break;
            }
        }
        return ret;
    }

    private static synchronized void addEdgeFreeID(long id) throws MappingDSException {
        try {
            idmanager.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_FREE_IDS_KEY + id, id);
        } catch (Exception E) {
            String msg = "Exception catched while adding edge free ID " + id + "...";
            log.error(msg);
            E.printStackTrace();
            log.error("Raise exception for rollback...");
            throw new MappingDSException(msg);
        }
    }

    private static synchronized boolean hasEdgeFreeID() {
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_FREE_IDS_KEY)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized long consumeEdgeFreeID() {
        long ret = 0;
        for (String key : idmanager.getPropertyKeys()) {
            if (key.contains(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_FREE_IDS_KEY)) {
                ret = idmanager.getProperty(key);
                idmanager.removeProperty(key);
                break;
            }
        }
        return ret;
    }

    public static synchronized MappingDSBlueprintsCacheEntity saveVertexEntity(MappingDSBlueprintsCacheEntity entity) {
        Vertex entityV = null;
        long id = 0;
        try {
            if (!hasVertexFreeID()) {
                id = incrementVertexMaxCursor();
            } else {
                id = consumeVertexFreeID();
            }
            entityV = ccgraph.addVertex(null);
            entityV.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, id);
            entity.setElement(entityV);
            MappingDSCache.putEntityToCache(entity);
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
        Edge edge = null;
        long id = 0;
        try {
            if (!hasEdgeFreeID()) {
                id = incrementEdgeMaxCursor();
            } else {
                id = consumeEdgeFreeID();
            }
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
            MappingDSCache.putEntityToCache(entity);
            entity.synchronizeToDB();
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while saving edge...");
            E.printStackTrace();
            autorollback();
        }
        return entity;
    }

    private static MappingDSBlueprintsCacheEntity getEdgeEntity(Edge edge) {
        long id = (long) edge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
        MappingDSBlueprintsCacheEntity ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        if (ret == null) {
            if (edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        return ret;
    }

    public static MappingDSBlueprintsCacheEntity getEdgeEntity(long id) {
        log.debug("Get cache entity {} if exists ...", new Object[]{"E"+id});
        MappingDSBlueprintsCacheEntity ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("E" + id);
        if (ret == null) {
            if (ccgraph instanceof Neo4j2Graph)
                //Tinkerpop Blueprint 2.5 forget to start transaction on getEdges(final String key, final Object value)
                ((Neo4j2Graph)ccgraph).autoStartTransaction(false);
            Edge edge = (ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID,id).iterator().hasNext() ?
                                 ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID,id).iterator().next() : null);
            if (edge!=null && edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        return ret;
    }

    private static MappingDSBlueprintsCacheEntity getVertexEntity(Vertex vertex) {
        long id = (long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
        MappingDSBlueprintsCacheEntity ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("V" + id);
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
                MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
        }
        log.debug("{} : {}", new Object[]{vertexType, ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE)) ? ((ClusterImpl)ret).getClusterName() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)) ? ((ContainerImpl)ret).getContainerPrimaryAdminGateURL() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) ? ((NodeImpl)ret).getNodeName() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) ? ((GateImpl)ret).getNodeName() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) ? ((EndpointImpl)ret).getEndpointURL() :
                                                     ((vertexType.equals(MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)) ? ((TransportImpl)ret).getTransportName() :"!!!!"))))))});
        return ret;
    }

    public static MappingDSBlueprintsCacheEntity getVertexEntity(long id) {
        if (id == 0)
            return null;
        log.debug("Get cache entity {} if exists ...", new Object[]{"V" + id});
        MappingDSBlueprintsCacheEntity ret = (MappingDSBlueprintsCacheEntity)MappingDSCache.getCachedEntity("V" + id);
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
                    MappingDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
            autocommit();
        } else {
            log.debug("Entity returned from cache {}", new Object[]{ret.toString()});
        }
        return ret;
    }

    public static Set<ClusterImpl> getClusters(){
        Set<ClusterImpl> ret = new HashSet<ClusterImpl>();
        log.debug("Get all clusters from graph {}...", new Object[]{ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            ClusterImpl tmp = (ClusterImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new ClusterImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add cluster {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<ContainerImpl> getContainers() {
        Set<ContainerImpl> ret = new HashSet<ContainerImpl>();
        log.debug("Get all containers from graph {}...", new Object[]{ccgraph.toString() + "(" + ccgraph.hashCode() + ")"});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            ContainerImpl tmp = (ContainerImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new ContainerImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add container {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<NodeImpl> getNodes() {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        log.debug("Get all nodes from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            NodeImpl tmp = (NodeImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new NodeImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add node {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            NodeImpl tmp = (NodeImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new NodeImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add node {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<NodeImpl> getNodes(String key, Object value) {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        log.debug("Get all nodes from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            NodeImpl tmp = (NodeImpl) getVertexEntity(id);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value)) {
                log.debug("Add node {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            NodeImpl tmp = (NodeImpl) getVertexEntity(id);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value)) {
                log.debug("Add node {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    public static Set<GateImpl> getGates() {
        Set<GateImpl> ret = new HashSet<GateImpl>();
        log.debug("Get all gates from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            GateImpl tmp = (GateImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new GateImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add gate {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<GateImpl> getGates(String key, Object value) {
        Set<GateImpl> ret = new HashSet<GateImpl>();
        log.debug("Get all gates from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            GateImpl tmp = (GateImpl) getVertexEntity(id);
            Object tmpValue = tmp.getNodeProperties().get(key);
            if (tmpValue.equals(value)) {
                log.debug("Add gate {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    public static Set<EndpointImpl> getEndpoints() {
        Set<EndpointImpl> ret = new HashSet<EndpointImpl>();
        log.debug("Get all endpoints from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            EndpointImpl tmp = (EndpointImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new EndpointImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add endpoint {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static Set<EndpointImpl> getEndpoints(String key, Object value) {
        Set<EndpointImpl> ret = new HashSet<EndpointImpl>();
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            log.debug("Test vertex {}...", new Object[]{id});
            EndpointImpl tmp = (EndpointImpl) getVertexEntity(id);
            Object tmpValue = (tmp.getEndpointProperties() != null) ? tmp.getEndpointProperties().get(key) : null;
            if (tmpValue != null && tmpValue.equals(value)) {
                log.debug("Add endpoint {} to Set...", new Object[]{id});
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    public static Set<TransportImpl> getTransports() {
        Set<TransportImpl> ret = new HashSet<TransportImpl>();
        log.debug("Get all transports from graph {}...", new Object[]{ccgraph.toString()});
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY,
                                                        MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)) {
            long id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
            TransportImpl tmp = (TransportImpl) getVertexEntity(id);
            if (tmp == null) {
                tmp = new TransportImpl();
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
            }
            log.debug("Add transport {} to Set...", new Object[]{id});
            ret.add(tmp);
        }
        autocommit();
        return ret;
    }

    public static ClusterImpl getIndexedCluster(String clusterName) {
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
                    MappingDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
            autocommit();
        }
        return (ClusterImpl) ret;
    }

    public static Set<NodeImpl> getIndexedNodes(String name) {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        for (Vertex vertex : ccgraph.getVertices(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY, name)) {
            String vertexType = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY);
            NodeImpl tmp = null;
            switch (vertexType) {
                case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                    tmp = new NodeImpl();
                    break;
                default:
                    break;
            }
            if (tmp != null) {
                tmp.setElement(vertex);
                MappingDSCache.putEntityToCache(tmp);
                tmp.synchronizeFromDB();
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    public static EndpointImpl getIndexedEndpoint(String url) {
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
                    MappingDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
            autocommit();
        }
        return (EndpointImpl) ret;
    }

    public static TransportImpl getIndexedTransport(String transportName) {
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
                    MappingDSCache.putEntityToCache(ret);
                    ret.synchronizeFromDB();
                }
            }
            autocommit();
        }
        return (TransportImpl) ret;
    }

    public static MappingDSBlueprintsCacheEntity getLink(long id) {
        if (id == 0)
            return null;
        MappingDSBlueprintsCacheEntity ret = (MappingDSBlueprintsCacheEntity) MappingDSCache.getCachedEntity("E" + id);
        if (ret == null && ccgraph != null) {
            if (ccgraph instanceof Neo4j2Graph)
                //Tinkerpop Blueprint 2.5 forget to start transaction on getEdges(final String key, final Object value)
                ((Neo4j2Graph)ccgraph).autoStartTransaction(false);
            Edge edge = ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id).iterator().hasNext() ?
                        ccgraph.getEdges(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID, id).iterator().next() : null;
            if (edge != null && edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                ret = new LinkImpl();
                ret.setElement(edge);
                MappingDSCache.putEntityToCache(ret);
                ret.synchronizeFromDB();
            }
            autocommit();
        }
        return ret;
    }

    public static Set<LinkImpl> getLinks() {
        Set<LinkImpl> ret = new HashSet<LinkImpl>();
        for (Edge edge : ccgraph.getEdges()) {
            if (edge.getLabel().equals(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)) {
                long id = edge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
                LinkImpl tmp = (LinkImpl) getLink(id);
                if (tmp == null) {
                    tmp = new LinkImpl();
                    tmp.setElement(edge);
                    MappingDSCache.putEntityToCache(tmp);
                    tmp.synchronizeFromDB();
                }
                ret.add(tmp);
            }
        }
        autocommit();
        return ret;
    }

    private static synchronized void removeVertex(Vertex vertex) throws MappingDSException {
        long vertexID = (long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        ccgraph.removeVertex(vertex);
        if (vertexID == getVertexMaxCursor()) {
            decrementVertexMaxCursor();
        } else {
            addVertexFreeID(vertexID);
        }
        autocommit();
    }

    private static synchronized void removeEdge(Edge edge) throws MappingDSException {
        long edgeID = (long) edge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID);
        ccgraph.removeEdge(edge);
        if (edgeID == getEdgeMaxCursor()) {
            decrementEdgeMaxCursor();
        } else {
            addEdgeFreeID(edgeID);
        }
        autocommit();
    }

    public static void deleteEntity(MappingDSBlueprintsCacheEntity entity) {
        Element elem = (Element)entity.getElement();
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

                    MappingDSCache.removeEntityFromCache(entity);
                    removeVertex(vertex);
                } else if (elem instanceof Edge) {
                    MappingDSCache.removeEntityFromCache(entity);
                    removeEdge((Edge) elem);
                }
            }
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while deleting entity " + ((Element)entity.getElement()).getId() + "...");
            E.printStackTrace();
            autorollback();
        }
    }

    public static void clear() {
        try {
            for (Edge edge : ccgraph.getEdges()) {
                ccgraph.removeEdge(edge);
            }
            for (Vertex vertex : ccgraph.getVertices()) {
                ccgraph.removeVertex(vertex);
            }
            autocommit();
        } catch (Exception E) {
            log.error("Exception catched while clearing DB Graph...");
            E.printStackTrace();
            autorollback();
        }
    }
}