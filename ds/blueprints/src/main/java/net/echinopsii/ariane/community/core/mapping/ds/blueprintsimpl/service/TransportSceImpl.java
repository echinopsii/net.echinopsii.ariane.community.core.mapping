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
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxTransportSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TransportSceImpl extends SProxTransportSceAbs<TransportImpl> {

	private static final Logger log = LoggerFactory.getLogger(LinkSceImpl.class);

	private MappingSceImpl sce = null;
	
	public TransportSceImpl(MappingSceImpl sce_) {
		sce = sce_;
	}

	@Override
	public TransportImpl createTransport(String transportName) throws MappingDSException {
		TransportImpl ret = null ;
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) ret = createTransport(session, transportName);
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else {
			ret = sce.getGlobalRepo().getTransportRepo().findTransportByName(transportName);
			if (ret == null) {
				ret = new TransportImpl();
				ret.setTransportName(transportName);
				sce.getGlobalRepo().getTransportRepo().save(ret);
			} else {
				log.debug("Transport ({}) creation failed: already exists", new Object[]{transportName});
			}
		}
		return ret;
	}

	@Override
	public void deleteTransport(String transportID) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) deleteTransport(session, transportID);//
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else {
			TransportImpl remove = sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
			if (remove != null) {
				sce.getGlobalRepo().getTransportRepo().delete(remove);
			} else {
				throw new MappingDSException("Unable to remove transport with id " + transportID + ": transport not found.");
			}
		}
	}

	@Override
    public TransportImpl getTransport(String transportID) throws MappingDSException {
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getTransport(session, transportID);//
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return sce.getGlobalRepo().getTransportRepo().findTransportByID(transportID);
    }

	@Override
    public Set<TransportImpl> getTransports(String selector) throws MappingDSException {
        // TODO : manage selector - check graphdb query
		String clientThreadName = Thread.currentThread().getName();
		String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
		if (clientThreadSessionID!=null) {
			Session session = sce.getSessionRegistry().get(clientThreadSessionID);
			if (session!=null) return getTransports(session, selector);//
			else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
		} else return TransportRepoImpl.getTransportRepository();
    }
}
