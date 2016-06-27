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
import net.echinopsii.ariane.community.core.mapping.ds.domain.TransportAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public abstract class SProxTransportAbs extends TransportAbs implements SProxTransport{

    @Override
    public void setTransportName(Session session, String name) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, OP_SET_TRANSPORT_NAME, new Object[]{name});
    }

    @Override
    public void addTransportProperty(Session session, String propertyKey, Object value) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, OP_ADD_TRANSPORT_PROPERTY, new Object[]{propertyKey, value});
    }

    @Override
    public void removeTransportProperty(Session session, String propertyKey) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, OP_REMOVE_TRANSPORT_PROPERTY, new Object[]{propertyKey});
    }


}
