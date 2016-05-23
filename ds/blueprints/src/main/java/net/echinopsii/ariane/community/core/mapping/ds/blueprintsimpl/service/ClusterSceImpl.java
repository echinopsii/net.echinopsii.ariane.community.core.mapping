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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.ClusterImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository.ClusterRepoImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.service.ClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ClusterSceImpl implements ClusterSce<ClusterImpl> {

	final static String CREATE_CLUSTER = "createCluster";
	final static String DELETE_CLUSTER = "deleteCluster";
	final static String GET_CLUSTER = "getCluster";
	final static String GET_CLUSTER_BY_NAME = "getClusterByName";
	final static String GET_CLUSTERS = "getClusters";

	private static final Logger log = LoggerFactory.getLogger(ClusterSceImpl.class);

	private MappingSceImpl sce = null;
	
	public ClusterSceImpl(MappingSceImpl sce_) {
		sce = sce_;
	}

	@Override
	public ClusterImpl createCluster(Session session, String clusterName) throws MappingDSException {
		ClusterImpl ret = null;
		if (session != null && session.isRunning())
			ret = (ClusterImpl) session.execute(this, CREATE_CLUSTER, new Object[]{clusterName});
		return ret;
	}

	@Override
	public ClusterImpl createCluster(String clusterName) throws MappingDSException {
		ClusterImpl ret = null;
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) ret = createCluster(session, clusterName);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else {
			ret = sce.getGlobalRepo().getClusterRepo().findClusterByName(clusterName);
			if (ret == null) {
				ret = new ClusterImpl();
				ret.setClusterName(clusterName);
				sce.getGlobalRepo().getClusterRepo().save(ret);
			} else log.debug("Cluster with this name ({}) already exist.", new Object[]{clusterName});
		}
		return ret;		
	}

	@Override
	public void deleteCluster(Session session, String clusterName) throws MappingDSException {
		if (session!=null && session.isRunning())
			session.execute(this, DELETE_CLUSTER, new Object[]{clusterName});
	}

	@Override
	public void deleteCluster(String clusterName) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) deleteCluster(session, clusterName);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else {
			ClusterImpl remove = sce.getGlobalRepo().getClusterRepo().findClusterByName(clusterName);
			if (remove != null) {
				sce.getGlobalRepo().getClusterRepo().delete(remove);
			} else {
				throw new MappingDSException("Unable to remove cluster with name " + clusterName + ": cluster not found .");
			}
		}
	}

	@Override
	public ClusterImpl getCluster(Session session, String clusterID) throws MappingDSException {
		ClusterImpl ret = null;
		if (session != null && session.isRunning())
			ret = (ClusterImpl) session.execute(this, GET_CLUSTER, new Object[]{clusterID});
		return ret;
	}

	@Override
    public ClusterImpl getCluster(String clusterID) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getCluster(session, clusterID);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return sce.getGlobalRepo().getClusterRepo().findClusterByID(clusterID);
    }

	@Override
	public ClusterImpl getClusterByName(Session session, String clusterName) throws MappingDSException {
		ClusterImpl ret = null;
		if (session != null && session.isRunning())
			ret = (ClusterImpl) session.execute(this, GET_CLUSTER_BY_NAME, new Object[]{clusterName});
		return ret;
	}

	@Override
    public ClusterImpl getClusterByName(String clusterName) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getClusterByName(session, clusterName);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return sce.getGlobalRepo().getClusterRepo().findClusterByName(clusterName);
    }

	@Override
	public Set<ClusterImpl> getClusters(Session session, String selector) throws MappingDSException {
		Set<ClusterImpl> ret = null;
		if (session != null && session.isRunning())
			ret = (Set<ClusterImpl>)session.execute(this, GET_CLUSTERS, new Object[]{selector});
		return ret;
	}

	@Override
    public Set<ClusterImpl> getClusters(String selector) throws MappingDSException {
        //TODO : manage selector - check graphdb query
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getClusters(session, selector);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return ClusterRepoImpl.getRepository();
    }
}
