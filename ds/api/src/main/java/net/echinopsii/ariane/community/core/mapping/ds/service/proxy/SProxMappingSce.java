/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
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

package net.echinopsii.ariane.community.core.mapping.ds.service.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Set;

public interface SProxMappingSce<S extends Session, SR extends SessionRegistry> extends MappingSce {

    String SESSION_MGR_OP_OPEN = "openSession";
    String SESSION_MGR_OP_CLOSE = "closeSession";
    String SESSION_MGR_PARAM_CLIENT_ID = "clientID";
    String SESSION_MGR_PARAM_SESSION_ID = "sessionID";

    String GET_NODE_BY_NAME = "getNodeByName";
    String GET_NODE_CONTAINING_SUB_NODE = "getNodeContainingSubnode";
    String GET_NODES_IN_PARENT_NODE = "getNodesInParentNode";
    String GET_GATE_BY_NAME = "getGateByName";
    String GET_LINKS_BY_SOURCE_EP = "getLinksBySourceEP";
    String GET_LINKS_BY_DESTINATION_EP = "getLinksByDestinationEP";
    String GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP = "getLinkBySourceEPandDestinationEP";
    String GET_LINK_BY_SOURCE_EP_AND_TRANSPORT = "getLinkBySourceEPandTransport";

    SR getSessionRegistry();

    S openSession(String clientID);

    S openSession(String clientID, boolean proxy);

    S closeSession(S toClose);

    S closeSession();

    Node getNodeByName(Session session, Container container, String nodeName) throws MappingDSException;

    Node getNodeContainingSubnode(Session session, Container container, Node node) throws MappingDSException;

    Set<Node> getNodesInParentNode(Session session, Container container, Node node) throws MappingDSException;

    Gate getGateByName(Session session, Container container, String nodeName) throws MappingDSException;

    Set<Link> getLinksBySourceEP(Session session, Endpoint endpoint) throws MappingDSException;

    Set<Link> getLinksByDestinationEP(Session session, Endpoint endpoint) throws MappingDSException;

    Link getLinkBySourceEPandDestinationEP(Session session, Endpoint esource, Endpoint edest) throws MappingDSException;

    Link getMulticastLinkBySourceEPAndTransport(Session session, Endpoint esource, Transport transport) throws MappingDSException;
}