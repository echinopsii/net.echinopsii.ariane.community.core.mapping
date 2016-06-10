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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpointAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndpointImpl extends SProxEndpointAbs implements SProxEndpoint {

    class EndpointReplyWorker implements AppMsgWorker {
        private EndpointImpl endpoint ;

        public EndpointReplyWorker(EndpointImpl endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            if (endpoint!=null) {
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
                } else EndpointImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            }
            return message;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(EndpointImpl.class);

    private EndpointReplyWorker endpointReplyWorker = new EndpointReplyWorker(this);
    private String parentNodeID;
    private List<String> twinEndpointsID;

    public EndpointReplyWorker getEndpointReplyWorker() {
        return endpointReplyWorker;
    }

    public String getParentNodeID() {
        return parentNodeID;
    }

    public void setParentNodeID(String parentNodeID) {
        this.parentNodeID = parentNodeID;
    }

    public List<String> getTwinEndpointsID() {
        return twinEndpointsID;
    }

    public void setTwinEndpointsID(List<String> twinEndpointsID) {
        this.twinEndpointsID = twinEndpointsID;
    }

    public void synchronizeFromJSON(EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint) throws MappingDSException {

    }

    @Override
    public void setEndpointURL(String url) throws MappingDSException {

    }

    @Override
    public Node getEndpointParentNode() {
        return super.getEndpointParentNode();
    }

    @Override
    public void setEndpointParentNode(Node node) throws MappingDSException {

    }

    @Override
    public void addEndpointProperty(String propertyKey, Object value) throws MappingDSException {

    }

    @Override
    public void removeEndpointProperty(String propertyKey) throws MappingDSException {

    }

    @Override
    public Set<Endpoint> getTwinEndpoints() {
        return super.getTwinEndpoints();
    }

    @Override
    public boolean addTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeTwinEndpoint(Endpoint endpoint) throws MappingDSException {
        return false;
    }
}
