/**
 * Mapping Datastore Messsaging Driver Implementation :
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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.HashMap;
import java.util.Set;

public class NodeImpl implements SProxNode {
    @Override
    public void setNodeName(Session session, String name) throws MappingDSException {

    }

    @Override
    public void setNodeContainer(Session session, Container container) throws MappingDSException {

    }

    @Override
    public void addNodeProperty(Session session, String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeNodeProperty(Session session, String propertyKey) throws MappingDSException {

    }

    @Override
    public void setNodeParentNode(Session session, Node node) throws MappingDSException {

    }

    @Override
    public boolean addNodeChildNode(Session session, Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeNodeChildNode(Session session, Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean addTwinNode(Session session, Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeTwinNode(Session session, Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean addEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public String getNodeID() {
        return null;
    }

    @Override
    public void setNodeID(String ID) {

    }

    @Override
    public String getNodeName() {
        return null;
    }

    @Override
    public void setNodeName(String name) throws MappingDSException {

    }

    @Override
    public Container getNodeContainer() {
        return null;
    }

    @Override
    public void setNodeContainer(Container container) throws MappingDSException {

    }

    @Override
    public long getNodeDepth() {
        return 0;
    }

    @Override
    public HashMap<String, Object> getNodeProperties() {
        return null;
    }

    @Override
    public void addNodeProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeNodeProperty(String propertyKey) throws MappingDSException {

    }

    @Override
    public Node getNodeParentNode() {
        return null;
    }

    @Override
    public void setNodeParentNode(Node node) throws MappingDSException {

    }

    @Override
    public Set<? extends Node> getNodeChildNodes() {
        return null;
    }

    @Override
    public boolean addNodeChildNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeNodeChildNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public Set<? extends Node> getTwinNodes() {
        return null;
    }

    @Override
    public boolean addTwinNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeTwinNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public Set<? extends Endpoint> getNodeEndpoints() {
        return null;
    }

    @Override
    public boolean addEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }
}
