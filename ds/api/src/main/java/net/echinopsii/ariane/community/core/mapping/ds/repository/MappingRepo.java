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

package net.echinopsii.ariane.community.core.mapping.ds.repository;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;

import java.util.Set;

public interface MappingRepo<C extends Container, N extends Node, G extends Gate, E extends Endpoint, L extends Link, T extends Transport> {

    ClusterRepo<? extends Cluster> getClusterRepo();

    ContainerRepo<? extends Container> getContainerRepo();

    NodeRepo<? extends Node> getNodeRepo();

    GateRepo<? extends Node, ? extends Gate> getGateRepo();

    EndpointRepo<? extends Endpoint> getEndpointRepo();

    LinkRepo<? extends Link> getLinkRepo();

    TransportRepo<? extends Transport> getTransportRepo();

    N findNodeByName(C container, String name) throws MappingDSException;

    G findGateByName(C container, String name) throws MappingDSException;

    Set<E> findEndpointBySelector(C container, String selector) throws MappingDSException;

    Set<E> findEndpointBySelector(N node, String selector) throws MappingDSException;

    Set<L> findLinksBySourceEP(E endpoint) throws MappingDSException;

    Set<L> findLinksByDestinationEP(E endpoint) throws MappingDSException;

    L findLinkBySourceEPandDestinationEP(E esource, E edest) throws MappingDSException;

    L findMulticastLinkBySourceEPandTransport(E esource, T transport) throws MappingDSException;
}