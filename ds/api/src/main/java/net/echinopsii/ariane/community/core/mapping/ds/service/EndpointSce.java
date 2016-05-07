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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;

import java.util.Set;

public interface EndpointSce<E extends Endpoint> {
    public final static String CREATE_ENDPOINT = "createEndpoint";
    public E createEndpoint(String url, Long parentNodeID) throws MappingDSException;

    public final static String DELETE_ENDPOINT = "deleteEndpoint";
    public void deleteEndpoint(Long endpointID) throws MappingDSException;

    public final static String GET_ENDPOINT = "getEndpoint";
    public E getEndpoint(Long id);
    public E getEndpoint(String URL);

    public final static String GET_ENDPOINTS = "getEndpoints";
    public Set<E> getEndpoints(String selector);
    public Set<E> getEndpoints(String key, Object value);
}
