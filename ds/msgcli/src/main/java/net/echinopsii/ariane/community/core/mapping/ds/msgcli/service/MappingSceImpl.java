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
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.GateImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.LinkImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.tools.SessionImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class MappingSceImpl extends SProxMappingSceAbs<SessionImpl, SessionRegistryImpl> {

    private final static Logger log = LoggerFactory.getLogger(MappingSceImpl.class);

    private MapSceImpl mapSce = new MapSceImpl();
    private ClusterSceImpl clusterSce = new ClusterSceImpl();
    private ContainerSceImpl containerSce = new ContainerSceImpl();
    private GateSceImpl gateSce = new GateSceImpl();
    private NodeSceImpl nodeSce = new NodeSceImpl();
    private EndpointSceImpl endpointSce = new EndpointSceImpl();
    private LinkSceImpl linkSce = new LinkSceImpl();
    private TransportSceImpl transportSce = new TransportSceImpl();

    public MappingSceImpl() {
        super.setSessionRegistry(new SessionRegistryImpl());
    }

    @Override
    public boolean init(Dictionary<Object, Object> properties) {
        boolean ret = false;
        try {
            MappingMsgcliMomSP.init(properties);
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public boolean start() {
        return MappingMsgcliMomSP.start();
    }

    @Override
    public boolean stop() {
        boolean ret = false;
        try {
            MappingMsgcliMomSP.stop();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Session openSession(String clientID) {
        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxMappingSce.SESSION_MGR_OP_OPEN);
        message.put(SProxMappingSce.SESSION_MGR_PARAM_CLIENT_ID, clientID);
        Session session = new SessionImpl();
        MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, Session.MAPPING_SESSION_SERVICE_Q, ((SessionImpl)session).getSessionReplyWorker());
        if (session.isRunning()) {
            ClientThreadSessionRegistry.addCliThreadSession(Thread.currentThread().getName(), session.getSessionID());
            super.getSessionRegistry().put(session);
            MappingMsgcliMomSP.getSharedMoMConnection().openMsgGroupRequest(session.getSessionID());
        }
        else session = null;
        return session;
    }

    @Override
    public Session openSession(String clientID, boolean proxy) {
        if (proxy)
            log.warn("As a remote client you should use openSession(String clientID) method only. Proxy parameter will be ignored !");
        return openSession(clientID);
    }

    @Override
    public Session closeSession(Session toClose) {
        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxMappingSce.SESSION_MGR_OP_CLOSE);
        message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, toClose.getSessionID());
        MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, Session.MAPPING_SESSION_SERVICE_Q, ((SessionImpl) toClose).getSessionReplyWorker());
        MappingMsgcliMomSP.getSharedMoMConnection().closeMsgGroupRequest(toClose.getSessionID());
        if (!toClose.isRunning()) {
            super.getSessionRegistry().remove(toClose);
            ClientThreadSessionRegistry.removeCliThreadSession(Thread.currentThread().getName());
        }
        return toClose;
    }

    @Override
    public Session closeSession() {
        Session ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            ret = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (ret!=null) closeSession(ret);
        }
        return ret;
    }

    @Override
    public MapSce getMapSce() {
        return mapSce;
    }

    @Override
    public SProxClusterSce<? extends Cluster> getClusterSce() {
        return clusterSce;
    }

    @Override
    public SProxContainerSce<? extends Container> getContainerSce() {
        return containerSce;
    }

    @Override
    public SProxGateSce<? extends Gate> getGateSce() {
        return gateSce;
    }

    @Override
    public SProxNodeSce<? extends Node> getNodeSce() {
        return nodeSce;
    }

    @Override
    public SProxEndpointSce<? extends Endpoint> getEndpointSce() {
        return endpointSce;
    }

    @Override
    public SProxLinkSce<? extends Link> getLinkSce() {
        return linkSce;
    }

    @Override
    public SProxTransportSce<? extends Transport> getTransportSce() {
        return transportSce;
    }

    @Override
    public Node getNodeByName(Container container, String nodeName) throws MappingDSException {
        NodeImpl node = new NodeImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_GET_NODE_BY_NAME);
        message.put(Container.TOKEN_CT_ID, container.getContainerID());
        message.put(NodeSce.PARAM_NODE_NAME, nodeName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MappingSce.Q_MAPPING_SCE_SERVICE, node.getNodeReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MappingSce.MAPPING_SCE_RET_NOT_FOUND) node = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return node;
    }

    @Override
    public Gate getGateByName(Container container, String nodeName) throws MappingDSException {
        GateImpl gate = new GateImpl();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_GET_GATE_BY_NAME);
        message.put(Container.TOKEN_CT_ID, container.getContainerID());
        message.put(GateSce.PARAM_GATE_NAME, nodeName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MappingSce.Q_MAPPING_SCE_SERVICE, gate.getGateReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MappingSce.MAPPING_SCE_RET_NOT_FOUND) gate = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }
        return gate;
    }

    class getLinksWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<LinkImpl> links = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    links = new HashSet<>();
                    for (LinkJSON.JSONDeserializedLink jsonDeserializedLink : LinkJSON.JSON2Links(body)) {
                        LinkImpl link = new LinkImpl();
                        link.synchronizeFromJSON(jsonDeserializedLink);
                        links.add(link);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", links);
            return message;
        }
    }

    @Override
    public Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_GET_LINKS_BY_SOURCE_EP);
        message.put(LinkSce.PARAM_LINK_SEPID, endpoint.getEndpointID());
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MappingSce.Q_MAPPING_SCE_SERVICE, new getLinksWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Link>) retMsg.get("RET"));

        return ret;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_GET_LINKS_BY_DESTINATION_EP);
        message.put(LinkSce.PARAM_LINK_TEPID, endpoint.getEndpointID());
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MappingSce.Q_MAPPING_SCE_SERVICE, new getLinksWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Link>) retMsg.get("RET"));

        return ret;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException {
        LinkImpl link = new LinkImpl();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP);
        message.put(LinkSce.PARAM_LINK_SEPID, esource.getEndpointID());
        message.put(LinkSce.PARAM_LINK_TEPID, edest.getEndpointID());
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MappingSce.Q_MAPPING_SCE_SERVICE, link.getLinkReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MappingSce.MAPPING_SCE_RET_NOT_FOUND) link = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }
        return link;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException {
        LinkImpl link = new LinkImpl();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_GET_LINK_BY_SOURCE_EP_AND_TRANSPORT);
        message.put(LinkSce.PARAM_LINK_SEPID, esource.getEndpointID());
        message.put(Transport.TOKEN_TP_ID, transport.getTransportID());
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, MappingSce.Q_MAPPING_SCE_SERVICE, link.getLinkReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MappingSce.MAPPING_SCE_RET_NOT_FOUND) link = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }
        return link;
    }
}
