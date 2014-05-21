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

package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.ClusterImpl;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.repository.ClusterRepoImpl;
import net.echinopsii.ariane.core.mapping.ds.service.ClusterSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ClusterSceImpl implements ClusterSce<ClusterImpl> {

    private static final Logger log = LoggerFactory.getLogger(ClusterSceImpl.class);

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
            log.debug("Cluster with this name ({}) already exist.", new Object[]{clusterName});
		}
		return ret;		
	}

	@Override
	public void deleteCluster(String clusterName) throws MappingDSException {
		ClusterImpl remove = sce.getGlobalRepo().getClusterRepo().findClusterByName(clusterName);
		if (remove!=null) {
			sce.getGlobalRepo().getClusterRepo().delete(remove);
		} else {
            throw new MappingDSException("Unable to remove cluster with name " + clusterName + ": cluster not found .");
		}
	}

    @Override
    public ClusterImpl getCluster(long clusterID) {
        return sce.getGlobalRepo().getClusterRepo().findClusteByID(clusterID);
    }

    @Override
    public Set<ClusterImpl> getClusters(String selector) {
        //TODO : manage selector - check graphdb query
        return ClusterRepoImpl.getRepository();
    }
}
