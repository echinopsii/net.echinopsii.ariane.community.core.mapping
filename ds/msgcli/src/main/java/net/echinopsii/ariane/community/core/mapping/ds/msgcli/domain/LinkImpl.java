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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLinkAbs;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LinkImpl extends SProxLinkAbs implements SProxLink {
    class LinkReplyWorker implements AppMsgWorker {
        private LinkImpl link;

        public LinkReplyWorker(LinkImpl link) {
            this.link = link;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (link!=null) {
                int rc = (int) message.get(MomMsgTranslator.MSG_RC);
                if (rc == 0) {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));
                    if (body != null) {
                        //TODO
                    }
                } else LinkImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(LinkImpl.class);

    private LinkReplyWorker linkReplyWorker = new LinkReplyWorker(this);
    private String transportID;
    private String sourceEndpointID;
    private String targetEndpointID;

    public LinkReplyWorker getLinkReplyWorker() {
        return linkReplyWorker;
    }

    public String getTransportID() {
        return transportID;
    }

    public void setTransportID(String transportID) {
        this.transportID = transportID;
    }

    public String getSourceEndpointID() {
        return sourceEndpointID;
    }

    public void setSourceEndpointID(String sourceEndpointID) {
        this.sourceEndpointID = sourceEndpointID;
    }

    public String getTargetEndpointID() {
        return targetEndpointID;
    }

    public void setTargetEndpointID(String targetEndpointID) {
        this.targetEndpointID = targetEndpointID;
    }

    @Override
    public Transport getLinkTransport() {
        return super.getLinkTransport();
    }

    @Override
    public void setLinkTransport(Transport transport) throws MappingDSException {

    }

    @Override
    public Endpoint getLinkEndpointSource() {
        return super.getLinkEndpointSource();
    }

    @Override
    public void setLinkEndpointSource(Endpoint source) throws MappingDSException {

    }

    @Override
    public Endpoint getLinkEndpointTarget() {
        return super.getLinkEndpointTarget();
    }

    @Override
    public void setLinkEndpointTarget(Endpoint target) throws MappingDSException {

    }


}
