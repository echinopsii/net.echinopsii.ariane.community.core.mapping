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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.cache.MappingDSCache;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.*;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.repository.MappingRepoImpl;
import com.spectral.cc.core.mapping.ds.domain.*;
import com.spectral.cc.core.mapping.ds.service.MapSce;
import com.spectral.cc.core.mapping.ds.service.MappingSce;

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

    public MappingSceImpl() {

    }

    public MappingRepoImpl getGlobalRepo() {
        return globalRepo;
    }

    @Override
    public boolean init(Dictionary<Object, Object> properties) {
        try {
            return MappingDSCache.init(properties) && MappingDSGraphDB.init(properties);
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
    public void unsetAutoCommit() {
        MappingDSGraphDB.setAutocommit(false);
    }

    @Override
    public void setAutoCommit() {
        MappingDSGraphDB.setAutocommit(true);
    }

    @Override
    public void commit() {
        MappingDSGraphDB.commit();
    }

    @Override
    public void rollback() {
        MappingDSGraphDB.rollback();
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
    public MapSce getMapSce() {
        return mapSce;
    }

    @Override
    public Node getNodeByName(Container container, String nodeName) {
        Node ret = null;
        if (container instanceof ContainerImpl) {
            ret = globalRepo.findNodeByName((ContainerImpl) container, nodeName);
        }
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

    @Override
    public Gate getGateByName(Container container, String nodeName) {
        Gate ret = null;
        if (container instanceof ContainerImpl) {
            ret = globalRepo.findGateByName((ContainerImpl) container, nodeName);
        }
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

    @Override
    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) {
        Link ret = null;
        if (esource instanceof EndpointImpl && edest instanceof EndpointImpl) {
            ret = globalRepo.findLinkBySourceEPandDestinationEP((EndpointImpl) esource, (EndpointImpl) edest);
        }
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