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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.TransportImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.TransportSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxTransportSceAbs;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TransportSceImpl extends SProxTransportSceAbs<TransportImpl> {

    private static final Logger log = LoggerFactory.getLogger(TransportSceImpl.class);

    @Override
    public Transport createTransport(String transportName) throws MappingDSException {
        TransportImpl transport = new TransportImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, TransportSce.OP_CREATE_TRANSPORT);
        message.put(TransportSce.PARAM_TRANSPORT_NAME, transportName);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, transport.getTransportReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return transport;
    }

    @Override
    public void deleteTransport(String transportID) throws MappingDSException {
        TransportImpl transport = new TransportImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, TransportSce.OP_DELETE_TRANSPORT);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, transportID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, transport.getTransportReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    public static Transport internalGetTransport(String transportID) throws MappingDSException {
        TransportImpl transport = new TransportImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, TransportSce.OP_GET_TRANSPORT);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, transportID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, transport.getTransportReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return transport;
    }

    @Override
    public Transport getTransport(String transportID) throws MappingDSException {
        return internalGetTransport(transportID);
    }

    class getTransportsWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<TransportImpl> transports = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    transports = new HashSet<>();
                    for (TransportJSON.JSONDeserializedTransport jsonDeserializedTransport : TransportJSON.JSON2Transports(body)) {
                        TransportImpl link = new TransportImpl();
                        link.synchronizeFromJSON(jsonDeserializedTransport);
                        transports.add(link);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else TransportSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", transports);
            return message;
        }
    }

    @Override
    public Set getTransports(String selector) throws MappingDSException {
        Set<Transport> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, TransportSce.OP_GET_TRANSPORTS);
        message.put(MappingSce.GLOBAL_PARAM_SELECTOR, (selector!=null) ? selector : MappingSce.GLOBAL_PARAM_OBJ_NONE);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, TransportSce.Q_MAPPING_TRANSPORT_SERVICE, new getTransportsWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Transport>) retMsg.get("RET"));

        return ret;
    }
}
