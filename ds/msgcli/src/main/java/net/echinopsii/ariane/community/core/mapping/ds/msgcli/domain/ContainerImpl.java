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
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainerAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesException;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.ClusterSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.ContainerSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.GateSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.NodeSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

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
                            ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(body);
                            if (container.getContainerID() == null || container.getContainerID().equals(jsonDeserializedContainer.getContainerID()))
                                container.synchronizeFromJSON(jsonDeserializedContainer);
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

    public void synchronizeFromJSON(ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer) throws MappingDSException {
        super.setContainerID(jsonDeserializedContainer.getContainerID());
        super.setContainerName(jsonDeserializedContainer.getContainerName());
        super.setContainerCompany(jsonDeserializedContainer.getContainerCompany());
        super.setContainerProduct(jsonDeserializedContainer.getContainerProduct());
        super.setContainerType(jsonDeserializedContainer.getContainerType());
        super.getContainerProperties().clear();
        if (jsonDeserializedContainer.getContainerProperties()!=null)
            for (PropertiesJSON.TypedPropertyField typedPropertyField : jsonDeserializedContainer.getContainerProperties())
                try {
                    super.addContainerProperty(typedPropertyField.getPropertyName(), PropertiesJSON.getValueFromTypedPropertyField(typedPropertyField));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new MappingDSException("Error with property " + typedPropertyField.getPropertyName() + " deserialization : " + e.getMessage());
                }
        this.setPrimaryAdminGateID(jsonDeserializedContainer.getContainerPrimaryAdminGateID());
        this.setClusterID(jsonDeserializedContainer.getContainerClusterID());
        this.setParentContainerID(jsonDeserializedContainer.getContainerParentContainerID());
        this.setChildContainersID(jsonDeserializedContainer.getContainerChildContainersID());
        this.setNodesID(jsonDeserializedContainer.getContainerNodesID());
        this.setGatesID(jsonDeserializedContainer.getContainerGatesID());
    }

    @Override
    public void setContainerName(String name) throws MappingDSException {
        if (super.getContainerID() != null) {
            if ((super.getContainerName()!=null && !super.getContainerName().equals(name)) ||
                (super.getContainerName() == null && name != null)) {
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
            }
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void setContainerCompany(String company) throws MappingDSException {
        if (super.getContainerID() != null) {
            if ((super.getContainerCompany()!=null && !super.getContainerCompany().equals(company)) ||
                (super.getContainerCompany() == null && company!=null)) {
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
            }
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void setContainerProduct(String product) throws MappingDSException {
        if (super.getContainerID() != null) {
            if ((super.getContainerProduct()!=null && !super.getContainerProduct().equals(product)) ||
                (super.getContainerProduct() == null && product!=null)) {
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
            }
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public void setContainerType(String type) throws MappingDSException {
        if (super.getContainerID() != null) {
            if ((super.getContainerType()!=null && !super.getContainerType().equals(type)) ||
                (super.getContainerType()==null && type!=null)) {
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
            }// else if (super.getContainerCompany() == null) super.setContainerType(type);
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
            if (gate != null && gate.getNodeID() != null) {
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
        try {
            Container update = ContainerSceImpl.internalGetContainer(super.getContainerID());
            this.setClusterID(((ContainerImpl)update).getClusterID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (clusterID!=null && (super.getContainerCluster()==null || !super.getContainerCluster().getClusterID().equals(clusterID))) {
            try {
                super.setContainerCluster(ClusterSceImpl.internalGetCluster(clusterID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        } else if (super.getContainerCluster()!=null && clusterID==null)
            try {
                super.setContainerCluster(null);
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getContainerCluster();
    }

    @Override
    public void setContainerCluster(Cluster cluster) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (cluster == null || cluster.getClusterID() != null) {
                if ((super.getContainerCluster()!=null && !super.getContainerCluster().equals(cluster)) ||
                    (clusterID!=null && cluster !=null && !clusterID.equals(cluster.getClusterID())) ||
                    (clusterID == null && cluster != null) || (clusterID!=null && cluster == null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_CLUSTER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(Cluster.TOKEN_CL_ID, (cluster != null) ? cluster.getClusterID() : MappingSce.GLOBAL_PARAM_OBJ_NONE);
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Cluster previousCluster = super.getContainerCluster();
                        if (previousCluster!=null) {
                            try {
                                if (retMsg.containsKey(Container.JOIN_PREVIOUS_CLUSTER)) {
                                    ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster = ClusterJSON.JSON2Cluster(
                                            (String) retMsg.get(Container.JOIN_PREVIOUS_CLUSTER)
                                    );
                                    ((ClusterImpl) previousCluster).synchronizeFromJSON(jsonDeserializedCluster);
                                }
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        super.setContainerCluster(cluster);
                        clusterID = (cluster!=null) ? cluster.getClusterID() : null;
                        if (cluster!=null) {
                            try {
                                if (retMsg.containsKey(Container.JOIN_CURRENT_CLUSTER)) {
                                    ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster = ClusterJSON.JSON2Cluster(
                                            (String) retMsg.get(Container.JOIN_CURRENT_CLUSTER)
                                    );
                                    ((ClusterImpl) cluster).synchronizeFromJSON(jsonDeserializedCluster);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
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
            try {
                message.put(MappingSce.GLOBAL_PARAM_PROP_FIELD, PropertiesJSON.propertyFieldToTypedPropertyField(propertyKey, value).toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new MappingDSException(e.getMessage());
            }
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
        try {
            Container update = ContainerSceImpl.internalGetContainer(super.getContainerID());
            this.setParentContainerID(((ContainerImpl) update).getParentContainerID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (parentContainerID!=null && (super.getContainerParentContainer()==null || !super.getContainerParentContainer().getContainerID().equals(parentContainerID))) {
            try {
                super.setContainerParentContainer(ContainerSceImpl.internalGetContainer(parentContainerID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        } else if (super.getContainerParentContainer()!=null && parentContainerID==null)
            try {
                super.setContainerParentContainer(null);
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getContainerParentContainer();
    }

    @Override
    public void setContainerParentContainer(Container container) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (container == null || container.getContainerID() != null) {
                if ((super.getContainerParentContainer()!=null && !super.getContainerParentContainer().equals(container)) ||
                    (parentContainerID != null && container != null && !parentContainerID.equals(container.getContainerID())) ||
                    (parentContainerID == null && container != null) || (parentContainerID!=null && container == null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CONTAINER_PARENT_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getContainerID());
                    message.put(SProxContainerSce.PARAM_CONTAINER_PCO_ID, (container != null) ? container.getContainerID() : MappingSce.GLOBAL_PARAM_OBJ_NONE);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, containerReplyWorker);
                    if (clientThreadSessionID != null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Container previousParentContainer = super.getContainerParentContainer();
                        if (previousParentContainer!=null) {
                            try {
                                if (retMsg.containsKey(Container.JOIN_PREVIOUS_PCONTAINER)) {
                                    ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                            (String) retMsg.get(Container.JOIN_PREVIOUS_PCONTAINER)
                                    );
                                    ((ContainerImpl) previousParentContainer).synchronizeFromJSON(jsonDeserializedContainer);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        super.setContainerParentContainer(container);
                        parentContainerID = (container!=null) ? container.getContainerID() : null;
                        if (container!=null) {
                            try {
                                if (retMsg.containsKey(Container.JOIN_CURRENT_PCONTAINER)) {
                                    ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                            (String) retMsg.get(Container.JOIN_CURRENT_PCONTAINER)
                                    );
                                    ((ContainerImpl) container).synchronizeFromJSON(jsonDeserializedContainer);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public Set<Container> getContainerChildContainers() {
        try {
            Container update = ContainerSceImpl.internalGetContainer(super.getContainerID());
            this.setChildContainersID(((ContainerImpl) update).getChildContainersID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Container cont : new ArrayList<>(super.getContainerChildContainers()))
            if (!childContainersID.contains(cont.getContainerID()))
                super.getContainerChildContainers().remove(cont);

        for (String contID : childContainersID)
            try {
                boolean toAdd = true;
                for (Container cont : super.getContainerChildContainers())
                    if (cont.getContainerID().equals(contID)) toAdd = false;
                if (toAdd) super.getContainerChildContainers().add(ContainerSceImpl.internalGetContainer(contID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        return super.getContainerChildContainers();
    }

    @Override
    public boolean addContainerChildContainer(Container container) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (container != null && container.getContainerID() != null) {
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
                        childContainersID.add(container.getContainerID());
                        try {
                            ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                    (String)retMsg.get(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY)
                            );
                            ((ContainerImpl)container).synchronizeFromJSON(jsonDeserializedContainer);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");

    }

    @Override
    public boolean removeContainerChildContainer(Container container) throws MappingDSException {
        if (super.getContainerID() != null) {
            if (container!= null && container.getContainerID() != null) {
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
                        childContainersID.remove(container.getContainerID());
                        try {
                            ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                    (String)retMsg.get(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY)
                            );
                            ((ContainerImpl)container).synchronizeFromJSON(jsonDeserializedContainer);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public Set<Node> getContainerNodes(long depth) {
        try {
            Container update = ContainerSceImpl.internalGetContainer(super.getContainerID());
            this.setNodesID(((ContainerImpl)update).getNodesID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Node node : new ArrayList<>(super.getContainerNodes(0)))
            if (!nodesID.contains(node.getNodeID()))
                super.getContainerNodes(0).remove(node);

        for (String nodeID : nodesID)
            try {
                boolean toAdd = true;
                for (Node node : super.getContainerNodes(0))
                    if (node.getNodeID().equals(nodeID)) toAdd = false;
                if (toAdd) super.getContainerNodes(0).add(NodeSceImpl.internalGetNode(nodeID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
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
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
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
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided node is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }

    @Override
    public Set<Gate> getContainerGates() {
        try {
            Container update = ContainerSceImpl.internalGetContainer(super.getContainerID());
            this.setGatesID(((ContainerImpl) update).getGatesID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        for (Gate gate : new ArrayList<>(super.getContainerGates()))
            if (!gatesID.contains(gate.getNodeID()))
                super.getContainerGates().remove(gate);

        for (String gateID : gatesID)
            try {
                boolean toAdd = true;
                for (Gate gate : super.getContainerGates())
                    if (gate.getNodeID().equals(gateID)) toAdd = false;
                if (toAdd) super.getContainerGates().add(GateSceImpl.internalGetGate(gateID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }

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
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided gate is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
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
                    return true;
                } else return false;
            } else throw new MappingDSException("Provided gate is not initialized !");
        } else throw new MappingDSException("This container is not initialized !");
    }
}