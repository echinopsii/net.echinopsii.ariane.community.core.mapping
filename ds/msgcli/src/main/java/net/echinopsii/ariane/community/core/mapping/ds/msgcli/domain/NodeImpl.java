/**
 * Mapping Datastore Messsaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
 * Author: mathilde.ffrench@echinopsii.net
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNodeAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.ContainerSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.EndpointSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.NodeSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class NodeImpl extends SProxNodeAbs implements SProxNode {

    class NodeReplyWorker implements AppMsgWorker {
        private NodeImpl node;

        public NodeReplyWorker(NodeImpl node) {
            this.node = node;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (node!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));
                    if (body != null) {
                        try {
                            NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(body);
                            if (node.getNodeID() == null || node.getNodeID().equals(jsonDeserializedNode.getNodeID()))
                                node.synchronizeFromJSON(jsonDeserializedNode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    switch (rc) {
                        case MomMsgTranslator.MSG_RET_NOT_FOUND:
                            NodeImpl.log.debug("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                            break;
                        default:
                            NodeImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                            break;
                    }
                }
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(NodeImpl.class);

    private NodeReplyWorker nodeReplyWorker = new NodeReplyWorker(this);
    private String containerID;
    private String parentNodeID;
    private List<String> childNodesID;
    private List<String> twinNodesID;
    private List<String> endpointsID;

    public NodeReplyWorker getNodeReplyWorker() {
        return nodeReplyWorker;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getParentNodeID() {
        return parentNodeID;
    }

    public void setParentNodeID(String parentNodeID) {
        this.parentNodeID = parentNodeID;
    }

    public List<String> getChildNodesID() {
        return childNodesID;
    }

    public void setChildNodesID(List<String> childNodesID) {
        this.childNodesID = childNodesID;
    }

    public List<String> getTwinNodesID() {
        return twinNodesID;
    }

    public void setTwinNodesID(List<String> twinNodesID) {
        this.twinNodesID = twinNodesID;
    }

    public void setEndpointsID(List<String> endpointsID) {
        this.endpointsID = endpointsID;
    }

    public List<String> getEndpointsID() {
        return endpointsID;
    }

    public void synchronizeFromJSON(NodeJSON.JSONDeserializedNode jsonDeserializedNode) throws MappingDSException {
        super.setNodeID(jsonDeserializedNode.getNodeID());
        super.setNodeName(jsonDeserializedNode.getNodeName());
        super.getNodeProperties().clear();
        if (jsonDeserializedNode.getNodeProperties()!=null)
            for (PropertiesJSON.TypedPropertyField typedPropertyField : jsonDeserializedNode.getNodeProperties())
                try {
                    super.addNodeProperty(typedPropertyField.getPropertyName(), PropertiesJSON.getValueFromTypedPropertyField(typedPropertyField));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new MappingDSException("Error with property " + typedPropertyField.getPropertyName() + " deserialization : " + e.getMessage());
                }
        this.setContainerID(jsonDeserializedNode.getNodeContainerID());
        this.setParentNodeID(jsonDeserializedNode.getNodeParentNodeID());
        this.setChildNodesID(jsonDeserializedNode.getNodeChildNodesID());
        this.setTwinNodesID(jsonDeserializedNode.getNodeTwinNodesID());
        this.setEndpointsID(jsonDeserializedNode.getNodeEndpointsID());
    }

    @Override
    public void setNodeName(String name) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if ((super.getNodeName()!=null && !super.getNodeName().equals(name)) ||
                    (super.getNodeName() == null && name != null)) {
                String clientThreadName = Thread.currentThread().getName();
                String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                Map<String, Object> message = new HashMap<>();
                message.put(MomMsgTranslator.OPERATION_FDN, OP_SET_NODE_NAME);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                message.put(SProxNodeSce.PARAM_NODE_NAME, name);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = null;
                try {
                    retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                } catch (TimeoutException e) {
                    throw new MappingDSException(e.getMessage());
                }
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setNodeName(name);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            }
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public Container getNodeContainer() {
        try {
            Node update = NodeSceImpl.internalGetNode(super.getNodeID());
            this.setContainerID(((NodeImpl) update).getContainerID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (containerID!=null && (super.getNodeContainer()==null || !super.getNodeContainer().getContainerID().equals(containerID))) {
            try {
                super.setNodeContainer(ContainerSceImpl.internalGetContainer(containerID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        } else if (super.getNodeContainer()!=null && containerID==null)
            try {
                super.setNodeContainer(null);
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getNodeContainer();
    }

    @Override
    public void setNodeContainer(Container container) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (container==null || container.getContainerID()!=null) {
                if ((container==null) || (super.getNodeContainer()!=null && !super.getNodeContainer().getContainerID().equals(container.getContainerID())) ||
                    (containerID!=null && !containerID.equals(container.getContainerID())) || (containerID==null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_SET_NODE_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(Container.TOKEN_CT_ID, (container != null) ? container.getContainerID() : MappingSce.GLOBAL_PARAM_OBJ_NONE);
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Container previousPContainer = super.getNodeContainer();
                        if (previousPContainer!=null) {
                            try {
                                if (retMsg.containsKey(Node.JOIN_PREVIOUS_PCONTAINER)) {
                                    ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                            (String) retMsg.get(Node.JOIN_PREVIOUS_PCONTAINER)
                                    );
                                    ((ContainerImpl)previousPContainer).synchronizeFromJSON(jsonDeserializedContainer);
                                }
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        super.setNodeContainer(container);
                        containerID = (container!=null) ? container.getContainerID() : null;
                        if (container!=null) {
                            try {
                                if (retMsg.containsKey(Node.JOIN_CURRENT_PCONTAINER)) {
                                    ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                            (String) retMsg.get(Node.JOIN_CURRENT_PCONTAINER)
                                    );
                                    ((ContainerImpl) container).synchronizeFromJSON(jsonDeserializedContainer);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public void addNodeProperty(String propertyKey, Object value) throws MappingDSException {
        if (super.getNodeID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_NODE_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
            try {
                message.put(MappingSce.GLOBAL_PARAM_PROP_FIELD, PropertiesJSON.propertyFieldToTypedPropertyField(propertyKey, value).toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new MappingDSException(e.getMessage());
            }
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = null;
            try {
                retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
            } catch (TimeoutException e) {
                throw new MappingDSException(e.getMessage());
            }
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.addNodeProperty(propertyKey, value);
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public void removeNodeProperty(String propertyKey) throws MappingDSException {
        if (super.getNodeID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_NODE_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
            message.put(MappingSce.GLOBAL_PARAM_PROP_NAME, propertyKey);
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = null;
            try {
                retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
            } catch (TimeoutException e) {
                throw new MappingDSException(e.getMessage());
            }
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.removeNodeProperty(propertyKey);
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public Node getNodeParentNode() {
        try {
            Node update = NodeSceImpl.internalGetNode(super.getNodeID());
            this.setParentNodeID(((NodeImpl) update).getParentNodeID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }
        if (parentNodeID!=null && (super.getNodeParentNode()==null || !super.getNodeParentNode().getNodeID().equals(parentNodeID))) {
            try {
                super.setNodeParentNode(NodeSceImpl.internalGetNode(parentNodeID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        } else if (super.getNodeParentNode()!=null && parentNodeID==null)
            try {
                super.setNodeParentNode(null);
            } catch (MappingDSException e) {
                e.printStackTrace();
            }

        return super.getNodeParentNode();
    }

    @Override
    public void setNodeParentNode(Node node) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (node == null || (node.getNodeID()!=null)) {
                if ((super.getNodeParentNode()!=null && node!=null && !super.getNodeParentNode().getNodeID().equals(node.getNodeID())) ||
                    (parentNodeID!=null && node!=null && !parentNodeID.equals(node.getNodeID())) ||
                    (parentNodeID==null && node!=null) || (parentNodeID!=null && node==null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_SET_NODE_PARENT_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(NodeSce.PARAM_NODE_PNID, (node != null) ? node.getNodeID() : MappingSce.GLOBAL_PARAM_OBJ_NONE);
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Node previousPNode = super.getNodeParentNode();
                        if (previousPNode!=null) {
                            try {
                                if (retMsg.containsKey(Node.JOIN_PREVIOUS_PNODE)) {
                                    NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                            (String) retMsg.get(Node.JOIN_PREVIOUS_PNODE)
                                    );
                                    ((NodeImpl)previousPNode).synchronizeFromJSON(jsonDeserializedNode);
                                }
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        super.setNodeParentNode(node);
                        parentNodeID = (node!=null) ? node.getNodeID() : null;
                        if (node!=null) {
                            try {
                                if (retMsg.containsKey(Node.JOIN_CURRENT_PNODE)) {
                                    NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                            (String) retMsg.get(Node.JOIN_CURRENT_PNODE)
                                    );
                                    ((NodeImpl) node).synchronizeFromJSON(jsonDeserializedNode);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public Set<Node> getNodeChildNodes(){
        try {
            Node update = NodeSceImpl.internalGetNode(super.getNodeID());
            this.setChildNodesID(((NodeImpl) update).getChildNodesID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Node node : new ArrayList<>(super.getNodeChildNodes()))
            if (!childNodesID.contains(node.getNodeID()))
                super.getNodeChildNodes().remove(node);

        for (String nodeID : childNodesID)
            try {
                boolean toAdd = true;
                for (Node node : super.getNodeChildNodes())
                    if (node.getNodeID().equals(nodeID)) toAdd = false;
                if (toAdd) super.getNodeChildNodes().add(NodeSceImpl.internalGetNode(nodeID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getNodeChildNodes();
    }

    @Override
    public boolean addNodeChildNode(Node node) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (node!=null && node.getNodeID()!=null) {
                if ((super.getNodeChildNodes()!=null && !super.getNodeChildNodes().contains(node)) ||
                    (childNodesID!=null && !childNodesID.contains(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_NODE_CHILD_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(NodeSce.PARAM_NODE_CNID, node.getNodeID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addNodeChildNode(node);
                        childNodesID.add(node.getNodeID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY)) {
                                NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY)
                                );
                                ((NodeImpl) node).synchronizeFromJSON(jsonDeserializedNode);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public boolean removeNodeChildNode(Node node) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (node!=null && node.getNodeID()!=null) {
                if ((super.getNodeChildNodes()!=null && super.getNodeChildNodes().contains(node)) ||
                        (childNodesID!=null && childNodesID.contains(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_NODE_CHILD_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(NodeSce.PARAM_NODE_CNID, node.getNodeID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeNodeChildNode(node);
                        childNodesID.remove(node.getNodeID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY)) {
                                NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY)
                                );
                                ((NodeImpl) node).synchronizeFromJSON(jsonDeserializedNode);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public Set<Node> getTwinNodes() {
        try {
            Node update = NodeSceImpl.internalGetNode(super.getNodeID());
            this.setTwinNodesID(((NodeImpl) update).getTwinNodesID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Node node : new ArrayList<>(super.getTwinNodes()))
            if (!twinNodesID.contains(node.getNodeID()))
                super.getTwinNodes().remove(node);

        for (String nodeID : twinNodesID)
            try {
                boolean toAdd = true;
                for (Node node : super.getTwinNodes())
                    if (node.getNodeID().equals(nodeID)) toAdd = false;
                if (toAdd) super.getTwinNodes().add(NodeSceImpl.internalGetNode(nodeID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getTwinNodes();
    }

    @Override
    public boolean addTwinNode(Node node) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (node!=null && node.getNodeID()!=null) {
                if ((super.getTwinNodes()!=null && !super.getTwinNodes().contains(node)) ||
                     (twinNodesID!=null && !twinNodesID.contains(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_TWIN_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(NodeSce.PARAM_NODE_TNID, node.getNodeID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addTwinNode(node);
                        twinNodesID.add(node.getNodeID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)) {
                                NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)
                                );
                                ((NodeImpl) node).synchronizeFromJSON(jsonDeserializedNode);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    } else return false;
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public boolean removeTwinNode(Node node) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (node!=null && node.getNodeID()!=null) {
                if ((super.getTwinNodes()!=null && super.getTwinNodes().contains(node)) ||
                    (twinNodesID!=null && twinNodesID.contains(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_TWIN_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(NodeSce.PARAM_NODE_TNID, node.getNodeID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeTwinNode(node);
                        twinNodesID.remove(node.getNodeID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)) {
                                NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)
                                );
                                ((NodeImpl) node).synchronizeFromJSON(jsonDeserializedNode);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    } else return false;
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public Set<Endpoint> getNodeEndpoints() {
        try {
            Node update = NodeSceImpl.internalGetNode(super.getNodeID());
            this.setEndpointsID(((NodeImpl) update).getEndpointsID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Endpoint endpoint : new ArrayList<>(super.getNodeEndpoints()))
            if (!endpointsID.contains(endpoint.getEndpointID()))
                super.getNodeEndpoints().remove(endpoint);

        for (String endpointID : endpointsID)
            try {
                boolean toAdd = true;
                for (Endpoint endpoint : super.getNodeEndpoints())
                    if (endpoint.getEndpointID().equals(endpointID)) toAdd = false;
                if (toAdd) super.getNodeEndpoints().add(EndpointSceImpl.internalGetEndpoint(endpointID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getNodeEndpoints();
    }

    @Override
    public boolean addEndpoint(Endpoint endpoint) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (endpoint!=null && endpoint.getEndpointID()!=null) {
                if ((super.getNodeEndpoints()!=null && !super.getNodeEndpoints().contains(endpoint)) ||
                    (endpointsID!=null && !endpointsID.contains(endpoint.getEndpointID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_ENDPOINT);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(Endpoint.TOKEN_EP_ID, endpoint.getEndpointID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addEndpoint(endpoint);
                        endpointsID.add(endpoint.getEndpointID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY)) {
                                EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint = EndpointJSON.JSON2Endpoint(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY)
                                );
                                ((EndpointImpl) endpoint).synchronizeFromJSON(jsonDeserializedEndpoint);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    } else return false;
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided endpoint is null or not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }

    @Override
    public boolean removeEndpoint(Endpoint endpoint) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (endpoint!=null && endpoint.getEndpointID()!=null) {
                if ((super.getNodeEndpoints()!=null && super.getNodeEndpoints().contains(endpoint)) ||
                        (endpointsID!=null && endpointsID.contains(endpoint.getEndpointID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_ENDPOINT);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(Endpoint.TOKEN_EP_ID, endpoint.getEndpointID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, nodeReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeEndpoint(endpoint);
                        endpointsID.remove(endpoint.getEndpointID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY)) {
                                EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint = EndpointJSON.JSON2Endpoint(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY)
                                );
                                ((EndpointImpl) endpoint).synchronizeFromJSON(jsonDeserializedEndpoint);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    } else return false;
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided endpoint is null or not initialized !");
        } else throw new MappingDSException("This node is not initialized !");
    }
}
