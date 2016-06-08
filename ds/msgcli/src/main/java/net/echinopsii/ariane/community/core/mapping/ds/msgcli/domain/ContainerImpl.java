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
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainerAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesException;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
                        try {
                            ContainerJSON.JSONDeserializedContainer jsonDeserializedCluster = ContainerJSON.JSON2Container(body);
                            if (container.getContainerID() == null) {
                                container.setClusterID(jsonDeserializedCluster.getContainerID());
                                container.setContainerName(jsonDeserializedCluster.getContainerName());
                                container.setContainerCompany(jsonDeserializedCluster.getContainerCompany());
                                container.setContainerProduct(jsonDeserializedCluster.getContainerProduct());
                                container.setContainerType(jsonDeserializedCluster.getContainerType());
                                container.setPrimaryAdminGateID(jsonDeserializedCluster.getContainerPrimaryAdminGateID());
                                container.setClusterID(jsonDeserializedCluster.getContainerClusterID());
                                container.setParentContainerID(jsonDeserializedCluster.getContainerParentContainerID());
                                container.setChildContainersID(jsonDeserializedCluster.getContainerChildContainersID());
                                container.setNodesID(jsonDeserializedCluster.getContainerNodesID());
                                container.setGatesID(jsonDeserializedCluster.getContainerGatesID());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    private String parentContainerID;
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

    public String getParentContainerID() {
        return parentContainerID;
    }

    public void setParentContainerID(String parentContainerID) {
        this.parentContainerID = parentContainerID;
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
        if (super.getContainerID() != null) {
            if (super.getContainerName()!=null && !super.getContainerName().equals(name)) {
                String clientThreadName = Thread.currentThread().getName();
                String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                Map<String, Object> message = new HashMap<>();
                message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_NAME);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                message.put(SProxContainerSce.PARAM_CONTAINER_NAME, name);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setContainerName(name);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            } else if (super.getContainerName() == null) super.setContainerName(name);
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void setContainerCompany(String company) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (super.getContainerCompany()!=null && !super.getContainerCompany().equals(company)) {
                String clientThreadName = Thread.currentThread().getName();
                String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                Map<String, Object> message = new HashMap<>();
                message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_COMPANY);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                message.put(SProxContainerSce.PARAM_CONTAINER_COMPANY, company);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setContainerCompany(company);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            } else if (super.getContainerCompany() == null) super.setContainerCompany(company);
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void setContainerProduct(String product) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (super.getContainerProduct()!=null && !super.getContainerProduct().equals(product)) {
                String clientThreadName = Thread.currentThread().getName();
                String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                Map<String, Object> message = new HashMap<>();
                message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_PRODUCT);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                message.put(SProxContainerSce.PARAM_CONTAINER_PRODUCT, product);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setContainerProduct(product);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            } else if (super.getContainerCompany() == null) super.setContainerProduct(product);
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void setContainerType(String type) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (super.getContainerType()!=null && !super.getContainerType().equals(type)) {
                String clientThreadName = Thread.currentThread().getName();
                String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                Map<String, Object> message = new HashMap<>();
                message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_TYPE);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                message.put(SProxContainerSce.PARAM_CONTAINER_TYPE, type);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setContainerType(type);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            } else if (super.getContainerCompany() == null) super.setContainerType(type);
        } else throw new MappingDSException("This container is not initialized !");
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
        if (super.getContainerID() != null) {
            if (gate.getNodeID() != null) {
                if ((super.getContainerPrimaryAdminGate()!=null && !super.getContainerPrimaryAdminGate().equals(gate)) ||
                        (primaryAdminGateID!=null && !primaryAdminGateID.equals(gate.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_PRIMARY_ADMIN_GATE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(SProxContainerSce.PARAM_CONTAINER_PAG_ID, gate.getNodeID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.setContainerPrimaryAdminGate(gate);
                        gate.setNodeContainer(this);
                        primaryAdminGateID = gate.getNodeID();
                    }
                    else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided gate is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public Cluster getContainerCluster() {
        return super.getContainerCluster();
    }

    @Override
    public void setContainerCluster(Cluster cluster) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (cluster.getClusterID() != null) {
                if ((super.getContainerCluster()!=null && !super.getContainerCluster().equals(cluster)) ||
                    (clusterID!=null && !clusterID.equals(cluster.getClusterID()) )) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_CLUSTER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(SProxContainerSce.PARAM_CONTAINER_PAG_ID, cluster.getClusterID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.setContainerCluster(cluster);
                        cluster.addClusterContainer(this);
                        clusterID = cluster.getClusterID();
                    }
                    else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided cluster is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void addContainerProperty(String propertyKey, Object value) throws MappingDSException {
        if (super.getContainerID() != null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_ADD_CONTAINER_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
            message.put(MappingSce.GLOBAL_PARAM_PROP_NAME, propertyKey);
            try {
                message.put(MappingSce.GLOBAL_PARAM_PROP_TYPE, PropertiesJSON.getTypeFromObject(value));
            } catch (PropertiesException e) {
                throw new MappingDSException(e.getMessage());
            }
            message.put(MappingSce.GLOBAL_PARAM_PROP_VALUE, value);
            Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.addContainerProperty(propertyKey, value);
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void removeContainerProperty(String propertyKey) throws MappingDSException {
        if (super.getContainerID() != null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            Map<String, Object> message = new HashMap<>();
            message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_REMOVE_CONTAINER_PROPERTY);
            message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
            message.put(MappingSce.GLOBAL_PARAM_PROP_NAME, propertyKey);
            Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
            if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
            if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.removeContainerProperty(propertyKey);
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public Container getContainerParentContainer() {
        return super.getContainerParentContainer();
    }

    @Override
    public void setContainerParentContainer(Container container) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (container.getContainerID() != null) {
                if ((super.getContainerParentContainer()!=null && !super.getContainerParentContainer().equals(container)) ||
                    (parentContainerID != null && !parentContainerID.equals(container.getContainerID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_PARENT_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(SProxContainerSce.PARAM_CONTAINER_PCO_ID, container.getContainerID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.setContainerParentContainer(container);
                        container.addContainerChildContainer(this);
                        parentContainerID = container.getContainerID();
                    }
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public Set<Container> getContainerChildContainers() {
        return super.getContainerChildContainers();
    }

    @Override
    public boolean addContainerChildContainer(Container container) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (container.getContainerID() != null) {
                if ((super.getContainerChildContainers()!=null && !super.getContainerChildContainers().contains(container)) ||
                    (childContainersID != null && !childContainersID.contains(container.getContainerID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_ADD_CONTAINER_CHILD_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(SProxContainerSce.PARAM_CONTAINER_CCO_ID, container.getContainerID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addContainerChildContainer(container);
                        container.setContainerParentContainer(this);
                        childContainersID.add(container.getContainerID());
                    }
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
        return false;
    }

    @Override
    public boolean removeContainerChildContainer(Container container) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (container.getContainerID() != null) {
                if ((super.getContainerChildContainers()!=null && super.getContainerChildContainers().contains(container)) ||
                    (childContainersID!=null && childContainersID.contains(container.getContainerID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_REMOVE_CONTAINER_CHILD_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(SProxContainerSce.PARAM_CONTAINER_CCO_ID, container.getContainerID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeContainerChildContainer(container);
                        container.setContainerParentContainer(null);
                        childContainersID.remove(container.getContainerID());
                    }
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
        return false;
    }

    @Override
    public Set<Node> getContainerNodes(long depth) {
        return super.getContainerNodes(depth);
    }

    @Override
    public boolean addContainerNode(Node node) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (node.getNodeID() != null) {
                if ((super.getContainerNodes(0)!=null && !super.getContainerNodes(0).contains(node)) ||
                    (nodesID!=null && !nodesID.contains(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_ADD_CONTAINER_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(Node.TOKEN_ND_ID, node.getNodeID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addContainerNode(node);
                        node.setNodeContainer(this);
                        nodesID.add(node.getNodeID());
                    }
                }
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
        return false;
    }

    @Override
    public boolean removeContainerNode(Node node) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (node.getNodeID() != null) {
                if ((super.getContainerNodes(0)!=null && super.getContainerNodes(0).contains(node)) ||
                        (nodesID!=null && nodesID.contains(node.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_REMOVE_CONTAINER_NODE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(Node.TOKEN_ND_ID, node.getNodeID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeContainerNode(node);
                        node.setNodeContainer(null);
                        nodesID.remove(node.getNodeID());
                    }
                }
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
        return false;
    }

    @Override
    public Set<Gate> getContainerGates() {
        return super.getContainerGates();
    }

    @Override
    public boolean addContainerGate(Gate gate) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (gate.getNodeID() != null) {
                if ((super.getContainerGates()!=null && !super.getContainerGates().contains(gate)) ||
                        (gatesID!=null && !gatesID.contains(gate.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_ADD_CONTAINER_GATE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(Node.TOKEN_ND_ID, gate.getNodeID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.addContainerGate(gate);
                        gate.setNodeContainer(this);
                        gatesID.add(gate.getNodeID());
                    }
                }
            } else throw new MappingDSException("Provided gate is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
        return false;
    }

    @Override
    public boolean removeContainerGate(Gate gate) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (gate.getNodeID() != null) {
                if ((super.getContainerGates()!=null && super.getContainerGates().contains(gate)) ||
                        (gatesID!=null && gatesID.contains(gate.getNodeID()))) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_REMOVE_CONTAINER_GATE);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(Node.TOKEN_ND_ID, gate.getNodeID());
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        super.removeContainerGate(gate);
                        gate.setNodeContainer(null);
                        gatesID.remove(gate.getNodeID());
                    }
                }
            } else throw new MappingDSException("Provided gate is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
        return false;
    }
}