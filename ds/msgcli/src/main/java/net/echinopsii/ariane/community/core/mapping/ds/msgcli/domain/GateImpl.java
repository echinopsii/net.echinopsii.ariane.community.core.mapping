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
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxGate;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.EndpointSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.GateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GateImpl extends NodeImpl implements SProxGate {

    class GateReplyWorker implements AppMsgWorker {
        private GateImpl gate;

        public GateReplyWorker(GateImpl gate) {
            this.gate = gate;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (gate!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));
                    if (body != null) {
                        try {
                            GateJSON.JSONDeserializedGate jsonDeserializedGate = GateJSON.JSON2Gate(body);
                            if (gate.getNodeID() == null || gate.getNodeID().equals(jsonDeserializedGate.getNode().getNodeID()))
                                gate.synchronizeFromJSON(jsonDeserializedGate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    switch (rc) {
                        case MappingSce.MAPPING_SCE_RET_NOT_FOUND:
                            GateImpl.log.warn("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                            break;
                        default:
                            GateImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                            break;
                    }
                }
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(GateImpl.class);

    private GateReplyWorker gateReplyWorker = new GateReplyWorker(this);
    private String gatePrimaryAdminEndpointID;
    private EndpointImpl gatePrimaryAdminEndpoint = null;

    public GateReplyWorker getGateReplyWorker() {
        return gateReplyWorker;
    }

    public String getGatePrimaryAdminEndpointID() {
        return gatePrimaryAdminEndpointID;
    }

    public void setGatePrimaryAdminEndpointID(String gatePrimaryAdminEndpointID) {
        this.gatePrimaryAdminEndpointID = gatePrimaryAdminEndpointID;
    }

    public void synchronizeFromJSON(GateJSON.JSONDeserializedGate jsonDeserializedGate) throws MappingDSException {
        super.synchronizeFromJSON(jsonDeserializedGate.getNode());
        this.setGatePrimaryAdminEndpointID(jsonDeserializedGate.getContainerGatePrimaryAdminEndpointID());
    }

    @Override
    public void setNodePrimaryAdminEnpoint(Session session, Endpoint endpoint) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, OP_SET_NODE_PRIMARY_ADMIN_ENDPOINT, new Object[]{endpoint});
    }

    @Override
    public boolean isAdminPrimary() {
        return (this.getNodePrimaryAdminEndpoint()!=null);
    }

    @Override
    public Endpoint getNodePrimaryAdminEndpoint() {
        if (gatePrimaryAdminEndpointID!=null && (gatePrimaryAdminEndpoint==null || !gatePrimaryAdminEndpoint.getEndpointID().equals(gatePrimaryAdminEndpointID))) {
            try {
                gatePrimaryAdminEndpoint = (EndpointImpl) EndpointSceImpl.internalGetEndpoint(gatePrimaryAdminEndpointID);
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        } else if (gatePrimaryAdminEndpoint!=null && gatePrimaryAdminEndpointID==null)
                gatePrimaryAdminEndpoint=null;

        return gatePrimaryAdminEndpoint;
    }

    @Override
    public void setNodePrimaryAdminEnpoint(Endpoint endpoint) throws MappingDSException {
        if (super.getNodeID()!=null) {
            if (endpoint == null || endpoint.getEndpointID()!=null) {
                if ((gatePrimaryAdminEndpoint!=null && endpoint!=null && !gatePrimaryAdminEndpoint.getEndpointID().equals(endpoint.getEndpointID())) ||
                    (gatePrimaryAdminEndpointID!=null && endpoint!=null && !gatePrimaryAdminEndpointID.equals(endpoint.getEndpointID())) ||
                    (gatePrimaryAdminEndpointID==null && endpoint!=null) || (gatePrimaryAdminEndpointID!=null && endpoint==null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_NODE_PRIMARY_ADMIN_ENDPOINT);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getNodeID());
                    message.put(Endpoint.TOKEN_EP_ID, (endpoint != null) ? endpoint.getEndpointID() : MappingSce.GLOBAL_PARAM_OBJ_NONE);
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, GateSce.Q_MAPPING_GATE_SERVICE, gateReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Endpoint previousPEndpoint = this.getNodePrimaryAdminEndpoint();
                        if (previousPEndpoint!=null) {
                            try {
                                if (retMsg.containsKey(Gate.JOIN_PREVIOUS_PAEP)) {
                                    EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEP = EndpointJSON.JSON2Endpoint(
                                            (String) retMsg.get(Gate.JOIN_PREVIOUS_PAEP)
                                    );
                                    ((EndpointImpl)previousPEndpoint).synchronizeFromJSON(jsonDeserializedEP);
                                }
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        gatePrimaryAdminEndpoint = (EndpointImpl) endpoint;
                        gatePrimaryAdminEndpointID = (endpoint!=null) ? endpoint.getEndpointID() : null;
                        if (endpoint!=null) {
                            try {
                                if (retMsg.containsKey(Gate.JOIN_CURRENT_PAEP)) {
                                    EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEP = EndpointJSON.JSON2Endpoint(
                                            (String) retMsg.get(Gate.JOIN_CURRENT_PAEP)
                                    );
                                    ((EndpointImpl) endpoint).synchronizeFromJSON(jsonDeserializedEP);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided endpoint is not initialized !");
        } else throw new MappingDSException("This gate is not initialized !");
    }
}
