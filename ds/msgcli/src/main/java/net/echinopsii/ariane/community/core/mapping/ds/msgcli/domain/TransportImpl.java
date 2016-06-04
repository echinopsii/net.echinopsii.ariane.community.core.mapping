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
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransportAbs;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransportImpl extends SProxTransportAbs implements SProxTransport {

    class TransportReplyWorker implements AppMsgWorker {
        private TransportImpl transport;

        public TransportReplyWorker(TransportImpl transport) {
            this.transport = transport;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (transport!=null) {
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
                } else TransportImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(TransportImpl.class);

    private TransportReplyWorker transportReplyWorker = new TransportReplyWorker(this);

    public TransportReplyWorker getTransportReplyWorker() {
        return transportReplyWorker;
    }

    @Override
    public void setTransportName(String name) throws MappingDSException {

    }

    @Override
    public void addTransportProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeTransportProperty(String propertyKey) throws MappingDSException {

    }
}
