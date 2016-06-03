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

public interface SProxClusterSce<CL extends Cluster> extends ClusterSce {
    String MAPPING_CLUSTER_SERVICE_Q = "ARIANE_MAPPING_CLUSTER_SERVICE_Q";

    CL   createCluster(Session session, String clusterName) throws MappingDSException;

    void deleteCluster(Session session, String clusterName) throws MappingDSException;

    CL getCluster(Session session, String clusterID) throws MappingDSException;

    CL getClusterByName(Session session, String clusterName) throws MappingDSException;


    Set<CL> getClusters(Session session, String selector) throws MappingDSException;
}
