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
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.tools.SessionImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        return null;
    }

    @Override
    public Gate getGateByName(Container container, String nodeName) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException {
        return null;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException {
        return null;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException {
        return null;
    }
}
