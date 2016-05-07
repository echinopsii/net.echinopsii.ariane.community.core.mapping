/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2013  Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.mapping.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;

import java.util.Set;

public interface ClusterSce<CL extends Cluster> {
    public final static String CREATE_CLUSTER = "createCluster";
	public CL   createCluster(String clusterName);

    public final static String DELETE_CLUSTER = "deleteCluster";
	public void deleteCluster(String clusterName) throws MappingDSException;

    public final static String GET_CLUSTER = "getCluster";
    public CL getCluster(Long clusterID);
    public CL getCluster(String clusterName);

    public final static String GET_CLUSTERS = "getClusters";
    public Set<CL> getClusters(String selector);
}
