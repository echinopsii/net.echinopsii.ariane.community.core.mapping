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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface SProxNodeSce<N extends Node> extends NodeSce {
    String CREATE_NODE = "createNode";
    String DELETE_NODE = "deleteNode";
    String GET_NODE = "getNode";
    String GET_NODE_BY_EPURL = "getNodeByEndpointURL";
    String GET_NODE_BY_NAME = "getNodeByName";
    String GET_NODES = "getNodes";

    N createNode(Session session, String nodeName, String containerID, String parentNodeID) throws MappingDSException;

    void deleteNode(Session session, String nodeID) throws MappingDSException;

    N getNode(Session session, String id) throws MappingDSException;
    N getNodeByEndpointURL(Session session, String endpointURL) throws MappingDSException;
    N getNodeByName(Session session, Node parentNode, String nodeName) throws MappingDSException;

    Set<N> getNodes(Session session, String selector) throws MappingDSException;
    Set<N> getNodes(Session session, String key, Object value) throws MappingDSException;
}