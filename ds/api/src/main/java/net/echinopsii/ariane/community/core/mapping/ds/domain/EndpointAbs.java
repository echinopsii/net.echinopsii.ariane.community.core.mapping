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

public abstract class EndpointAbs implements Endpoint {
    private String endpointID = null;
    private String endpointURL = null;
    private Node endpointParentNode = null;
    private HashMap<String, Object> endpointProperties = new HashMap<>();
    private Set<Endpoint> endpointTwinEndpoints = new HashSet<>();

    @Override
    public String getEndpointID() {
        return this.endpointID;
    }

    @Override
    public void setEndpointID(String ID) {
        this.endpointID = ID;
    }

    @Override
    public String getEndpointURL() {
        return this.endpointURL;
    }

    @Override
    public void setEndpointURL(String url) throws MappingDSException {
        this.endpointURL = url;
    }

    @Override
    public Node getEndpointParentNode() {
        return this.endpointParentNode;
    }

    @Override
    public void setEndpointParentNode(Node node) throws MappingDSException {
        this.endpointParentNode = node;
    }

    @Override
    public HashMap<String, Object> getEndpointProperties() {
        return endpointProperties;
    }

    @Override
    public void addEndpointProperty(String propertyKey, Object value) throws MappingDSException {
        this.endpointProperties.put(propertyKey, value);
    }

    @Override
    public void removeEndpointProperty(String propertyKey) throws MappingDSException {
        if (this.endpointProperties!=null) this.endpointProperties.remove(propertyKey);
    }

    @Override
    public Set<Endpoint> getTwinEndpoints() {
        return this.endpointTwinEndpoints;
    }

    @Override
    public boolean addTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        return this.endpointTwinEndpoints.add(endpoint);
    }

    @Override
    public boolean removeTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        return this.endpointTwinEndpoints.remove(endpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || (getClass() != o.getClass() && !o.getClass().isAssignableFrom(getClass()))) {
            return false;
        }

        Endpoint tmp = (Endpoint) o;
        if (this.endpointID ==null) {
            return super.equals(o);
        }
        return (this.endpointID.equals(tmp.getEndpointID()));
    }

    @Override
    public int hashCode() {
        return (endpointID != null && !endpointID.equals("")) ? endpointID.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Endpoint{ID='%s', URL='%s'}", this.endpointID, this.endpointURL);
    }

}
