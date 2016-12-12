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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.GateImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.GateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxGateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;

public class GateSceImpl implements SProxGateSce<GateImpl> {

    private static final Logger log = LoggerFactory.getLogger(GateSceImpl.class);

    @Override
    public GateImpl createGate(Session session, String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException {
        GateImpl ret = null;
        if (session!=null && session.isRunning())
            ret= (GateImpl) session.execute(this, GateSce.OP_CREATE_GATE, new Object[]{url, name, containerid, isPrimaryAdmin});
        return ret;
    }

    @Override
    public void deleteGate(Session session, String nodeID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, GateSce.OP_DELETE_GATE, new Object[]{nodeID});
    }

    @Override
    public GateImpl getGate(Session session, String id) throws MappingDSException {
        GateImpl ret = null;
        if (session!=null && session.isRunning())
            ret = (GateImpl)session.execute(this, GateSce.OP_GET_GATE, new Object[]{id});
        return ret;
    }

    @Override
    public Set<GateImpl> getGates(Session session, String selector) throws MappingDSException {
        Set<GateImpl> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<GateImpl>) session.execute(this, GateSce.OP_GET_GATES, new Object[]{selector});
        return ret;
    }

    @Override
    public Gate createGate(String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException {
        GateImpl gate = new GateImpl();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, SProxGateSce.OP_CREATE_GATE);
        message.put(GateSce.PARAM_GATE_URL, url);
        message.put(GateSce.PARAM_GATE_NAME, name);
        message.put(Container.TOKEN_CT_ID, containerid);
        message.put(GateSce.PARAM_GATE_IPADM, isPrimaryAdmin);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, GateSce.Q_MAPPING_GATE_SERVICE, gate.getGateReplyWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        return gate;
    }

    @Override
    public void deleteGate(String nodeID) throws MappingDSException {
        GateImpl gate = new GateImpl();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, SProxGateSce.OP_DELETE_GATE);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, nodeID);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, GateSce.Q_MAPPING_GATE_SERVICE, gate.getGateReplyWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    public static Gate internalGetGate(String id) throws MappingDSException {
        GateImpl gate = new GateImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, SProxGateSce.OP_GET_GATE);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, id);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, GateSce.Q_MAPPING_GATE_SERVICE, gate.getGateReplyWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MomMsgTranslator.MSG_RET_NOT_FOUND) gate = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return gate;
    }

    @Override
    public Gate getGate(String id) throws MappingDSException {
        return internalGetGate(id);
    }

    class getGatesWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<GateImpl> gates = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    gates = new HashSet<>();
                    for (GateJSON.JSONDeserializedGate jsonDeserializedGate : GateJSON.JSON2Gates(body)) {
                        GateImpl gate = new GateImpl();
                        gate.synchronizeFromJSON(jsonDeserializedGate);
                        gates.add(gate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else GateSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", gates);
            return message;
        }
    }

    @Override
    public Set getGates(String selector) throws MappingDSException {
        Set<Gate> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, SProxGateSce.OP_GET_GATES);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, GateSce.Q_MAPPING_GATE_SERVICE, new getGatesWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }
        int rc = (int) retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Gate>) retMsg.get("RET"));

        return ret;
    }
}
