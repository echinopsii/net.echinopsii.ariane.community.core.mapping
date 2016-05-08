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
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.MapSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MappingSceImpl implements MappingSce {

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

    private SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

    public MappingSceImpl() {

    }

    public MappingRepoImpl getGlobalRepo() {
        return globalRepo;
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
        MappingDSGraphDB.stop();
        return true;
    }

    @Override
    public Session openSession(String clientID) {
        SessionImpl session = new SessionImpl(clientID);
        sessionRegistry.put(session);
        session.start();
        return session;
    }

    @Override
    public Session closeSession(Session toClose) {
        sessionRegistry.remove(toClose);
        toClose.stop();
        return toClose;
    }

    @Override
    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    @Deprecated
    @Override
    public void unsetAutoCommit() {
        MappingDSGraphDB.unsetAutocommit();
    }

    @Deprecated
    @Override
    public void setAutoCommit(boolean autoCommit) {
        MappingDSGraphDB.setAutocommit(autoCommit);
    }

    @Deprecated
    @Override
    public void commit() {
        MappingDSGraphDB.commit();
    }

    @Deprecated
    @Override
    public void rollback() {
        MappingDSGraphDB.rollback();
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

    static final String GET_NODE_BY_NAME = "getNodeByName";

    @Override
    public Node getNodeByName(Session session, Container container, String nodeName) throws MappingDSException {
        Node ret = null;
        if (session!=null && session.isRunning())
            ret = (Node)session.execute(this, GET_NODE_BY_NAME, new Object[]{nodeName});
        return ret;
    }

    @Override
    public Node getNodeByName(Container container, String nodeName) {
        Node ret = null;
        if (container instanceof ContainerImpl) {
            ret = globalRepo.findNodeByName((ContainerImpl) container, nodeName);
        }
        return ret;
    }

    static final String GET_NODE_CONTAINING_SUB_NODE = "getNodeContainingSubnode";

    @Override
    public Node getNodeContainingSubnode(Session session, Container container, Node node) throws MappingDSException {
        Node ret = null;
        if (session!=null && session.isRunning())
            ret = (Node)session.execute(this, GET_NODE_CONTAINING_SUB_NODE, new Object[]{container, node});
        return ret;
    }

    @Override
    public Node getNodeContainingSubnode(Container container, Node node) {
        Node ret = null;
        if (container instanceof ContainerImpl && node instanceof NodeImpl) {
            ret = globalRepo.findNodeContainingSubnode((ContainerImpl) container, (NodeImpl) node);
        }
        return ret;
    }

    static final String GET_NODES_IN_PARENT_NODE = "getNodesInParentNode";

    @Override
    public Set<Node> getNodesInParentNode(Session session, Container container, Node node) throws MappingDSException {
        Set<Node> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<Node>)session.execute(this, GET_NODES_IN_PARENT_NODE, new Object[]{container, node});
        return ret;
    }

    @Override
    public Set<Node> getNodesInParentNode(Container container, Node node) {
        Set<Node> ret = new HashSet<Node>();
        if (container instanceof ContainerImpl && node instanceof NodeImpl) {
            for (NodeImpl nodeLoop : globalRepo.findNodesInParentNode((ContainerImpl) container, (NodeImpl) node)) {
                ret.add((Node) nodeLoop);
            }
        }
        return ret;
    }

    static final String GET_GATE_BY_NAME = "getGateByName";

    @Override
    public Gate getGateByName(Session session, Container container, String nodeName) throws MappingDSException {
        Gate ret = null;
        if (session!=null && session.isRunning())
            ret = (Gate)session.execute(this, GET_GATE_BY_NAME, new Object[]{container, nodeName});
        return ret;
    }

    @Override
    public Gate getGateByName(Container container, String nodeName) {
        Gate ret = null;
        if (container instanceof ContainerImpl) {
            ret = globalRepo.findGateByName((ContainerImpl) container, nodeName);
        }
        return ret;
    }

    static final String GET_LINKS_BY_SOURCE_EP = "getLinksBySourceEP";

    @Override
    public Set<Link> getLinksBySourceEP(Session session, Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<Link>)session.execute(this, GET_LINKS_BY_SOURCE_EP, new Object[]{endpoint});
        return ret;
    }

    @Override
    public Set<Link> getLinksBySourceEP(Endpoint endpoint) {
        Set<Link> ret = new HashSet<Link>();
        if (endpoint instanceof EndpointImpl) {
            for (LinkImpl linkLoop : globalRepo.findLinksBySourceEP((EndpointImpl) endpoint)) {
                ret.add((Link) linkLoop);
            }
        }
        return ret;
    }

    static final String GET_LINKS_BY_DESTINATION_EP = "getLinksByDestinationEP";

    @Override
    public Set<Link> getLinksByDestinationEP(Session session, Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<Link>)session.execute(this, GET_LINKS_BY_DESTINATION_EP, new Object[]{endpoint});
        return ret;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Endpoint endpoint) {
        Set<Link> ret = new HashSet<Link>();
        if (endpoint instanceof EndpointImpl) {
            for (LinkImpl linkLoop : globalRepo.findLinksByDestinationEP((EndpointImpl) endpoint)) {
                ret.add((Link) linkLoop);
            }
        }
        return ret;
    }

    static final String GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP = "getLinkBySourceEPandDestinationEP";

    @Override
    public Link getLinkBySourceEPandDestinationEP(Session session, Endpoint esource, Endpoint edest) throws MappingDSException {
        Link ret = null;
        if (session!=null && session.isRunning())
            ret = (Link)session.execute(this, GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP, new Object[]{esource, edest});
        return ret;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) {
        Link ret = null;
        if (esource instanceof EndpointImpl && edest instanceof EndpointImpl) {
            ret = globalRepo.findLinkBySourceEPandDestinationEP((EndpointImpl) esource, (EndpointImpl) edest);
        }
        return ret;
    }

    static final String GET_LINK_BY_SOURCE_EP_AND_TRANSPORT = "getLinkBySourceEPandTransport";

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Session session, Endpoint esource, Transport transport) throws MappingDSException {
        Link ret = null;
        if (session!=null && session.isRunning())
            ret = (Link)session.execute(this, GET_LINK_BY_SOURCE_EP_AND_TRANSPORT, new Object[]{esource, transport});
        return ret;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) {
        Link ret = null;
        if (esource instanceof EndpointImpl && transport instanceof TransportImpl) {
            ret = globalRepo.findMulticastLinkBySourceEPandTransport((EndpointImpl)esource, (TransportImpl)transport);
        }
        return ret;
    }
}