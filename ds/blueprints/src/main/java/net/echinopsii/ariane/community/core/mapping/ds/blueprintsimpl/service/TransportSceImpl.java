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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository.TransportRepoImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.TransportSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TransportSceImpl implements TransportSce<TransportImpl> {

    private static final Logger log = LoggerFactory.getLogger(LinkSceImpl.class);

	private MappingSceImpl sce = null;
	
	public TransportSceImpl(MappingSceImpl sce_) {
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
            log.debug("Transport ({}) creation failed: already exists", new Object[]{transportName});
		}
		return ret;
	}

	@Override
	public void deleteTransport(Long transportID) throws MappingDSException {
		TransportImpl remove = sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
		if (remove != null) {
			sce.getGlobalRepo().getTransportRepo().delete(remove);
		} else {
            throw new MappingDSException("Unable to remove transport with id " + transportID + ": transport not found.");
		}
	}

    @Override
    public TransportImpl getTransport(Long transportID) {
        return sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
    }

    @Override
    public Set<TransportImpl> getTransports(String selector) {
        // TODO : manage selector - check graphdb query
        return TransportRepoImpl.getTransportRepository();
    }
}
