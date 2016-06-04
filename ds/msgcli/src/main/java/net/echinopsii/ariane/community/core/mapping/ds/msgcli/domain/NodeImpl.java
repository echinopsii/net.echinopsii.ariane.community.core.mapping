/**
 * Mapping Datastore Messsaging Driver Implementation :
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNodeAbs;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodeImpl extends SProxNodeAbs implements SProxNode {

    class NodeReplyWorker implements AppMsgWorker {
        private NodeImpl node;

        public NodeReplyWorker(NodeImpl node) {
            this.node = node;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (node!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));
                    if (body != null) {
                        //TODO
                    }
                } else NodeImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(NodeImpl.class);

    private NodeReplyWorker nodeReplyWorker = new NodeReplyWorker(this);
    private String containerID;
    private String parentNodeID;
    private List<String> childNodesID;
    private List<String> twinNodesID;
    private List<String> nodeEndpoints;

    public NodeReplyWorker getNodeReplyWorker() {
        return nodeReplyWorker;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getParentNodeID() {
        return parentNodeID;
    }

    public void setParentNodeID(String parentNodeID) {
        this.parentNodeID = parentNodeID;
    }

    public List<String> getChildNodesID() {
        return childNodesID;
    }

    public void setChildNodesID(List<String> childNodesID) {
        this.childNodesID = childNodesID;
    }

    public List<String> getTwinNodesID() {
        return twinNodesID;
    }

    public void setTwinNodesID(List<String> twinNodesID) {
        this.twinNodesID = twinNodesID;
    }

    public void setNodeEndpoints(List<String> nodeEndpoints) {
        this.nodeEndpoints = nodeEndpoints;
    }

    @Override
    public void setNodeName(String name) throws MappingDSException {

    }

    @Override
    public Container getNodeContainer() {
        return super.getNodeContainer();
    }

    @Override
    public void setNodeContainer(Container container) throws MappingDSException {

    }

    @Override
    public void addNodeProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeNodeProperty(String propertyKey) throws MappingDSException {

    }

    @Override
    public Node getNodeParentNode() {
        return super.getNodeParentNode();
    }

    @Override
    public void setNodeParentNode(Node node) throws MappingDSException {

    }

    @Override
    public Set<Node> getNodeChildNodes(){
        return super.getNodeChildNodes();
    }

    @Override
    public boolean addNodeChildNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeNodeChildNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public Set<Node> getTwinNodes() {
        return super.getTwinNodes();
    }

    @Override
    public boolean addTwinNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeTwinNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public Set<Endpoint> getNodeEndpoints() {
        return super.getNodeEndpoints();
    }

    @Override
    public boolean addEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }
}
