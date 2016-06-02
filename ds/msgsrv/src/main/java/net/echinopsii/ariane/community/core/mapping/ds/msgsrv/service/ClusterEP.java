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

import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxCluster;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
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

public class ClusterEP {

    static class ClusterWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.MAPPING_SCE_OPERATION_FDN);
            String operation;
            String sid;
            String cid;
            String name;
            Session session = null;

            if (oOperation==null)
                operation = MappingSce.MAPPING_SCE_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case ClusterSce.CLUSTER_SCE_OP_CREATE:
                    try {
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME);
                        if (name != null) {
                            if (sid!=null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, 1);
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

                            message.put(MomMsgTranslator.MSG_RC, 0);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no name provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
                    }
                    break;
                case ClusterSce.CLUSTER_SCE_OP_DELETE:
                    try {
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME);
                        if (name != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, 1);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            if (session != null)
                                MappingMsgsrvBootstrap.getMappingSce().getClusterSce().deleteCluster(session, name);
                            else MappingMsgsrvBootstrap.getMappingSce().getClusterSce().deleteCluster(name);

                            message.put(MomMsgTranslator.MSG_RC, 0);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no name provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
                    }
                    break;
                case ClusterSce.CLUSTER_SCE_OP_GET:
                    try {
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        cid = (String) message.get(MappingSce.MAPPING_SCE_PARAM_OBJ_ID);
                        if (cid != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, 1);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(session, cid);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(cid);

                            if (cluster!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON(cluster, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, 0);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no cluster ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
                    }
                    break;
                case ClusterSce.CLUSTER_SCE_OP_GET_BY_NAME:
                    try {
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME);
                        if (name != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, 1);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusterByName(session, name);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusterByName(name);

                            if (cluster != null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON(cluster, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, 0);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : no cluster name provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
                    }
                    break;
                case ClusterSce.CLUSTER_SCE_OP_GETS:
                    try {
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        if (sid != null) {
                            session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                            if (session == null) {
                                message.put(MomMsgTranslator.MSG_RC, 1);
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

                            message.put(MomMsgTranslator.MSG_RC, 0);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
                    }
                    break;
                case SProxCluster.CLUSTER_OP_SET_CLUSTER_NAME:
                    try {
                        sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
                        name = (String) message.get(ClusterSce.CLUSTER_SCE_PARAM_CLUSTER_NAME);
                        cid = (String) message.get(MappingSce.MAPPING_SCE_PARAM_OBJ_ID);
                        if (name != null && cid != null) {
                            if (sid != null) {
                                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                                if (session == null) {
                                    message.put(MomMsgTranslator.MSG_RC, 1);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                                    return message;
                                }
                            }

                            Cluster cluster;
                            if (session != null) cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusterByName(session, name);
                            else cluster = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getClusterByName(name);

                            if (cluster != null) {
                                cluster.setClusterName(name);

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ClusterJSON.oneCluster2JSON(cluster, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, 0);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : cluster with provided id not found");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
                    }
                    break;
                case SProxCluster.CLUSTER_OP_ADD_CLUSTER_CONTAINER:
                    break;
                case SProxCluster.CLUSTER_OP_REMOVE_CLUSTER_CONTAINER:
                    break;
                case MappingSce.MAPPING_SCE_OPERATION_NOT_DEFINED:
                    message.put(MomMsgTranslator.MSG_RC, 1);
                    message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                    break;
                default:
                    message.put(MomMsgTranslator.MSG_RC, 1);
                    message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                    break;
            }

            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected())
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().requestService(
                    ClusterSce.MAPPING_CLUSTER_SERVICE_Q, new ClusterWorker()
            );
    }
}
