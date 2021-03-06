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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.EndpointSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxEndpointSceAbs;
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

public class EndpointEp {
    private static final Logger log = MomLoggerFactory.getLogger(EndpointEp.class);

    static class EndpointWorker implements AppMsgWorker {

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String id;
            String url;
            String payload;
            String pn_id;
            String selector;
            String te_id;
            String prop_field;
            String prop_name;
            Session session = null;
            Endpoint endpoint = null;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(true);
            ((MomLogger)log).traceMessage("EndpointWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

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
                    ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setMsgTraceLevel(false);
                    return message;
                } else if (message.containsKey(MomMsgTranslator.MSG_TRACE)) session.traceSession(true);
            }

            try {
                switch (operation) {
                    case EndpointSce.OP_CREATE_ENDPOINT:
                        url = (String) message.get(Endpoint.TOKEN_EP_URL);
                        pn_id = (String) message.get(NodeSce.PARAM_NODE_PNID);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload!=null) {
                            DeserializedPushResponse deserializationResponse = SProxEndpointSceAbs.pushDeserializedEndpoint(
                                    EndpointJSON.JSON2Endpoint(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                EndpointJSON.oneEndpoint2JSON((Endpoint) deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (url!=null && pn_id!=null) {
                            Node pnode = null;
                            if (session!=null) pnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, pn_id);
                            else pnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(pn_id);
                            if (pnode!=null) {
                                if (session!=null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().createEndpoint(session, url, pn_id);
                                else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().createEndpoint(url, pn_id);

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                EndpointJSON.oneEndpoint2JSONWithTypedProps(endpoint, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Parent container not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : endpoint url or parent node ID not provided.");
                        }
                        break;
                    case EndpointSce.OP_DELETE_ENDPOINT:
                        id = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (id!=null) {
                            if (session!=null) MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().deleteEndpoint(session, id);
                            else MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().deleteEndpoint(id);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : endpoint ID not provided.");
                        }
                        break;
                    case EndpointSce.OP_GET_ENDPOINT:
                    case EndpointSce.OP_GET_ENDPOINT_BY_URL:
                        id = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        url = (String) message.get(Endpoint.TOKEN_EP_URL);

                        if (operation.equals(EndpointSce.OP_GET_ENDPOINT) && id!=null) {
                            if (session!=null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, id);
                            else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);
                        } else if (operation.equals(EndpointSce.OP_GET_ENDPOINT_BY_URL) && url!=null) {
                            if (session!=null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpointByURL(session, url);
                            else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpointByURL(url);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            switch (operation) {
                                case EndpointSce.OP_GET_ENDPOINT:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : endpoint ID not provided.");
                                    break;
                                case EndpointSce.OP_GET_ENDPOINT_BY_URL:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : endpoint URL not provided.");
                                    break;
                            }
                            ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                if (session!=null) session.traceSession(false);
                                ((MomLogger) log).setMsgTraceLevel(false);
                            }
                            return message;
                        }
                        if (endpoint!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            EndpointJSON.oneEndpoint2JSONWithTypedProps(endpoint, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_BODY, result);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Endpoint not found.");
                        }
                        break;
                    case EndpointSce.OP_GET_ENDPOINTS:
                        selector = (message.get(MappingSce.GLOBAL_PARAM_SELECTOR)==null || ((String) message.get(MappingSce.GLOBAL_PARAM_SELECTOR)).equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) ? null : (String) message.get(MappingSce.GLOBAL_PARAM_SELECTOR);
                        prop_field = (message.containsKey(MappingSce.GLOBAL_PARAM_PROP_FIELD)) ? message.get(MappingSce.GLOBAL_PARAM_PROP_FIELD).toString() : null;

                        HashSet<Endpoint> endpoints;
                        if (prop_field!=null) {
                            PropertiesJSON.TypedPropertyField typedPropertyField = PropertiesJSON.typedPropertyFieldFromJSON(prop_field);
                            Object value = ToolBox.extractPropertyObjectValueFromString(typedPropertyField.getPropertyValue(), typedPropertyField.getPropertyType());
                            if (session != null) endpoints = (HashSet<Endpoint>) MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoints(session, typedPropertyField.getPropertyName(), value);
                            else endpoints = (HashSet<Endpoint>) MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoints(typedPropertyField.getPropertyName(), value);
                        } else {
                            if (session != null) endpoints = (HashSet<Endpoint>) MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoints(session, selector);
                            else endpoints = (HashSet<Endpoint>) MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoints(selector);
                        }

                        if (endpoints!=null && endpoints.size()>0){
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            EndpointJSON.manyEndpoints2JSONWithTypedProps(endpoints, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Endpoints not found.");
                        }
                        break;
                    case Endpoint.OP_ADD_ENDPOINT_PROPERTY:
                    case Endpoint.OP_REMOVE_ENDPOINT_PROPERTY:
                        id = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (id!=null) {
                            if (session != null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, id);
                            else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);

                            if (endpoint!=null) {
                                if (operation.equals(Endpoint.OP_ADD_ENDPOINT_PROPERTY)) {
                                    prop_field = (message.containsKey(MappingSce.GLOBAL_PARAM_PROP_FIELD)) ? message.get(MappingSce.GLOBAL_PARAM_PROP_FIELD).toString() : null;
                                    if (prop_field!=null) {
                                        PropertiesJSON.TypedPropertyField typedPropertyField = PropertiesJSON.typedPropertyFieldFromJSON(prop_field);
                                        Object value = ToolBox.extractPropertyObjectValueFromString(typedPropertyField.getPropertyValue(), typedPropertyField.getPropertyType());
                                        if (session != null) ((SProxEndpoint)endpoint).addEndpointProperty(session, typedPropertyField.getPropertyName(), value);
                                        else endpoint.addEndpointProperty(typedPropertyField.getPropertyName(), value);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property field not provided.");
                                        ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    prop_name = (String) message.get(MappingSce.GLOBAL_PARAM_PROP_NAME);
                                    if (prop_name!=null) {
                                        if (session!=null) ((SProxEndpoint)endpoint).removeEndpointProperty(session, prop_name);
                                        else endpoint.removeEndpointProperty(prop_name);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property name not provided.");
                                        ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                }

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                EndpointJSON.oneEndpoint2JSONWithTypedProps(endpoint, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : Endpoint not found.");
                            }
                        }
                        break;
                    case Endpoint.OP_SET_ENDPOINT_URL:
                    case Endpoint.OP_SET_ENDPOINT_PARENT_NODE:
                    case Endpoint.OP_ADD_TWIN_ENDPOINT:
                    case Endpoint.OP_REMOVE_TWIN_ENDPOINT:
                        id = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        url = (String) message.get(Endpoint.TOKEN_EP_URL);
                        pn_id = (String) message.get(NodeSce.PARAM_NODE_PNID);
                        te_id = (String) message.get(EndpointSce.PARAM_ENDPOINT_TEID);
                        if (id!=null) {
                            if (session!=null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, id);
                            else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(id);

                            if (endpoint!=null) {
                                if (operation.equals(Endpoint.OP_SET_ENDPOINT_URL) && url!=null) {
                                    if (session!=null) ((SProxEndpoint)endpoint).setEndpointURL(session, url);
                                    else endpoint.setEndpointURL(url);
                                } else if (operation.equals(Endpoint.OP_SET_ENDPOINT_PARENT_NODE) && pn_id!=null) {
                                    Node node = null;
                                    if (session != null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, pn_id);
                                    else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(pn_id);
                                    if (node != null) {
                                        Node previousParentNode = endpoint.getEndpointParentNode();
                                        if (session != null) ((SProxEndpoint) endpoint).setEndpointParentNode(session, node);
                                        else endpoint.setEndpointParentNode(node);
                                        if (previousParentNode!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            NodeJSON.oneNode2JSONWithTypedProps(previousParentNode, outStream);
                                            String resultPnode = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Endpoint.JOIN_PREVIOUS_PNODE, resultPnode);
                                        }
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                        String resultPnode = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(Endpoint.JOIN_CURRENT_PNODE, resultPnode);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : Parent node not found.");
                                        ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else if ((operation.equals(Endpoint.OP_ADD_TWIN_ENDPOINT) || operation.equals(Endpoint.OP_REMOVE_TWIN_ENDPOINT)) && te_id!=null) {
                                    Endpoint twinEndpoint = null;
                                    if (session!=null) twinEndpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, te_id);
                                    else twinEndpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(te_id);
                                    if (twinEndpoint!=null) {
                                        if (operation.equals(Endpoint.OP_ADD_TWIN_ENDPOINT)) {
                                            if (session!=null) ((SProxEndpoint)endpoint).addTwinEndpoint(session, twinEndpoint);
                                            else endpoint.addTwinEndpoint(twinEndpoint);
                                        } else {
                                            if (session!=null) ((SProxEndpoint)endpoint).removeTwinEndpoint(session, twinEndpoint);
                                            else endpoint.removeTwinEndpoint(twinEndpoint);
                                        }
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(twinEndpoint, outStream);
                                        String resultEp = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(MappingDSGraphPropertyNames.DD_ENDPOINT_EDGE_TWIN_KEY, resultEp);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : twin endpoint not found.");
                                        ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setMsgTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    switch (operation) {
                                        case Endpoint.OP_SET_ENDPOINT_URL:
                                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : url not provided.");
                                            break;
                                        case Endpoint.OP_SET_ENDPOINT_PARENT_NODE:
                                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parent node id not provided.");
                                            break;
                                        case Endpoint.OP_ADD_TWIN_ENDPOINT:
                                        case Endpoint.OP_REMOVE_TWIN_ENDPOINT:
                                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : twin endpoint id not provided.");
                                            break;
                                    }
                                    ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                        if (session!=null) session.traceSession(false);
                                        ((MomLogger)log).setMsgTraceLevel(false);
                                    }
                                    return message;
                                }
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                EndpointJSON.oneEndpoint2JSONWithTypedProps(endpoint, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : Endpoint not found.");
                            }
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
            ((MomLogger)log).traceMessage("EndpointWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
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
                    EndpointSce.Q_MAPPING_ENDPOINT_SERVICE, new EndpointWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + EndpointSce.Q_MAPPING_ENDPOINT_SERVICE + "...");
        }
    }
}