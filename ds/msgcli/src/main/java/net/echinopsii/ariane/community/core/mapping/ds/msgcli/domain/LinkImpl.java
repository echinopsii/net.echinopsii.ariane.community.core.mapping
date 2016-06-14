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
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLinkAbs;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.EndpointSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.LinkSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.TransportSceImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.LinkSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
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
                        try {
                            LinkJSON.JSONDeserializedLink jsonDeserializedLink = LinkJSON.JSON2Link(body);
                            if (link.getLinkID() == null || link.getLinkID().equals(jsonDeserializedLink.getLinkID()))
                                link.synchronizeFromJSON(jsonDeserializedLink);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        switch (rc) {
                            case MappingSce.MAPPING_SCE_RET_NOT_FOUND:
                                LinkImpl.log.debug("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                                break;
                            default:
                                LinkImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
                                break;
                        }
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

    public void synchronizeFromJSON(LinkJSON.JSONDeserializedLink jsonDeserializedLink) throws MappingDSException {
        super.setLinkID(jsonDeserializedLink.getLinkID());
        this.setTransportID(jsonDeserializedLink.getLinkTRPID());
        this.setSourceEndpointID(jsonDeserializedLink.getLinkSEPID());
        this.setTargetEndpointID(jsonDeserializedLink.getLinkTEPID());
    }

    @Override
    public Transport getLinkTransport() {
        try {
            Link update = LinkSceImpl.internalGetLink(super.getLinkID());
            this.setTransportID(((LinkImpl) update).getTransportID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (transportID!=null && (super.getLinkTransport()==null || !super.getLinkTransport().getTransportID().equals(transportID))) {
            try {
                super.setLinkTransport(TransportSceImpl.internalGetTransport(transportID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        }
        return super.getLinkTransport();
    }

    @Override
    public void setLinkTransport(Transport transport) throws MappingDSException {
        if (this.getLinkID()!=null) {
            if (transport!=null && transport.getTransportID()!=null) {
                if ((super.getLinkTransport()!=null && !super.getLinkTransport().equals(transport)) ||
                    (super.getLinkTransport() == null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_LINK_TRANSPORT);
                    message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, super.getLinkID());
                    message.put(Transport.TOKEN_TP_ID, transport.getTransportID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, linkReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Transport previousTransport = this.getLinkTransport();
                        super.setLinkTransport(transport);
                        transportID = transport.getTransportID();
                        if (previousTransport!=null) {
                            try {
                                if (retMsg.containsKey(Link.JOIN_PREVIOUS_TRP)) {
                                    TransportJSON.JSONDeserializedTransport jsonDeserializedTransport = TransportJSON.JSON2Transport(
                                            (String) retMsg.get(Link.JOIN_PREVIOUS_TRP)
                                    );
                                    ((TransportImpl) previousTransport).synchronizeFromJSON(jsonDeserializedTransport);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            if (retMsg.containsKey(Link.JOIN_CURRENT_TRP)) {
                                TransportJSON.JSONDeserializedTransport jsonDeserializedTransport = TransportJSON.JSON2Transport(
                                        (String) retMsg.get(Link.JOIN_CURRENT_TRP)
                                );
                                ((TransportImpl) transport).synchronizeFromJSON(jsonDeserializedTransport);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided transport is not initialized !");
        } else throw new MappingDSException("This link is not initialized !");
    }

    @Override
    public Endpoint getLinkEndpointSource() {
        try {
            Link update = LinkSceImpl.internalGetLink(super.getLinkID());
            this.setSourceEndpointID(((LinkImpl) update).getSourceEndpointID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (sourceEndpointID!=null && (super.getLinkEndpointSource()==null || !super.getLinkEndpointSource().getEndpointID().equals(sourceEndpointID))) {
            try {
                super.setLinkEndpointSource(EndpointSceImpl.internalGetEndpoint(sourceEndpointID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        }
        return super.getLinkEndpointSource();
    }

    @Override
    public void setLinkEndpointSource(Endpoint source) throws MappingDSException {
        if (this.getLinkID()!=null) {
            if (source!=null && source.getEndpointID()!=null) {
                if ((super.getLinkEndpointSource()!=null && !super.getLinkEndpointSource().equals(source)) ||
                    (super.getLinkEndpointSource() == null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_LINK_ENDPOINT_SOURCE);
                    message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, super.getLinkID());
                    message.put(LinkSce.PARAM_LINK_SEPID, source.getEndpointID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, linkReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Endpoint previousSource = super.getLinkEndpointSource();
                        super.setLinkEndpointSource(source);
                        sourceEndpointID = source.getEndpointID();
                        if (previousSource!=null) {
                            try {
                                if (retMsg.containsKey(Link.JOIN_PREVIOUS_SEP)) {
                                    EndpointJSON.JSONDeserializedEndpoint jsonDeserializedSource = EndpointJSON.JSON2Endpoint(
                                            (String) retMsg.get(Link.JOIN_PREVIOUS_SEP)
                                    );
                                    ((EndpointImpl) previousSource).synchronizeFromJSON(jsonDeserializedSource);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            if (retMsg.containsKey(Link.JOIN_CURRENT_SEP)) {
                                EndpointJSON.JSONDeserializedEndpoint jsonDeserializedSource = EndpointJSON.JSON2Endpoint(
                                        (String) retMsg.get(Link.JOIN_CURRENT_SEP)
                                );
                                ((EndpointImpl) source).synchronizeFromJSON(jsonDeserializedSource);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided endpoint is not initialized !");
        } else throw new MappingDSException("This link is not initialized !");
    }

    @Override
    public Endpoint getLinkEndpointTarget() {
        try {
            Link update = LinkSceImpl.internalGetLink(super.getLinkID());
            this.setTargetEndpointID(((LinkImpl) update).getTargetEndpointID());
        } catch (MappingDSException e) {
            e.printStackTrace();
        }

        if (targetEndpointID!=null && (super.getLinkEndpointTarget()==null || !super.getLinkEndpointTarget().getEndpointID().equals(targetEndpointID))) {
            try {
                super.setLinkEndpointTarget(EndpointSceImpl.internalGetEndpoint(targetEndpointID));
            } catch (MappingDSException e) {
                e.printStackTrace();
            }
        }
        return super.getLinkEndpointTarget();
    }

    @Override
    public void setLinkEndpointTarget(Endpoint target) throws MappingDSException {
        if (this.getLinkID()!=null) {
            if (target!=null && target.getEndpointID()!=null) {
                if ((super.getLinkEndpointTarget()!=null && !super.getLinkEndpointTarget().equals(target)) ||
                    (super.getLinkEndpointTarget() == null)) {
                    String clientThreadName = Thread.currentThread().getName();
                    String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
                    Map<String, Object> message = new HashMap<>();
                    message.put(MappingSce.GLOBAL_OPERATION_FDN, OP_SET_LINK_ENDPOINT_TARGET);
                    message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, super.getLinkID());
                    message.put(LinkSce.PARAM_LINK_TEPID, target.getEndpointID());
                    if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
                    Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, LinkSce.Q_MAPPING_LINK_SERVICE, linkReplyWorker);
                    if ((int) retMsg.get(MomMsgTranslator.MSG_RC) == 0) {
                        Endpoint previousTarget = super.getLinkEndpointTarget();
                        super.setLinkEndpointTarget(target);
                        targetEndpointID = target.getEndpointID();
                        if (previousTarget!=null) {
                            try {
                                if (retMsg.containsKey(Link.JOIN_PREVIOUS_TEP)) {
                                    EndpointJSON.JSONDeserializedEndpoint jsonDeserializedTarget = EndpointJSON.JSON2Endpoint(
                                            (String) retMsg.get(Link.JOIN_PREVIOUS_TEP)
                                    );
                                    ((EndpointImpl) previousTarget).synchronizeFromJSON(jsonDeserializedTarget);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            if (retMsg.containsKey(Link.JOIN_CURRENT_TEP)) {
                                EndpointJSON.JSONDeserializedEndpoint jsonDeserializedTarget = EndpointJSON.JSON2Endpoint(
                                        (String) retMsg.get(Link.JOIN_CURRENT_TEP)
                                );
                                ((EndpointImpl) target).synchronizeFromJSON(jsonDeserializedTarget);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else throw new MappingDSException("Ariane server raised an error... Check your logs !");
                }
            } else throw new MappingDSException("Provided endpoint is not initialized !");
        } else throw new MappingDSException("This link is not initialized !");
    }
}