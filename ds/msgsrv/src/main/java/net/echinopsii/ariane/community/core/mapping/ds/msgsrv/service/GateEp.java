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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxGate;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.GateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSceAbs;
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

public class GateEp {
    private static final Logger log = MomLoggerFactory.getLogger(GateEp.class);

    static class GateWorker implements AppMsgWorker {

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String gid;
            String url;
            String name;
            String payload;
            String pc_id;
            String ep_id;
            Boolean is_admin;
            Session session=null;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(true);
            ((MomLogger)log).traceMessage("GateWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

            if (oOperation==null)
                operation = MomMsgTranslator.OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
            if (sid != null) {
                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                if (session == null) {
                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                    ((MomLogger)log).traceMessage("GateWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(false);
                    return message;
                } else if (message.containsKey(MomMsgTranslator.MSG_TRACE)) session.traceSession(true);
            }

            try {
                switch (operation) {
                    case GateSce.OP_CREATE_GATE:
                        name = (String) message.get(GateSce.PARAM_GATE_NAME);
                        url = (String) message.get(GateSce.PARAM_GATE_URL);
                        pc_id  = (String) message.get(Container.TOKEN_CT_ID);
                        is_admin = (Boolean) message.get(GateSce.PARAM_GATE_IPADM);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload!=null) {
                            DeserializedPushResponse deserializationResponse = SProxNodeSceAbs.pushDeserializedGate(
                                    GateJSON.JSON2Gate(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                GateJSON.oneGate2JSON((Gate) deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (name!=null && url!=null && pc_id!=null && is_admin!=null) {
                            Container parentContainer;
                            if (session!=null) parentContainer = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, pc_id);
                            else parentContainer = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(pc_id);

                            if (parentContainer!=null) {
                                Gate gate ;
                                if (session!=null) gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().createGate(session, url, name, pc_id, is_admin);
                                else gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().createGate(url, name, pc_id, is_admin);

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                GateJSON.oneGate2JSONWithTypedProps(gate, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Parent container not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : gate name and/or parent container ID " +
                                    "and/or url and/or is admin not provided.");
                        }
                        break;
                    case GateSce.OP_DELETE_GATE:
                        gid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (gid!=null) {
                            if (session!=null) MappingMsgsrvBootstrap.getMappingSce().getGateSce().deleteGate(session, gid);
                            else MappingMsgsrvBootstrap.getMappingSce().getGateSce().deleteGate(gid);

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : gate ID not provided.");
                        }
                        break;
                    case GateSce.OP_GET_GATE:
                        gid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (gid!=null) {
                            Gate gate;
                            if (session!=null) gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(session, gid);
                            else gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(gid);

                            if (gate!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                GateJSON.oneGate2JSONWithTypedProps(gate, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Gate not found.");
                                ((MomLogger)log).traceMessage("GateWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                    if (session!=null) session.traceSession(false);
                                    ((MomLogger) log).setMsgTraceLevel(false);
                                }
                                return message;
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : gate ID not provided.");
                        }
                        break;
                    case GateSce.OP_GET_GATES:
                        HashSet<Gate> gates;
                        if (session!=null) gates = (HashSet<Gate>) MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGates(session, null);
                        else gates = (HashSet<Gate>) MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGates(null);

                        if (gates!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            GateJSON.manyGates2JSONWithTypedProps(gates, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Gates not found.");
                        }
                        break;
                    case Gate.OP_SET_NODE_PRIMARY_ADMIN_ENDPOINT:
                        gid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        ep_id = (String) message.get(Endpoint.TOKEN_EP_ID);
                        if (gid!=null && ep_id!=null) {
                            Gate gate;
                            if (session!=null) gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(session, gid);
                            else gate = MappingMsgsrvBootstrap.getMappingSce().getGateSce().getGate(gid);

                            if (gate!=null) {
                                Endpoint endpoint = null;
                                if (!ep_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                    if (session != null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, ep_id);
                                    else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(ep_id);
                                }

                                if (endpoint!=null || ep_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                    Endpoint previousAGEP = gate.getNodePrimaryAdminEndpoint();
                                    if (session!=null) ((SProxGate)gate).setNodePrimaryAdminEndpoint(session, endpoint);
                                    else gate.setNodePrimaryAdminEndpoint(endpoint);

                                    if (previousAGEP!=null) {
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(previousAGEP, outStream);
                                        String resultEp = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(Gate.JOIN_PREVIOUS_PAEP, resultEp);
                                    }

                                    if (endpoint!=null) {
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(endpoint, outStream);
                                        String resultEp = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(Gate.JOIN_CURRENT_PAEP, resultEp);
                                    }

                                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                    GateJSON.oneGate2JSONWithTypedProps(gate, outStream);
                                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                    message.put(MomMsgTranslator.MSG_BODY, result);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : endpoint not found with provided ID.");
                                    ((MomLogger)log).traceMessage("GateWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                        if (session!=null) session.traceSession(false);
                                        ((MomLogger) log).setMsgTraceLevel(false);
                                    }
                                    return message;
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : Gate not found with provided ID.");
                                ((MomLogger)log).traceMessage("GateWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                    if (session!=null) session.traceSession(false);
                                    ((MomLogger)log).setMsgTraceLevel(false);
                                }
                                return message;
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : gate ID and/or endpoint ID not provided.");
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
                if (e.getMessage() == null || (!e.getMessage().equals(MappingDSException.MAPPING_OVERLOAD) && !e.getMessage().equals(MappingDSException.MAPPING_TIMEOUT)))
                    e.printStackTrace();
                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
            }
            ((MomLogger)log).traceMessage("GateWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
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
                    GateSce.Q_MAPPING_GATE_SERVICE, new GateWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + GateSce.Q_MAPPING_GATE_SERVICE + "...");
        }
    }
}
