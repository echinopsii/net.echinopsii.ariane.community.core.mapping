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

package net.echinopsii.ariane.core.mapping.ds.repository;

import net.echinopsii.ariane.core.mapping.ds.domain.*;

import java.util.Set;

public interface MappingRepo<C extends Container, N extends Node, G extends Gate, E extends Endpoint, L extends Link, T extends Transport> {

    public ClusterRepo<? extends Cluster> getClusterRepo();

    public ContainerRepo<? extends Container> getContainerRepo();

    public NodeRepo<? extends Node> getNodeRepo();

    public GateRepo<? extends Node, ? extends Gate> getGateRepo();

    public EndpointRepo<? extends Endpoint> getEndpointRepo();

    public LinkRepo<? extends Link> getLinkRepo();

    public TransportRepo<? extends Transport> getTransportRepo();

    public N findNodeByName(C container, String name);

    public N findNodeContainingSubnode(C container, N node);

    public Set<N> findNodesInParentNode(C container, N node);

    public G findGateByName(C container, String name);

    public Set<L> findLinksBySourceEP(E endpoint);

    public Set<L> findLinksByDestinationEP(E endpoint);

    public L findLinkBySourceEPandDestinationEP(E esource, E edest);

    public L findMulticastLinkBySourceEPandTransport(E esource, T transport);
}