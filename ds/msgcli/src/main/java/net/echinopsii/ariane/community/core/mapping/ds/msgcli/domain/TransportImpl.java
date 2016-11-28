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
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransportAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.TransportSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class TransportImpl extends SProxTransportAbs implements SProxTransport {

    class TransportReplyWorker implements AppMsgWorker {
        private TransportImpl transport;

        public TransportReplyWorker(TransportImpl transport) {
            this.transport = transport;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (transport!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));
                    if (body != null) {
                        try {
                            TransportJSON.JSONDeserializedTransport jsonDeserializedTransport = TransportJSON.JSON2Transport(body);
                            if (transport.getTransportID() == null || transport.getTransportID().equals(jsonDeserializedTransport.getTransportID()))
                                transport.synchronizeFromJSON(jsonDeserializedTransport);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    switch (rc) {
                        case MomMsgTranslator.MSG_RET_NOT_FOUND:
                            TransportImpl.log.debug("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                            break;
                        default:
                            TransportImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                            break;
                    }
                }
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(TransportImpl.class);

    private TransportReplyWorker transportReplyWorker = new TransportReplyWorker(this);

    public TransportReplyWorker getTransportReplyWorker() {
        return transportReplyWorker;
    }

    public void synchronizeFromJSON(TransportJSON.JSONDeserializedTransport jsonDeserializedTransport) throws MappingDSException {
        super.setTransportID(jsonDeserializedTransport.getTransportID());
        super.setTransportName(jsonDeserializedTransport.getTransportName());
        if (jsonDeserializedTransport.getTransportProperties()!=null)
            for (PropertiesJSON.TypedPropertyField typedPropertyField : jsonDeserializedTransport.getTransportProperties())
                try {
                    super.addTransportProperty(typedPropertyField.getPropertyName(), PropertiesJSON.getValueFromTypedPropertyField(typedPropertyField));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new MappingDSException("Error with property " + typedPropertyField.getPropertyName() + " deserialization : " + e.getMessage());
                }
    }

    @Override
    public void setTransportName(String name) throws MappingDSException {
        if (super.getTransportID()!=null) {
            if (name !=null && (super.getTransportName()!=null && !super.getTransportID().equals(name))) {
                if (super.getTransportName()!=null && !super.getTransportName().equals(name)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MomMsgTranslator.OPERATION_FDN, OP_SET_TRANSPORT_NAME);
                    message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, super.getTransportID());
                    message.put(TransportSce.PARAM_TRANSPORT_NAME, name);
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = null;
                    try {
                        retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, transportReplyWorker);
                    } catch (TimeoutException e) {
                        throw new MappingDSException(e.getMessage());
                    }
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setTransportName(name);
                    else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            }
        } else throw new MappingDSException("This transport is not initialized !");
    }

    @Override
    public void addTransportProperty(String propertyKey, Object value) throws MappingDSException {
        if (super.getTransportID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MomMsgTranslator.OPERATION_FDN, OP_ADD_TRANSPORT_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getTransportID());
            try {
                message.put(MappingSce.GLOBAL_PARAM_PROP_FIELD, PropertiesJSON.propertyFieldToTypedPropertyField(propertyKey, value).toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new MappingDSException(e.getMessage());
            }
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = null;
            try {
                retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, transportReplyWorker);
            } catch (TimeoutException e) {
                throw new MappingDSException(e.getMessage());
            }
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.addTransportProperty(propertyKey, value);
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        } else throw new MappingDSException("This transport is not initialized !");
    }

    @Override
    public void removeTransportProperty(String propertyKey) throws MappingDSException {
        if (super.getTransportID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MomMsgTranslator.OPERATION_FDN, OP_REMOVE_TRANSPORT_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getTransportID());
            message.put(MappingSce.GLOBAL_PARAM_PROP_NAME, propertyKey);
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            Map<String, Object> retMsg = null;
            try {
                retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, transportReplyWorker);
            } catch (TimeoutException e) {
                throw new MappingDSException(e.getMessage());
            }
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.removeTransportProperty(propertyKey);
        } else throw new MappingDSException("This transport is not initialized !");
    }
}