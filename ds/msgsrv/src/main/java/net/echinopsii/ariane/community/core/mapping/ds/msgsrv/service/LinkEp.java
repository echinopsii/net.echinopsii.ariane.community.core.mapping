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

import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.LinkSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxLinkSceAbs;
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

public class LinkEp {
    private static final Logger log = MomLoggerFactory.getLogger(LinkEp.class);

    static class LinkWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String lid;
            String sep_id;
            String tep_id;
            String t_id;
            String payload;
            Session session = null;
            Endpoint sourceEndpoint = null;
            Endpoint targetEndpoint = null;
            Transport transport = null;
            Link link = null;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(true);
            ((MomLogger)log).traceMessage("LinkWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

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
                    ((MomLogger)log).traceMessage("LinkWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                    return message;
                }
            }

            try {
                switch (operation) {
                    case LinkSce.OP_CREATE_LINK:
                        sep_id = (String) message.get(LinkSce.PARAM_LINK_SEPID);
                        tep_id = (String) message.get(LinkSce.PARAM_LINK_TEPID);
                        t_id = (String) message.get(Transport.TOKEN_TP_ID);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload!=null) {
                            DeserializedPushResponse deserializationResponse = SProxLinkSceAbs.pushDeserializedLink(
                                    LinkJSON.JSON2Link(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                LinkJSON.oneLink2JSON((Link)deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (sep_id!=null && t_id!=null) {
                            if (session!=null) link = MappingMsgsrvBootstrap.getMappingSce().getLinkSce().createLink(session, sep_id, tep_id, t_id);
                            else link = MappingMsgsrvBootstrap.getMappingSce().getLinkSce().createLink(sep_id, tep_id, t_id);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            LinkJSON.oneLink2JSON(link, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_BODY, result);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint and/or transport ID not provided.");
                        }
                        break;
                    case LinkSce.OP_DELETE_LINK:
                        lid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (lid!=null) {
                            if (session != null) MappingMsgsrvBootstrap.getMappingSce().getLinkSce().deleteLink(session, lid);
                            else MappingMsgsrvBootstrap.getMappingSce().getLinkSce().deleteLink(lid);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : link ID not provided.");
                        }
                        break;
                    case LinkSce.OP_GET_LINK:
                        lid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (lid!=null) {
                            if (session!=null) link = MappingMsgsrvBootstrap.getMappingSce().getLinkSce().getLink(session, lid);
                            else link = MappingMsgsrvBootstrap.getMappingSce().getLinkSce().getLink(lid);
                            if (link!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                LinkJSON.oneLink2JSON(link, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : link not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : link ID not provided.");
                        }
                        break;
                    case LinkSce.OP_GET_LINKS:
                        HashSet<Link> links ;
                        if (session!=null) links = (HashSet<Link>) MappingMsgsrvBootstrap.getMappingSce().getLinkSce().getLinks(session,null);
                        else links = (HashSet<Link>) MappingMsgsrvBootstrap.getMappingSce().getLinkSce().getLinks(null);
                        if (links!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            LinkJSON.manyLinks2JSON(links, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_BODY, result);
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : links not found.");
                        }
                        break;
                    case Link.OP_SET_LINK_ENDPOINT_SOURCE:
                    case Link.OP_SET_LINK_ENDPOINT_TARGET:
                    case Link.OP_SET_LINK_TRANSPORT:
                        lid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        sep_id = (String) message.get(LinkSce.PARAM_LINK_SEPID);
                        tep_id = (String) message.get(LinkSce.PARAM_LINK_TEPID);
                        t_id = (String) message.get(Transport.TOKEN_TP_ID);
                        if (lid!=null) {
                            if (session!=null) link = MappingMsgsrvBootstrap.getMappingSce().getLinkSce().getLink(session, lid);
                            else link = MappingMsgsrvBootstrap.getMappingSce().getLinkSce().getLink(lid);
                            if (link!=null) {
                                if (operation.equals(Link.OP_SET_LINK_ENDPOINT_SOURCE) && sep_id!=null) {
                                    if (session!=null) sourceEndpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, sep_id);
                                    else sourceEndpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(sep_id);
                                    if (sourceEndpoint!=null) {
                                        Endpoint previousSourceEndpoint = link.getLinkEndpointSource();
                                        if (session!=null) ((SProxLink)link).setLinkEndpointSource(session, sourceEndpoint);
                                        else link.setLinkEndpointSource(sourceEndpoint);
                                        if (previousSourceEndpoint!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            EndpointJSON.oneEndpoint2JSONWithTypedProps(previousSourceEndpoint, outStream);
                                            String resultPep = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Link.JOIN_PREVIOUS_SEP, resultPep);
                                        }
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(sourceEndpoint, outStream);
                                        String resultCep = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(Link.JOIN_CURRENT_SEP, resultCep);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint not found with provided ID.");
                                        ((MomLogger)log).traceMessage("LinkWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                                        return message;
                                    }
                                } else if (operation.equals(Link.OP_SET_LINK_ENDPOINT_TARGET) && tep_id!=null) {
                                    if (session!=null) targetEndpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, t_id);
                                    else targetEndpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(t_id);
                                    if (targetEndpoint!=null) {
                                        Endpoint previousTargetEndpoint = link.getLinkEndpointTarget();
                                        if (session!=null) ((SProxLink)link).setLinkEndpointTarget(session, targetEndpoint);
                                        else link.setLinkEndpointTarget(targetEndpoint);
                                        if (previousTargetEndpoint!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            EndpointJSON.oneEndpoint2JSONWithTypedProps(previousTargetEndpoint, outStream);
                                            String resultPep = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Link.JOIN_PREVIOUS_TEP, resultPep);
                                        }
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(sourceEndpoint, outStream);
                                        String resultCep = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(Link.JOIN_CURRENT_TEP, resultCep);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : target endpoint not found with provided ID.");
                                        ((MomLogger)log).traceMessage("LinkWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                                        return message;
                                    }
                                } else if (operation.equals(Link.OP_SET_LINK_TRANSPORT) && t_id!=null) {
                                    if (session!=null) transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(session, t_id);
                                    else transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(t_id);
                                    if (transport!=null) {
                                        Transport previousTransport = link.getLinkTransport();
                                        if (session!=null) ((SProxLink)link).setLinkTransport(transport);
                                        else link.setLinkTransport(transport);
                                        if (previousTransport!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            TransportJSON.oneTransport2JSONWithTypedProps(previousTransport, outStream);
                                            String resultPtransport = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Link.JOIN_PREVIOUS_TEP, resultPtransport);
                                        }
                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(sourceEndpoint, outStream);
                                        String resultPnode = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(Link.JOIN_CURRENT_TEP, resultPnode);

                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport not found with provided ID.");
                                        ((MomLogger)log).traceMessage("LinkWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                                        return message;
                                    }
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    switch (operation) {
                                        case Link.OP_SET_LINK_ENDPOINT_SOURCE:
                                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint ID not provided.");
                                            break;
                                        case Link.OP_SET_LINK_ENDPOINT_TARGET:
                                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : target endpoint ID not provided.");
                                            break;
                                        case Link.OP_SET_LINK_TRANSPORT:
                                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport ID not provided.");
                                            break;
                                    }
                                    ((MomLogger)log).traceMessage("LinkWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                                    return message;

                                }
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                LinkJSON.oneLink2JSON(link, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : link not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : link ID not provided.");
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
            ((MomLogger)log).traceMessage("LinkWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected()) {
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    LinkSce.Q_MAPPING_LINK_SERVICE, new LinkWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + LinkSce.Q_MAPPING_LINK_SERVICE + "...");
        }
    }
}
