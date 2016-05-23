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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
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

public class NodeImpl implements Node, MappingDSBlueprintsCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(NodeImpl.class);

    private String nodeID = null;
    private String nodeName = null;
    private long nodeDepth = 0;
    private ContainerImpl nodeContainer = null;
    private HashMap<String, Object> nodeProperties = null;
    private NodeImpl nodeParentNode = null;
    private Set<NodeImpl> nodeChildNodes = new HashSet<NodeImpl>();
    private Set<NodeImpl> nodeTwinNodes = new HashSet<NodeImpl>();
    private Set<EndpointImpl> nodeEndpoints = new HashSet<EndpointImpl>();

    private transient Vertex nodeVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public String getNodeID() {
        return this.nodeID;
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
    }

    static final String SET_NODE_NAME = "setNodeName";

    @Override
    public void setNodeName(Session session, String name) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_NODE_NAME, new Object[]{name});
    }

    @Override
    public void setNodeName(String name) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setNodeName(session, name);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (this.nodeName == null || !this.nodeName.equals(name)) {
                this.nodeName = name;
                synchronizeNameToDB();
            }
        }
    }

    @Override
    public long getNodeDepth() {
        return this.nodeDepth;
    }

    @Override
    public ContainerImpl getNodeContainer() {
        return this.nodeContainer;
    }

    static final String SET_NODE_CONTAINER = "setNodeContainer";

    @Override
    public void setNodeContainer(Session session, Container container) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_NODE_CONTAINER, new Object[]{container});
    }

    @Override
    public void setNodeContainer(Container container) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setNodeContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (this.nodeContainer == null || !this.nodeContainer.equals(container)) {
                if (container instanceof ContainerImpl) {
                    this.nodeContainer = (ContainerImpl) container;
                    synchronizeContainerToDB();
                }
            }
        }
    }

    @Override
    public HashMap<String, Object> getNodeProperties() {
        return this.nodeProperties;
    }

    static final String ADD_NODE_PROPERTY = "addNodeProperty";

    @Override
    public void addNodeProperty(Session session, String propertyKey, Object value) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ADD_NODE_PROPERTY, new Object[]{propertyKey, value});
    }

    @Override
    public void addNodeProperty(String propertyKey, Object value) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.addNodeProperty(session, propertyKey, value);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (propertyKey != null && value != null) {
                if (this.nodeProperties == null) {
                    this.nodeProperties = new HashMap<String, Object>();
                }
                this.nodeProperties.put(propertyKey, value);
                synchronizePropertyToDB(propertyKey, value);
                log.debug("Set node {} property : ({},{})", new Object[]{this.getNodeID(),
                        propertyKey,
                        this.nodeProperties.get(propertyKey)});
            }
        }
    }

    static final String REMOVE_NODE_PROPERTY = "removeNodeProperty";

    @Override
    public void removeNodeProperty(Session session, String propertyKey) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, REMOVE_NODE_PROPERTY, new Object[]{propertyKey});
    }

    @Override
    public void removeNodeProperty(String propertyKey) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.removeNodeProperty(session, propertyKey);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (this.nodeProperties != null) {
                this.nodeProperties.remove(propertyKey);
                removePropertyFromDB(propertyKey);
            }
        }
    }

    @Override
    public NodeImpl getNodeParentNode() {
        return this.nodeParentNode;
    }

    static final String SET_NODE_PARENT_NODE = "setNodeParentNode";

    @Override
    public void setNodeParentNode(Session session, Node node) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_NODE_PARENT_NODE, new Object[]{node});
    }

    @Override
    public void setNodeParentNode(Node node) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setNodeParentNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (this.nodeParentNode == null || !this.nodeParentNode.equals(node)) {
                if (node instanceof NodeImpl) {
                    this.nodeParentNode = (NodeImpl) node;
                    synchronizeParentNodeToDB();
                }
            }
        }
    }

    @Override
    public Set<NodeImpl> getNodeChildNodes() {
        return this.nodeChildNodes;
    }

    static final String ADD_NODE_CHILD_NODE = "addNodeChildNode";

    @Override
    public boolean addNodeChildNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_NODE_CHILD_NODE, new Object[]{node});
        return ret;
    }

    @Override
    public boolean addNodeChildNode(Node node) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addNodeChildNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (node instanceof NodeImpl) {
                try {
                    ret = this.nodeChildNodes.add((NodeImpl) node);
                    if (ret) {
                        synchronizeChildNodeToDB((NodeImpl) node);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding child node {}...", new Object[]{node.getNodeID()});
                    this.nodeChildNodes.remove((NodeImpl) node);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
        return ret;
    }

    static final String REMOVE_NODE_CHILD_NODE = "removeNodeChildNode";

    @Override
    public boolean removeNodeChildNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_NODE_CHILD_NODE, new Object[]{node});
        return ret;
    }

    @Override
    public boolean removeNodeChildNode(Node node) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeNodeChildNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (node instanceof NodeImpl) {
                ret = this.nodeChildNodes.remove((NodeImpl) node);
                if (ret) removeChildNodeFromDB((NodeImpl) node);
            }
        }
        return ret;
    }

    @Override
    public Set<NodeImpl> getTwinNodes() {
        return this.nodeTwinNodes;
    }

    static final String ADD_TWIN_NODE = "addTwinNode";

    @Override
    public boolean addTwinNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_TWIN_NODE, new Object[]{node});
        return ret;
    }

    @Override
    public boolean addTwinNode(Node node) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addTwinNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (node instanceof NodeImpl) {
                try {
                    ret = this.nodeTwinNodes.add((NodeImpl) node);
                    if (ret) synchronizeTwinNodeToDB((NodeImpl) node);
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding twin node {}...", new Object[]{node.getNodeID()});
                    this.nodeTwinNodes.remove((NodeImpl) node);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
        return ret;
    }

    static final String REMOVE_TWIN_NODE = "removeTwinNode";

    @Override
    public boolean removeTwinNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_TWIN_NODE, new Object[]{node});
        return ret;
    }

    @Override
    public boolean removeTwinNode(Node node) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeTwinNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (node instanceof NodeImpl) {
                ret = this.nodeTwinNodes.remove((NodeImpl) node);
                if (ret) removeTwindNodeFromDB((NodeImpl) node);
            }
        }
        return ret;
    }

    @Override
    public Set<EndpointImpl> getNodeEndpoints() {
        return this.nodeEndpoints;
    }

    static final String ADD_ENDPOINT = "addEndpoint";

    @Override
    public boolean addEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_ENDPOINT, new Object[]{endpoint});
        return ret;
    }

    @Override
    public boolean addEndpoint(Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addEndpoint(session, endpoint);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (endpoint instanceof EndpointImpl) {
                try {
                    ret = this.nodeEndpoints.add((EndpointImpl) endpoint);
                    if (ret) synchronizeEndpointToDB((EndpointImpl) endpoint);
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding endpoint {}...", new Object[]{endpoint.getEndpointID()});
                    this.nodeEndpoints.remove((EndpointImpl) endpoint);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
        return ret;
    }

    static final String REMOVE_ENDPOINT = "removeEndpoint";

    @Override
    public boolean removeEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_ENDPOINT, new Object[]{endpoint});
        return ret;
    }

    @Override
    public boolean removeEndpoint(Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeEndpoint(session, endpoint) ;
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (endpoint instanceof EndpointImpl) {
                ret = this.nodeEndpoints.remove((EndpointImpl) endpoint);
                if (ret) removeEndpointFromDB((EndpointImpl) endpoint);
            }
        }
        return ret;
    }

    public Vertex getElement() {
        return nodeVertex;
    }

    public void setElement(Element nodeVertex) {
        this.nodeVertex = (Vertex) nodeVertex;
        if (MappingDSGraphDB.isBlueprintsNeo4j() && this.nodeVertex instanceof Neo4j2Vertex)
            ((Neo4j2Vertex) this.nodeVertex).addLabel(MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE);
        this.nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE);
        this.nodeID = this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        log.debug("Node vertex has been initialized ({},{}).", new Object[]{this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                   this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    @Override
    public String getEntityCacheID() {
        return "V" + this.nodeID;
    }

    public void synchronizeToDB() throws MappingDSException {
        synchronizeDepthToDB();
        synchronizeNameToDB();
        synchronizePropertiesToDB();
        synchronizeContainerToDB();
        synchronizeParentNodeToDB();
        synchronizeChildNodesToDB();
        synchronizeTwinNodesToDB();
        synchronizeEndpointsToDB();
    }

    private void synchronizeDepthToDB() {
        if (this.nodeVertex != null) {
            log.debug("Synchronize node depth {} to db...", new Object[]{this.nodeDepth});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_DEPTH_KEY, this.nodeDepth);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeNameToDB() {
        if (this.nodeVertex != null && this.nodeName != null) {
            log.debug("Synchronize node name {} to db...", new Object[]{this.nodeName});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY, this.nodeName);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizePropertiesToDB() {
        if (nodeProperties != null) {
            Iterator<String> iterK = this.nodeProperties.keySet().iterator();
            while (iterK.hasNext()) {
                String key = iterK.next();
                Object value = nodeProperties.get(key);
                synchronizePropertyToDB(key, value);
            }
        }
    }

    private void synchronizePropertyToDB(String key, Object value) {
        if (this.nodeVertex != null && key != null && value != null) {
            log.debug("Synchronize node property {} to db...", new Object[]{key});
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyToDB(nodeVertex, key, value, MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeContainerToDB() {
        if (this.nodeVertex != null && nodeContainer != null && nodeContainer.getElement() != null) {
            log.debug("Synchronize node container {} to db...", new Object[]{this.nodeContainer.getContainerID()});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_CONT_KEY, this.nodeContainer.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeParentNodeToDB() {
        if (this.nodeVertex != null && nodeParentNode != null && nodeParentNode.getElement() != null) {
            log.debug("Synchronize node parent node {} to db...", new Object[]{this.nodeParentNode.getNodeID()});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY, this.nodeParentNode.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeChildNodesToDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            Iterator<NodeImpl> iterCN = this.nodeChildNodes.iterator();
            while (iterCN.hasNext()) {
                NodeImpl aChild = iterCN.next();
                synchronizeChildNodeToDB(aChild);
            }
        }
    }

    private void synchronizeChildNodeToDB(NodeImpl child) throws MappingDSException {
        if (this.nodeVertex != null && child.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            for (Vertex vertex : query.vertices()) {
                if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(child.getNodeID())) {
                    return;
                }
            }
            log.debug("Synchronize node child node {} to db...", new Object[]{child.getNodeID()});
            Edge owns = MappingDSGraphDB.createEdge(this.nodeVertex, child.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            owns.setProperty(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTwinNodesToDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            Iterator<NodeImpl> iterTN = this.nodeTwinNodes.iterator();
            while (iterTN.hasNext()) {
                NodeImpl aTwin = iterTN.next();
                synchronizeTwinNodeToDB(aTwin);
            }
        }
    }

    private void synchronizeTwinNodeToDB(NodeImpl twin) throws MappingDSException {
        if (this.nodeVertex != null && twin.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Vertex vertex : query.vertices()) {
                if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(twin.getNodeID())) {
                    return;
                }
            }
            log.debug("Synchronize node twin node {} to db...", new Object[]{twin.getNodeID()});
            MappingDSGraphDB.createEdge(this.nodeVertex, twin.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeEndpointsToDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            Iterator<EndpointImpl> iterEP = this.nodeEndpoints.iterator();
            while (iterEP.hasNext()) {
                EndpointImpl anEP = iterEP.next();
                synchronizeEndpointToDB(anEP);
            }
        }
    }

    private void synchronizeEndpointToDB(EndpointImpl endpoint) throws MappingDSException {
        if (this.nodeVertex != null && endpoint.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, true);
            for (Vertex vertex : query.vertices()) {
                Object id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
                if (id!=null && id instanceof String) {
                    if (((String) id).equals(endpoint.getEndpointID())) {
                        return;
                    }
                } else {
                    if (id == null)
                        log.error("CONSISTENCY ERROR: Vertex {} has null property {} !", new Object[]{vertex.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID});
                    else
                        log.error("CONSISTENCY ERROR: Vertex {} property {} is not a Long instance !", new Object[]{vertex.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID});
                }
            }
            log.debug("Synchronize node endpoint {} to db...", new Object[]{endpoint.getEndpointID()});
            Edge owns = MappingDSGraphDB.createEdge(this.nodeVertex, endpoint.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            owns.setProperty(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, true);
            MappingDSGraphDB.autocommit();
        }
    }

    public void synchronizeFromDB() {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            synchronizeIDFromDB();
            synchronizeDepthFromDB();
            synchronizeNameFromDB();
            synchronizePropertiesFromDB();
            synchronizeContainerFromDB();
            synchronizeParentNodeFromDB();
            synchronizeChildNodesFromDB();
            synchronizeTwinNodesFromDB();
            synchronizeEndpointsFromDB();
            isBeingSyncFromDB = false;
        }
    }

    private void synchronizeIDFromDB() {
        if (this.nodeVertex != null) {
            this.nodeID = this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        }
    }

    private void synchronizeDepthFromDB() {
        if (this.nodeVertex != null) {
            this.nodeDepth = nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_DEPTH_KEY);
        }
    }

    private void synchronizeNameFromDB() {
        if (this.nodeVertex != null) {
            this.nodeName = nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY);
        }
    }

    private void synchronizePropertiesFromDB() {
        if (this.nodeVertex != null) {
            if (nodeProperties == null) {
                nodeProperties = new HashMap<String, Object>();
            } else {
                nodeProperties.clear();
            }
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(nodeVertex, nodeProperties, MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY);
        }
    }

    private void removePropertyFromDB(String key) {
        if (this.nodeVertex != null) {
            log.debug("Remove node property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(nodeVertex, key, MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeContainerFromDB() {
        if (this.nodeVertex != null) {
            Object containerID = nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_CONT_KEY);
            if (containerID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) containerID);
                if (entity != null) {
                    if (entity instanceof ContainerImpl) {
                        nodeContainer = (ContainerImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a node.", nodeID);
                    }
                }
            }
        }
    }

    private void synchronizeParentNodeFromDB() {
        if (this.nodeVertex != null) {
            Object parentNodeID = nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY);
            if (parentNodeID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) parentNodeID);
                if (entity != null) {
                    if (entity instanceof NodeImpl) {
                        nodeParentNode = (NodeImpl) entity;
                    } else {
                        log.error("CACHE CONSISTENCY ERROR : entity {} is not a node.", nodeID);
                    }
                }
            }
        }
    }

    private void synchronizeChildNodesFromDB() {
        if (this.nodeVertex != null) {
            VertexQuery query = nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            this.nodeChildNodes.clear();
            for (Vertex vertex : query.vertices()) {
                NodeImpl child = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof NodeImpl) {
                        child = (NodeImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a node.", nodeID);
                    }
                }
                if (child != null) {
                    this.nodeChildNodes.add(child);
                }
            }
        }
    }

    private void removeChildNodeFromDB(NodeImpl node) {
        if (this.nodeVertex != null && node.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.OUT).equals(node.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTwinNodesFromDB() {
        if (this.nodeVertex != null) {
            VertexQuery query = nodeVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            this.nodeTwinNodes.clear();
            for (Vertex vertex : query.vertices()) {
                NodeImpl twin = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof NodeImpl) {
                        twin = (NodeImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a node.", nodeID);
                    }
                }
                if (entity != null) {
                    this.nodeTwinNodes.add(twin);
                }
            }
        }
    }

    private void removeTwindNodeFromDB(NodeImpl node) {
        if (this.nodeVertex != null && node.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Edge edge : query.edges()) {
                Vertex vo = edge.getVertex(Direction.OUT);
                Vertex vi = edge.getVertex(Direction.IN);
                if (vo != null && vo.equals(node.getElement()))
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                if (vi != null && vi.equals(node.getElement()))
                    MappingDSGraphDB.getGraph().removeEdge(edge);
            }
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeEndpointsFromDB() {
        if (this.nodeVertex != null) {
            VertexQuery query = nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, true);
            this.nodeEndpoints.clear();
            for (Vertex vertex : query.vertices()) {
                EndpointImpl endpoint = null;
                log.debug("Get {} from vertex {}", new Object[]{MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID,vertex.toString()});
                Object id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
                if (id!=null && id instanceof String) {
                    log.debug("Get entity {} ...", new Object[]{id});
                    MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) id);
                    if (entity != null) {
                        if (entity instanceof EndpointImpl) {
                            endpoint = (EndpointImpl) entity;
                        } else {
                            log.error("CONSISTENCY ERROR : entity {} is not a node.", nodeID);
                        }
                    }
                } else {
                    if (id==null)
                        log.error("CONSISTENCY ERROR : Vertex {} has null property {} !", new Object[]{vertex.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID});
                    else
                        log.error("CONSISTENCY ERROR : Vertex {} property {} is not a Long instance !", new Object[]{vertex.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID});
                }
                if (endpoint != null) {
                    this.nodeEndpoints.add(endpoint);
                }
            }
        }
    }

    private void removeEndpointFromDB(EndpointImpl endpoint) {
        MappingDSGraphDB.deleteEntity((EndpointImpl) endpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NodeImpl tmp = (NodeImpl) o;
        if (this.nodeVertex == null) {
            return super.equals(o);
        }
        boolean nameEq = false;
        if (this.nodeName != null && this.nodeContainer != null) {
            if (this.nodeParentNode != null)
                nameEq = this.nodeName.equals(tmp.getNodeName()) && this.nodeParentNode.equals(tmp.getNodeParentNode());
            else
                nameEq = this.nodeName.equals(tmp.getNodeName()) && this.nodeContainer.equals(tmp.getNodeContainer());
        }
        return (this.getNodeID().equals(tmp.getNodeID()) && nameEq);
    }

    @Override
    public int hashCode() {
        return (this.nodeID != null && !this.nodeID.equals("")) ? this.getNodeID().hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Node{ID='%s', nodename='%s'}", this.getNodeID(), this.nodeName);
    }
}