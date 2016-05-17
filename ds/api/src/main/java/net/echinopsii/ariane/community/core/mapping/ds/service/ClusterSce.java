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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface ClusterSce<CL extends Cluster> {
    public CL   createCluster(Session session, String clusterName) throws MappingDSException;
	public CL   createCluster(String clusterName);

    public void deleteCluster(Session session, String clusterName) throws MappingDSException;
	public void deleteCluster(String clusterName) throws MappingDSException;

    public CL getCluster(Session session, String clusterID) throws MappingDSException;
    public CL getCluster(String clusterID);
    public CL getClusterByName(Session session, String clusterName) throws MappingDSException;
    public CL getClusterByName(String clusterName);

    public Set<CL> getClusters(Session session, String selector) throws MappingDSException;
    public Set<CL> getClusters(String selector);
}
