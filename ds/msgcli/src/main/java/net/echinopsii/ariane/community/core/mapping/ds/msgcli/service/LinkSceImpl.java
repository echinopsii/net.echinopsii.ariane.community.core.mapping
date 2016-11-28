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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.LinkImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.LinkSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxLinkSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;

public class LinkSceImpl extends SProxLinkSceAbs<LinkImpl>{

    private static final Logger log = LoggerFactory.getLogger(LinkSceImpl.class);

    @Override
    public Link createLink(String sourceEndpointID, String targetEndpointID, String transportID) throws MappingDSException {
        LinkImpl link = new LinkImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, LinkSce.OP_CREATE_LINK);
        message.put(LinkSce.PARAM_LINK_SEPID, sourceEndpointID);
        message.put(LinkSce.PARAM_LINK_TEPID, (targetEndpointID!=null) ? targetEndpointID : MappingSce.GLOBAL_PARAM_OBJ_NONE);
        message.put(Transport.TOKEN_TP_ID, transportID);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, link.getLinkReplyWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return link;
    }

    @Override
    public void deleteLink(String linkID) throws MappingDSException {
        LinkImpl link = new LinkImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, LinkSce.OP_DELETE_LINK);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, linkID);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, link.getLinkReplyWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    public static Link internalGetLink(String id) throws MappingDSException {
        LinkImpl link = new LinkImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, LinkSce.OP_GET_LINK);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, id);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, link.getLinkReplyWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);

        if (rc != 0) {
            if (rc == MomMsgTranslator.MSG_RET_NOT_FOUND) link = null;
            else throw new MappingDSException("Ariane server raised an error... Check your logs !");
        }

        return link;
    }

    @Override
    public Link getLink(String id) throws MappingDSException {
        return internalGetLink(id);
    }

    class getLinksWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<LinkImpl> links = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    links = new HashSet<>();
                    for (LinkJSON.JSONDeserializedLink jsonDeserializedLink : LinkJSON.JSON2Links(body)) {
                        LinkImpl link = new LinkImpl();
                        link.synchronizeFromJSON(jsonDeserializedLink);
                        links.add(link);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else LinkSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", links);
            return message;
        }
    }

    @Override
    public Set getLinks(String selector) throws MappingDSException {
        Set<Link> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MomMsgTranslator.OPERATION_FDN, LinkSce.OP_GET_LINKS);
        message.put(MappingSce.GLOBAL_PARAM_SELECTOR, (selector!=null) ? selector : MappingSce.GLOBAL_PARAM_OBJ_NONE);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = null;
        try {
            retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, new getLinksWorker());
        } catch (TimeoutException e) {
            throw new MappingDSException(e.getMessage());
        }
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Link>) retMsg.get("RET"));

        return ret;
    }
}
