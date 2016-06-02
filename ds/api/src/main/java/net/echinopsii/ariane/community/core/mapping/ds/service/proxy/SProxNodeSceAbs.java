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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxNodeSceAbs<N extends Node> implements SProxNodeSce {
    @Override
    public N createNode(Session session, String nodeName, String containerID, String parentNodeID) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, CREATE_NODE, new Object[]{nodeName, containerID, parentNodeID});
        return ret;
    }

    @Override
    public void deleteNode(Session session, String nodeID) throws MappingDSException {
        if (session != null && session.isRunning())
            session.execute(this, DELETE_NODE, new Object[]{nodeID});
    }

    @Override
    public N getNode(Session session, String id) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, GET_NODE, new Object[]{id});
        return ret;
    }

    @Override
    public N getNodeByEndpointURL(Session session, String endpointURL) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, GET_NODE_BY_EPURL, new Object[]{endpointURL});
        return ret;
    }

    @Override
    public N getNodeByName(Session session, Node parentNode, String nodeName) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, GET_NODE_BY_NAME, new Object[]{parentNode, nodeName});
        return ret;
    }

    @Override
    public Set<N> getNodes(Session session, String key, Object value) throws MappingDSException {
        Set<N> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<N>) session.execute(this, GET_NODES, new Object[]{key, value});
        return ret;
    }

    @Override
    public Set<N> getNodes(Session session, String selector) throws MappingDSException {
        Set<N> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<N>) session.execute(this, GET_NODES, new Object[]{selector});
        return ret;
    }


}
