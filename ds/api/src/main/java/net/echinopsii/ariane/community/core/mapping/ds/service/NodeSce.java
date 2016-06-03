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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface NodeSce<N extends Node> {
    String Q_MAPPING_CONTAINER_SERVICE = "ARIANE_MAPPING_CONTAINER_SERVICE_Q";

    String OP_CREATE_NODE = "createNode";
    String OP_DELETE_NODE = "deleteNode";
    String OP_GET_NODE = "getNode";
    String OP_GET_NODE_BY_EPURL = "getNodeByEndpointURL";
    String OP_GET_NODE_BY_NAME = "getNodeByName";
    String OP_GET_NODES = "getNodes";

    N createNode(String nodeName, String containerID, String parentNodeID) throws MappingDSException;

    void deleteNode(String nodeID) throws MappingDSException;

    N getNode(String id) throws MappingDSException;
    N getNodeByEndpointURL(String endpointURL) throws MappingDSException;
    N getNodeByName(Node parentNode, String nodeName) throws MappingDSException;

    Set<N> getNodes(String selector) throws MappingDSException;
    Set<N> getNodes(String key, Object value) throws MappingDSException;
}