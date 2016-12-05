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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;

public class ContainerEp {

    private static final Logger log = MomLoggerFactory.getLogger(ContainerEp.class);

    static class ContainerWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String cid;
            String name;
            String payload;
            String pag_name;
            String pag_url;
            String pag_id;
            String company;
            String product;
            String type;
            String pc_id;
            String cl_id;
            String cc_id;
            String ga_id;
            String nd_id;
            String prop_name;
            String prop_field;
            Session session = null;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(true);
            ((MomLogger)log).traceMessage("ContainerWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

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
                    ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(false);
                    return message;
                } else if (message.containsKey(MomMsgTranslator.MSG_TRACE)) session.traceSession(true);
            }

            try {
                switch (operation) {
                    case ContainerSce.OP_CREATE_CONTAINER:
                        name = (String) message.get(ContainerSce.PARAM_CONTAINER_NAME);
                        pc_id = (String) message.get(ContainerSce.PARAM_CONTAINER_PCO_ID);
                        pag_url = (String) message.get(ContainerSce.PARAM_CONTAINER_PAG_URL);
                        pag_name = (String) message.get(ContainerSce.PARAM_CONTAINER_PAG_NAME);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload!=null) {
                            DeserializedPushResponse deserializationResponse = SProxContainerSceAbs.pushDeserializedContainer(
                                    ContainerJSON.JSON2Container(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ContainerJSON.oneContainer2JSON((Container) deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (pag_url!=null && pag_name!=null) {
                            Container cont;
                            if (name!=null) {
                                if (pc_id!=null) {
                                    Container pcont;
                                    if (session != null) pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, pc_id);
                                    else pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(pc_id);
                                    if (pcont!=null) {
                                        if (session != null) cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(session, name, pag_url, pag_name, pcont);
                                        else cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(name, pag_url, pag_name, pcont);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parent container with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    if (session != null) cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(session, name, pag_url, pag_name);
                                    else cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(name, pag_url, pag_name);
                                }
                            } else {
                                if (pc_id != null) {
                                    Container pcont;
                                    if (session != null) pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, pc_id);
                                    else pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(pc_id);
                                    if (pcont!=null) {
                                        if (session != null) cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(session, pag_url, pag_name, pcont);
                                        else cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(pag_url, pag_name, pcont);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parent container with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    if (session != null) cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(session, pag_url, pag_name);
                                    else cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().createContainer(pag_url, pag_name);
                                }
                            }

                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            ContainerJSON.oneContainer2JSONWithTypedProps(cont, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : primary admin gate name and/or url not provided.");
                        }
                        break;
                    case ContainerSce.OP_DELETE_CONTAINER:
                        pag_url = (String) message.get(ContainerSce.PARAM_CONTAINER_PAG_URL);
                        if (pag_url!=null) {
                            if (session != null) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().deleteContainer(session, pag_url);
                            else MappingMsgsrvBootstrap.getMappingSce().getContainerSce().deleteContainer(pag_url);

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : primary admin gate url not provided.");
                        }
                        break;
                    case ContainerSce.OP_GET_CONTAINER:
                    case ContainerSce.OP_GET_CONTAINER_BY_PRIMARY_ADMIN_URL:
                        pag_url = (String) message.get(ContainerSce.PARAM_CONTAINER_PAG_URL);
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (cid!=null || pag_url != null) {
                            Container cont;
                            if (cid!=null) {
                                if (session != null) cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, cid);
                                else cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(cid);
                            } else {
                                if (session != null) cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainerByPrimaryAdminURL(session, pag_url);
                                else cont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainerByPrimaryAdminURL(pag_url);
                            }

                            if (cont != null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ContainerJSON.oneContainer2JSONWithTypedProps(cont, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                if (operation.equals(ContainerSce.OP_GET_CONTAINER)) message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : container with provided id not found");
                                else if (operation.equals(ContainerSce.OP_GET_CONTAINER_BY_PRIMARY_ADMIN_URL)) message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : container with provided primary admin gate url not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            if (operation.equals(ContainerSce.OP_GET_CONTAINER)) message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container id are not provided.");
                            else message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : primary admin gate url are not provided.");
                        }
                        break;
                    case ContainerSce.OP_GET_CONTAINERS:
                        HashSet<Container> containers;
                        if (session!=null) containers = (HashSet<Container>) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainers(session, null);
                        else containers = (HashSet<Container>) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainers(null);

                        if (containers != null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            ContainerJSON.manyContainers2JSONWithTypedProps(containers, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Containers not found.");
                        }
                        break;
                    case Container.OP_SET_CONTAINER_NAME:
                    case Container.OP_SET_CONTAINER_COMPANY:
                    case Container.OP_SET_CONTAINER_PRODUCT:
                    case Container.OP_SET_CONTAINER_TYPE:
                    case Container.OP_SET_CONTAINER_PRIMARY_ADMIN_GATE:
                    case Container.OP_SET_CONTAINER_PARENT_CONTAINER:
                    case Container.OP_SET_CONTAINER_CLUSTER:
                    case Container.OP_ADD_CONTAINER_CHILD_CONTAINER:
                    case Container.OP_REMOVE_CONTAINER_CHILD_CONTAINER:
                    case Container.OP_ADD_CONTAINER_GATE:
                    case Container.OP_REMOVE_CONTAINER_GATE:
                    case Container.OP_ADD_CONTAINER_NODE:
                    case Container.OP_REMOVE_CONTAINER_NODE:
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        name = (String) message.get(ContainerSce.PARAM_CONTAINER_NAME);
                        company = (String) message.get(ContainerSce.PARAM_CONTAINER_COMPANY);
                        product = (String) message.get(ContainerSce.PARAM_CONTAINER_PRODUCT);
                        type = (String) message.get(ContainerSce.PARAM_CONTAINER_TYPE);
                        pag_id = (String) message.get(ContainerSce.PARAM_CONTAINER_GAT_ID);
                        pc_id = (String) message.get(ContainerSce.PARAM_CONTAINER_PCO_ID);
                        cl_id = (String) message.get(Cluster.TOKEN_CL_ID);
                        cc_id = (String) message.get(ContainerSce.PARAM_CONTAINER_CCO_ID);
                        ga_id = (String) message.get(Node.TOKEN_ND_ID);
                        nd_id = (String) message.get(Node.TOKEN_ND_ID);
                        if (cid!=null &&
                                (name!=null || company!=null || product!=null || type!=null || pag_id!=null ||
                                        pc_id!=null || cl_id!=null || cc_id!=null || ga_id!=null || nd_id!=null)) {
                            SProxContainer cont;
                            if (session != null) cont = (SProxContainer) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, cid);
                            else cont = (SProxContainer) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(cid);

                            if (cont!=null) {
                                if (operation.equals(Container.OP_SET_CONTAINER_NAME) && name!=null)
                                    if (session!=null) cont.setContainerName(session, name);
                                    else cont.setContainerName(name);
                                else if (operation.equals(Container.OP_SET_CONTAINER_COMPANY) && company!=null)
                                    if (session!=null) cont.setContainerCompany(session, company);
                                    else cont.setContainerCompany(company);
                                else if (operation.equals(Container.OP_SET_CONTAINER_PRODUCT) && product!=null)
                                    if (session!=null) cont.setContainerProduct(session, product);
                                    else cont.setContainerProduct(product);
                                else if (operation.equals(Container.OP_SET_CONTAINER_TYPE) && type!=null)
                                    if (session!=null) cont.setContainerType(session, type);
                                    else cont.setContainerType(type);
                                else if (operation.equals(Container.OP_SET_CONTAINER_PRIMARY_ADMIN_GATE) && pag_id!=null) {
                                    Gate gat;
                                    if (session != null)
                                        gat = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(session, pag_id);
                                    else gat = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(pag_id);
                                    if (gat != null) {
                                        if (session != null) cont.setContainerPrimaryAdminGate(session, gat);
                                        else cont.setContainerPrimaryAdminGate(gat);

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        GateJSON.oneGate2JSONWithTypedProps(gat, outStream);
                                        String resultGate = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                        message.put(MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY, resultGate);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : gate with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else if (operation.equals(Container.OP_SET_CONTAINER_PARENT_CONTAINER) && pc_id!=null) {
                                    Container pcont = null;
                                    if (!pc_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                        if (session != null) pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, pc_id);
                                        else pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(pc_id);
                                    }
                                    if (pcont!=null || pc_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                        Container previousParentContaner = cont.getContainerParentContainer();
                                        if (session != null) cont.setContainerParentContainer(session, pcont);
                                        else cont.setContainerParentContainer(pcont);

                                        if (previousParentContaner!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            ContainerJSON.oneContainer2JSONWithTypedProps(previousParentContaner, outStream);
                                            String resultPcont = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Container.JOIN_PREVIOUS_PCONTAINER, resultPcont);
                                        }

                                        if (pcont!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            ContainerJSON.oneContainer2JSONWithTypedProps(pcont, outStream);
                                            String resultPcont = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Container.JOIN_CURRENT_PCONTAINER, resultPcont);
                                        }
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parent container with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else if (operation.equals(Container.OP_SET_CONTAINER_CLUSTER) && cl_id!=null) {
                                    Cluster clu = null;
                                    if (!cl_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                        if (session != null) clu = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(session, cl_id);
                                        else clu = MappingMsgsrvBootstrap.getMappingSce().getClusterSce().getCluster(cl_id);
                                    }
                                    if (clu!=null || cl_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                        Cluster previousCluster = cont.getContainerCluster();
                                        if (session != null) cont.setContainerCluster(session, clu);
                                        else cont.setContainerCluster(clu);

                                        if (previousCluster!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            ClusterJSON.oneCluster2JSON(previousCluster, outStream);
                                            String resultClu = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Container.JOIN_PREVIOUS_CLUSTER, resultClu);
                                        }

                                        if (clu!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            ClusterJSON.oneCluster2JSON(clu, outStream);
                                            String resultClu = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Container.JOIN_CURRENT_CLUSTER, resultClu);
                                        }
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : cluster with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else if ((operation.equals(Container.OP_ADD_CONTAINER_CHILD_CONTAINER) || operation.equals(Container.OP_REMOVE_CONTAINER_CHILD_CONTAINER))
                                        && cc_id!=null) {
                                    Container ccont = null;
                                    if (session != null) ccont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, cc_id);
                                    else ccont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(cc_id);
                                    if (ccont!=null) {
                                        if (operation.equals(Container.OP_ADD_CONTAINER_CHILD_CONTAINER))
                                            if (session != null) cont.addContainerChildContainer(session, ccont);
                                            else cont.addContainerChildContainer(ccont);
                                        else
                                            if (session!=null) cont.removeContainerChildContainer(session, ccont);
                                            else cont.removeContainerChildContainer(ccont);

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        ContainerJSON.oneContainer2JSONWithTypedProps(ccont, outStream);
                                        String resultCCo = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                        message.put(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, resultCCo);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : child container with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else if ((operation.equals(Container.OP_ADD_CONTAINER_GATE) || operation.equals(Container.OP_REMOVE_CONTAINER_GATE))
                                    && ga_id!=null) {
                                    Gate gate;
                                    if (session!=null) gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(session, ga_id);
                                    else gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(ga_id);
                                    if (gate!=null) {
                                        if (operation.equals(Container.OP_ADD_CONTAINER_GATE))
                                            if (session != null) cont.addContainerGate(session, gate);
                                            else cont.addContainerGate(gate);
                                        else
                                            if (session!=null) cont.removeContainerGate(session, gate);
                                            else cont.removeContainerGate(gate);

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        GateJSON.oneGate2JSONWithTypedProps(gate, outStream);
                                        String resultGate = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                        message.put(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, resultGate);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : gate with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else if ((operation.equals(Container.OP_ADD_CONTAINER_NODE) || operation.equals(Container.OP_REMOVE_CONTAINER_NODE))
                                    && nd_id!=null) {
                                    Node node;
                                    if (session!=null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, nd_id);
                                    else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(nd_id);
                                    if (node!=null) {
                                        if (operation.equals(Container.OP_ADD_CONTAINER_NODE))
                                            if (session!=null) cont.addContainerNode(session, node);
                                            else cont.addContainerNode(node);
                                        else
                                            if (session!=null) cont.removeContainerNode(session, node);
                                            else cont.removeContainerNode(node);

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                        String resultNode = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                        message.put(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, resultNode);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node with provided id not found");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parameter inconsistent with operation");
                                    ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                        if (session!=null) session.traceSession(false);
                                        ((MomLogger) log).setMsgTraceLevel(false);
                                    }
                                    return message;
                                }

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ContainerJSON.oneContainer2JSONWithTypedProps(cont, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + operation + ") : container with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            switch (operation) {
                                case Container.OP_SET_CONTAINER_NAME:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or name not provided.");
                                    break;
                                case Container.OP_SET_CONTAINER_COMPANY:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or company not provided.");
                                    break;
                                case Container.OP_SET_CONTAINER_PRODUCT:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or product not provided.");
                                    break;
                                case Container.OP_SET_CONTAINER_TYPE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or type not provided.");
                                    break;
                                case Container.OP_SET_CONTAINER_PRIMARY_ADMIN_GATE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or primary admin gate id not provided.");
                                    break;
                                case Container.OP_SET_CONTAINER_PARENT_CONTAINER:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or parent container id not provided.");
                                    break;
                                case Container.OP_SET_CONTAINER_CLUSTER:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or cluster id not provided.");
                                    break;
                                case Container.OP_ADD_CONTAINER_CHILD_CONTAINER:
                                case Container.OP_REMOVE_CONTAINER_CHILD_CONTAINER:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or child container id not provided.");
                                    break;
                                case Container.OP_ADD_CONTAINER_GATE:
                                case Container.OP_REMOVE_CONTAINER_GATE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or gate id not provided.");
                                    break;
                                case Container.OP_ADD_CONTAINER_NODE:
                                case Container.OP_REMOVE_CONTAINER_NODE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or node id not provided.");
                                    break;
                            }
                        }
                        break;
                    case Container.OP_ADD_CONTAINER_PROPERTY:
                    case Container.OP_REMOVE_CONTAINER_PROPERTY:
                        cid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        prop_field = (message.containsKey(MappingSce.GLOBAL_PARAM_PROP_FIELD)) ? message.get(MappingSce.GLOBAL_PARAM_PROP_FIELD).toString() : null;
                        if (cid!=null) {
                            SProxContainer cont;
                            if (session != null) cont = (SProxContainer) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, cid);
                            else cont = (SProxContainer) MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(cid);

                            if (cont!=null) {
                                if (operation.equals(Container.OP_ADD_CONTAINER_PROPERTY)) {
                                    if (prop_field!=null) {
                                        PropertiesJSON.TypedPropertyField typedPropertyField = PropertiesJSON.typedPropertyFieldFromJSON(prop_field);
                                        Object value = ToolBox.extractPropertyObjectValueFromString(typedPropertyField.getPropertyValue(), typedPropertyField.getPropertyType());
                                        if (session != null) cont.addContainerProperty(session, typedPropertyField.getPropertyName(), value);
                                        else cont.addContainerProperty(typedPropertyField.getPropertyName(), value);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property field not provided.");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    prop_name = (String) message.get(MappingSce.GLOBAL_PARAM_PROP_NAME);
                                    if (prop_name!=null) {
                                        if (session != null) cont.removeContainerProperty(session, prop_name);
                                        else cont.removeContainerProperty(prop_name);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property name not provided.");
                                        ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger) log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                }

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ContainerJSON.oneContainer2JSONWithTypedProps(cont, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id not provided.");
                        }
                        break;
                    case MomMsgTranslator.OPERATION_NOT_DEFINED:
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                        break;
                    default:
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
            }
            ((MomLogger)log).traceMessage("ContainerWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                if (session!=null) session.traceSession(false);
                ((MomLogger) log).setMsgTraceLevel(false);
            }
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected()) {
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    ContainerSce.Q_MAPPING_CONTAINER_SERVICE, new ContainerWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + ContainerSce.Q_MAPPING_CONTAINER_SERVICE + "...");
        }
    }
}