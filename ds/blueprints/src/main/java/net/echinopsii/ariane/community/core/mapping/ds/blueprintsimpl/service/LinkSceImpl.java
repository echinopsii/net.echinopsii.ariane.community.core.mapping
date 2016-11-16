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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.LinkImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxLinkSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.util.Set;

public class LinkSceImpl extends SProxLinkSceAbs<LinkImpl> {

    private static final Logger log = MomLoggerFactory.getLogger(LinkSceImpl.class);

    private MappingSceImpl sce = null;

    public LinkSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public LinkImpl createLink(String sourceEndpointID, String targetEndpointID,
                               String transportID) throws MappingDSException {
        LinkImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createLink(session, sourceEndpointID, targetEndpointID, transportID);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            EndpointImpl epsource = sce.getGlobalRepo().getEndpointRepo().findEndpointByID(sourceEndpointID);
            EndpointImpl eptarget = sce.getGlobalRepo().getEndpointRepo().findEndpointByID(targetEndpointID);
            TransportImpl transport = sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);

            if (epsource != null && eptarget != null && transport != null) {
                ret = sce.getGlobalRepo().findLinkBySourceEPandDestinationEP(epsource, eptarget);
                if (ret == null) {
                    ret = new LinkImpl();
                    ret.setLinkEndpointSource(epsource);
                    ret.setLinkEndpointTarget(eptarget);
                    ret.setLinkTransport(transport);
                    sce.getGlobalRepo().getLinkRepo().save(ret);
                    log.debug("Unicast link ({}) saved !", new Object[]{ret.toString()});
                } else {
                    log.debug("Unicast link ({},{},{}) creation failed : already exists", new Object[]{sourceEndpointID, targetEndpointID, transportID});
                }
            } else {
                if (epsource != null && eptarget == null && transport != null && transport.getTransportName().contains("multicast")) {
                    ret = sce.getGlobalRepo().findMulticastLinkBySourceEPandTransport(epsource, transport);
                    if (ret == null) {
                        ret = new LinkImpl();
                        ret.setLinkEndpointSource(epsource);
                        ret.setLinkEndpointTarget(eptarget);
                        ret.setLinkTransport(transport);
                        sce.getGlobalRepo().getLinkRepo().save(ret);
                        log.debug("Multicast link ({}) saved !", new Object[]{ret.toString()});
                    } else {
                        log.debug("Multicast link ({},{}) creation failed : already exists", new Object[]{sourceEndpointID, transportID});
                    }
                } else {
                    if (transport != null) {
                        if (transport.getTransportName().contains("multicast")) {
                            if (eptarget != null) {
                                throw new MappingDSException("Multicast link creation failed : provided target endpoint != 0");
                            } else {
                                throw new MappingDSException("Multicast link creation failed : provided source endpoint " + sourceEndpointID + " | transport " + transportID + "doesn't exists.");
                            }
                        } else {
                            throw new MappingDSException("Unicast link creation failed : provided source endpoint " + sourceEndpointID + " | target endpoint " + targetEndpointID + " | transport " + transportID + "doesn't exists.");
                        }
                    } else {
                        throw new MappingDSException("Multicast link creation failed : provided transport " + transportID + "doesn't exists.");
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public void deleteLink(String linkID) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) deleteLink(session, linkID);//
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            LinkImpl remove = sce.getGlobalRepo().getLinkRepo().findLinkByID(linkID);
            if (remove != null) {
                sce.getGlobalRepo().getLinkRepo().delete(remove);
            } else {
                throw new MappingDSException("Unable to remove link with id " + linkID + ": link not found.");
            }
        }
    }

    @Override
    public LinkImpl getLink(String id) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getLink(session, id);//
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getLinkRepo().findLinkByID(id);
    }

    @Override
    public Set<LinkImpl> getLinks(String selector) throws MappingDSException {
        // TODO : manage selector - check graphdb queries
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getLinks(session, selector);//
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return MappingDSGraphDB.getLinks(null, null, null);
    }
}