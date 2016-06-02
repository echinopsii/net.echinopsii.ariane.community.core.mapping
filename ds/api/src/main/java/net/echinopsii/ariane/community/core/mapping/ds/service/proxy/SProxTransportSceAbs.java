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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxTransportSceAbs<T extends Transport> implements SProxTransportSce {
    @Override
    public T createTransport(Session session, String transportName) throws MappingDSException {
        T ret = null;
        if (session!=null && session.isRunning())
            ret = (T)session.execute(this, CREATE_TRANSPORT, new Object[]{transportName});
        return ret;
    }

    @Override
    public void deleteTransport(Session session, String transportID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, DELETE_TRANSPORT, new Object[]{transportID});
    }

    @Override
    public T getTransport(Session session, String transportID) throws MappingDSException {
        T ret = null;
        if (session!=null && session.isRunning())
            ret = (T) session.execute(this, GET_TRANSPORT, new Object[]{transportID});
        return ret;
    }

    @Override
    public Set<T> getTransports(Session session, String selector) throws MappingDSException {
        Set<T> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<T>) session.execute(this, GET_TRANSPORTS, new Object[]{selector});
        return ret;
    }
}
