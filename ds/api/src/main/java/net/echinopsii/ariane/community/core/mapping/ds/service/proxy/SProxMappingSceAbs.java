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
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Set;

public abstract class SProxMappingSceAbs<S extends Session, SR extends SessionRegistry> implements SProxMappingSce {
    private SR sessionRegistry;

    @Override
    public SR getSessionRegistry() {
        return sessionRegistry;
    }

    public void setSessionRegistry(SR sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Node getNodeByName(Session session, Container container, String nodeName) throws MappingDSException {
        Node ret = null;
        if (session!=null && session.isRunning())
            ret = (Node)session.execute(this, OP_GET_NODE_BY_NAME, new Object[]{container, nodeName});
        return ret;
    }

    @Override
    public Gate getGateByName(Session session, Container container, String nodeName) throws MappingDSException {
        Gate ret = null;
        if (session!=null && session.isRunning())
            ret = (Gate)session.execute(this, OP_GET_GATE_BY_NAME, new Object[]{container, nodeName});
        return ret;
    }

    @Override
    public Set<Link> getLinksBySourceEP(Session session, Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<Link>)session.execute(this, OP_GET_LINKS_BY_SOURCE_EP, new Object[]{endpoint});
        return ret;
    }

    @Override
    public Set<Link> getLinksByDestinationEP(Session session, Endpoint endpoint) throws MappingDSException {
        Set<Link> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<Link>)session.execute(this, OP_GET_LINKS_BY_DESTINATION_EP, new Object[]{endpoint});
        return ret;
    }

    @Override
    public Link getLinkBySourceEPandDestinationEP(Session session, Endpoint esource, Endpoint edest) throws MappingDSException {
        Link ret = null;
        if (session!=null && session.isRunning())
            ret = (Link)session.execute(this, OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP, new Object[]{esource, edest});
        return ret;
    }

    @Override
    public Link getMulticastLinkBySourceEPAndTransport(Session session, Endpoint esource, Transport transport) throws MappingDSException {
        Link ret = null;
        if (session!=null && session.isRunning())
            ret = (Link)session.execute(this, OP_GET_LINK_BY_SOURCE_EP_AND_TRANSPORT, new Object[]{esource, transport});
        return ret;
    }
}
