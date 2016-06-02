/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
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

package net.echinopsii.ariane.community.core.mapping.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;

public interface Link {

	String getLinkID();
	void   setLinkID(String ID);

	Transport getLinkTransport();
	void      setLinkTransport(Transport transport) throws MappingDSException;
	
	Endpoint getLinkEndpointSource();
	void     setLinkEndpointSource(Endpoint source) throws MappingDSException;
	
	Endpoint getLinkEndpointTarget();
	void     setLinkEndpointTarget(Endpoint target) throws MappingDSException;
}