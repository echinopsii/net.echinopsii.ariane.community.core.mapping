/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
 *
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
package net.echinopsii.ariane.community.core.mapping.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class NodeAbs implements Node {
    private String nodeID = null;
    private String nodeName = null;
    private Container nodeContainer = null;
    private final HashMap<String, Object> nodeProperties = new HashMap<>();
    private Node nodeParentNode = null;
    private final Set<Node> nodeChildNodes = new HashSet<>();
    private final Set<Node> nodeTwinNodes = new HashSet<>();
    private final Set<Endpoint> nodeEndpoints = new HashSet<>();

    @Override
    public String getNodeID() {
        return this.nodeID;
    }

    @Override
    public void setNodeID(String ID) {
        this.nodeID = ID;
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    public void setNodeName(String name) throws MappingDSException {
        this.nodeName = name;
    }

    @Override
    public Container getNodeContainer() {
        return this.nodeContainer;
    }

    @Override
    public void setNodeContainer(Container container) throws MappingDSException {
        this.nodeContainer = container;
    }

    @Override
    public HashMap<String, Object> getNodeProperties() {
        HashMap<String, Object> ret;
        synchronized (this.nodeProperties) {
            ret = new HashMap<>(this.nodeProperties);
        }
        return ret;
    }

    @Override
    public void addNodeProperty(String propertyKey, Object value) throws MappingDSException {
        synchronized (this.nodeProperties) {
            this.nodeProperties.put(propertyKey, value);
        }
    }

    @Override
    public void removeNodeProperty(String propertyKey) throws MappingDSException {
        synchronized (this.nodeProperties) {
            this.nodeProperties.remove(propertyKey);
        }
    }

    @Override
    public Node getNodeParentNode() {
        return this.nodeParentNode;
    }

    @Override
    public void setNodeParentNode(Node node) throws MappingDSException {
        this.nodeParentNode = node;
    }

    @Override
    public Set<Node> getNodeChildNodes() {
        Set<Node> ret;
        synchronized (this.nodeChildNodes) {
            ret = new HashSet<>(this.nodeChildNodes);
        }
        return ret;
    }

    @Override
    public boolean addNodeChildNode(Node node) throws MappingDSException {
        boolean ret;
        synchronized (this.nodeChildNodes) {
            ret = this.nodeChildNodes.add(node);
        }
        return ret;
    }

    @Override
    public boolean removeNodeChildNode(Node node) throws MappingDSException {
        boolean ret;
        synchronized (this.nodeChildNodes) {
            ret = this.nodeChildNodes.remove(node);
        }
        return ret;
    }

    @Override
    public Set<Node> getTwinNodes() {
        Set<Node> ret;
        synchronized (this.nodeTwinNodes) {
            ret = new HashSet<>(nodeTwinNodes);
        }
        return ret;
    }

    @Override
    public boolean addTwinNode(Node node) throws MappingDSException {
        boolean ret;
        synchronized (this.nodeTwinNodes) {
            ret = this.nodeTwinNodes.add(node);
        }
        return ret;
    }

    @Override
    public boolean removeTwinNode(Node node) throws MappingDSException {
        boolean ret;
        synchronized (this.nodeTwinNodes) {
            ret = this.nodeTwinNodes.remove(node);
        }
        return ret;
    }

    @Override
    public Set<Endpoint> getNodeEndpoints() {
        Set<Endpoint> ret;
        synchronized (this.nodeEndpoints) {
            ret = new HashSet<>(this.nodeEndpoints);
        }
        return ret;
    }

    @Override
    public boolean addEndpoint(Endpoint endpoint) throws MappingDSException {
        boolean ret;
        synchronized (this.nodeEndpoints) {
            ret = this.nodeEndpoints.add(endpoint);
        }
        return ret;
    }

    @Override
    public boolean removeEndpoint(Endpoint endpoint) throws MappingDSException {
        boolean ret;
        synchronized (this.nodeEndpoints) {
            ret = this.nodeEndpoints.remove(endpoint);
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || (getClass() != o.getClass() && !o.getClass().isAssignableFrom(getClass()))) {
            return false;
        }

        Node tmp = (Node) o;

        return (this.getNodeID().equals(tmp.getNodeID()));
    }

    @Override
    public int hashCode() {
        return (this.nodeID != null && !this.nodeID.equals("")) ? this.getNodeID().hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Node{ID='%s', nodename='%s'}", this.getNodeID(), this.nodeName);
    }

}