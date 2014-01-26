/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.ClusterImpl;
import com.spectral.cc.core.mapping.ds.service.ClusterSce;

public class ClusterSceImpl implements ClusterSce<ClusterImpl> {

	private MappingSceImpl sce = null;
	
	public ClusterSceImpl(MappingSceImpl sce_) {
		sce = sce_;
	}
	
	@Override
	public ClusterImpl createCluster(String clusterName) {
		ClusterImpl ret = sce.getGlobalRepo().getClusterRepo().findClusterByName(clusterName);
		if (ret==null) {
			ret = new ClusterImpl();
			ret.setClusterName(clusterName);
			sce.getGlobalRepo().getClusterRepo().save(ret);
		} else {
			//TODO : raise exception
		}
		return ret;		
	}

	@Override
	public void removeCluster(String clusterName) {
		ClusterImpl remove = sce.getGlobalRepo().getClusterRepo().findClusterByName(clusterName);
		if (remove!=null) {
			sce.getGlobalRepo().getClusterRepo().delete(remove);
		} else {
			//TODO : raise exception
		}
	}
}
