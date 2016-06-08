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
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;

public class ClusterEp {

    static class ClusterWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.GLOBAL_OPERATION_FDN);
            String operation;
            String sid;
            String cid;
            String ccid;
            String name;
            Session session = null;

            if (oOperation==null)
                operation = MappingSce.GLOBAL_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            try {
                switch (operation) {
                    case ClusterSce.OP_CREATE_CLUSTER:
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        if (name != null) {
                            if (sid!=null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().createCluster(session, name);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().createCluster(name);

                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            ClusterJSON.oneCluster2JSON(cluster, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no name provided");
                        }
                        break;
                    case ClusterSce.OP_DELETE_CLUSTER:
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        if (name != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            if (session != null)
                                MappingMsgsrvBootstrap.getMappingSce().getClusterSce().deleteCluster(session, name);
                            else MappingMsgsrvBootstrap.getMappingSce().getClusterSce().deleteCluster(name);

                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no name provided");
                        }
                        break;
                    case ClusterSce.OP_GET_CLUSTER:
                    case ClusterSce.OP_GET_CLUSTER_BY_NAME:
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);

                        if (cid != null || name != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

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

                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                                if (operation.equals(ClusterSce.OP_GET_CLUSTER)) message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                                else message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided name not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            if (operation.equals(ClusterSce.OP_GET_CLUSTER)) message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no cluster ID provided");
                            else message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no cluster name provided");
                        }
                        break;
                    case ClusterSce.OP_GET_CLUSTERS:
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        if (sid != null) {
                            session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                            if (session == null) {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                return message;
                            }
                        }

                        HashSet<Cluster> clusters;
                        if (session != null) clusters = (HashSet<Cluster>)MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusters(session, null);
                        else clusters = (HashSet<Cluster>)MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusters(null);

                        if (clusters!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            ClusterJSON.manyClusters2JSON(clusters, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        }
                        break;
                    case Cluster.OP_SET_CLUSTER_NAME:
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.PARAM_CLUSTER_NAME);
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (name != null && cid != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(session, cid);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(cid);

                            if (cluster != null) {
                                if (session!=null) ((SProxCluster)cluster).setClusterName(session, name);
                                else cluster.setClusterName(name);

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON(cluster, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : missing parameter (cluster id and/or container name)");
                        }
                        break;
                    case Cluster.OP_ADD_CLUSTER_CONTAINER:
                    case Cluster.OP_REMOVE_CLUSTER_CONTAINER:
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        ccid = (String) message.get(Container.TOKEN_CT_ID);
                        if (ccid != null && cid != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

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

                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                    message.put(MomMsgTranslator.MSG_BODY, result);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                                    message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : container with provided id not found");
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : missing parameter (cluster id and/or container id)");
                        }
                        break;
                    case MappingSce.GLOBAL_OPERATION_NOT_DEFINED:
                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                        message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                        break;
                    default:
                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                        message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SERVER_ERR);
                message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
            }

            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected())
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().requestService(
                    ClusterSce.Q_MAPPING_CLUSTER_SERVICE, new ClusterWorker()
            );
    }
}
