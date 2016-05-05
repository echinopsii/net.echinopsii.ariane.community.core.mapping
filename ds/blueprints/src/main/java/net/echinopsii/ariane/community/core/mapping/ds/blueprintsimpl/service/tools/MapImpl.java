/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 07/04/14 echinopsii
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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Map;

import java.util.HashSet;
import java.util.Set;

public class MapImpl implements Map<ClusterImpl, ContainerImpl, GateImpl, NodeImpl, EndpointImpl, LinkImpl, TransportImpl> {

    private HashSet<Cluster> clusters = new HashSet<Cluster>();
    private HashSet<Container> containers = new HashSet<Container>();
    private HashSet<Gate> gates = new HashSet<Gate>();
    private HashSet<Node> nodes = new HashSet<Node>();
    private HashSet<Endpoint> endpoints = new HashSet<Endpoint>();
    private HashSet<Link> links = new HashSet<Link>();
    private HashSet<Transport> transports = new HashSet<Transport>();

    @Override
    public Set<Cluster> getClusters() {
        return clusters;
    }

    @Override
    public Map addCluster(Cluster cluster) {
        clusters.add(cluster);
        return this;
    }

    @Override
    public Set<Container> getContainers() {
        return containers;
    }

    @Override
    public Map addContainer(Container container) {
        containers.add(container);
        return this;
    }

    @Override
    public Set<Gate> getGates() {
        return gates;
    }

    @Override
    public Map addGate(Gate gate) {
        gates.add(gate);
        return this;
    }

    @Override
    public Set<Node> getNodes() {
        return nodes;
    }

    @Override
    public Map addNode(Node node) {
        nodes.add(node);
        return this;
    }

    @Override
    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public Map addEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
        return this;
    }

    @Override
    public Set<Link> getLinks() {
        return links;
    }

    @Override
    public Map addLink(Link link) {
        links.add(link);
        return this;
    }

    @Override
    public Set<Transport> getTransports() {
        return transports;
    }

    @Override
    public Map addTransport(Transport transport) {
        transports.add(transport);
        return this;
    }
}