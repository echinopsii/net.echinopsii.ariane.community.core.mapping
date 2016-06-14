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
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import com.tinkerpop.blueprints.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNodeAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeImpl extends SProxNodeAbs implements SProxNode, MappingDSBlueprintsCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(NodeImpl.class);

    private transient Vertex nodeVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public void setNodeName(String name) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setNodeName(session, name);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getNodeName() == null || !super.getNodeName().equals(name)) {
                super.setNodeName(name);
                synchronizeNameToDB();
            }
        }
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
            if (super.getNodeContainer() == null || !super.getNodeContainer().equals(container)) {
                if (container!=null && container instanceof ContainerImpl) {
                    Container previousParentContainer = super.getNodeContainer();
                    super.setNodeContainer(container);
                    if (previousParentContainer!=null) previousParentContainer.removeContainerNode(this);
                    if (!container.getContainerNodes().contains(this) && super.getNodeParentNode()==null) container.addContainerNode(this);
                    synchronizeContainerToDB();
                } else if (container == null) {
                    Container previousParentContainer = super.getNodeContainer();
                    super.setNodeContainer(null);
                    synchronizeContainerToDB();
                    if (previousParentContainer!=null && previousParentContainer.getContainerNodes().contains(this))
                        previousParentContainer.removeContainerNode(this);
                    log.info("Node " + this.toString() + " has no more parent container. This state should be avoided.");
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
                super.addNodeProperty(propertyKey, value);
                synchronizePropertyToDB(propertyKey, value);
                log.debug("Set node {} property : ({},{})", new Object[]{this.getNodeID(),
                        propertyKey,
                        super.getNodeProperties().get(propertyKey)});
            }
        }
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
            super.removeNodeProperty(propertyKey);
            removePropertyFromDB(propertyKey);
        }
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
            if (super.getNodeParentNode() == null || !super.getNodeParentNode().equals(node)) {
                if (node instanceof NodeImpl || node ==null) {
                    Node previousParentNode = super.getNodeParentNode();
                    super.setNodeParentNode(node);
                    if (node != null) {
                        if (previousParentNode==null && super.getNodeContainer()!=null) super.getNodeContainer().removeContainerNode(this);
                        if (!node.getNodeChildNodes().contains(this)) node.addNodeChildNode(this);
                    } else if (super.getNodeContainer()!=null) super.getNodeContainer().addContainerNode(this);
                    synchronizeParentNodeToDB();
                }
            }
        }
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
                    ret = super.addNodeChildNode(node);
                    if (ret) {
                        if (node.getNodeParentNode()==null || !node.getNodeParentNode().equals(this)) node.setNodeParentNode(this);
                        synchronizeChildNodeToDB((NodeImpl) node);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding child node {}...", new Object[]{node.getNodeID()});
                    super.removeNodeChildNode(node);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
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
                ret = super.removeNodeChildNode(node);
                if (ret) {
                    node.setNodeParentNode(null);
                    removeChildNodeFromDB((NodeImpl) node);
                }
            }
        }
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
                    ret = super.addTwinNode(node);
                    if (ret) {
                        node.addTwinNode(this);
                        synchronizeTwinNodeToDB((NodeImpl) node);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding twin node {}...", new Object[]{node.getNodeID()});
                    super.removeTwinNode(node);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
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
                ret = super.removeTwinNode(node);
                if (ret) {
                    node.removeTwinNode(this);
                    removeTwindNodeFromDB((NodeImpl) node);
                }
            }
        }
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
                    ret = super.addEndpoint(endpoint);
                    if (ret) {
                        if (endpoint.getEndpointParentNode()==null || !endpoint.getEndpointParentNode().equals(this))
                            endpoint.setEndpointParentNode(this);
                        synchronizeEndpointToDB((EndpointImpl) endpoint);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding endpoint {}...", new Object[]{endpoint.getEndpointID()});
                    super.removeEndpoint(endpoint);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
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
                ret = super.removeEndpoint(endpoint);
                if (ret) {
                    if (endpoint.getEndpointParentNode().equals(this)) endpoint.setEndpointParentNode(null);
                    removeEndpointFromDB((EndpointImpl) endpoint);
                }
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
        super.setNodeID((String) this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        log.debug("Node vertex has been initialized ({},{}).", new Object[]{this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                   this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    @Override
    public String getEntityCacheID() {
        return "V" + super.getNodeID();
    }

    public void synchronizeToDB() throws MappingDSException {
        synchronizeNameToDB();
        synchronizePropertiesToDB();
        synchronizeContainerToDB();
        synchronizeParentNodeToDB();
        synchronizeChildNodesToDB();
        synchronizeTwinNodesToDB();
        synchronizeEndpointsToDB();
    }

    private void synchronizeNameToDB() {
        if (this.nodeVertex != null && super.getNodeName() != null) {
            log.debug("Synchronize node name {} to db...", new Object[]{super.getNodeName()});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY, super.getNodeName());
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizePropertiesToDB() {
        if (super.getNodeProperties() != null)
            for (String key : super.getNodeProperties().keySet()) {
                Object value = super.getNodeProperties().get(key);
                synchronizePropertyToDB(key, value);
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
        if (this.nodeVertex != null && super.getNodeContainer() != null && ((ContainerImpl)super.getNodeContainer()).getElement() != null) {
            log.debug("Synchronize node container {} to db...", new Object[]{super.getNodeContainer().getContainerID()});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_CONT_KEY, ((ContainerImpl) super.getNodeContainer()).getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            MappingDSGraphDB.autocommit();
        } else if (this.nodeVertex!=null && this.nodeVertex.getPropertyKeys().contains(MappingDSGraphPropertyNames.DD_NODE_CONT_KEY) && super.getNodeContainer()==null) {
            nodeVertex.removeProperty(MappingDSGraphPropertyNames.DD_NODE_CONT_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeParentNodeToDB() {
        if (this.nodeVertex != null && super.getNodeParentNode() != null && ((NodeImpl)super.getNodeParentNode()).getElement() != null) {
            log.debug("Synchronize node parent node {} to db...", new Object[]{super.getNodeParentNode().getNodeID()});
            nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY, ((NodeImpl) super.getNodeParentNode()).getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            MappingDSGraphDB.autocommit();
        } else if (this.nodeVertex != null && super.getNodeParentNode()==null && nodeVertex.getPropertyKeys().contains(MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY)) {
            nodeVertex.removeProperty(MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeChildNodesToDB() throws MappingDSException {
        if (this.nodeVertex != null)
            for (Node aChild : super.getNodeChildNodes()) synchronizeChildNodeToDB((NodeImpl) aChild);
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
        if (this.nodeVertex != null)
            for (Node aTwin : super.getTwinNodes()) synchronizeTwinNodeToDB((NodeImpl) aTwin);
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
        if (this.nodeVertex != null)
            for (Endpoint anEP : super.getNodeEndpoints()) synchronizeEndpointToDB((EndpointImpl) anEP);
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
                    if (id.equals(endpoint.getEndpointID())) return;
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

    public void synchronizeFromDB() throws MappingDSException {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            synchronizeIDFromDB();
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
            super.setNodeID((String) this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        }
    }

    private void synchronizeNameFromDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            super.setNodeName((String) nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_NAME_KEY));
        }
    }

    private void synchronizePropertiesFromDB() {
        if (this.nodeVertex != null) {
            if (super.getNodeProperties() != null) super.getNodeProperties().clear();
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(nodeVertex, super.getNodeProperties(), MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY);
        }
    }

    private void removePropertyFromDB(String key) {
        if (this.nodeVertex != null) {
            log.debug("Remove node property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(nodeVertex, key, MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeContainerFromDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            Object containerID = nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_CONT_KEY);
            if (containerID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) containerID);
                if (entity != null) {
                    if (entity instanceof ContainerImpl) super.setNodeContainer((Container) entity);
                    else log.error("CONSISTENCY ERROR : entity {} is not a node.", super.getNodeID());
                }
            }
        }
    }

    private void synchronizeParentNodeFromDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            Object parentNodeID = nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY);
            if (parentNodeID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) parentNodeID);
                if (entity != null) {
                    if (entity instanceof NodeImpl) super.setNodeParentNode((Node)entity);
                    else log.error("CACHE CONSISTENCY ERROR : entity {} is not a node.", super.getNodeID());
                }
            }
        }
    }

    private void synchronizeChildNodesFromDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            VertexQuery query = nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            super.getNodeChildNodes().clear();
            for (Vertex vertex : query.vertices()) {
                NodeImpl child = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof NodeImpl) child = (NodeImpl) entity;
                    else log.error("CONSISTENCY ERROR : entity {} is not a node.", super.getNodeID());
                }
                if (child != null)  super.addNodeChildNode(child);
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
                if (edge.getVertex(Direction.IN).equals(node.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTwinNodesFromDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            VertexQuery query = nodeVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            super.getTwinNodes().clear();
            for (Vertex vertex : query.vertices()) {
                NodeImpl twin = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity != null) {
                    if (entity instanceof NodeImpl) twin = (NodeImpl) entity;
                    else log.error("CONSISTENCY ERROR : entity {} is not a node.", super.getNodeID());
                }
                if (entity != null) super.addTwinNode(twin);
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

    private void synchronizeEndpointsFromDB() throws MappingDSException {
        if (this.nodeVertex != null) {
            VertexQuery query = nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, true);
            super.getNodeEndpoints().clear();
            for (Vertex vertex : query.vertices()) {
                EndpointImpl endpoint = null;
                log.debug("Get {} from vertex {}", new Object[]{MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID,vertex.toString()});
                Object id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
                if (id!=null && id instanceof String) {
                    log.debug("Get entity {} ...", new Object[]{id});
                    MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) id);
                    if (entity != null) {
                        if (entity instanceof EndpointImpl) endpoint = (EndpointImpl) entity;
                        else log.error("CONSISTENCY ERROR : entity {} is not a node.", super.getNodeID());
                    }
                } else {
                    if (id==null)
                        log.error("CONSISTENCY ERROR : Vertex {} has null property {} !", new Object[]{vertex.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID});
                    else
                        log.error("CONSISTENCY ERROR : Vertex {} property {} is not a Long instance !", new Object[]{vertex.toString(), MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID});
                }
                if (endpoint != null) super.addEndpoint(endpoint);
            }
        }
    }

    private void removeEndpointFromDB(EndpointImpl endpoint) {
        if (this.nodeVertex != null && endpoint.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, true);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.IN).equals(endpoint.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }
}