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

package net.echinopsii.ariane.community.core.mapping.ds.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.domain.*;

import java.util.Set;

public interface Map<CL extends Cluster, CO extends Container, G extends Gate, N extends Node, E extends Endpoint, L extends Link, T extends Transport> {
    public Set<Cluster> getClusters();
    public Map          addCluster(Cluster cluster);

    public Set<Container> getContainers();
    public Map            addContainer(Container container);

    public Set<Gate> getGates();
    public Map       addGate(Gate gate);

    public Set<Node> getNodes();
    public Map       addNode(Node node);

    public Set<Endpoint> getEndpoints();
    public Map           addEndpoint(Endpoint endpoint);

    public Set<Link> getLinks();
    public Map       addLink(Link link);

    public Set<Transport> getTransports();
    public Map            addTransport(Transport transport);
}