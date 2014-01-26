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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.repository;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.*;
import com.spectral.cc.core.mapping.ds.repository.MappingRepo;

import java.util.HashSet;
import java.util.Set;

public class MappingRepoImpl implements MappingRepo<ContainerImpl, NodeImpl, GateImpl, EndpointImpl, LinkImpl, TransportImpl> {

    private ClusterRepoImpl clusterRepo = new ClusterRepoImpl();
    private ContainerRepoImpl containerRepo = new ContainerRepoImpl();
    private GateRepoImpl gateRepoImpl = new GateRepoImpl();
    private NodeRepoImpl nodeRepo = new NodeRepoImpl();
    private EndpointRepoImpl endpointRepo = new EndpointRepoImpl();
    private LinkRepoImpl linkRepo = new LinkRepoImpl();
    private TransportRepoImpl transportRepo = new TransportRepoImpl();

    public void clear() {
        MappingDSGraphDB.clear();
    }

    public ClusterRepoImpl getClusterRepo() {
        return clusterRepo;
    }

    public ContainerRepoImpl getContainerRepo() {
        return containerRepo;
    }

    public NodeRepoImpl getNodeRepo() {
        return nodeRepo;
    }

    public GateRepoImpl getGateRepo() {
        return gateRepoImpl;
    }

    public EndpointRepoImpl getEndpointRepo() {
        return endpointRepo;
    }

    public LinkRepoImpl getLinkRepo() {
        return linkRepo;
    }

    public TransportRepoImpl getTransportRepo() {
        return transportRepo;
    }

    @Override
    public NodeImpl findNodeByName(ContainerImpl cont, String name) {
        NodeImpl ret = null;
        for (NodeImpl node : MappingDSGraphDB.getIndexedNodes(name)) {
            if (node.getNodeContainer().equals(cont)) {
                ret = node;
                break;
            }
        }
        return ret;
    }

    @Override
    public NodeImpl findNodeContainingSubnode(ContainerImpl container,
                                              NodeImpl node) {
        NodeImpl ret = null;
        for (NodeImpl pnode : MappingDSGraphDB.getNodes()) {
            if (pnode.getNodeContainer().equals(container)) {
                for (NodeImpl cnode : pnode.getNodeChildNodes()) {
                    if (cnode.equals(node)) {
                        ret = pnode;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public Set<NodeImpl> findNodesInParentNode(ContainerImpl container,
                                               NodeImpl node) {
        Set<NodeImpl> ret = new HashSet<NodeImpl>();
        for (NodeImpl cnode : MappingDSGraphDB.getNodes()) {
            if (cnode.getNodeContainer().equals(container) && cnode.getNodeParentNode().equals(node)) {
                ret.add(cnode);
            }
        }
        return ret;
    }

    @Override
    public GateImpl findGateByName(ContainerImpl container, String name) {
        return (GateImpl) findNodeByName(container, name);
    }

    @Override
    public Set<LinkImpl> findLinksBySourceEP(EndpointImpl endpoint) {
        Set<LinkImpl> ret = new HashSet<LinkImpl>();
        for (LinkImpl link : MappingDSGraphDB.getLinks()) {
            if (link.getLinkEndpointSource().equals(endpoint)) {
                ret.add(link);
            }
        }
        return ret;
    }

    @Override
    public Set<LinkImpl> findLinksByDestinationEP(EndpointImpl endpoint) {
        Set<LinkImpl> ret = new HashSet<LinkImpl>();
        for (LinkImpl link : MappingDSGraphDB.getLinks()) {
            if (link.getLinkEndpointTarget()!=null /*else target is multicast transport*/ &&
                        link.getLinkEndpointTarget().equals(endpoint)) {
                ret.add(link);
            }
        }
        return ret;
    }

    @Override
    public LinkImpl findLinkBySourceEPandDestinationEP(EndpointImpl esource, EndpointImpl edest) {
        LinkImpl ret = null;
        for (LinkImpl link : MappingDSGraphDB.getLinks()) {
            if (link.getLinkEndpointSource() != null && link.getLinkEndpointTarget() != null &&
                        link.getLinkEndpointSource().equals(esource) && link.getLinkEndpointTarget().equals(edest)) {
                ret = link;
                break;
            }
        }
        return ret;
    }

    @Override
    public LinkImpl findMulticastLinkBySourceEPandTransport(EndpointImpl esource, TransportImpl transport) {
        LinkImpl ret = null;
        for (LinkImpl link : MappingDSGraphDB.getLinks()) {
            if (link.getLinkEndpointSource() != null && link.getLinkEndpointTarget() == null && link.getLinkTransport() != null &&
                        link.getLinkEndpointSource().equals(esource) && link.getLinkTransport().equals(transport)) {
                ret = link;
                break;
            }
        }
        return ret;
    }
}