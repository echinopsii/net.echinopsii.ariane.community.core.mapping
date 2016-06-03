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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.ClusterImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxClusterSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ClusterSceImpl extends SProxClusterSceAbs<ClusterImpl> {

    private static final Logger log = LoggerFactory.getLogger(ClusterSceImpl.class);

    @Override
    public Cluster createCluster(String clusterName) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxClusterSce.OP_CREATE_CLUSTER);
        message.put(ClusterSce.PARAM_CLUSTER_NAME, clusterName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, cluster.getClusterReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return cluster;
    }

    @Override
    public void deleteCluster(String clusterName) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxClusterSce.OP_DELETE_CLUSTER);
        message.put(ClusterSce.PARAM_CLUSTER_NAME, clusterName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, cluster.getClusterReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    @Override
    public Cluster getCluster(String clusterID) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxClusterSce.OP_GET_CLUSTER);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, clusterID);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, cluster.getClusterReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MappingSce.MAPPING_SCE_RET_NOT_FOUND) cluster = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return cluster;
    }

    @Override
    public Cluster getClusterByName(String clusterName) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxClusterSce.OP_GET_CLUSTER_BY_NAME);
        message.put(ClusterSce.PARAM_CLUSTER_NAME, clusterName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, cluster.getClusterReplyWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) {
            if (rc == MappingSce.MAPPING_SCE_RET_NOT_FOUND) cluster = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return cluster;
    }

    class getClustersWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<ClusterImpl> clusters = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    clusters = new HashSet<>();
                    for (ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster : ClusterJSON.JSON2Clusters(body)) {
                        ClusterImpl cluster = new ClusterImpl();
                        cluster.setClusterID(jsonDeserializedCluster.getClusterID());
                        cluster.setClusterName(jsonDeserializedCluster.getClusterName());
                        cluster.setClusterContainersID(jsonDeserializedCluster.getClusterContainersID());
                        clusters.add(cluster);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else ClusterSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", clusters);
            return message;
        }
    }

    @Override
    public Set<Cluster> getClusters(String selector) throws MappingDSException {
        Set<Cluster> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxClusterSce.OP_GET_CLUSTERS);
        //message.put(MappingSce.GLOBAL_PARAM_SELECTOR, selector);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.Q_MAPPING_CLUSTER_SERVICE, new getClustersWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Cluster>) retMsg.get("RET"));

        return ret;
    }
}
