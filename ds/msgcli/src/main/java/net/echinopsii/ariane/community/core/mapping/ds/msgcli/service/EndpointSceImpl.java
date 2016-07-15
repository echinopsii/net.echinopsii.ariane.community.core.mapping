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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.EndpointSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxEndpointSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxEndpointSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EndpointSceImpl extends SProxEndpointSceAbs<EndpointImpl>{

    private static final Logger log = LoggerFactory.getLogger(EndpointSceImpl.class);

    @Override
    public Endpoint createEndpoint(String url, String parentNodeID) throws MappingDSException {
        EndpointImpl endpoint = new EndpointImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, EndpointSce.OP_CREATE_ENDPOINT);
        message.put(Endpoint.TOKEN_EP_URL, url);
        message.put(NodeSce.PARAM_NODE_PNID, parentNodeID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpoint.getEndpointReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return endpoint;
    }

    @Override
    public void deleteEndpoint(String endpointID) throws MappingDSException {
        EndpointImpl endpoint = new EndpointImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, EndpointSce.OP_DELETE_ENDPOINT);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, endpointID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpoint.getEndpointReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    public static Endpoint internalGetEndpoint(String id) throws MappingDSException {
        EndpointImpl endpoint = new EndpointImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, EndpointSce.OP_GET_ENDPOINT);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, id);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpoint.getEndpointReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MomMsgTranslator.MSG_RET_NOT_FOUND) endpoint = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return endpoint;
    }

    @Override
    public Endpoint getEndpoint(String id) throws MappingDSException {
        return internalGetEndpoint(id);
    }

    @Override
    public Endpoint getEndpointByURL(String URL) throws MappingDSException {
        EndpointImpl endpoint = new EndpointImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, EndpointSce.OP_GET_ENDPOINT_BY_URL);
        message.put(Endpoint.TOKEN_EP_URL, URL);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, endpoint.getEndpointReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MomMsgTranslator.MSG_RET_NOT_FOUND) endpoint = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return endpoint;
    }

    class getEndpointsWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<EndpointImpl> endpoints = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    endpoints = new HashSet<>();
                    for (EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint : EndpointJSON.JSON2Endpoints(body)) {
                        EndpointImpl endpoint = new EndpointImpl();
                        endpoint.synchronizeFromJSON(jsonDeserializedEndpoint);
                        endpoints.add(endpoint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else EndpointSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", endpoints);
            return message;
        }
    }

    @Override
    public Set getEndpoints(String selector) throws MappingDSException {
        Set<Endpoint> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, SProxEndpointSce.OP_GET_ENDPOINTS);
        message.put(MappingSce.GLOBAL_PARAM_SELECTOR, (selector!=null) ? selector : MappingSce.GLOBAL_PARAM_OBJ_NONE);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, new getEndpointsWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Endpoint>) retMsg.get("RET"));

        return ret;
    }

    @Override
    public Set getEndpoints(String key, Object value) throws MappingDSException {
        Set<Endpoint> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, SProxEndpointSce.OP_GET_ENDPOINTS);
        try {
            message.put(MappingSce.GLOBAL_PARAM_PROP_FIELD, PropertiesJSON.propertyFieldToTypedPropertyField(key, value).toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MappingDSException(e.getMessage());
        }
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, new getEndpointsWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Endpoint>) retMsg.get("RET"));

        return ret;
    }
}