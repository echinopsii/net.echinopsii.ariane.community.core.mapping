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

import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.GateImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.repository.GateRepoImpl;
import com.spectral.cc.core.mapping.ds.service.GateSce;

import java.util.Set;

public class GateSceImpl implements GateSce<GateImpl> {
	
	private MappingSceImpl sce = null;
	
	public GateSceImpl(MappingSceImpl sce_) {
		sce = sce_;
	}
	
	@Override
	public GateImpl createGate(String url, String name, long containerid, boolean isPrimaryAdmin) {
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
				ret.addEnpoint(ep);
				ep.setEndpointParentNode(ret);
				container.addContainerGate(ret);
			} else {
				// TODO: raise exception !!!
			}				
		} else {
			// TODO: log error : gate already exists
		}
		return ret;
	}

	@Override
	public void deleteGate(long nodeID) {
		GateImpl remove = sce.getGlobalRepo().getGateRepo().findGateByID(nodeID);
		if ( remove != null ) {			
			sce.getGlobalRepo().getGateRepo().delete(remove);
		} else {
			// TODO: raise exception
		}		
	}

	@Override
	public GateImpl getGate(long id) {
		return sce.getGlobalRepo().getGateRepo().findGateByID(id);
	}

    @Override
    public Set<GateImpl> getGates(String selector) {
        // TODO : manage selector - check graphdb query
        return GateRepoImpl.getGateRepository();
    }
}