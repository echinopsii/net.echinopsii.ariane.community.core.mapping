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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxContainerSceAbs<C extends Container> implements SProxContainerSce {
    @Override
    public C createContainer(Session session, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, SProxContainerSce.CREATE_CONTAINER, new Object[]{primaryAdminURL, primaryAdminGateName});
        return ret;
    }

    @Override
    public C createContainer(Session session, String name, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, CREATE_CONTAINER, new Object[]{name, primaryAdminURL, primaryAdminGateName});
        return ret;
    }

    @Override
    public C createContainer(Session session, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, CREATE_CONTAINER, new Object[]{primaryAdminURL, primaryAdminGateName, parentContainer});
        return ret;
    }

    @Override
    public C createContainer(Session session, String name, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, CREATE_CONTAINER, new Object[]{name, primaryAdminURL, primaryAdminGateName, parentContainer});
        return ret;
    }

    @Override
    public void deleteContainer(Session session, String primaryAdminURL) throws MappingDSException {
        if (session != null && session.isRunning())
            session.execute(this, DELETE_CONTAINER, new Object[]{primaryAdminURL});
    }

    @Override
    public C getContainer(Session session, String id) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, GET_CONTAINER, new Object[]{id});
        return ret;
    }

    @Override
    public C getContainerByPrimaryAdminURL(Session session, String primaryAdminURL) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, GET_CONTAINER_BY_PAURL, new Object[]{primaryAdminURL});
        return ret;
    }

    @Override
    public Set<C> getContainers(Session session, String selector) throws MappingDSException {
        Set<C> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<C>) session.execute(this, GET_CONTAINERS, new Object[]{selector});
        return ret;
    }
}
