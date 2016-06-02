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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.neo4j.shell.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ClusterSceImpl extends SProxClusterSceAbs<ClusterImpl> {

    private static final Logger log = LoggerFactory.getLogger(ClusterSceImpl.class);

    private MappingSceImpl sce = null;

    public ClusterSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public Cluster createCluster(String clusterName) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        Session session = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) session = sce.getSessionRegistry().get(clientThreadSessionID);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SProxClusterSce.CLUSTER_SCE_OP_CREATE);
        message.put(SProxClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME, clusterName);
        if (session!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, session.getSessionID());
        MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.MAPPING_CLUSTER_SERVICE_Q, cluster.getClusterReplyWorker());
        if (cluster.getClusterID() == null) cluster = null;

        return cluster;
    }

    @Override
    public void deleteCluster(String clusterName) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        Session session = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) session = sce.getSessionRegistry().get(clientThreadSessionID);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SProxClusterSce.CLUSTER_SCE_OP_DELETE);
        message.put(SProxClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME, clusterName);
        if (session!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, session.getSessionID());

        MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.MAPPING_CLUSTER_SERVICE_Q, cluster.getClusterReplyWorker());
    }

    @Override
    public Cluster getCluster(String clusterID) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        Session session = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) session = sce.getSessionRegistry().get(clientThreadSessionID);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SProxClusterSce.CLUSTER_SCE_OP_GET);
        message.put(MappingSce.MAPPING_SCE_PARAM_OBJ_ID, clusterID);
        if (session!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, session.getSessionID());
        MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.MAPPING_CLUSTER_SERVICE_Q, cluster.getClusterReplyWorker());
        if (cluster.getClusterID() == null) cluster = null;

        return cluster;
    }

    @Override
    public Cluster getClusterByName(String clusterName) throws MappingDSException {
        ClusterImpl cluster = new ClusterImpl();

        Session session = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) session = sce.getSessionRegistry().get(clientThreadSessionID);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SProxClusterSce.CLUSTER_SCE_OP_GET_BY_NAME);
        message.put(ClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME, clusterName);
        if (session!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, session.getSessionID());
        MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.MAPPING_CLUSTER_SERVICE_Q, cluster.getClusterReplyWorker());
        if (cluster.getClusterID() == null) cluster = null;

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
        Session session = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) session = sce.getSessionRegistry().get(clientThreadSessionID);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SProxClusterSce.CLUSTER_SCE_OP_GET_BY_NAME);
        //message.put(MappingSce.MAPPING_SCE_PARAM_SELECTOR, selector);
        if (session!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, session.getSessionID());


        ret.addAll(
                (Collection<? extends ClusterImpl>) MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ClusterSce.MAPPING_CLUSTER_SERVICE_Q, new getClustersWorker()).get("RET")
        );

        return ret;
    }
}
