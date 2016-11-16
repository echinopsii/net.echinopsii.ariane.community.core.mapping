/**
 * Mapping Messaging Server
 * Map service messaging endpoint
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

import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;

public class MappingEp {
    private static final Logger log = MomLoggerFactory.getLogger(MappingEp.class);

    static class MappingWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String sid;
            String name;
            String c_id;
            String sep_id;
            String dep_id;
            String t_id;
            Session session = null;
            Container container;
            Endpoint source_ep;
            Endpoint destin_ep;
            Transport transport;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(true);
            ((MomLogger)log).traceMessage("MappingWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

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
                    ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
                    return message;
                } else if (message.containsKey(MomMsgTranslator.MSG_TRACE)) session.traceSession(true);
            }

            try {
                switch (operation) {
                    case MappingSce.OP_GET_NODE_BY_NAME:
                        name = (String) message.get(NodeSce.PARAM_NODE_NAME);
                        c_id = (String) message.get(Container.TOKEN_CT_ID);
                        if (name != null && c_id != null) {
                            if (session!=null) container = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, c_id);
                            else container = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(c_id);
                            if (container!=null) {
                                Node node ;
                                if (session!=null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeByName(session, container, name);
                                else node = MappingMsgsrvBootstrap.getMappingSce().getNodeByName(container, name);
                                if (node!=null) {
                                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                    NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                    message.put(MomMsgTranslator.MSG_BODY, result);
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                    message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : node not found.");
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container id and/or node name not provided.");
                        }
                        break;
                    case MappingSce.OP_GET_GATE_BY_NAME:
                        name = (String) message.get(GateSce.PARAM_GATE_NAME);
                        c_id = (String) message.get(Container.TOKEN_CT_ID);
                        if (name != null && c_id != null) {
                            if (session!=null) container = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, c_id);
                            else container = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(c_id);
                            if (container!=null) {
                                Gate gate ;
                                if (session!=null) gate = MappingMsgsrvBootstrap.getMappingSce().getGateByName(session, container, name);
                                else gate = MappingMsgsrvBootstrap.getMappingSce().getGateByName(container, name);
                                if (gate!=null) {
                                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                    GateJSON.oneGate2JSONWithTypedProps(gate, outStream);
                                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                    message.put(MomMsgTranslator.MSG_BODY, result);
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                    message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : gate not found.");
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : container id and/or gate name not provided.");
                        }
                        break;
                    case MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP:
                    case MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_TRANSPORT:
                        sep_id = (String) message.get(LinkSce.PARAM_LINK_SEPID);
                        dep_id = (String) message.get(LinkSce.PARAM_LINK_TEPID);
                        t_id = (String) message.get(Transport.TOKEN_TP_ID);
                        Link link;
                        if ((sep_id != null && dep_id != null && operation.equals(MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP)) ||
                            (sep_id != null && t_id != null && operation.equals(MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_TRANSPORT))) {
                            if (session!=null) source_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, sep_id);
                            else source_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(sep_id);
                            if (source_ep!=null) {
                                if (operation.equals(MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP)) {
                                    if (session != null) destin_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, dep_id);
                                    else destin_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(dep_id);
                                    if (destin_ep != null) {
                                        if (session != null) link = MappingMsgsrvBootstrap.getMappingSce().getLinkBySourceEPandDestinationEP(session, source_ep, destin_ep);
                                        else link = MappingMsgsrvBootstrap.getMappingSce().getLinkBySourceEPandDestinationEP(source_ep, destin_ep);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : destination endpoint not found with provided ID.");
                                        ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setTraceLevel(false);
                                        }
                                        return message;
                                    }
                                } else {
                                    if (session != null) transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(session, t_id);
                                    else transport = MappingMsgsrvBootstrap.getMappingSce().getTransportSce().getTransport(t_id);
                                    if (transport!=null) {
                                        if (session != null) link = MappingMsgsrvBootstrap.getMappingSce().getMulticastLinkBySourceEPAndTransport(session, source_ep, transport);
                                        else link = MappingMsgsrvBootstrap.getMappingSce().getMulticastLinkBySourceEPAndTransport(source_ep, transport);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : transport not found with provided ID.");
                                        ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                        if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                            if (session!=null) session.traceSession(false);
                                            ((MomLogger)log).setTraceLevel(false);
                                        }
                                        return message;
                                    }
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint not found with provided ID.");
                                ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                    if (session!=null) session.traceSession(false);
                                    ((MomLogger)log).setTraceLevel(false);
                                }
                                return message;
                            }

                            if (link!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                LinkJSON.oneLink2JSON(link, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : link not found.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            if (operation.equals(MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP))
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source and/or destination endpoint id not provided.");
                            else if (operation.equals(MappingSce.OP_GET_LINK_BY_SOURCE_EP_AND_TRANSPORT))
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint and/or transport id not provided.");
                        }
                        break;
                    case MappingSce.OP_GET_LINKS_BY_SOURCE_EP:
                    case MappingSce.OP_GET_LINKS_BY_DESTINATION_EP:
                        sep_id = (String) message.get(LinkSce.PARAM_LINK_SEPID);
                        dep_id = (String) message.get(LinkSce.PARAM_LINK_TEPID);
                        if ((sep_id != null && operation.equals(MappingSce.OP_GET_LINKS_BY_SOURCE_EP)) ||
                            (dep_id != null && operation.equals(MappingSce.OP_GET_LINKS_BY_DESTINATION_EP))) {
                            HashSet<Link> links ;
                            if (operation.equals(MappingSce.OP_GET_LINKS_BY_SOURCE_EP)) {
                                if (session!=null) source_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, sep_id);
                                else source_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(sep_id);
                                if (source_ep!=null) {
                                    if (session!=null) links = (HashSet<Link>) MappingMsgsrvBootstrap.getMappingSce().getLinksBySourceEP(session, source_ep);
                                    else links = (HashSet<Link>) MappingMsgsrvBootstrap.getMappingSce().getLinksBySourceEP(source_ep);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint not found with provided ID.");
                                    ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                        if (session!=null) session.traceSession(false);
                                        ((MomLogger)log).setTraceLevel(false);
                                    }
                                    return message;
                                }
                            } else {
                                if (session!=null) destin_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, dep_id);
                                else destin_ep = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(dep_id);
                                if (destin_ep!=null) {
                                    if (session!=null) links = (HashSet<Link>) MappingMsgsrvBootstrap.getMappingSce().getLinksByDestinationEP(session, destin_ep);
                                    else links = (HashSet<Link>) MappingMsgsrvBootstrap.getMappingSce().getLinksByDestinationEP(destin_ep);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : destination endpoint not found with provided ID.");
                                    ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
                                    if (message.containsKey(MomMsgTranslator.MSG_TRACE)) {
                                        if (session!=null) session.traceSession(false);
                                        ((MomLogger)log).setTraceLevel(false);
                                    }
                                    return message;
                                }
                            }

                            if (links!=null && links.size()>0) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                LinkJSON.manyLinks2JSON(links, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Not found (" + operation + ") : links not found.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : source endpoint id not provided.");
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
            ((MomLogger)log).traceMessage("MappingWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
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
                    MappingSce.Q_MAPPING_SCE_SERVICE, new MappingWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + MappingSce.Q_MAPPING_SCE_SERVICE + "...");
        }
    }
}