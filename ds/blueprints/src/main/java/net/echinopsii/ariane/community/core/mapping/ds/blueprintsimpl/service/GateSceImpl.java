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
import net.echinopsii.ariane.community.core.mapping.ds.service.GateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GateSceImpl implements GateSce<GateImpl> {

	final static String CREATE_GATE = "createGate";
	final static String DELETE_GATE = "deleteGate";
	final static String GET_GATE = "getGate";
	final static String GET_GATES = "getGates";

	private static final Logger log = LoggerFactory.getLogger(GateSceImpl.class);

	private MappingSceImpl sce = null;
	
	public GateSceImpl(MappingSceImpl sce_) {
		sce = sce_;
	}

	@Override
	public GateImpl createGate(Session session, String url, String name, Long containerid, Boolean isPrimaryAdmin) throws MappingDSException {
		GateImpl ret = null;
		if (session!=null && session.isRunning())
			ret= (GateImpl) session.execute(this, CREATE_GATE, new Object[]{url, name, containerid, isPrimaryAdmin});
		return ret;
	}

	@Override
	public GateImpl createGate(String url, String name, Long containerid, Boolean isPrimaryAdmin) throws MappingDSException {
		GateImpl ret = null;
		NodeImpl check = sce.getGlobalRepo().getGateRepo().findNodeByEndpointURL(url);
		if (check instanceof GateImpl)
			ret=(GateImpl)check;
		if (ret==null) {
			ContainerImpl container = sce.getGlobalRepo().getContainerRepo().findContainerByID(containerid);
			if (container != null) {
				EndpointImpl ep = sce.getGlobalRepo().getEndpointRepo().findEndpointByURL(url); 
				if ( ep == null) {
					ep = new EndpointImpl();
					ep.setEndpointURL(url);
					sce.getGlobalRepo().getEndpointRepo().save(ep);
				}

				ret = new GateImpl();
				ret.setNodeName(name);
				ret.setNodeContainer(container);
				if (isPrimaryAdmin)
					ret.setNodePrimaryAdminEnpoint(ep);				
				sce.getGlobalRepo().getGateRepo().save(ret);
				ret.addEndpoint(ep);
				ep.setEndpointParentNode(ret);
				container.addContainerGate(ret);

			} else {
                throw new MappingDSException("Gate creation failed : provided container " + containerid + " doesn't exists.");
			}				
		} else {
            log.debug("Gate ({}) creation failed : already exists", name);
		}
		return ret;
	}

	@Override
	public void deleteGate(Session session, Long nodeID) throws MappingDSException {
		if (session!=null && session.isRunning())
			session.execute(this, DELETE_GATE, new Object[]{nodeID});
	}

	@Override
	public void deleteGate(Long nodeID) throws MappingDSException {
		GateImpl remove = sce.getGlobalRepo().getGateRepo().findGateByID(nodeID);
		if ( remove != null ) {			
			sce.getGlobalRepo().getGateRepo().delete(remove);
		} else {
            throw new MappingDSException("Unable to remove gate with id " + nodeID + ": gate not found.");
		}		
	}

	@Override
	public GateImpl getGate(Session session, Long id) throws MappingDSException {
		GateImpl ret = null;
		if (session!=null && session.isRunning())
			ret = (GateImpl)session.execute(this, GET_GATE, new Object[]{id});
		return ret;
	}

	@Override
	public GateImpl getGate(Long id) {
		return sce.getGlobalRepo().getGateRepo().findGateByID(id);
	}

	@Override
	public Set<GateImpl> getGates(Session session, String selector) throws MappingDSException {
		Set<GateImpl> ret = null;
		if (session!=null && session.isRunning())
			ret = (Set<GateImpl>) session.execute(this, GET_GATES, new Object[]{selector});
		return ret;
	}

	@Override
    public Set<GateImpl> getGates(String selector) {
        // TODO : manage selector - check graphdb query
        return GateRepoImpl.getGateRepository();
    }
}