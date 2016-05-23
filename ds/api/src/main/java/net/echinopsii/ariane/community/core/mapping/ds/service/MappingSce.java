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

package net.echinopsii.ariane.community.core.mapping.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Dictionary;
import java.util.Set;

public interface MappingSce {

    public SessionRegistry getSessionRegistry();

    public MapSce getMapSce();

    public ClusterSce<? extends Cluster> getClusterSce();

    public ContainerSce<? extends Container> getContainerSce();

    public GateSce<? extends Gate> getGateSce();

    public NodeSce<? extends Node> getNodeSce();

    public EndpointSce<? extends Endpoint> getEndpointSce();

    public LinkSce<? extends Link> getLinkSce();

    public TransportSce<? extends Transport> getTransportSce();

    public Node getNodeByName(Session session, Container container, String nodeName) throws MappingDSException;

    public Node getNodeByName(Container container, String nodeName) throws MappingDSException;

    public Node getNodeContainingSubnode(Session session, Container container, Node node) throws MappingDSException;

    public Node getNodeContainingSubnode(Container container, Node node) throws MappingDSException;

    public Set<Node> getNodesInParentNode(Session session, Container container, Node node) throws MappingDSException;

    public Set<Node> getNodesInParentNode(Container container, Node node) throws MappingDSException;

    public Gate getGateByName(Session session, Container container, String nodeName) throws MappingDSException;

    public Gate getGateByName(Container container, String nodeName) throws MappingDSException;

    public Set<Link> getLinksBySourceEP(Session session, Endpoint endpoint) throws MappingDSException;

    public Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException;

    public Set<Link> getLinksByDestinationEP(Session session, Endpoint endpoint) throws MappingDSException;

    public Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException;

    public Link getLinkBySourceEPandDestinationEP(Session session, Endpoint esource, Endpoint edest) throws MappingDSException;

    public Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException;

    Link getMulticastLinkBySourceEPAndTransport(Session session, Endpoint esource, Transport transport) throws MappingDSException;

    Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException;

    public boolean init(Dictionary<Object, Object> properties);

    public boolean start();

    public boolean stop();

    public Session openSession(String clientID);

    public Session openSession(String clientID, boolean proxy);

    public Session closeSession(Session toClose);

    public Session closeSession();

    @Deprecated
    public void unsetAutoCommit();

    @Deprecated
    public void setAutoCommit(boolean autoCommit);

    @Deprecated
    public void commit();

    @Deprecated
    public void rollback();
}
