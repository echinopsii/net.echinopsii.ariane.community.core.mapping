/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
 * Copyright (C) 2013  Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCache;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository.MappingRepoImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.MapSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MappingSceImpl extends SProxMappingSceAbs<SessionImpl, SessionRegistryImpl> {

    //private final static Logger log = LoggerFactory.getLogger(MappingSceImpl.class);

    private MappingRepoImpl globalRepo = new MappingRepoImpl();

    private MapSceImpl mapSce = new MapSceImpl(this);
    private ClusterSceImpl clusterSce = new ClusterSceImpl(this);
    private ContainerSceImpl containerSce = new ContainerSceImpl(this);
    private GateSceImpl gateSce = new GateSceImpl(this);
    private NodeSceImpl nodeSce = new NodeSceImpl(this);
    private EndpointSceImpl endpointSce = new EndpointSceImpl(this);
    private LinkSceImpl linkSce = new LinkSceImpl(this);
    private TransportSceImpl transportSce = new TransportSceImpl(this);

    public MappingSceImpl() {
        super.setSessionRegistry(new SessionRegistryImpl());
    }


    @Override
    public boolean init(Dictionary<Object, Object> properties) {
        try {
            return MappingDSGraphDB.init(properties) && MappingDSCache.init(properties);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean start() {
        try {
            return MappingDSCache.start() && MappingDSGraphDB.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean stop() {
        for (Session session: super.getSessionRegistry().getSessionRegistry().values())
            session.stop();
        MappingDSGraphDB.stop();
        MappingDSCache.stop();
        return true;
    }

    @Override
    public Session openSession(String clientID, boolean proxy) {
        Session session = new SessionImpl(clientID);
        super.getSessionRegistry().put(session);
        session.start();
        if (!proxy) ClientThreadSessionRegistry.addCliThreadSession(Thread.currentThread().getName(), session.getSessionID());
        return session;
    }

    @Override
    public Session openSession(String clientID) {
        return openSession(clientID, false);
    }

    @Override
    public Session closeSession(Session toClose) {
        toClose.stop();
        if (ClientThreadSessionRegistry.getSessionFromThread(Thread.currentThread().getName())!=null &&
                ClientThreadSessionRegistry.getSessionFromThread(Thread.currentThread().getName()).equals(toClose.getSessionID()))
            ClientThreadSessionRegistry.removeCliThreadSession(Thread.currentThread().getName());
        super.getSessionRegistry().remove(toClose);
        return toClose;
    }

    @Override
    public Session closeSession() {
        Session ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            ret = super.getSessionRegistry().get(clientThreadSessionID);
            if (ret!=null) closeSession(ret);
        }
        return ret;
    }

    public MappingRepoImpl getGlobalRepo() {
        return globalRepo;
    }

    @Override
    public MapSce getMapSce() {
        return mapSce;
    }

    @Override
    public ClusterSceImpl getClusterSce() {
        return clusterSce;
    }

    @Override
    public ContainerSceImpl getContainerSce() {
        return containerSce;
    }

    @Override
    public GateSceImpl getGateSce() {
        return gateSce;
    }

    @Override
    public NodeSceImpl getNodeSce() {
        return nodeSce;
    }

    @Override
    public EndpointSceImpl getEndpointSce() {
        return endpointSce;
    }

    @Override
    public LinkSceImpl getLinkSce() {
        return linkSce;
    }

    @Override
    public TransportSceImpl getTransportSce() {
        return transportSce;
    }

    @Override
    public Node getNodeByName(Container container, String nodeName) throws MappingDSException {
        Node ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.getNodeByName(session, container, nodeName);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else if (container instanceof ContainerImpl) ret = globalRepo.findNodeByName((ContainerImpl) container, nodeName);
        return ret;
    }

    @Override
    public Gate getGateByName(Container container, String nodeName) throws MappingDSException {
        Gate ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.getGateByName(session, container, nodeName);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else if (container instanceof ContainerImpl) ret = globalRepo.findGateByName((ContainerImpl) container, nodeName);
        return ret;
    }

    @Override
    public Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = new HashSet<Link>();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.getLinksBySourceEP(session, endpoint);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (endpoint instanceof EndpointImpl) {
                for (LinkImpl linkLoop : globalRepo.findLinksBySourceEP((EndpointImpl) endpoint)) {
                    ret.add((Link) linkLoop);
                }
            }
        }
        return ret;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = new HashSet<Link>();
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.getLinksByDestinationEP(session, endpoint);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (endpoint instanceof EndpointImpl) {
                for (LinkImpl linkLoop : globalRepo.findLinksByDestinationEP((EndpointImpl) endpoint)) {
                    ret.add((Link) linkLoop);
                }
            }
        }
        return ret;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException {
        Link ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.getLinkBySourceEPandDestinationEP(session, esource, edest);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (esource instanceof EndpointImpl && edest instanceof EndpointImpl) {
                ret = globalRepo.findLinkBySourceEPandDestinationEP((EndpointImpl) esource, (EndpointImpl) edest);
            }
        }
        return ret;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException {
        Link ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.getMulticastLinkBySourceEPAndTransport(session, esource, transport);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (esource instanceof EndpointImpl && transport instanceof TransportImpl) {
                ret = globalRepo.findMulticastLinkBySourceEPandTransport((EndpointImpl) esource, (TransportImpl) transport);
            }
        }
        return ret;
    }
}