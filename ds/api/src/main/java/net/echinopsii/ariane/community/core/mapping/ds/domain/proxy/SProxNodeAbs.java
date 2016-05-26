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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.NodeAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public abstract class SProxNodeAbs extends NodeAbs implements SProxNode {
    static final String SET_NODE_NAME = "setNodeName";

    @Override
    public void setNodeName(Session session, String name) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_NODE_NAME, new Object[]{name});
    }

    static final String SET_NODE_CONTAINER = "setNodeContainer";

    @Override
    public void setNodeContainer(Session session, Container container) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_NODE_CONTAINER, new Object[]{container});
    }

    static final String ADD_NODE_PROPERTY = "addNodeProperty";

    @Override
    public void addNodeProperty(Session session, String propertyKey, Object value) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ADD_NODE_PROPERTY, new Object[]{propertyKey, value});
    }

    static final String REMOVE_NODE_PROPERTY = "removeNodeProperty";

    @Override
    public void removeNodeProperty(Session session, String propertyKey) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, REMOVE_NODE_PROPERTY, new Object[]{propertyKey});
    }

    static final String SET_NODE_PARENT_NODE = "setNodeParentNode";

    @Override
    public void setNodeParentNode(Session session, Node node) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_NODE_PARENT_NODE, new Object[]{node});
    }

    static final String ADD_NODE_CHILD_NODE = "addNodeChildNode";

    @Override
    public boolean addNodeChildNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_NODE_CHILD_NODE, new Object[]{node});
        return ret;
    }

    static final String REMOVE_NODE_CHILD_NODE = "removeNodeChildNode";

    @Override
    public boolean removeNodeChildNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_NODE_CHILD_NODE, new Object[]{node});
        return ret;
    }

    static final String ADD_TWIN_NODE = "addTwinNode";

    @Override
    public boolean addTwinNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_TWIN_NODE, new Object[]{node});
        return ret;
    }

    static final String REMOVE_TWIN_NODE = "removeTwinNode";

    @Override
    public boolean removeTwinNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_TWIN_NODE, new Object[]{node});
        return ret;
    }

    static final String ADD_ENDPOINT = "addEndpoint";

    @Override
    public boolean addEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_ENDPOINT, new Object[]{endpoint});
        return ret;
    }

    static final String REMOVE_ENDPOINT = "removeEndpoint";

    @Override
    public boolean removeEndpoint(Session session, Endpoint endpoint) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_ENDPOINT, new Object[]{endpoint});
        return ret;
    }


}
