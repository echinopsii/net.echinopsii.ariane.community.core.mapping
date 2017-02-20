/**
 * Mapping Messaging Server
 * Session service messaging endpoint
 * Copyright (C) 27/05/16 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.msgsrv.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxCluster;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxClusterSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;

public class ClusterEp {

    private static final Logger log = MomLoggerFactory.getLogger(ClusterEp.class);

    static class ClusterWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String cid;
            String ccid;
            String name;
            String payload;
            Session session = null;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(true);
            ((MomLogger)log).traceMessage("ClusterWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

            if (oOperation==null)
                operation = MomMsgTranslator.OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
            if (sid!=null) {
                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                if (session == null) {
                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                    ((MomLogger)log).traceMessage("ClusterWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(false);
                    return message;
                } else if (message.containsKey(MomMsgTranslator.MSG_TRACE)) session.traceSession(true);
            }

            try {
                switch (operation) {
                    case ClusterSce.OP_CREATE_CLUSTER:
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload != null) {
                            DeserializedPushResponse deserializationResponse = SProxClusterSceAbs.pushDeserializedCluster(
                                    ClusterJSON.JSON2Cluster(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON((Cluster) deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (name != null){
                            Cluster cluster;
                            if (session != null)
                                cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().createCluster(session, name);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().createCluster(name);

                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            ClusterJSON.oneCluster2JSON(cluster, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no name provided");
                        }
                        break;
                    case ClusterSce.OP_DELETE_CLUSTER:
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        if (name != null) {
                            if (session != null)
                                MappingMsgsrvBootstrap.getMappingSce().getClusterSce().deleteCluster(session, name);
                            else MappingMsgsrvBootstrap.getMappingSce().getClusterSce().deleteCluster(name);

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no name provided");
                        }
                        break;
                    case ClusterSce.OP_GET_CLUSTER:
                    case ClusterSce.OP_GET_CLUSTER_BY_NAME:
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);

                        if (cid != null || name != null) {
                            Cluster cluster;
                            if (cid != null) {
                                if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(session, cid);
                                else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(cid);
                            } else {
                                if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusterByName(session, name);
                                else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusterByName(name);
                            }

                            if (cluster!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON(cluster, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                if (operation.equals(ClusterSce.OP_GET_CLUSTER)) message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                                else message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided name not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            if (operation.equals(ClusterSce.OP_GET_CLUSTER)) message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no cluster ID provided");
                            else message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no cluster name provided");
                        }
                        break;
                    case ClusterSce.OP_GET_CLUSTERS:
                        HashSet<Cluster> clusters;
                        if (session != null) clusters = (HashSet<Cluster>)MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusters(session, null);
                        else clusters = (HashSet<Cluster>)MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusters(null);

                        if (clusters!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            ClusterJSON.manyClusters2JSON(clusters, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Clusters not found.");
                        }
                        break;
                    case Cluster.OP_SET_CLUSTER_NAME:
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (name != null && cid != null) {
                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(session, cid);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(cid);

                            if (cluster != null) {
                                if (session!=null) ((SProxCluster)cluster).setClusterName(session, name);
                                else cluster.setClusterName(name);

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON(cluster, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : cluster with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : missing parameter (cluster id and/or container name)");
                        }
                        break;
                    case Cluster.OP_ADD_CLUSTER_CONTAINER:
                    case Cluster.OP_REMOVE_CLUSTER_CONTAINER:
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        ccid = (String) message.get(Container.TOKEN_CT_ID);
                        if (ccid != null && cid != null) {
                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(session, cid);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(cid);

                            if (cluster != null) {
                                Container container;
                                if (session != null) container = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, ccid);
                                else container = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(ccid);

                                if (container != null) {
                                    if (operation.equals(Cluster.OP_ADD_CLUSTER_CONTAINER)) {
                                        if (session != null) ((SProxCluster) cluster).addClusterContainer(session, container);
                                        else cluster.addClusterContainer(container);
                                    } else {
                                        if (session != null) ((SProxCluster) cluster).removeClusterContainer(session, container);
                                        else cluster.removeClusterContainer(container);
                                    }

                                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                    ContainerJSON.oneContainer2JSONWithTypedProps(container, outStream);
                                    String resultCCo = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                    message.put(MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY, resultCCo);

                                    outStream = new ByteArrayOutputStream();
                                    ClusterJSON.oneCluster2JSON(cluster, outStream);
                                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                    message.put(MomMsgTranslator.MSG_BODY, result);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container with provided id not found");
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : cluster with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : missing parameter (cluster id and/or container id)");
                        }
                        break;
                    case MomMsgTranslator.OPERATION_NOT_DEFINED:
                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                        break;
                    default:
                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                        break;
                }
            } catch (Exception e) {
                if (e.getMessage() == null || (!e.getMessage().equals(MappingDSException.MAPPING_OVERLOAD) && !e.getMessage().equals(MappingDSException.MAPPING_TIMEOUT)))
                    e.printStackTrace();
                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
            }

            ((MomLogger)log).traceMessage("ClusterWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                if (session!=null) session.traceSession(false);
                ((MomLogger)log).setMsgTraceLevel(false);
            }
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected()) {
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    ClusterSce.Q_MAPPING_CLUSTER_SERVICE, new ClusterWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + ClusterSce.Q_MAPPING_CLUSTER_SERVICE + "...");
        }
    }
}
