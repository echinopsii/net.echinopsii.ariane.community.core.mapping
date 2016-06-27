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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxLinkSceAbs<L extends Link> implements SProxLinkSce {
    @Override
    public L createLink(Session session, String sourceEndpointID, String targetEndpointID, String transportID) throws MappingDSException {
        L ret = null;
        if (session!=null && session.isRunning())
            ret = (L) session.execute(this, CREATE_LINK, new Object[]{sourceEndpointID, targetEndpointID, transportID});
        return ret;
    }

    @Override
    public void deleteLink(Session session, String linkID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, DELETE_LINK, new Object[]{linkID});
    }

    @Override
    public L getLink(Session session, String id) throws MappingDSException {
        L ret = null;
        if (session!=null && session.isRunning())
            ret = (L) session.execute(this, GET_LINK, new Object[]{id});
        return ret;
    }

    @Override
    public Set<L> getLinks(Session session, String selector) throws MappingDSException {
        Set<L> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<L>) session.execute(this, GET_LINKS, new Object[]{selector});
        return ret;
    }
}
