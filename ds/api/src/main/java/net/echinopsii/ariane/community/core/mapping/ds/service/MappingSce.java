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
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Dictionary;
import java.util.Set;

public interface MappingSce {

    String MAPPING_SCE_SERVICE_Q = "ARIANE_MAPPING_SERVICE_Q";

    String MAPPING_SCE_OPERATION_FDN = "OPERATION";
    String MAPPING_SCE_OPERATION_NOT_DEFINED = "NOT_DEFINED";

    String MAPPING_SCE_PARAM_OBJ_ID = "ID";
    String MAPPING_SCE_PARAM_SELECTOR = "selector";

    String SESSION_MGR_OP_OPEN = "openSession";
    String SESSION_MGR_OP_CLOSE = "closeSession";
    String SESSION_MGR_PARAM_CLIENT_ID = "clientID";
    String SESSION_MGR_PARAM_SESSION_ID = "sessionID";

    SessionRegistry getSessionRegistry();

    MapSce getMapSce();

    SProxClusterSce<? extends Cluster> getClusterSce();

    ContainerSce<? extends Container> getContainerSce();

    GateSce<? extends Gate> getGateSce();

    NodeSce<? extends Node> getNodeSce();

    EndpointSce<? extends Endpoint> getEndpointSce();

    LinkSce<? extends Link> getLinkSce();

    TransportSce<? extends Transport> getTransportSce();

    Node getNodeByName(Session session, Container container, String nodeName) throws MappingDSException;

    Node getNodeByName(Container container, String nodeName) throws MappingDSException;

    Node getNodeContainingSubnode(Session session, Container container, Node node) throws MappingDSException;

    Node getNodeContainingSubnode(Container container, Node node) throws MappingDSException;

    Set<Node> getNodesInParentNode(Session session, Container container, Node node) throws MappingDSException;

    Set<Node> getNodesInParentNode(Container container, Node node) throws MappingDSException;

    Gate getGateByName(Session session, Container container, String nodeName) throws MappingDSException;

    Gate getGateByName(Container container, String nodeName) throws MappingDSException;

    Set<Link> getLinksBySourceEP(Session session, Endpoint endpoint) throws MappingDSException;

    Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException;

    Set<Link> getLinksByDestinationEP(Session session, Endpoint endpoint) throws MappingDSException;

    Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException;

    Link getLinkBySourceEPandDestinationEP(Session session, Endpoint esource, Endpoint edest) throws MappingDSException;

    Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException;

    Link getMulticastLinkBySourceEPAndTransport(Session session, Endpoint esource, Transport transport) throws MappingDSException;

    Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException;

    boolean init(Dictionary<Object, Object> properties);

    boolean start();

    boolean stop();

    Session openSession(String clientID);

    Session openSession(String clientID, boolean proxy);

    Session closeSession(Session toClose);

    Session closeSession();
}
