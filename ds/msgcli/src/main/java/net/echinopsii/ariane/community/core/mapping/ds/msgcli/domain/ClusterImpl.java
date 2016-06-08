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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxClusterAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterImpl extends SProxClusterAbs {

    class ClusterReplyWorker implements AppMsgWorker {
        private ClusterImpl cluster = null;

        public ClusterReplyWorker(ClusterImpl cluster) {
            this.cluster = cluster;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (cluster!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY)!=null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY)!=null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[])message.get(MomMsgTranslator.MSG_BODY));
                    if (body!=null) {
                        try {
                            ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster = ClusterJSON.JSON2Cluster(body);
                            if (cluster.getClusterID() == null || cluster.getClusterID().equals(jsonDeserializedCluster.getClusterID()))
                                cluster.synchronizeFromJSON(jsonDeserializedCluster);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else ClusterImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);
    private ClusterReplyWorker clusterReplyWorker = new ClusterReplyWorker(this);
    private List<String> clusterContainersID;

    public ClusterReplyWorker getClusterReplyWorker() {
        return clusterReplyWorker;
    }

    public List<String> getClusterContainersID() {
        return clusterContainersID;
    }

    public void setClusterContainersID(List<String> clusterContainersID) {
        this.clusterContainersID = clusterContainersID;
    }

    public void synchronizeFromJSON(ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster) throws MappingDSException {
        super.setClusterID(jsonDeserializedCluster.getClusterID());
        super.setClusterName(jsonDeserializedCluster.getClusterName());
        this.setClusterContainersID(jsonDeserializedCluster.getClusterContainersID());
    }

    @Override
    public void setClusterName(String name) throws MappingDSException {
        if (super.getClusterID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

            if (super.getClusterName()!=null && !super.getClusterName().equals(name)) {
                Map<String, Object> message = new HashMap<>();
                message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_CLUSTER_NAME);
                message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getClusterID());
                message.put(SProxClusterSce.PARAM_CLUSTER_NAME, name);
                if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, clusterReplyWorker);
                if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) super.setClusterName(name);
                else throw new MappingDSException("Ariane server raised an error... Check your logs !");
            }// else if (super.getClusterName() == null) super.setClusterName(name);
        } else throw new MappingDSException("This cluster is not initialized !");
    }

    @Override
    public boolean addClusterContainer(Container container) throws MappingDSException {
        if (super.getClusterID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
            if (container.getContainerID()!=null) {
                if (!this.clusterContainersID.contains(container.getContainerID())) {
                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_ADD_CLUSTER_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getClusterID());
                    message.put(Container.TOKEN_CT_ID, container.getContainerID());
                    if (clientThreadSessionID != null)
                        message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, clusterReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        this.clusterContainersID.add(container.getContainerID());
                        super.addClusterContainer(container);
                        try {
                            ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                    (String)retMsg.get(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY)
                            );
                            ((ContainerImpl)container).synchronizeFromJSON(jsonDeserializedContainer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This cluster is not initialized !");
        return true;
    }

    @Override
    public boolean removeClusterContainer(Container container) throws MappingDSException {
        if (super.getClusterID()!=null) {
            String clientThreadName = Thread.currentThread().getName();
            String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
            if (container.getContainerID()!=null) {
                if (this.clusterContainersID.contains(container.getContainerID())) {
                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_REMOVE_CLUSTER_CONTAINER);
                    message.put(SProxMappingSce.GLOBAL_PARAM_OBJ_ID, super.getClusterID());
                    message.put(Container.TOKEN_CT_ID, container.getContainerID());
                    if (clientThreadSessionID != null)
                        message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, clusterReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        this.clusterContainersID.remove(container.getContainerID());
                        super.removeClusterContainer(container);
                        try {
                            ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer = ContainerJSON.JSON2Container(
                                    (String)retMsg.get(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY)
                            );
                            ((ContainerImpl)container).synchronizeFromJSON(jsonDeserializedContainer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided container is not initialized !");
        } else throw new MappingDSException("This cluster is not initialized !");
        return true;
    }

    @Override
    public Set<Container> getClusterContainers() {
        //TODO
        return super.getClusterContainers();
    }

}
