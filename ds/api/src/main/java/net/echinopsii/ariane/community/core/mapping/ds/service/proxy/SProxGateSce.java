/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
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

package net.echinopsii.ariane.community.core.mapping.ds.service.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.service.GateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface SProxGateSce<G extends Gate> extends GateSce {
	String CREATE_GATE = "createGate";
	String DELETE_GATE = "deleteGate";
	String GET_GATE = "getGate";
	String GET_GATES = "getGates";

	G    createGate(Session session, String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException;

	void deleteGate(Session session, String nodeID) throws MappingDSException;

	G    getGate(Session session, String id) throws MappingDSException;

	Set<G> getGates(Session session, String selector) throws MappingDSException;
}
