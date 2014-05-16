/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.repository;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.cache.MappingDSCacheEntity;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import com.spectral.cc.core.mapping.ds.repository.NodeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class NodeRepoImpl implements NodeRepo<NodeImpl> {

    private final static Logger log = LoggerFactory.getLogger(NodeRepoImpl.class);

    public static Set<NodeImpl> getRepository() {
        return MappingDSGraphDB.getNodes();
    }

    @Override
    public NodeImpl saveNode(NodeImpl node) {
        MappingDSGraphDB.saveVertexEntity(node);
        log.debug("Added node {} to graph({}).", new Object[]{node.toString(), MappingDSGraphDB.getVertexMaxCursor()});
        return node;
    }

    @Override
    public void deleteNode(NodeImpl node) {
        for (NodeImpl childNode : node.getNodeChildNodes()) {
            this.deleteNode(childNode);
        }

        for (EndpointImpl endpoint : node.getNodeEndpoints()) {
            log.debug("Deleted endpoint {} from graph({}).", new Object[]{endpoint.getEndpointURL(), MappingDSGraphDB.getVertexMaxCursor()});
            node.removeEndpoint(endpoint);
        }

        for (NodeImpl twinNode : node.getNodeChildNodes()) {
            twinNode.removeTwinNode(node);
        }

        if (node.getNodeParentNode() != null) {
            node.getNodeParentNode().removeNodeChildNode(node);
        }

        if (node.getNodeDepth() == 1) {
            node.getNodeContainer().removeContainerNode(node);
        }

        MappingDSGraphDB.deleteEntity(node);
        log.debug("Deleted node {} and all its linked entities from graph({}).", new Object[]{node.toString(), MappingDSGraphDB.getVertexMaxCursor()});
    }

    @Override
    public NodeImpl findNodeByID(long ID) {
        NodeImpl ret = null;
        MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity(ID);
        if (entity != null) {
            if (entity instanceof NodeImpl) {
                ret = (NodeImpl) entity;
            } else {
                log.error("CONSISTENCY ERROR : entity {} is not a node.", entity.getElement().getId());
            }
        }
        return ret;
    }

    @Override
    public NodeImpl findNodeByEndpointURL(String URL) {
        NodeImpl ret = null;
        EndpointImpl ep = MappingDSGraphDB.getIndexedEndpoint(URL);
        if (ep != null) {
            ret = ep.getEndpointParentNode();
        }
        return ret;
    }

    @Override
    public Set<NodeImpl> findNodesByProperties(String key, Object value) {
        return MappingDSGraphDB.getNodes(key, value);
    }
}