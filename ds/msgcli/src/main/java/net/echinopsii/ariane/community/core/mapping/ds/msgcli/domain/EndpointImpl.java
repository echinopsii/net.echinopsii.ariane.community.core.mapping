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

package net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpointAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.EndpointSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.NodeSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.EndpointSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class EndpointImpl extends SProxEndpointAbs implements SProxEndpoint {

    class EndpointReplyWorker implements AppMsgWorker {
        private EndpointImpl endpoint ;

        public EndpointReplyWorker(EndpointImpl endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (endpoint!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));
                    if (body != null) {
                        try {
                            EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint = EndpointJSON.JSON2Endpoint(body);
                            if (endpoint.getEndpointID() == null || endpoint.getEndpointID().equals(jsonDeserializedEndpoint.getEndpointID()))
                                endpoint.synchronizeFromJSON(jsonDeserializedEndpoint);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    switch (rc) {
                    case MomMsgTranslator.MSG_RET_NOT_FOUND:
                        EndpointImpl.log.debug("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                        break;
                    default:
                        EndpointImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                        break;
                }
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(EndpointImpl.class);

    private EndpointReplyWorker endpointReplyWorker = new EndpointReplyWorker(this);
    private String parentNodeID;
    private List<String> twinEndpointsID;

    public EndpointReplyWorker getEndpointReplyWorker() {
        return endpointReplyWorker;
    }

    public String getParentNodeID() {
        return parentNodeID;
    }

    public void setParentNodeID(String parentNodeID) {
        this.parentNodeID = parentNodeID;
    }

    public List<String> getTwinEndpointsID() {
        return twinEndpointsID;
    }

    public void setTwinEndpointsID(List<String> twinEndpointsID) {
        this.twinEndpointsID = twinEndpointsID;
    }

    public void synchronizeFromJSON(EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint) throws MappingDSException {
        super.setEndpointID(jsonDeserializedEndpoint.getEndpointID());
        super.setEndpointURL(jsonDeserializedEndpoint.getEndpointURL());
        if (jsonDeserializedEndpoint.getEndpointProperties()!=null)
            for (PropertiesJSON.TypedPropertyField typedPropertyField : jsonDeserializedEndpoint.getEndpointProperties())
                try {
                    super.addEndpointProperty(typedPropertyField.getPropertyName(), PropertiesJSON.getValueFromTypedPropertyField(typedPropertyField));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new MappingDSException("Error with property " + typedPropertyField.getPropertyName() + " deserialization : " + e.getMessage());
                }
        this.setParentNodeID(jsonDeserializedEndpoint.getEndpointParentNodeID());
        this.setTwinEndpointsID(jsonDeserializedEndpoint.getEndpointTwinEndpointsID());
    }

    @Override
    public void setEndpointURL(String url) throws MappingDSException {
        if (super.getEndpointID()!=null) {
            if ((super.getEndpointURL()!=null && !super.getEndpointURL().equals(url)) ||
                (super.getEndpointURL() == null && url != null)) {
                String clientThreadName = Thread.currentThread().getName();
                String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
                Map<String, Object> message = new HashMap<>();
                message.put(MomMsgTranslator.OPERATION_FDN, OP_SET_ENDPOINT_URL);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getEndpointID());
                message.put(Endpoint.TOKEN_EP_URL, url);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpointReplyWorker);
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setEndpointURL(url);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            }
        } else throw new MappingDSException("This endpoint is not initialized !");
    }

    @Override
    public Node getEndpointParentNode() {
        try {
            Endpoint update = EndpointSceImpl.internalGetEndpoint(super.getEndpointID());
            this.setParentNodeID(((EndpointImpl) update).getParentNodeID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (parentNodeID!=null && (super.getEndpointParentNode()==null || !super.getEndpointParentNode().getNodeID().equals(parentNodeID))) {
            try {
                super.setEndpointParentNode(NodeSceImpl.internalGetNode(parentNodeID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        } else if (parentNodeID==null)
            try {
                super.setEndpointParentNode(null);
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getEndpointParentNode();
    }

    @Override
    public void setEndpointParentNode(Node node) throws MappingDSException {
        if (super.getEndpointID()!=null) {
            if (node!=null && node.getNodeID()!=null) {
                if (parentNodeID == null || (super.getEndpointParentNode()!=null && !super.getEndpointParentNode().equals(node)) ||
                    (parentNodeID!=null && !parentNodeID.equals(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_SET_ENDPOINT_PARENT_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getEndpointID());
                    message.put(NodeSce.PARAM_NODE_PNID, node.getNodeID());
                    if (clientThreadSessionID != null)
                        message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpointReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Node previousParentNode = super.getEndpointParentNode();
                        if (previousParentNode != null) {
                            try {
                                if (retMsg.containsKey(Endpoint.JOIN_PREVIOUS_PNODE)) {
                                    NodeJSON.JSONDeserializedNode jsonDeserializedNode = NodeJSON.JSON2Node(
                                            (String) retMsg.get(Endpoint.JOIN_PREVIOUS_PNODE)
                                    );
                                    ((NodeImpl) previousParentNode).synchronizeFromJSON(jsonDeserializedNode);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        super.setEndpointParentNode(node);
                        parentNodeID = node.getNodeID();
                        try {
                            if (retMsg.containsKey(Endpoint.JOIN_CURRENT_PNODE)) {
                                NodeJSON.JSONDeserializedNode jsonDeserializedEndpoint = NodeJSON.JSON2Node(
                                        (String) retMsg.get(Endpoint.JOIN_CURRENT_PNODE)
                                );
                                ((NodeImpl) node).synchronizeFromJSON(jsonDeserializedEndpoint);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided node is null or not initialized !");
        } else throw new MappingDSException("This endpoint is not initialized !");
    }

    @Override
    public void addEndpointProperty(String propertyKey, Object value) throws MappingDSException {
        if (super.getEndpointID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_ENDPOINT_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getEndpointID());
            try {
                message.put(MappingSce.GLOBAL_PARAM_PROP_FIELD, PropertiesJSON.propertyFieldToTypedPropertyField(propertyKey, value).toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new MappingDSException(e.getMessage());
            }
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpointReplyWorker);
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                super.addEndpointProperty(propertyKey, value);
            }
        } else throw new MappingDSException("This endpoint is not initialized !");
    }

    @Override
    public void removeEndpointProperty(String propertyKey) throws MappingDSException {
        if (super.getEndpointID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_ENDPOINT_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getEndpointID());
            message.put(MappingSce.GLOBAL_PARAM_PROP_NAME, propertyKey);
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpointReplyWorker);
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.removeEndpointProperty(propertyKey);
        } else throw new MappingDSException("This endpoint is not initialized !");
    }

    @Override
    public Set<Endpoint> getTwinEndpoints() {
        try {
            Endpoint update = EndpointSceImpl.internalGetEndpoint(super.getEndpointID());
            this.setTwinEndpointsID(((EndpointImpl) update).getTwinEndpointsID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Endpoint ep : new ArrayList<>(super.getTwinEndpoints()))
            if (!twinEndpointsID.contains(ep.getEndpointID()))
                super.getTwinEndpoints().remove(ep);

        for (String epID : twinEndpointsID)
            try {
                boolean toAdd = true;
                for (Endpoint ep : super.getTwinEndpoints())
                    if (ep.getEndpointID().equals(epID)) toAdd = false;
                if (toAdd) super.getTwinEndpoints().add(EndpointSceImpl.internalGetEndpoint(epID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getTwinEndpoints();
    }

    @Override
    public boolean addTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        if (super.getEndpointID()!=null) {
            if (endpoint!=null && endpoint.getEndpointID()!=null) {
                if ((super.getTwinEndpoints()!=null && !super.getTwinEndpoints().contains(endpoint)) ||
                    (twinEndpointsID!=null && !twinEndpointsID.contains(endpoint.getEndpointID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_TWIN_ENDPOINT);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getEndpointID());
                    message.put(EndpointSce.PARAM_ENDPOINT_TEID, endpoint.getEndpointID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpointReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addTwinEndpoint(endpoint);
                        if (twinEndpointsID==null) twinEndpointsID = new ArrayList<>();
                        twinEndpointsID.add(endpoint.getEndpointID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)) {
                                EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint = EndpointJSON.JSON2Endpoint(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)
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
            } else throw new MappingDSException("Provided twin endpoint is null or not initialized !");
        } else throw new MappingDSException("This endpoint is not initialized !");
    }

    @Override
    public boolean removeTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        if (super.getEndpointID()!=null) {
            if (endpoint!=null && endpoint.getEndpointID()!=null) {
                if ((super.getTwinEndpoints()!=null && super.getTwinEndpoints().contains(endpoint)) ||
                    (twinEndpointsID!=null && twinEndpointsID.contains(endpoint.getEndpointID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_TWIN_ENDPOINT);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getEndpointID());
                    message.put(EndpointSce.PARAM_ENDPOINT_TEID, endpoint.getEndpointID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpointReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeTwinEndpoint(endpoint);
                        twinEndpointsID.remove(endpoint.getEndpointID());
                        try {
                            if (retMsg.containsKey(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)) {
                                EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint = EndpointJSON.JSON2Endpoint(
                                        (String) retMsg.get(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY)
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
            } else throw new MappingDSException("Provided twin endpoint is null or not initialized !");
        } else throw new MappingDSException("This endpoint is not initialized !");
    }
}
