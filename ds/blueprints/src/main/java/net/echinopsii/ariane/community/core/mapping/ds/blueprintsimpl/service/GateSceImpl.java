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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.GateImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository.GateRepoImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxGateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.util.Set;

public class GateSceImpl implements SProxGateSce<GateImpl> {

	private static final Logger log = MomLoggerFactory.getLogger(GateSceImpl.class);

	private MappingSceImpl sce = null;
	
	public GateSceImpl(MappingSceImpl sce_) {
		sce = sce_;
	}

	@Override
	public GateImpl createGate(Session session, String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException {
		GateImpl ret = null;
		if (session!=null && session.isRunning())
			ret= (GateImpl) session.execute(this, CREATE_GATE, new Object[]{url, name, containerid, isPrimaryAdmin});
		return ret;
	}

	@Override
	public GateImpl createGate(String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException {
		GateImpl ret = null;
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) ret = createGate(session, url, name, containerid, isPrimaryAdmin);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else {
			NodeImpl check = sce.getGlobalRepo().getGateRepo().findNodeByEndpointURL(url);
			if (check instanceof GateImpl)
				ret = (GateImpl) check;
			if (ret == null) {
				ContainerImpl container = sce.getGlobalRepo().getContainerRepo().findContainerByID(containerid);
				if (container != null) {
					EndpointImpl ep = sce.getGlobalRepo().getEndpointRepo().findEndpointByURL(url);
					if (ep == null) {
						ep = new EndpointImpl();
						ep.setEndpointURL(url);
						sce.getGlobalRepo().getEndpointRepo().save(ep);
					}

					ret = new GateImpl();
					sce.getGlobalRepo().getGateRepo().save(ret);
					ret.setNodeName(name);
					ret.setNodeContainer(container);
					if (isPrimaryAdmin)
						ret.setNodePrimaryAdminEnpoint(ep);
					ret.addEndpoint(ep);
					ep.setEndpointParentNode(ret);
					container.addContainerGate(ret);
					if (isPrimaryAdmin)
						container.setContainerPrimaryAdminGate(ret);
				} else {
					throw new MappingDSException("Gate creation failed : provided container " + containerid + " doesn't exists.");
				}
			}
		}
		return ret;
	}

	@Override
	public void deleteGate(Session session, String nodeID) throws MappingDSException {
		if (session!=null && session.isRunning())
			session.execute(this, DELETE_GATE, new Object[]{nodeID});
	}

	@Override
	public void deleteGate(String nodeID) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) deleteGate(session, nodeID);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else {
			GateImpl remove = sce.getGlobalRepo().getGateRepo().findGateByID(nodeID);
			if (remove != null) {
				sce.getGlobalRepo().getGateRepo().delete(remove);
			} else {
				throw new MappingDSException("Unable to remove gate with id " + nodeID + ": gate not found.");
			}
		}
	}

	@Override
	public GateImpl getGate(Session session, String id) throws MappingDSException {
		GateImpl ret = null;
		if (session!=null && session.isRunning())
			ret = (GateImpl)session.execute(this, GET_GATE, new Object[]{id});
		return ret;
	}

	@Override
	public GateImpl getGate(String id) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getGate(session, id);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return sce.getGlobalRepo().getGateRepo().findGateByID(id);
	}

	@Override
	public Set<GateImpl> getGates(Session session, String selector) throws MappingDSException {
		Set<GateImpl> ret = null;
		if (session!=null && session.isRunning())
			ret = (Set<GateImpl>) session.execute(this, GET_GATES, new Object[]{selector});
		return ret;
	}

	@Override
    public Set<GateImpl> getGates(String selector) throws MappingDSException {
        // TODO : manage selector - check graphdb query
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getGates(session, selector);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return GateRepoImpl.getGateRepository();
    }
}