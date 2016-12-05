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

import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.service.SessionJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomLogger;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class SessionEp {
    private static final Logger log = MomLoggerFactory.getLogger(SessionEp.class);

    static class SessionWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
            String operation;
            String clientID;
            String sessionID;
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(true);
            ((MomLogger)log).traceMessage("SessionWorker.apply - in", message, MappingSce.GLOBAL_PARAM_PAYLOAD);

            if (oOperation==null)
                operation = MomMsgTranslator.OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case SProxMappingSce.SESSION_MGR_OP_OPEN:
                    try {
                        if (message.get(SProxMappingSce.SESSION_MGR_PARAM_CLIENT_ID)!=null) {
                            clientID = message.get(SProxMappingSce.SESSION_MGR_PARAM_CLIENT_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().openSession(clientID, true);
                            MappingMsgsrvMomSP.getSharedMoMConnection().openMsgGroupServices(session.getSessionID());
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            SessionJSON.oneSession2JSON(session, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_RC, 0);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + SProxMappingSce.SESSION_MGR_OP_OPEN + ") : no client ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case SProxMappingSce.SESSION_MGR_OP_CLOSE:
                    try {
                        if (message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID)!=null) {
                            sessionID = message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                            if (session != null) {
                                MappingMsgsrvMomSP.getSharedMoMConnection().closeMsgGroupServices(session.getSessionID());
                                MappingMsgsrvBootstrap.getMappingSce().closeSession(session);
                                message.put(MomMsgTranslator.MSG_RC, 0);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + SProxMappingSce.SESSION_MGR_OP_CLOSE +
                                        ") : no session found for ID " +
                                        sessionID + " provided");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + SProxMappingSce.SESSION_MGR_OP_CLOSE + ") : no session ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case Session.SESSION_OP_COMMIT:
                    try {
                        if (message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID)!=null) {
                            sessionID = message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                            if (session != null) {
                                session.commit();
                                message.put(MomMsgTranslator.MSG_RC, 0);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + SProxMappingSce.SESSION_MGR_OP_CLOSE + ") : no session for ID provided");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + SProxMappingSce.SESSION_MGR_OP_CLOSE + ") : no session ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case Session.SESSION_OP_ROLLBACK:
                    try {
                        if (message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID)!=null) {
                            sessionID = message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                            if (session != null) {
                                session.rollback();
                                message.put(MomMsgTranslator.MSG_RC, 0);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + SProxMappingSce.SESSION_MGR_OP_CLOSE + ") : no session for ID provided");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + SProxMappingSce.SESSION_MGR_OP_CLOSE + ") : no session ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
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
            ((MomLogger)log).traceMessage("SessionWorker.apply - out", message, MappingSce.GLOBAL_PARAM_PAYLOAD);
            if (message.containsKey(MomMsgTranslator.MSG_TRACE)) ((MomLogger)log).setTraceLevel(false);
            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected()) {
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    Session.MAPPING_SESSION_SERVICE_Q, new SessionWorker()
            );
            log.info("Ariane Mapping Messaging Service is waiting message on  " + Session.MAPPING_SESSION_SERVICE_Q + "...");
        }
    }
}
