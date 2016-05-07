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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository.NodeRepoImpl;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class NodeSceImpl implements NodeSce<NodeImpl> {

    final static String CREATE_NODE = "createNode";
    final static String DELETE_NODE = "deleteNode";
    final static String GET_NODE = "getNode";
    final static String GET_NODES = "getNodes";

    private static final Logger log = LoggerFactory.getLogger(NodeSceImpl.class);

    private MappingSceImpl sce = null;

    public NodeSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public NodeImpl createNode(Session session, String nodeName, Long containerID, Long parentNodeID) throws MappingDSException {
        NodeImpl ret = null;
        if (session != null && session.isRunning())
            ret = (NodeImpl) session.execute(this, CREATE_NODE, new Object[]{nodeName, containerID, parentNodeID});
        return ret;
    }

    @Override
    public NodeImpl createNode(String nodeName, Long containerID, Long parentNodeID) throws MappingDSException {
        ContainerImpl container = sce.getGlobalRepo().getContainerRepo().findContainerByID(containerID);
        NodeImpl ret = sce.getGlobalRepo().findNodeByName(container, nodeName);
        if (ret == null) {
            if (container != null) {
                ret = new NodeImpl();
                ret.setNodeName(nodeName);
                ret.setNodeContainer(container);
                if (parentNodeID != 0) {
                    NodeImpl parent = sce.getGlobalRepo().getNodeRepo().findNodeByID(parentNodeID);
                    if (parent != null) {
                        ret.setNodeParentNode(parent);
                    } else {
                        throw new MappingDSException("Node creation failed : provided parend node " + parentNodeID + " doesn't exists.");
                    }
                }
                sce.getGlobalRepo().getNodeRepo().saveNode(ret);
                if (ret.getNodeParentNode() != null)
                    ret.getNodeParentNode().addNodeChildNode(ret);
                else
                    container.addContainerNode(ret);
            } else {
                throw new MappingDSException("Node creation failed : provided container " + containerID + " doesn't exists.");
            }
        } else {
            log.debug("Node ({}) creation failed : already exists", nodeName);
        }
        return ret;
    }

    @Override
    public void deleteNode(Session session, Long nodeID) throws MappingDSException {
        if (session != null && session.isRunning())
            session.execute(this, DELETE_NODE, new Object[]{nodeID});
    }

    @Override
    public void deleteNode(Long nodeID) throws MappingDSException {
        NodeImpl remove = sce.getGlobalRepo().getNodeRepo().findNodeByID(nodeID);
        if (remove != null) {
            sce.getGlobalRepo().getNodeRepo().deleteNode(remove);
        } else {
            throw new MappingDSException("Unable to remove node with id " + nodeID + ": node not found .");
        }
    }

    @Override
    public NodeImpl getNode(Session session, Long id) throws MappingDSException {
        NodeImpl ret = null;
        if (session != null && session.isRunning())
            ret = (NodeImpl) session.execute(this, GET_NODE, new Object[]{id});
        return ret;
    }

    @Override
    public NodeImpl getNode(Long id) {
        return sce.getGlobalRepo().getNodeRepo().findNodeByID(id);
    }

    @Override
    public NodeImpl getNode(Session session, String endpointURL) throws MappingDSException {
        NodeImpl ret = null;
        if (session != null && session.isRunning())
            ret = (NodeImpl) session.execute(this, GET_NODE, new Object[]{endpointURL});
        return ret;
    }

    @Override
    public NodeImpl getNode(String endpointURL) {
        return sce.getGlobalRepo().getNodeRepo().findNodeByEndpointURL(endpointURL);
    }

    @Override
    public NodeImpl getNode(Session session, Node parentNode, String nodeName) throws MappingDSException {
        NodeImpl ret = null;
        if (session != null && session.isRunning())
            ret = (NodeImpl) session.execute(this, GET_NODE, new Object[]{parentNode, nodeName});
        return ret;
    }

    @Override
    public NodeImpl getNode(Node parentNode, String nodeName) {
        NodeImpl ret = null;
        if (parentNode instanceof NodeImpl) {
            for (Node childNode : parentNode.getNodeChildNodes())
                if (childNode instanceof NodeImpl && childNode.getNodeName().equals(nodeName)) {
                    ret = (NodeImpl)childNode;
                    break;
                }
        }
        return ret;
    }

    @Override
    public Set<NodeImpl> getNodes(Session session, String key, Object value) throws MappingDSException {
        Set<NodeImpl> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<NodeImpl>) session.execute(this, GET_NODES, new Object[]{key, value});
        return ret;
    }

    @Override
    public Set<NodeImpl> getNodes(String key, Object value) {
        return sce.getGlobalRepo().getNodeRepo().findNodesByProperties(key, value);
    }

    @Override
    public Set<NodeImpl> getNodes(Session session, String selector) throws MappingDSException {
        Set<NodeImpl> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<NodeImpl>) session.execute(this, GET_NODES, new Object[]{selector});
        return ret;
    }

    @Override
    public Set<NodeImpl> getNodes(String selector) {
        if (selector!=null)
            return sce.getGlobalRepo().getNodeRepo().findNodesBySelector(selector);
        else
            return NodeRepoImpl.getRepository();
    }
}