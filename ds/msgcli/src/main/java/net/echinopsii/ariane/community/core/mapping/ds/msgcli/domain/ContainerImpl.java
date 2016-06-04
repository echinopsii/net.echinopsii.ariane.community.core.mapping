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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainerAbs;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContainerImpl extends SProxContainerAbs implements SProxContainer {

    class ContainerReplyWorker implements AppMsgWorker {
        private ContainerImpl container;

        public ContainerReplyWorker(ContainerImpl container) {
            this.container = container;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (container != null) {
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
                } else ContainerImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);

    private ContainerReplyWorker containerReplyWorker = new ContainerReplyWorker(this);
    private String primaryAdminGateID;
    private String clusterID;
    private String parentContainer;
    private List<String> childContainersID;
    private List<String> nodesID;
    private List<String> gatesID;

    public ContainerReplyWorker getContainerReplyWorker() {
        return containerReplyWorker;
    }

    public String getPrimaryAdminGateID() {
        return primaryAdminGateID;
    }

    public void setPrimaryAdminGateID(String primaryAdminGateID) {
        this.primaryAdminGateID = primaryAdminGateID;
    }

    public String getClusterID() {
        return clusterID;
    }

    public void setClusterID(String clusterID) {
        this.clusterID = clusterID;
    }

    public String getParentContainer() {
        return parentContainer;
    }

    public void setParentContainer(String parentContainer) {
        this.parentContainer = parentContainer;
    }

    public List<String> getChildContainersID() {
        return childContainersID;
    }

    public void setChildContainersID(List<String> childContainersID) {
        this.childContainersID = childContainersID;
    }

    public List<String> getNodesID() {
        return nodesID;
    }

    public void setNodesID(List<String> nodesID) {
        this.nodesID = nodesID;
    }

    public List<String> getGatesID() {
        return gatesID;
    }

    public void setGatesID(List<String> gatesID) {
        this.gatesID = gatesID;
    }

    @Override
    public void setContainerName(String name) throws MappingDSException {

    }

    @Override
    public void setContainerCompany(String company) throws MappingDSException {

    }

    @Override
    public void setContainerProduct(String product) throws MappingDSException {

    }

    @Override
    public void setContainerType(String type) throws MappingDSException {

    }

    @Override
    public String getContainerPrimaryAdminGateURL() {
        return super.getContainerPrimaryAdminGateURL();
    }

    @Override
    public Gate getContainerPrimaryAdminGate() {
        return super.getContainerPrimaryAdminGate();
    }

    @Override
    public void setContainerPrimaryAdminGate(Gate gate) throws MappingDSException {

    }

    @Override
    public Cluster getContainerCluster() {
        return super.getContainerCluster();
    }

    @Override
    public void setContainerCluster(Cluster cluster) throws MappingDSException {

    }

    @Override
    public void addContainerProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeContainerProperty(String propertyKey) throws MappingDSException {

    }

    @Override
    public Container getContainerParentContainer() {
        return super.getContainerParentContainer();
    }

    @Override
    public void setContainerParentContainer(Container container) throws MappingDSException {

    }
    @Override
    public Set<Container> getContainerChildContainers() {
        return super.getContainerChildContainers();
    }

    @Override
    public boolean addContainerChildContainer(Container container) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerChildContainer(Container container) throws MappingDSException {
        return false;
    }

    @Override
    public Set<Node> getContainerNodes(long depth) {
        return super.getContainerNodes(depth);
    }

    @Override
    public boolean addContainerNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerNode(Node node) throws MappingDSException {
        return false;
    }

    @Override
    public Set<Gate> getContainerGates() {
        return super.getContainerGates();
    }

    @Override
    public boolean addContainerGate(Gate gate) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeContainerGate(Gate gate) throws MappingDSException {
        return false;
    }
}