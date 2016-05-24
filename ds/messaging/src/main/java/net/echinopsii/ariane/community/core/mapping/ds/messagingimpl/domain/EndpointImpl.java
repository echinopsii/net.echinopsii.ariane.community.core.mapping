/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
 * Author: mathilde.ffrench@echinopsii.net
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

package net.echinopsii.ariane.community.core.mapping.ds.messagingimpl.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.HashMap;
import java.util.Set;

public class EndpointImpl implements SProxEndpoint {
    @Override
    public void setEndpointURL(Session session, String url) throws MappingDSException {

    }

    @Override
    public void setEndpointParentNode(Session session, Node node) throws MappingDSException {

    }

    @Override
    public boolean addTwinEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeTwinEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public void addEndpointProperty(Session session, String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeEndpointProperty(Session session, String propertyKey) throws MappingDSException {

    }

    @Override
    public String getEndpointID() {
        return null;
    }

    @Override
    public String getEndpointURL() {
        return null;
    }

    @Override
    public void setEndpointURL(String url) throws MappingDSException {

    }

    @Override
    public Node getEndpointParentNode() {
        return null;
    }

    @Override
    public void setEndpointParentNode(Node node) throws MappingDSException {

    }

    @Override
    public Set<? extends Endpoint> getTwinEndpoints() {
        return null;
    }

    @Override
    public boolean addTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public HashMap<String, Object> getEndpointProperties() {
        return null;
    }

    @Override
    public void addEndpointProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeEndpointProperty(String propertyKey) throws MappingDSException {

    }
}
