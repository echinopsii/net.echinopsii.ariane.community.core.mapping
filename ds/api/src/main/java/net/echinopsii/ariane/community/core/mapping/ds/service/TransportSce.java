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

package net.echinopsii.ariane.community.core.mapping.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface TransportSce<T> {
    public T    createTransport(Session session, String transportName) throws MappingDSException;
	public T    createTransport(String transportName) throws MappingDSException;

    public void deleteTransport(Session session, String transportID) throws MappingDSException;
	public void deleteTransport(String transportID) throws MappingDSException;

    public T    getTransport(Session session, String transportID) throws MappingDSException;
    public T    getTransport(String transportID) throws MappingDSException;

    public Set<T> getTransports(Session session, String selector) throws MappingDSException;
    public Set<T> getTransports(String selector) throws MappingDSException;
}
