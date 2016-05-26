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
import net.echinopsii.ariane.community.core.mapping.ds.domain.EndpointAbs;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public abstract class SProxEndpointAbs extends EndpointAbs implements SProxEndpoint {
    static final String SET_ENDPOINT_URL = "setEndpointURL";

    @Override
    public void setEndpointURL(Session session, String url) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_ENDPOINT_URL, new Object[]{url});
    }

    static final String SET_ENDPOINT_PARENT_NODE = "setEndpointParentNode";

    @Override
    public void setEndpointParentNode(Session session, Node node) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_ENDPOINT_PARENT_NODE, new Object[]{node});
    }

    static final String ADD_ENDPOINT_PROPERTY = "addEndpointProperty";

    @Override
    public void addEndpointProperty(Session session, String propertyKey, Object value) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ADD_ENDPOINT_PROPERTY, new Object[]{propertyKey, value});
    }

    static final String REMOVE_ENDPOINT_PROPERTY = "removeEndpointProperty";

    @Override
    public void removeEndpointProperty(Session session, String propertyKey) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, REMOVE_ENDPOINT_PROPERTY, new Object[]{propertyKey});
    }

    static final String ADD_TWIN_ENDPOINT = "addTwinEndpoint";

    @Override
    public boolean addTwinEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_TWIN_ENDPOINT, new Object[]{endpoint});
        return ret;
    }

    static final String REMOVE_TWIN_ENDPOINT = "removeTwinEndpoint";

    @Override
    public boolean removeTwinEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_TWIN_ENDPOINT, new Object[]{endpoint});
        return ret;
    }


}
