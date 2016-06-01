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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class SessionEp {

    static class SessionWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.OPERATION_FDN);
            String operation;
            String clientID;
            String sessionID;

            if (oOperation==null)
                operation = MappingSce.OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            switch (operation) {
                case MappingSce.SESSION_MGR_OP_OPEN:
                    try {
                        if (message.get(MappingSce.SESSION_MGR_OP_CLIENT_ID)!=null) {
                            clientID = message.get(MappingSce.SESSION_MGR_OP_CLIENT_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().openSession(clientID, true);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            SessionJSON.oneSession2JSON(session, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                            message.put(MomMsgTranslator.MSG_RC, 0);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + MappingSce.SESSION_MGR_OP_OPEN + ") : no client ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case MappingSce.SESSION_MGR_OP_CLOSE:
                    try {
                        if (message.get(MappingSce.SESSION_MGR_OP_SESSION_ID)!=null) {
                            sessionID = message.get(MappingSce.SESSION_MGR_OP_SESSION_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                            if (session != null) {
                                MappingMsgsrvBootstrap.getMappingSce().closeSession(session);
                                message.put(MomMsgTranslator.MSG_RC, 0);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Not Found (" + MappingSce.SESSION_MGR_OP_CLOSE +
                                        ") : no session found for ID " +
                                        sessionID + " provided");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + MappingSce.SESSION_MGR_OP_CLOSE + ") : no session ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case Session.SESSION_OP_COMMIT:
                    try {
                        if (message.get(MappingSce.SESSION_MGR_OP_SESSION_ID)!=null) {
                            sessionID = message.get(MappingSce.SESSION_MGR_OP_SESSION_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                            if (session != null) {
                                session.commit();
                                message.put(MomMsgTranslator.MSG_RC, 0);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + MappingSce.SESSION_MGR_OP_CLOSE + ") : no session for ID provided");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + MappingSce.SESSION_MGR_OP_CLOSE + ") : no session ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case Session.SESSION_OP_ROLLBACK:
                    try {
                        if (message.get(MappingSce.SESSION_MGR_OP_SESSION_ID)!=null) {
                            sessionID = message.get(MappingSce.SESSION_MGR_OP_SESSION_ID).toString();
                            Session session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sessionID);
                            if (session != null) {
                                session.rollback();
                                message.put(MomMsgTranslator.MSG_RC, 0);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, 1);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + MappingSce.SESSION_MGR_OP_CLOSE + ") : no session for ID provided");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, 1);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + MappingSce.SESSION_MGR_OP_CLOSE + ") : no session ID provided");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Internal server error : " + e.getMessage());
                    }
                    break;
                case MappingSce.OPERATION_NOT_DEFINED:
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
                    Session.MAPPING_SESSION_SERVICE_Q, new SessionWorker()
            );
    }
}
