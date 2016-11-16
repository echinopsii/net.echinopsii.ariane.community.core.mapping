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

import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransport;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.TransportSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxTransportSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TransportEp {
    private static final Logger log = MomLoggerFactory.getLogger(TransportEp.class);

    static class TransportWorker implements AppMsgWorker {

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String tid;
            String name;
            String payload;
            String prop_field;
            String prop_name;
            Session session = null;
            Transport transport = null;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(true);
            ((MomLogger)log).traceMessage("TransportWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

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
                    ((MomLogger)log).traceMessage("TransportWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                    return message;
                } else if (message.containsKey(MomMsgTranslator.MSG_TRACE)) session.traceSession(true);
            }

            try {
                switch (operation) {
                    case TransportSce.OP_CREATE_TRANSPORT:
                        name = (String) message.get(TransportSce.PARAM_TRANSPORT_NAME);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload!=null) {
                            DeserializedPushResponse deserializationResponse = SProxTransportSceAbs.pushDeserializedTransport(
                                    TransportJSON.JSON2Transport(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                TransportJSON.oneTransport2JSON((Transport)deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (name!=null) {
                            if (session!=null) transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().createTransport(session, name);
                            else transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().createTransport(name);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            TransportJSON.oneTransport2JSONWithTypedProps(transport, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_BODY, result);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport name not provided.");
                        }
                        break;
                    case TransportSce.OP_DELETE_TRANSPORT:
                        tid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (tid!=null) {
                            if (session!=null) MappingMsgsrvBootstrap.getMappingSce().getTransportSce().deleteTransport(session, tid);
                            else MappingMsgsrvBootstrap.getMappingSce().getTransportSce().deleteTransport(tid);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport id not provided.");
                        }
                        break;
                    case TransportSce.OP_GET_TRANSPORT:
                        tid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (tid!=null) {
                            if (session!=null) transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(session, tid);
                            else transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(tid);
                            if (transport!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                TransportJSON.oneTransport2JSONWithTypedProps(transport, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : transport not found with provided id.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport id not provided.");
                        }
                        break;
                    case TransportSce.OP_GET_TRANSPORTS:
                        HashSet<Transport> transports;
                        if (session!=null) transports = (HashSet<Transport>) MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransports(session, null);
                        else transports = (HashSet<Transport>) MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransports(null);
                        if (transports!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            TransportJSON.manyTransports2JSONWithTypedProps(transports, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_BODY, result);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Transports not found.");
                        }
                        break;
                    case Transport.OP_SET_TRANSPORT_NAME:
                        tid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        name = (String) message.get(TransportSce.PARAM_TRANSPORT_NAME);
                        if (tid!=null && name!=null) {
                            if (session!=null) transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(session, tid);
                            else transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(tid);
                            if (transport!=null) {
                                if (session!=null) ((SProxTransport)transport).setTransportName(session, name);
                                else transport.setTransportID(name);
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                TransportJSON.oneTransport2JSONWithTypedProps(transport, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport id and or name not provided.");
                        }
                        break;
                    case Transport.OP_ADD_TRANSPORT_PROPERTY:
                    case Transport.OP_REMOVE_TRANSPORT_PROPERTY:
                        tid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (tid!=null) {
                            if (session!=null) transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(session, tid);
                            else transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(tid);
                            if (transport!=null) {
                                if (operation.equals(Transport.OP_ADD_TRANSPORT_PROPERTY)) {
                                    prop_field = (message.containsKey(MappingSce.GLOBAL_PARAM_PROP_FIELD)) ? message.get(MappingSce.GLOBAL_PARAM_PROP_FIELD).toString() : null;
                                    if (prop_field!=null) {
                                        PropertiesJSON.TypedPropertyField typedPropertyField = PropertiesJSON.typedPropertyFieldFromJSON(prop_field);
                                        Object value = ToolBox.extractPropertyObjectValueFromString(typedPropertyField.getPropertyValue(), typedPropertyField.getPropertyType());
                                        if (session != null) ((SProxTransport)transport).addTransportProperty(session, typedPropertyField.getPropertyName(), value);
                                        else transport.addTransportProperty(typedPropertyField.getPropertyName(), value);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property field not provided.");
                                        ((MomLogger)log).traceMessage("TransportWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    prop_name = (String) message.get(MappingSce.GLOBAL_PARAM_PROP_NAME);
                                    if (prop_name!=null) {
                                        if (session!=null) ((SProxTransport)transport).removeTransportProperty(session, prop_name);
                                        else transport.removeTransportProperty(prop_name);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property name not provided.");
                                        ((MomLogger)log).traceMessage("TransportWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setTraceLevel(false);
                                        }
                                        return message;
                                    }
                                }
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                TransportJSON.oneTransport2JSONWithTypedProps(transport, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport id not provided.");
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
            ((MomLogger)log).traceMessage("TransportWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                if (session!=null) session.traceSession(false);
                ((MomLogger)log).setTraceLevel(false);
            }
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected()) {
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    TransportSce.Q_MAPPING_TRANSPORT_SERVICE, new TransportWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + TransportSce.Q_MAPPING_TRANSPORT_SERVICE + "...");
        }
    }
}