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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSceAbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class NodeSceImpl extends SProxNodeSceAbs<NodeImpl> {

    private static final Logger log = LoggerFactory.getLogger(NodeSceImpl.class);


    @Override
    public Node createNode(String nodeName, String containerID, String parentNodeID) throws MappingDSException {
        return null;
    }

    @Override
    public void deleteNode(String nodeID) throws MappingDSException {

    }

    @Override
    public Node getNode(String id) throws MappingDSException {
        return null;
    }

    @Override
    public Node getNodeByEndpointURL(String endpointURL) throws MappingDSException {
        return null;
    }

    @Override
    public Node getNodeByName(Node parentNode, String nodeName) throws MappingDSException {
        return null;
    }

    @Override
    public Set getNodes(String selector) throws MappingDSException {
        return null;
    }

    @Override
    public Set getNodes(String key, Object value) throws MappingDSException {
        return null;
    }
}
