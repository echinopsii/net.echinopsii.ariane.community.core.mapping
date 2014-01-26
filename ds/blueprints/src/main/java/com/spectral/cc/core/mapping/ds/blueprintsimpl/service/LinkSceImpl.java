/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
 * Copyright (C) 2013  Mathilde Ffrench
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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.LinkImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import com.spectral.cc.core.mapping.ds.service.LinkSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class LinkSceImpl implements LinkSce<LinkImpl> {

    private static final Logger log = LoggerFactory.getLogger(LinkSceImpl.class);

    private MappingSceImpl sce = null;

    public LinkSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public LinkImpl createLink(long sourceEndpointID, long targetEndpointID,
                               long transportID, long upLinkID) {
        LinkImpl ret = null;

        EndpointImpl epsource = sce.getGlobalRepo().getEndpointRepo().findEndpointByID(sourceEndpointID);
        EndpointImpl eptarget = sce.getGlobalRepo().getEndpointRepo().findEndpointByID(targetEndpointID);
        TransportImpl transport = sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
        LinkImpl upLink = sce.getGlobalRepo().getLinkRepo().findLinkByID(upLinkID);

        if (epsource != null && eptarget != null) {
            ret = sce.getGlobalRepo().findLinkBySourceEPandDestinationEP(epsource, eptarget);
            if (ret == null) {
                ret = new LinkImpl();
                ret.setLinkEndpointSource(epsource);
                ret.setLinkEndpointTarget(eptarget);
                ret.setLinkTransport(transport);
                ret.setLinkUpLink(upLink);
                sce.getGlobalRepo().getLinkRepo().save(ret);
                log.debug("Unicast link ({}) saved !", new Object[]{ret.toString()});
            } else {
                //TODO : log warn
            }
        } else {
            log.debug("transport name: " + transport.getTransportName());
            if (epsource != null && eptarget == null && transport.getTransportName().contains("multicast")) {
                ret = sce.getGlobalRepo().findMulticastLinkBySourceEPandTransport(epsource, transport);
                if (ret == null) {
                    ret = new LinkImpl();
                    ret.setLinkEndpointSource(epsource);
                    ret.setLinkEndpointTarget(eptarget);
                    ret.setLinkTransport(transport);
                    ret.setLinkUpLink(upLink);
                    sce.getGlobalRepo().getLinkRepo().save(ret);
                    log.debug("Multicast link ({}) saved !", new Object[]{ret.toString()});
                }
            } else {
                //TODO : log error
            }
        }

        return ret;
    }

    @Override
    public void deleteLink(long linkID) {
        LinkImpl remove = sce.getGlobalRepo().getLinkRepo().findLinkByID(linkID);
        if (remove != null) {
            sce.getGlobalRepo().getLinkRepo().delete(remove);
        } else {
            // TODO: raise exception
        }
    }

    @Override
    public LinkImpl getLink(long id) {
        return sce.getGlobalRepo().getLinkRepo().findLinkByID(id);
    }

    @Override
    public Set<LinkImpl> getLinks(String selector) {
        // TODO : manage selector - check graphdb queries
        return MappingDSGraphDB.getLinks();
    }
}