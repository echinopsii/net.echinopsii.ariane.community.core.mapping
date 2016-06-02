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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxEndpointSceAbs<E extends Endpoint> implements SProxEndpointSce {
    @Override
    public E createEndpoint(Session session, String url, String parentNodeID) throws MappingDSException {
        E ret = null;
        if (session!=null && session.isRunning())
            ret = (E) session.execute(this, CREATE_ENDPOINT, new Object[]{url, parentNodeID});
        return ret;
    }

    @Override
    public void deleteEndpoint(Session session, String endpointID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, DELETE_ENDPOINT, new Object[]{endpointID});
    }

    @Override
    public E getEndpoint(Session session, String id) throws MappingDSException {
        E ret = null;
        if (session!=null && session.isRunning())
            ret = (E) session.execute(this, GET_ENDPOINT, new Object[]{id});
        return ret;
    }

    @Override
    public E getEndpointByURL(Session session, String URL) throws MappingDSException {
        E ret = null;
        if (session!=null && session.isRunning())
            ret = (E) session.execute(this, GET_ENDPOINT_BY_URL, new Object[]{URL});
        return ret;
    }

    @Override
    public Set<E> getEndpoints(Session session, String selector) throws MappingDSException {
        Set<E> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<E>) session.execute(this, GET_ENDPOINTS, new Object[]{selector});
        return ret;
    }

    @Override
    public Set<E> getEndpoints(Session session, String key, Object value) throws MappingDSException {
        Set<E> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<E>) session.execute(this, GET_ENDPOINTS, new Object[]{key, value});
        return ret;
    }
}
