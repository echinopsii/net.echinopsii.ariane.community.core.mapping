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
import java.util.Iterator;
import java.util.Set;

public abstract class ContainerAbs implements Container {
    private String containerID = null;
    private String containerName = null;
    private String containerCompany = null;
    private String containerProduct = null;
    private String containerType = null;
    private Gate containerPrimaryAdminGate = null;

    private Cluster containerCluster = null;

    private Container containerParentContainer = null;
    private final Set<Container> containerChildContainers = new HashSet<>();

    private final HashMap<String, Object> containerProperties = new HashMap<>();

    private final Set<Node> containerNodes = new HashSet<>();
    private final Set<Gate> containerGates = new HashSet<>();

    @Override
    public String getContainerID() {
        return this.containerID;
    }

    @Override
    public void setContainerID(String ID) {
        this.containerID = ID;
    }

    @Override
    public String getContainerName() {
        return this.containerName;
    }

    @Override
    public void setContainerName(String name) throws MappingDSException {
        this.containerName = name;
    }

    @Override
    public String getContainerCompany() {
        return containerCompany;
    }

    @Override
    public void setContainerCompany(String company) throws MappingDSException {
        this.containerCompany = company;
    }

    @Override
    public String getContainerProduct() {
        return this.containerProduct;
    }

    @Override
    public void setContainerProduct(String product) throws MappingDSException {
        this.containerProduct = product;
    }

    @Override
    public String getContainerType() {
        return this.containerType;
    }

    @Override
    public void setContainerType(String type) throws MappingDSException {
        this.containerType = type;
    }

    @Override
    public String getContainerPrimaryAdminGateURL() {
        String ret = null;
        if (this.containerPrimaryAdminGate != null && containerPrimaryAdminGate.getNodePrimaryAdminEndpoint() != null)
            ret = containerPrimaryAdminGate.getNodePrimaryAdminEndpoint().getEndpointURL();
        return ret;
    }

    @Override
    public Gate getContainerPrimaryAdminGate() {
        return this.containerPrimaryAdminGate;
    }

    @Override
    public void setContainerPrimaryAdminGate(Gate gate) throws MappingDSException {
        this.containerPrimaryAdminGate = gate;
    }

    @Override
    public Cluster getContainerCluster() {
        return this.containerCluster;
    }

    @Override
    public void setContainerCluster(Cluster cluster) throws MappingDSException {
        this.containerCluster = cluster;
    }

    @Override
    public HashMap<String, Object> getContainerProperties() {
        HashMap<String, Object> ret;
        synchronized (this.containerProperties) {
            ret = new HashMap<>(this.containerProperties);
        }
        return ret;
    }

    @Override
    public void addContainerProperty(String propertyKey, Object value) throws MappingDSException {
        synchronized (this.containerProperties) {
            this.containerProperties.put(propertyKey, value);
        }
    }

    @Override
    public void removeContainerProperty(String propertyKey) throws MappingDSException {
        synchronized (this.containerProperties) {
            this.containerProperties.remove(propertyKey);
        }
    }

    @Override
    public Container getContainerParentContainer() {
        return this.containerParentContainer;
    }

    @Override
    public void setContainerParentContainer(Container container) throws MappingDSException {
        this.containerParentContainer = container;
    }

    @Override
    public Set<Container> getContainerChildContainers() {
        HashSet<Container> ret ;
        synchronized (this.containerChildContainers) {
            ret = new HashSet<>(this.containerChildContainers);
        }
        return ret;
    }

    @Override
    public boolean addContainerChildContainer(Container container) throws MappingDSException {
        boolean ret ;
        synchronized (this.containerChildContainers) {
            ret = this.containerChildContainers.add(container);
        }
        return ret;
    }

    @Override
    public boolean removeContainerChildContainer(Container container) throws MappingDSException {
        boolean ret;
        synchronized (this.containerChildContainers) {
            ret = this.containerChildContainers.remove(container);
        }
        return ret;
    }

    @Override
    public Set<Node> getContainerNodes() {
        HashSet<Node> ret;
        synchronized (this.containerNodes) {
            ret = new HashSet<>(this.containerNodes);
        }
        return ret;
    }

    @Override
    public boolean addContainerNode(Node node) throws MappingDSException {
        boolean ret;
        synchronized (this.containerNodes) {
            ret = this.containerNodes.add(node);
        }
        return ret;
    }

    @Override
    public boolean removeContainerNode(Node node) throws MappingDSException {
        boolean ret;
        synchronized (this.containerNodes) {
            ret = this.containerNodes.remove(node);
        }
        return ret;
    }

    @Override
    public Set<Gate> getContainerGates() {
        HashSet<Gate> ret;
        synchronized (this.containerGates) {
            ret = new HashSet<>(this.containerGates);
        }
        return ret;
    }

    @Override
    public boolean addContainerGate(Gate gate) throws MappingDSException {
        boolean ret;
        synchronized (this.containerGates) {
            ret = this.containerGates.add(gate);
        }
        return ret;
    }

    @Override
    public boolean removeContainerGate(Gate gate) throws MappingDSException {
        boolean ret;
        synchronized (this.containerGates) {
            ret = this.containerGates.remove(gate);
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

        Container tmp = (Container) o;
        if (this.containerID==null) {
            return super.equals(o);
        }
        return (this.containerID.equals(tmp.getContainerID()));
    }

    @Override
    public int hashCode() {
        return (this.containerID != null && !this.containerID.equals("")) ? this.containerID.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        String adminUrl = null;
        if (this.containerPrimaryAdminGate != null && this.containerPrimaryAdminGate.getNodePrimaryAdminEndpoint() != null) {
            adminUrl = this.containerPrimaryAdminGate.getNodePrimaryAdminEndpoint().getEndpointURL();
        }
        return String.format("Container{ID='%s', Primary Admin URL='%s'}", this.containerID, adminUrl);
    }
}