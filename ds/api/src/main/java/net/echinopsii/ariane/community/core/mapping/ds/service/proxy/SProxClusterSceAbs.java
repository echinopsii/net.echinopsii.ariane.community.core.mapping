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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.service.ClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public abstract class SProxClusterSceAbs<CL extends Cluster> implements SProxClusterSce {
    @Override
    public CL createCluster(Session session, String clusterName) throws MappingDSException {
        CL ret = null;
        if (session != null && session.isRunning())
            ret = (CL) session.execute(this, ClusterSce.OP_CREATE_CLUSTER, new Object[]{clusterName});
        return ret;
    }

    @Override
    public void deleteCluster(Session session, String clusterName) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ClusterSce.OP_DELETE_CLUSTER, new Object[]{clusterName});
    }

    @Override
    public CL getCluster(Session session, String clusterID) throws MappingDSException {
        CL ret = null;
        if (session != null && session.isRunning())
            ret = (CL) session.execute(this, ClusterSce.OP_GET_CLUSTER, new Object[]{clusterID});
        return ret;
    }

    @Override
    public CL getClusterByName(Session session, String clusterName) throws MappingDSException {
        CL ret = null;
        if (session != null && session.isRunning())
            ret = (CL) session.execute(this, ClusterSce.OP_GET_CLUSTER_BY_NAME, new Object[]{clusterName});
        return ret;
    }

    @Override
    public Set<CL> getClusters(Session session, String selector) throws MappingDSException {
        Set<CL> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<CL>)session.execute(this, ClusterSce.OP_GET_CLUSTERS, new Object[]{selector});
        return ret;
    }
}
