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
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class NodeSceImpl extends SProxNodeSceAbs<NodeImpl> {

    private static final Logger log = LoggerFactory.getLogger(NodeSceImpl.class);

    private MappingSceImpl sce = null;

    public NodeSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public NodeImpl createNode(String nodeName, String containerID, String parentNodeID) throws MappingDSException {
        NodeImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createNode(session, nodeName, containerID, parentNodeID);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ContainerImpl container = sce.getGlobalRepo().getContainerRepo().findContainerByID(containerID);;
            NodeImpl parentNode = null;
            if (parentNodeID!=null) {
                parentNode = sce.getGlobalRepo().getNodeRepo().findNodeByID(parentNodeID);
                if (parentNode!=null) ret = sce.getGlobalRepo().getNodeRepo().findNodeByName(parentNode, nodeName);
                else throw new MappingDSException("Node creation failed : provided parend node " + parentNodeID + " doesn't exists.");
            } else ret = sce.getGlobalRepo().findNodeByName(container, nodeName);

            if (ret == null) {
                if (container != null) {
                    ret = new NodeImpl();
                    sce.getGlobalRepo().getNodeRepo().saveNode(ret);
                    ret.setNodeName(nodeName);
                    if (parentNode != null) ret.setNodeParentNode(parentNode);
                    ret.setNodeContainer(container);

                    if (ret.getNodeParentNode() != null)
                        ret.getNodeParentNode().addNodeChildNode(ret);
                    else
                        container.addContainerNode(ret);
                } else throw new MappingDSException("Node creation failed : provided container " + containerID + " doesn't exists.");
            }
        }
        return ret;
    }

    @Override
    public void deleteNode(String nodeID) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) deleteNode(session, nodeID);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            NodeImpl remove = sce.getGlobalRepo().getNodeRepo().findNodeByID(nodeID);
            if (remove != null) {
                sce.getGlobalRepo().getNodeRepo().deleteNode(remove);
            } else {
                throw new MappingDSException("Unable to remove node with id " + nodeID + ": node not found .");
            }
        }
    }

    @Override
    public NodeImpl getNode(String id) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getNode(session, id);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getNodeRepo().findNodeByID(id);
    }

    @Override
    public NodeImpl getNodeByEndpointURL(String endpointURL) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getNodeByEndpointURL(session, endpointURL);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getNodeRepo().findNodeByEndpointURL(endpointURL);
    }

    @Override
    public NodeImpl getNodeByName(Node parentNode, String nodeName) throws MappingDSException {
        NodeImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getNodeByName(session, parentNode, nodeName);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (parentNode instanceof NodeImpl) {
                for (Node childNode : parentNode.getNodeChildNodes())
                    if (childNode instanceof NodeImpl && childNode.getNodeName().equals(nodeName)) {
                        ret = (NodeImpl) childNode;
                        break;
                    }
            }
        }
        return ret;
    }

    @Override
    public Set<NodeImpl> getNodes(String key, Object value) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getNodes(session, key, value);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getNodeRepo().findNodesByProperties(key, value);
    }

    @Override
    public Set<NodeImpl> getNodes(String selector) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getNodes(session, selector);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (selector != null)
                return sce.getGlobalRepo().getNodeRepo().findNodesBySelector(selector);
            else
                return NodeRepoImpl.getRepository();
        }
    }
}