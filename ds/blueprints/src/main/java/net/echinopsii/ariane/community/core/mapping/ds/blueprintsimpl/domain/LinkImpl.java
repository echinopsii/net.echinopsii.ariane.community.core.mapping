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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLinkAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

public class LinkImpl extends SProxLinkAbs implements SProxLink, MappingDSBlueprintsCacheEntity {

    private static final Logger log = MomLoggerFactory.getLogger(NodeImpl.class);

    private transient Edge linkEdge = null;

    @Override
    public void setLinkTransport(Transport transport) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setLinkTransport(session, transport);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getLinkTransport() == null || !super.getLinkTransport().equals(transport)) {
                if (transport instanceof TransportImpl) {
                    super.setLinkTransport(transport);
                    synchronizeTransportNameToDB();
                }
            }
        }
    }

    @Override
    public void setLinkEndpointSource(Endpoint source) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setLinkEndpointSource(session, source);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getLinkEndpointSource() == null || !super.getLinkEndpointSource().equals(source)) {
                if (source instanceof EndpointImpl) {
                    super.setLinkEndpointSource(source);
                    synchronizeSourceEndpointToDB();
                }
            }
        }
    }

    @Override
    public void setLinkEndpointTarget(Endpoint target) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setLinkEndpointTarget(session, target);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getLinkEndpointTarget() == null || !super.getLinkEndpointTarget().equals(target)) {
                if (target instanceof EndpointImpl) {
                    super.setLinkEndpointTarget(target);
                    synchronizeTargetEndpointToDB();
                }
            }
        }
    }

/*
    @Override
    public Set<LinkImpl> getLinkSubLinks() {
        return this.linkSubLinks;
    }

    @Override
    public boolean addLinkSubLink(Link link) {
        if (link instanceof LinkImpl) {
            boolean ret = this.linkSubLinks.add((LinkImpl) link);
            if (ret) {
                synchronizeSubLinkToDB((LinkImpl) link);
            }
            return ret;
        } else {
            return false;
        }
    }

    @Override
    public LinkImpl getLinkUpLink() {
        return this.linkUpLink;
    }

    @Override
    public void setLinkUpLink(Link link) {
        if (this.linkUpLink == null || !this.linkUpLink.equals(link)) {
            if (link instanceof LinkImpl) {
                this.linkUpLink = (LinkImpl) link;
                synchronizeUpLinkToDB();
            }
        }
    }
*/
    @Override
    public Element getElement() {
        return this.linkEdge;
    }

    @Override
    public void setElement(Element edge) {
        this.linkEdge = (Edge) edge;
        super.setLinkID((String) this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID));
        log.debug("Link edge has been initialized ({}).", new Object[]{this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID)});
    }

    @Override
    public String getEntityCacheID() {
        return "E"+super.getLinkID();
    }

    @Override
    public void synchronizeToDB() {
        synchronizeSourceEndpointToDB();
        synchronizeTargetEndpointToDB();
        //synchronizeUpLinkToDB();
        //synchronizeSubLinksToDB();
        synchronizeTransportNameToDB();
    }

    private void synchronizeSourceEndpointToDB() {
        if (this.linkEdge != null && super.getLinkEndpointSource() != null) {
            this.linkEdge.setProperty(MappingDSGraphPropertyNames.DD_LINK_SOURCE_EP_KEY, super.getLinkEndpointSource().getEndpointID());
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeTargetEndpointToDB() {
        if (this.linkEdge != null && super.getLinkEndpointTarget() != null) {
            this.linkEdge.setProperty(MappingDSGraphPropertyNames.DD_LINK_TARGET_EP_KEY, super.getLinkEndpointTarget().getEndpointID());
            MappingDSGraphDB.autocommit();
        }
    }
/*
    private void synchronizeUpLinkToDB() {
        if (this.linkEdge != null && this.linkUpLink != null) {
            this.linkEdge.setProperty(MappingDSGraphPropertyNames.DD_LINK_UPLINK_KEY, this.linkUpLink.getLinkID());
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeSubLinksToDB() {
        for (LinkImpl subLink : this.linkSubLinks) {
            synchronizeSubLinkToDB(subLink);
        }
    }

    private void synchronizeSubLinkToDB(LinkImpl subLink) {
        if (this.linkEdge != null && subLink.getLinkID() != null) {
            this.linkEdge.setProperty(MappingDSGraphPropertyNames.DD_LINK_SUBLINKS_KEY + subLink.getLinkID(), subLink.getLinkID());
            MappingDSGraphDB.autocommit();
        }
    }
*/

    private void synchronizeTransportNameToDB() {
        if (this.linkEdge != null && super.getLinkTransport() != null) {
            this.linkEdge.setProperty(MappingDSGraphPropertyNames.DD_LINK_TRANSPORT_KEY, super.getLinkTransport().getTransportID());
            MappingDSGraphDB.autocommit();
        }
    }

    @Override
    public void synchronizeFromDB() throws MappingDSException {
        synchronizeIDFromDB();
        synchronizeSourceEndpointFromDB();
        synchronizeTargetEndpointFromDB();
        //synchronizeUpLinkFromDB();
        //synchronizeSubLinksFromDB();
        synchronizeTransportNameFromDB();
    }

    private void synchronizeIDFromDB() {
        if (this.linkEdge != null) {
            super.setLinkID((String) this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID));
        }
    }

    private void synchronizeSourceEndpointFromDB() throws MappingDSException {
        if (this.linkEdge != null) {
            Object endpointID = this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_LINK_SOURCE_EP_KEY);
            if (endpointID != null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) endpointID);
                if (entity != null) {
                    if (entity instanceof EndpointImpl) super.setLinkEndpointSource((Endpoint)entity);
                    else log.error("CACHE CONSISTENCY ERROR : entity {} is not an endpoint.", entity.getElement().getId());
                }
            }
        }
    }

    private void synchronizeTargetEndpointFromDB() throws MappingDSException {
        if (this.linkEdge != null) {
            Object endpointID = this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_LINK_TARGET_EP_KEY);
            if (endpointID != null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) endpointID);
                if (entity != null) {
                    if (entity instanceof EndpointImpl) super.setLinkEndpointTarget((EndpointImpl) entity);
                    else log.error("CACHE CONSISTENCY ERROR : entity {} is not an endpoint.", entity.getElement().getId());
                }
            }
        }
    }
/*
    private void synchronizeUpLinkFromDB() {
        if (this.linkEdge != null) {
            Object upLinkID = this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_LINK_UPLINK_KEY);
            if (upLinkID != null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getLink((String) upLinkID);
                if (entity != null) {
                    if (entity instanceof LinkImpl) {
                        linkUpLink = (LinkImpl) entity;
                    } else {
                        log.error("CACHE CONSISTENCY ERROR : entity {} is not a link.", entity.getElement().getId());
                    }
                }
            }
        }
    }

    private void synchronizeSubLinksFromDB() {
        if (this.linkEdge != null) {
            linkSubLinks.clear();
            for (String key : this.linkEdge.getPropertyKeys()) {
                if (key.contains(MappingDSGraphPropertyNames.DD_LINK_SUBLINKS_KEY)) {
                    String subLinkID = this.linkEdge.getProperty(key);
                    if (subLinkID != null) {
                        LinkImpl subLink = null;
                        MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getLink(subLinkID);
                        if (entity != null) {
                            if (entity instanceof LinkImpl) {
                                subLink = (LinkImpl) entity;
                            } else {
                                log.error("CACHE CONSISTENCY ERROR : entity {} is not a link.", entity.getElement().getId());
                            }
                        }
                        if (subLink != null) {
                            linkSubLinks.add(subLink);
                        }
                    }
                }
            }
        }
    }
*/
    private void synchronizeTransportNameFromDB() throws MappingDSException {
        if (this.linkEdge != null) {
            Object transportID = this.linkEdge.getProperty(MappingDSGraphPropertyNames.DD_LINK_TRANSPORT_KEY);
            if (transportID != null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) transportID);
                if (entity != null) {
                    if (entity instanceof TransportImpl) super.setLinkTransport((TransportImpl) entity);
                    else log.error("CACHE CONSISTENCY ERROR : entity {} is not a link.", entity.getElement().getId());
                }
            }
        }
    }
}