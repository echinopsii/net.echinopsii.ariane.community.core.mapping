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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.repository.NodeRepoImpl;
import com.spectral.cc.core.mapping.ds.service.NodeSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class NodeSceImpl implements NodeSce<NodeImpl> {

    private static final Logger log = LoggerFactory.getLogger(NodeSceImpl.class);

    private MappingSceImpl sce = null;

    public NodeSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public NodeImpl createNode(String nodeName, long containerID, long parentNodeID) {
        ContainerImpl cont = sce.getGlobalRepo().getContainerRepo().findContainerByID(containerID);
        NodeImpl ret = sce.getGlobalRepo().findNodeByName(cont, nodeName);
        if (ret == null) {
            ContainerImpl container = sce.getGlobalRepo().getContainerRepo().findContainerByID(containerID);
            if (container != null) {
                ret = new NodeImpl();
                ret.setNodeName(nodeName);
                ret.setNodeContainer(container);
                if (parentNodeID != 0) {
                    NodeImpl parent = sce.getGlobalRepo().getNodeRepo().findNodeByID(parentNodeID);
                    if (parent != null) {
                        ret.setNodeParentNode(parent);
                    } else {
                        log.error("Parent Node {} is not is the node repository !", new Object[]{parentNodeID});
                    }
                }
                sce.getGlobalRepo().getNodeRepo().saveNode(ret);
                container.addContainerNode(ret);
                if (ret.getNodeParentNode() != null) {
                    ret.getNodeParentNode().addNodeChildNode(ret);
                }
            } else {
                log.error("Container {} is not is the container repository !", new Object[]{containerID});
            }
        } else {
            // TODO: raise exception
        }
        return ret;
    }

    @Override
    public void deleteNode(long nodeID) {
        NodeImpl remove = sce.getGlobalRepo().getNodeRepo().findNodeByID(nodeID);
        if (remove != null) {
            sce.getGlobalRepo().getNodeRepo().deleteNode(remove);
        } else {
            // TODO: raise exception
        }
    }

    @Override
    public NodeImpl getNode(long id) {
        return sce.getGlobalRepo().getNodeRepo().findNodeByID(id);
    }

    @Override
    public Set<NodeImpl> getNodes(String selector) {
        // TODO : manage selector - check graphdb query
        return NodeRepoImpl.getRepository();
    }

    @Override
    public Set<NodeImpl> getNodes(String key, Object value) {
        return sce.getGlobalRepo().getNodeRepo().findNodesByProperties(key, value);
    }
}