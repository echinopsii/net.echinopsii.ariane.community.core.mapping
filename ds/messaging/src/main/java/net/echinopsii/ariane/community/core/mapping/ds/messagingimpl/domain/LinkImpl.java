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
package net.echinopsii.ariane.community.core.mapping.ds.messagingimpl.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxLink;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public class LinkImpl implements SProxLink {
    @Override
    public void setLinkTransport(Session session, Transport transport) throws MappingDSException {

    }

    @Override
    public void setLinkEndpointSource(Session session, Endpoint source) throws MappingDSException {

    }

    @Override
    public void setLinkEndpointTarget(Session session, Endpoint target) throws MappingDSException {

    }

    @Override
    public String getLinkID() {
        return null;
    }

    @Override
    public Transport getLinkTransport() {
        return null;
    }

    @Override
    public void setLinkTransport(Transport transport) throws MappingDSException {

    }

    @Override
    public Endpoint getLinkEndpointSource() {
        return null;
    }

    @Override
    public void setLinkEndpointSource(Endpoint source) throws MappingDSException {

    }

    @Override
    public Endpoint getLinkEndpointTarget() {
        return null;
    }

    @Override
    public void setLinkEndpointTarget(Endpoint target) throws MappingDSException {

    }
}
