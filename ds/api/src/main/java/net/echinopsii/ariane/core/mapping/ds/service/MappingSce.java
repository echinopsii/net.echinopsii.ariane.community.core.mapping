/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
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

package net.echinopsii.ariane.core.mapping.ds.service;

import net.echinopsii.ariane.core.mapping.ds.domain.*;

import java.util.Dictionary;
import java.util.Set;

public interface MappingSce {

    public MapSce getMapSce();

    public ClusterSce<? extends Cluster> getClusterSce();

    public ContainerSce<? extends Container> getContainerSce();

    public GateSce<? extends Gate> getGateSce();

    public NodeSce<? extends Node> getNodeSce();

    public EndpointSce<? extends Endpoint> getEndpointSce();

    public LinkSce<? extends Link> getLinkSce();

    public TransportSce<? extends Transport> getTransportSce();

    public Node getNodeByName(Container container, String nodeName);

    public Node getNodeContainingSubnode(Container container, Node node);

    public Set<Node> getNodesInParentNode(Container container, Node node);

    public Gate getGateByName(Container container, String nodeName);

    public Set<Link> getLinksBySourceEP(Endpoint endpoint);

    public Set<Link> getLinksByDestinationEP(Endpoint endpoint);

    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest);

    public boolean init(Dictionary<Object, Object> properties);

    public boolean start();

    public boolean stop();

    public void unsetAutoCommit();

    public void setAutoCommit();

    public void commit();

    public void rollback();

    Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport);
}
