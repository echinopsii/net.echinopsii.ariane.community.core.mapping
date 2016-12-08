/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.service.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.LinkJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.LinkSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxLinkSceAbs<L extends Link> implements SProxLinkSce {

    public static DeserializedPushResponse pushDeserializedLink(LinkJSON.JSONDeserializedLink jsonDeserializedLink,
                                                                Session mappingSession,
                                                                SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Endpoint reqSourceEndpoint=null;
        Endpoint reqTargetEndpoint=null;
        Transport reqTransport=null;

        if (jsonDeserializedLink.getLinkSEPID()!=null) {
            if (mappingSession!=null) reqSourceEndpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, jsonDeserializedLink.getLinkSEPID());
            else reqSourceEndpoint = mappingSce.getEndpointSce().getEndpoint(jsonDeserializedLink.getLinkSEPID());
            if (reqSourceEndpoint==null) ret.setErrorMessage("Request Error : source endpoint with provided ID " + jsonDeserializedLink.getLinkSEPID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedLink.getLinkTEPID()!=null) {
            if (mappingSession!=null) reqTargetEndpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, jsonDeserializedLink.getLinkTEPID());
            else reqTargetEndpoint = mappingSce.getEndpointSce().getEndpoint(jsonDeserializedLink.getLinkTEPID());
            if (reqTargetEndpoint==null) ret.setErrorMessage("Request Error : target endpoint with provided ID " + jsonDeserializedLink.getLinkTEPID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedLink.getLinkTRPID()!=null) {
            if (mappingSession!=null) reqTransport = mappingSce.getTransportSce().getTransport(mappingSession, jsonDeserializedLink.getLinkTRPID());
            else reqTransport = mappingSce.getTransportSce().getTransport(jsonDeserializedLink.getLinkTRPID());
            if (reqTransport == null) ret.setErrorMessage("Request Error : transport with provided ID " + jsonDeserializedLink.getLinkTRPID() + " was not found.");
        }

        // LOOK IF LINK MAYBE UPDATED OR CREATED
        Link deserializedLink = null;
        if (ret.getErrorMessage() == null && jsonDeserializedLink.getLinkID()!=null) {
            if (mappingSession!=null) deserializedLink = mappingSce.getLinkSce().getLink(mappingSession, jsonDeserializedLink.getLinkID());
            else deserializedLink = mappingSce.getLinkSce().getLink(jsonDeserializedLink.getLinkID());
            if (deserializedLink==null) ret.setErrorMessage("Request Error : link with provided ID " + jsonDeserializedLink.getLinkID() + " was not found.");
        }

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            if (deserializedLink==null) {
                if (mappingSession!=null) deserializedLink = mappingSce.getLinkSce().createLink(
                        mappingSession,
                        jsonDeserializedLink.getLinkSEPID(),
                        jsonDeserializedLink.getLinkTEPID(),
                        jsonDeserializedLink.getLinkTRPID());
                else deserializedLink = mappingSce.getLinkSce().createLink(
                        jsonDeserializedLink.getLinkSEPID(),
                        jsonDeserializedLink.getLinkTEPID(),
                        jsonDeserializedLink.getLinkTRPID());
            } else {
                if (reqSourceEndpoint!=null)
                    if (mappingSession!=null) ((SProxLink) deserializedLink).setLinkEndpointSource(mappingSession, reqSourceEndpoint);
                    else deserializedLink.setLinkEndpointSource(reqSourceEndpoint);
                if (mappingSession!=null) ((SProxLink)deserializedLink).setLinkEndpointTarget(mappingSession, reqTargetEndpoint);
                else deserializedLink.setLinkEndpointTarget(reqTargetEndpoint);
                if (reqTransport!=null)
                    if (mappingSession!=null) ((SProxLink)deserializedLink).setLinkTransport(mappingSession, reqTransport);
                    else deserializedLink.setLinkTransport(reqTransport);
            }
            ret.setDeserializedObject(deserializedLink);
        }

        return ret;
    }

    @Override
    public L createLink(Session session, String sourceEndpointID, String targetEndpointID, String transportID) throws MappingDSException {
        L ret = null;
        if (session!=null && session.isRunning())
            ret = (L) session.execute(this, LinkSce.OP_CREATE_LINK, new Object[]{sourceEndpointID, targetEndpointID, transportID});
        return ret;
    }

    @Override
    public void deleteLink(Session session, String linkID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, LinkSce.OP_DELETE_LINK, new Object[]{linkID});
    }

    @Override
    public L getLink(Session session, String id) throws MappingDSException {
        L ret = null;
        if (session!=null && session.isRunning())
            ret = (L) session.execute(this, LinkSce.OP_GET_LINK, new Object[]{id});
        return ret;
    }

    @Override
    public Set<L> getLinks(Session session, String selector) throws MappingDSException {
        Set<L> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<L>) session.execute(this, LinkSce.OP_GET_LINKS, new Object[]{selector});
        return ret;
    }
}
