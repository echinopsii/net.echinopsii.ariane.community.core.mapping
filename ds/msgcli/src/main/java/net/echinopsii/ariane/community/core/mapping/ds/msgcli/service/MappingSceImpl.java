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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Dictionary;
import java.util.Set;

public class MappingSceImpl implements MappingSce {
    @Override
    public SessionRegistry getSessionRegistry() {
        return null;
    }

    @Override
    public MapSce getMapSce() {
        return null;
    }

    @Override
    public ClusterSce<? extends Cluster> getClusterSce() {
        return null;
    }

    @Override
    public ContainerSce<? extends Container> getContainerSce() {
        return null;
    }

    @Override
    public GateSce<? extends Gate> getGateSce() {
        return null;
    }

    @Override
    public NodeSce<? extends Node> getNodeSce() {
        return null;
    }

    @Override
    public EndpointSce<? extends Endpoint> getEndpointSce() {
        return null;
    }

    @Override
    public LinkSce<? extends Link> getLinkSce() {
        return null;
    }

    @Override
    public TransportSce<? extends Transport> getTransportSce() {
        return null;
    }

    @Override
    public Node getNodeByName(Session session, Container container, String nodeName) throws MappingDSException {
        return null;
    }

    @Override
    public Node getNodeByName(Container container, String nodeName) throws MappingDSException {
        return null;
    }

    @Override
    public Node getNodeContainingSubnode(Session session, Container container, Node node) throws MappingDSException {
        return null;
    }

    @Override
    public Node getNodeContainingSubnode(Container container, Node node) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Node> getNodesInParentNode(Session session, Container container, Node node) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Node> getNodesInParentNode(Container container, Node node) throws MappingDSException {
        return null;
    }

    @Override
    public Gate getGateByName(Session session, Container container, String nodeName) throws MappingDSException {
        return null;
    }

    @Override
    public Gate getGateByName(Container container, String nodeName) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Link> getLinksBySourceEP(Session session, Endpoint endpoint) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Session session, Endpoint endpoint) throws MappingDSException {
        return null;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException {
        return null;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Session session, Endpoint esource, Endpoint edest) throws MappingDSException {
        return null;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException {
        return null;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Session session, Endpoint esource, Transport transport) throws MappingDSException {
        return null;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException {
        return null;
    }

    @Override
    public boolean init(Dictionary<Object, Object> properties) {
        return false;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public Session openSession(String clientID) {
        return null;
    }

    @Override
    public Session openSession(String clientID, boolean proxy) {
        return null;
    }

    @Override
    public Session closeSession(Session toClose) {
        return null;
    }

    @Override
    public Session closeSession() {
        return null;
    }

    @Override
    public void unsetAutoCommit() {

    }

    @Override
    public void setAutoCommit(boolean autoCommit) {

    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }
}
