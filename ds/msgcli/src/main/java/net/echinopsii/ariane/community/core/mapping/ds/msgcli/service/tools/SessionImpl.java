/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
 * Author: mathilde.ffrench@echinopsii.net
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.json.service.SessionJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionImpl implements Session {

    class SessionReplyWorker implements AppMsgWorker {
        private SessionImpl session ;

        public SessionReplyWorker(SessionImpl session) {
            this.session = session;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (this.session!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    Object oOperation = message.get(MappingSce.MAPPING_SCE_OPERATION_FDN);
                    String operation;

                    if (oOperation == null)
                        operation = MappingSce.MAPPING_SCE_OPERATION_NOT_DEFINED;
                    else
                        operation = oOperation.toString();

                    switch (operation) {
                        case SProxMappingSce.SESSION_MGR_OP_OPEN:
                            try {
                                String body = null;
                                if (message.get(MomMsgTranslator.MSG_BODY)!=null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                                    body = (String) message.get(MomMsgTranslator.MSG_BODY);
                                else if (message.get(MomMsgTranslator.MSG_BODY)!=null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                                    body = new String((byte[])message.get(MomMsgTranslator.MSG_BODY));
                                this.session.setSessionID(SessionJSON.JSON2Session(body).getSessionID());
                                this.session.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case SProxMappingSce.SESSION_MGR_OP_CLOSE:
                            this.session.stop();
                            break;
                        case MappingSce.MAPPING_SCE_OPERATION_NOT_DEFINED:
                            SessionImpl.log.error("Ariane Mapping Service didn't return origin operation ! ");
                        default:
                            break;
                    }
                } else SessionImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    protected final static Logger log = LoggerFactory.getLogger(SessionImpl.class);
    private String sessionID = null;
    private boolean isRunning = false;
    private AppMsgWorker sessionReplyWorker = new SessionReplyWorker(this);

    @Override
    public String getSessionID() {
        return sessionID;
    }

    protected void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public AppMsgWorker getSessionReplyWorker() {
        return sessionReplyWorker;
    }

    @Override
    public Session stop() {
        this.sessionReplyWorker = null;
        this.isRunning = false;
        return this;
    }

    @Override
    public Session start() {
        this.isRunning = true;
        return this;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public Object execute(Object o, String methodName, Object[] args) throws MappingDSException {
        /*
           SEND REQ LIKE :
           String ObjectType to Queue (Mapping Domain or Mapping Service)
           String sessionID
           String methodName
        */
        return null;
    }

    @Override
    public Session commit() throws MappingDSException {
        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SESSION_OP_COMMIT);
        message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, this.sessionID);
        Map<String, Object> reply = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, Session.MAPPING_SESSION_SERVICE_Q, this.getSessionReplyWorker());
        if (reply.get(MomMsgTranslator.MSG_RC)!=null && reply.get(MomMsgTranslator.MSG_RC)!=0)
            throw new MappingDSException("Error returned by Ariane Mapping Service ! " + reply.get(MomMsgTranslator.MSG_ERR));
        return this;
    }

    @Override
    public Session rollback() throws MappingDSException {
        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.MAPPING_SCE_OPERATION_FDN, SESSION_OP_ROLLBACK);
        message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, this.sessionID);
        Map<String, Object> reply = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, Session.MAPPING_SESSION_SERVICE_Q, this.getSessionReplyWorker());
        if (reply.get(MomMsgTranslator.MSG_RC)!=null && reply.get(MomMsgTranslator.MSG_RC)!=0)
            throw new MappingDSException("Error returned by Ariane Mapping Service ! " + reply.get(MomMsgTranslator.MSG_ERR));
        return this;
    }
}
