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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.repository.NodeRepo;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class NodeRepoImpl implements NodeRepo<NodeImpl> {

    private final static Logger log = LoggerFactory.getLogger(NodeRepoImpl.class);

    public static Set<NodeImpl> getRepository() throws MappingDSException {
        return MappingDSGraphDB.getNodes();
    }

    @Override
    public NodeImpl saveNode(NodeImpl node) {
        MappingDSGraphDB.saveVertexEntity(node);
        log.debug("Added node {} to graph.", new Object[]{node.toString()});
        return node;
    }

    @Override
    public void deleteNode(NodeImpl node) throws MappingDSException {
        node.setIsBeingDeleted();
        Set<Node> childNodesToRemove = new HashSet<>(node.getNodeChildNodes());
        for (Node childNode : childNodesToRemove) this.deleteNode((NodeImpl)childNode);
        childNodesToRemove.clear();

        Set<Endpoint> clonedESet = new HashSet<>(node.getNodeEndpoints());
        for (Endpoint endpoint : clonedESet) {
            log.debug("Deleted endpoint {} from graph.", new Object[]{endpoint.getEndpointURL()});
            node.removeEndpoint(endpoint);
        }
        clonedESet.clear();

        Set<Node> twinNodesToRemove = new HashSet<>(node.getTwinNodes());
        for (Node twinNode : twinNodesToRemove) twinNode.removeTwinNode(node);
        twinNodesToRemove.clear();

        if (node.getNodeParentNode() != null) node.getNodeParentNode().removeNodeChildNode(node);

        if (node.getNodeParentNode()==null) node.getNodeContainer().removeContainerNode(node);

        MappingDSGraphDB.deleteEntity(node);
        log.debug("Deleted node {} and all its linked entities from graph.", new Object[]{node.toString()});
    }

    @Override
    public NodeImpl findNodeByID(String ID) throws MappingDSException {
        NodeImpl ret = null;
        MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity(ID);
        if (entity != null) {
            if (entity instanceof NodeImpl) {
                ret = (NodeImpl) entity;
            } else {
                log.error("CONSISTENCY ERROR : entity {} is not a node.", entity.getElement().getId());
                log.error(entity.getClass().toString());
                throw new MappingDSException("CONSISTENCY ERROR : entity " + entity.getElement().getId() + " is not a node.");
            }
        }
        return ret;
    }

    @Override
    public NodeImpl findNodeByName(NodeImpl parentNode, String nodeName) throws MappingDSException {
        NodeImpl ret = null;
        for (NodeImpl node : MappingDSGraphDB.getIndexedNodes(nodeName)) {
            if (node.getNodeParentNode()!=null && node.getNodeParentNode().equals(parentNode)) {
                ret = node;
                break;
            }
        }
        return ret;
    }

    @Override
    public NodeImpl findNodeByEndpointURL(String URL) throws MappingDSException {
        NodeImpl ret = null;
        EndpointImpl ep = MappingDSGraphDB.getIndexedEndpoint(URL);
        if (ep != null) ret = (NodeImpl) ep.getEndpointParentNode();
        return ret;
    }

    @Override
    public Set<NodeImpl> findNodesByProperties(String key, Object value) throws MappingDSException {
        return MappingDSGraphDB.getNodes(key, value);
    }

    @Override
    public Set<NodeImpl> findNodesBySelector(String selector) throws MappingDSException {
        return MappingDSGraphDB.getNodes(selector);
    }

}