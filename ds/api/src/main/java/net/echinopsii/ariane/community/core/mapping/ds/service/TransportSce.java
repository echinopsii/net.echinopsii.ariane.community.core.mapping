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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface TransportSce<T extends Transport> {
    String Q_MAPPING_TRANSPORT_SERVICE = "ARIANE_MAPPING_TRANSPORT_SERVICE_Q";

    String OP_CREATE_TRANSPORT = "createTransport";
    String OP_DELETE_TRANSPORT = "deleteTransport";
    String OP_GET_TRANSPORT = "getTransport";
    String OP_GET_TRANSPORTS = "getTransports";

    String PARAM_TRANSPORT_NAME = "name";

	T    createTransport(String transportName) throws MappingDSException;

	void deleteTransport(String transportID) throws MappingDSException;

    T    getTransport(String transportID) throws MappingDSException;

    Set<T> getTransports(String selector) throws MappingDSException;
}
