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

import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.repository.TransportRepoImpl;
import com.spectral.cc.core.mapping.ds.service.TransportSce;

import java.util.Set;

public class TransportSceImpl implements TransportSce<TransportImpl> {

	private TopoSceImpl sce = null;
	
	public TransportSceImpl(TopoSceImpl sce_) {
		sce = sce_;
	}
	
	@Override
	public TransportImpl createTransport(String transportName) {
		TransportImpl ret = sce.getGlobalRepo().getTransportRepo().findTransportByName(transportName);
		if (ret == null) {
			ret = new TransportImpl();
			ret.setTransportName(transportName);
			sce.getGlobalRepo().getTransportRepo().save(ret);
		} else {
			//TODO: log debug
		}
		return ret;
	}

	@Override
	public void deleteTransport(long transportID) {
		TransportImpl remove = sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
		if (remove != null) {
			sce.getGlobalRepo().getTransportRepo().delete(remove);
		} else { 
			//TODO: log info/warn (?)
		}
	}

    @Override
    public TransportImpl getTransport(long transportID) {
        return sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
    }

    @Override
    public Set<TransportImpl> getTransports(String selector) {
        // TODO : manage selector - check graphdb query
        return TransportRepoImpl.getTransportRepository();
    }
}
