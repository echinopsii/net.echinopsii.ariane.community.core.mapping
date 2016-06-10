/**
 * Mapping Datastore Messaging Driver Implementation :
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSceAbs;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NodeSceImpl extends SProxNodeSceAbs<NodeImpl> {

    private static final Logger log = LoggerFactory.getLogger(NodeSceImpl.class);


    @Override
    public Node createNode(String nodeName, String containerID, String parentNodeID) throws MappingDSException {
        NodeImpl node = new NodeImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_CREATE_NODE);
        message.put(NodeSce.PARAM_NODE_NAME, nodeName);
        message.put(Container.TOKEN_CT_ID, containerID);
        message.put(NodeSce.PARAM_NODE_PNID, parentNodeID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, node.getNodeReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return node;
    }

    @Override
    public void deleteNode(String nodeID) throws MappingDSException {
        NodeImpl node = new NodeImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_DELETE_NODE);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, nodeID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, node.getNodeReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    public static Node internalGetNode(String id) throws MappingDSException {
        NodeImpl node = new NodeImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_GET_NODE);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, id);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, node.getNodeReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return node;
    }

    @Override
    public Node getNode(String id) throws MappingDSException {
        return internalGetNode(id);
    }

    @Override
    public Node getNodeByEndpointURL(String endpointURL) throws MappingDSException {
        NodeImpl node = new NodeImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_GET_NODE_BY_EPURL);
        message.put(Endpoint.TOKEN_EP_URL, endpointURL);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, node.getNodeReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return node;
    }

    @Override
    public Node getNodeByName(Node parentNode, String nodeName) throws MappingDSException {
        if (parentNode!=null && parentNode.getNodeID()!=null) {
            NodeImpl node = new NodeImpl();

            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_GET_NODE_BY_NAME);
            message.put(NodeSce.PARAM_NODE_PNID, parentNode.getNodeID());
            message.put(NodeSce.PARAM_NODE_NAME, nodeName);
            if (clientThreadSessionID != null)
                message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, node.getNodeReplyWorker());

            int rc = (int) retMsg.get(MomMsgTranslator.MSG_RC);
            if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
            return node;
        } else throw new MappingDSException("Parent node is not initialized !");
    }

    class getNodesWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<NodeImpl> nodes = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    nodes = new HashSet<>();
                    for (NodeJSON.JSONDeserializedNode jsonDeserializedNode : NodeJSON.JSON2Nodes(body)) {
                        NodeImpl node = new NodeImpl();
                        node.synchronizeFromJSON(jsonDeserializedNode);
                        nodes.add(node);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else NodeSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", nodes);
            return message;
        }
    }

    @Override
    public Set getNodes(String selector) throws MappingDSException {
        Set<Node> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_GET_NODES);
        message.put(MappingSce.GLOBAL_PARAM_SELECTOR, selector);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, new getNodesWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Node>) retMsg.get("RET"));

        return ret;
    }

    @Override
    public Set getNodes(String key, Object value) throws MappingDSException {
        Set<Node> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxNodeSce.OP_GET_NODES);
        try {
            message.put(MappingSce.GLOBAL_PARAM_PROP_FIELD, PropertiesJSON.propertyFieldToTypedPropertyField(key, value).toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MappingDSException(e.getMessage());
        }
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, NodeSce.Q_MAPPING_NODE_SERVICE, new getNodesWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Node>) retMsg.get("RET"));

        return ret;
    }
}
