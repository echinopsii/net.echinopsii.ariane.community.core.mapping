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
import net.echinopsii.ariane.community.core.mapping.ds.domain.ClusterAbs;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public abstract class SProxClusterAbs extends ClusterAbs implements SProxCluster {

    @Override
    public void setClusterName(Session session, String name) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, CLUSTER_OP_SET_CLUSTER_NAME, new Object[]{name});
    }

    @Override
    public boolean addClusterContainer(Session session, Container container) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean)session.execute(this, CLUSTER_OP_ADD_CLUSTER_CONTAINER, new Object[]{container});
        return ret;
    }

    @Override
    public boolean removeClusterContainer(Session session, Container container) throws MappingDSException {
        boolean ret = false;
        if (session != null && session.isRunning())
            ret = (boolean) session.execute(this, CLUSTER_OP_REMOVE_CLUSTER_CONTAINER, new Object[]{container});
        return ret;
    }
}
