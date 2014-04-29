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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.domain;

import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.*;
import com.spectral.cc.core.mapping.ds.domain.Container;
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.tinkerpop.blueprints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NodeImpl implements Node, MappingDSCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(NodeImpl.class);

    private long nodeID = 0;
    private String nodeName = null;
    private long nodeDepth = 0;
    private ContainerImpl nodeContainer = null;
    private HashMap<String, Object> nodeProperties = null;
    private NodeImpl nodeParentNode = null;
    private Set<NodeImpl> nodeChildNodes = new HashSet<NodeImpl>();
    private Set<NodeImpl> nodeTwinNodes = new HashSet<NodeImpl>();
    private Set<EndpointImpl> nodeEndpoints = new HashSet<EndpointImpl>();

    private Vertex nodeVertex = null;
    private boolean isBeingSyncFromDB = false;

    @Override
    public long getNodeID() {
        return this.nodeID;
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    public void setNodeName(String name) {
        if (this.nodeName == null || !this.nodeName.equals(name)) {
            this.nodeName = name;
            synchronizeNameToDB();
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

    @Override
    public void setNodeContainer(Container container) {
        if (this.nodeContainer == null || !this.nodeContainer.equals(container)) {
            if (container instanceof ContainerImpl) {
                this.nodeContainer = (ContainerImpl) container;
                synchronizeContainerToDB();
            }
        }
    }

    @Override
    public HashMap<String, Object> getNodeProperties() {
        return this.nodeProperties;
    }

    @Override
    public void addNodeProperty(String propertyKey, Object value) {
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

    @Override
    public void removeNodeProperty(String propertyKey) {
        if (this.nodeProperties!=null) {
            this.nodeProperties.remove(propertyKey);
            removePropertyFromDB(propertyKey);
        }
    }

    @Override
    public NodeImpl getNodeParentNode() {
        return this.nodeParentNode;
    }

    @Override
    public void setNodeParentNode(Node node) {
        if (this.nodeParentNode == null || !this.nodeParentNode.equals(node)) {
            if (node instanceof NodeImpl) {
                this.nodeParentNode = (NodeImpl) node;
                synchronizeParentNodeToDB();
            }
        }
    }

    @Override
    public Set<NodeImpl> getNodeChildNodes() {
        return this.nodeChildNodes;
    }

    @Override
    public boolean addNodeChildNode(Node node) {
        if (node instanceof NodeImpl) {
            boolean ret = false;
            try {
                ret = this.nodeChildNodes.add((NodeImpl) node);
                if (ret) {
                    synchronizeChildNodeToDB((NodeImpl) node);
                }
            } catch (MappingDSGraphDBException E) {
                E.printStackTrace();
                log.error("Exception while adding child node {}...", new Object[]{node.getNodeID()});
                this.nodeChildNodes.remove((NodeImpl) node);
                MappingDSGraphDB.autorollback();
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeNodeChildNode(Node node) {
        if (node instanceof NodeImpl) {
            boolean ret = this.nodeChildNodes.remove((NodeImpl) node);
            if (ret) {
                removeChildNodeFromDB((NodeImpl) node);
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public Set<NodeImpl> getTwinNodes() {
        return this.nodeTwinNodes;
    }

    @Override
    public boolean addTwinNode(Node node) {
        if (node instanceof NodeImpl) {
            boolean ret = false;
            try {
                ret = this.nodeTwinNodes.add((NodeImpl) node);
                if (ret) {
                    synchronizeTwinNodeToDB((NodeImpl) node);
                }
            } catch (MappingDSGraphDBException E) {
                E.printStackTrace();
                log.error("Exception while adding twin node {}...", new Object[]{node.getNodeID()});
                this.nodeTwinNodes.remove((NodeImpl) node);
                MappingDSGraphDB.autorollback();
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeTwinNode(Node node) {
        if (node instanceof NodeImpl) {
            boolean ret = this.nodeTwinNodes.remove((NodeImpl) node);
            if (ret) {
                removeTwindNodeFromDB((NodeImpl) node);
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public Set<EndpointImpl> getNodeEndpoints() {
        return this.nodeEndpoints;
    }

    @Override
    public boolean addEnpoint(Endpoint endpoint) {
        if (endpoint instanceof EndpointImpl) {
            boolean ret = false;
            try {
                ret = this.nodeEndpoints.add((EndpointImpl) endpoint);
                if (ret) {
                    synchronizeEndpointToDB((EndpointImpl) endpoint);
                }
            } catch (MappingDSGraphDBException E) {
                E.printStackTrace();
                log.error("Exception while adding endpoint {}...", new Object[]{endpoint.getEndpointID()});
                this.nodeEndpoints.remove((EndpointImpl) endpoint);
                MappingDSGraphDB.autorollback();
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEndpoint(Endpoint endpoint) {
        if (endpoint instanceof EndpointImpl) {
            boolean ret = this.nodeEndpoints.remove((EndpointImpl) endpoint);
            if (ret) {
                removeEndpointFromDB((EndpointImpl) endpoint);
            }
            return ret;
        } else {
            return false;
        }
    }

    public Vertex getElement() {
        return nodeVertex;
    }

    public void setElement(Element nodeVertex) {
        this.nodeVertex = (Vertex) nodeVertex;
        this.nodeVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE);
        this.nodeID = this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
        log.debug("Node vertex has been initialized ({},{}).", new Object[]{this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                   this.nodeVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    public void synchronizeToDB() throws MappingDSGraphDBException {
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

    private void synchronizeChildNodesToDB() throws MappingDSGraphDBException {
        if (this.nodeVertex != null) {
            Iterator<NodeImpl> iterCN = this.nodeChildNodes.iterator();
            while (iterCN.hasNext()) {
                NodeImpl aChild = iterCN.next();
                synchronizeChildNodeToDB(aChild);
            }
        }
    }

    private void synchronizeChildNodeToDB(NodeImpl child) throws MappingDSGraphDBException {
        if (this.nodeVertex != null && child.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            for (Vertex vertex : query.vertices()) {
                if ((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID) == child.getNodeID()) {
                    return;
                }
            }
            log.debug("Synchronize node child node {} to db...", new Object[]{child.getNodeID()});
            Edge owns = MappingDSGraphDB.createEdge(this.nodeVertex, child.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            owns.setProperty(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTwinNodesToDB() throws MappingDSGraphDBException {
        if (this.nodeVertex != null) {
            Iterator<NodeImpl> iterTN = this.nodeTwinNodes.iterator();
            while (iterTN.hasNext()) {
                NodeImpl aTwin = iterTN.next();
                synchronizeTwinNodeToDB(aTwin);
            }
        }
    }

    private void synchronizeTwinNodeToDB(NodeImpl twin) throws MappingDSGraphDBException {
        if (this.nodeVertex != null && twin.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.BOTH);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Vertex vertex : query.vertices()) {
                if ((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID) == twin.getNodeID()) {
                    return;
                }
            }
            log.debug("Synchronize node twin node {} to db...", new Object[]{twin.getNodeID()});
            MappingDSGraphDB.createEdge(this.nodeVertex, twin.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeEndpointsToDB() throws MappingDSGraphDBException {
        if (this.nodeVertex != null) {
            Iterator<EndpointImpl> iterEP = this.nodeEndpoints.iterator();
            while (iterEP.hasNext()) {
                EndpointImpl anEP = iterEP.next();
                synchronizeEndpointToDB(anEP);
            }
        }
    }

    private void synchronizeEndpointToDB(EndpointImpl endpoint) throws MappingDSGraphDBException {
        if (this.nodeVertex != null && endpoint.getElement() != null) {
            VertexQuery query = this.nodeVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, true);
            for (Vertex vertex : query.vertices()) {
                Object id = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
                if (id!=null && id instanceof Long) {
                    if (((long) id) == endpoint.getEndpointID()) {
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
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) containerID);
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
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) parentNodeID);
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
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
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
                    MappingDSGraphDB.getDDgraph().removeEdge(edge);
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
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
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
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_TWIN_LABEL_KEY);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.OUT).equals(node.getElement())) {
                    MappingDSGraphDB.getDDgraph().removeEdge(edge);
                }
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
                if (id!=null && id instanceof Long) {
                    log.debug("Get entity {} ...", new Object[]{id});
                    MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) id);
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
        if (this.nodeName != null) {
            nameEq = this.nodeName.equals(tmp.getNodeName());
        }
        return (this.getNodeID() == tmp.getNodeID() || nameEq);
    }

    @Override
    public int hashCode() {
        return this.nodeVertex != null ? new Long(this.getNodeID()).hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Node{ID='%d', nodename='%s'}", this.getNodeID(), this.nodeName);
    }
}