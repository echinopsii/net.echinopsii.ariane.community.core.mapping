/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
 *
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
package net.echinopsii.ariane.community.core.mapping.ds.domain.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.LinkAbs;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public abstract class SProxLinkAbs extends LinkAbs implements SProxLink {
    static final String SET_LINK_TRANSPORT = "setLinkTransport";

    @Override
    public void setLinkTransport(Session session, Transport transport) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_LINK_TRANSPORT, new Object[]{transport});
    }

    static final String SET_LINK_ENDPOINT_SOURCE = "setLinkEndpointSource";

    @Override
    public void setLinkEndpointSource(Session session, Endpoint source) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_LINK_ENDPOINT_SOURCE, new Object[]{source});
    }

    static final String SET_LINK_ENDPOINT_TARGET = "setLinkEndpointTarget";

    @Override
    public void setLinkEndpointTarget(Session session, Endpoint target) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_LINK_ENDPOINT_TARGET, new Object[]{target});
    }


}