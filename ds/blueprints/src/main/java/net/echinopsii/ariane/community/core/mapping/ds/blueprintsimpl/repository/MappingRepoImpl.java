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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.repository.MappingRepo;

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
    public NodeImpl findNodeByName(ContainerImpl cont, String name) throws MappingDSException {
        NodeImpl ret = null;
        for (NodeImpl node : MappingDSGraphDB.getIndexedNodes(name)) {
            if (node.getNodeContainer()!=null && node.getNodeContainer().equals(cont)) {
                ret = node;
                break;
            }
        }
        return ret;
    }

    @Override
    public GateImpl findGateByName(ContainerImpl container, String name) throws MappingDSException {
        return (GateImpl) findNodeByName(container, name);
    }

    @Override
    public Set<EndpointImpl> findEndpointsBySelector(ContainerImpl container, String selector) throws MappingDSException {
        Set<EndpointImpl> ret = new HashSet<>();
        for (EndpointImpl endpoint : MappingDSGraphDB.getEndpoints(selector))
            if (endpoint.getEndpointParentNode()!=null &&
                endpoint.getEndpointParentNode().getNodeContainer() != null) {
                if (endpoint.getEndpointParentNode().getNodeContainer().equals(container)) ret.add(endpoint);
                else if (endpoint.getEndpointParentNode().getNodeContainer().getContainerParentContainer()!=null) {
                    Container loopContainer = endpoint.getEndpointParentNode().getNodeContainer();
                    while (loopContainer!=null) {
                        if (loopContainer.getContainerParentContainer()!=null && loopContainer.getContainerParentContainer().equals(container)) {
                            ret.add(endpoint);
                            break;
                        } else loopContainer = loopContainer.getContainerParentContainer();
                    }
                }
            }
        return ret;
    }

    @Override
    public Set<EndpointImpl> findEndpointsBySelector(NodeImpl node, String selector) throws MappingDSException {
        Set<EndpointImpl> ret = new HashSet<>();
        for (EndpointImpl endpoint : MappingDSGraphDB.getEndpoints(selector))
            if (endpoint.getEndpointParentNode() != null)
                if (endpoint.getEndpointParentNode().equals(node)) ret.add(endpoint);
                else {
                    Node loopNode = endpoint.getEndpointParentNode();
                    while(loopNode!=null) {
                        if (loopNode.getNodeParentNode()!=null && loopNode.getNodeParentNode().equals(node)) {
                            ret.add(endpoint);
                            break;
                        } else loopNode = loopNode.getNodeParentNode();
                    }
                }
        return ret;
    }

    @Override
    public Set<LinkImpl> findLinksBySourceEP(EndpointImpl endpoint) throws MappingDSException {
        return MappingDSGraphDB.getLinks(endpoint, null, null);
    }

    @Override
    public Set<LinkImpl> findLinksByDestinationEP(EndpointImpl endpoint) throws MappingDSException {
        return MappingDSGraphDB.getLinks(null, endpoint, null);
    }

    @Override
    public LinkImpl findLinkBySourceEPandDestinationEP(EndpointImpl esource, EndpointImpl edest) throws MappingDSException {
        LinkImpl ret = null;
        for (LinkImpl link : MappingDSGraphDB.getLinks(esource, edest, null)) {
            if (link.getLinkEndpointSource() != null && link.getLinkEndpointTarget() != null &&
                        link.getLinkEndpointSource().equals(esource) && link.getLinkEndpointTarget().equals(edest)) {
                ret = link;
                break;
            }
        }
        return ret;
    }

    @Override
    public LinkImpl findMulticastLinkBySourceEPandTransport(EndpointImpl esource, TransportImpl transport) throws MappingDSException {
        LinkImpl ret = null;
        for (LinkImpl link : MappingDSGraphDB.getLinks(esource, null, transport)) {
            if (link.getLinkEndpointSource() != null && link.getLinkEndpointTarget() == null && link.getLinkTransport() != null &&
                        link.getLinkEndpointSource().equals(esource) && link.getLinkTransport().equals(transport)) {
                ret = link;
                break;
            }
        }
        return ret;
    }
}