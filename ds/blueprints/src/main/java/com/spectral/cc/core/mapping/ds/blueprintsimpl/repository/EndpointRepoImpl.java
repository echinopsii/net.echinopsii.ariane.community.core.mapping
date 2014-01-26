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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.repository;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSCacheEntity;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import com.spectral.cc.core.mapping.ds.repository.EndpointRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class EndpointRepoImpl implements EndpointRepo<EndpointImpl> {

    private final static Logger log = LoggerFactory.getLogger(EndpointRepoImpl.class);

    @Override
    public EndpointImpl save(EndpointImpl endpoint) {
        MappingDSGraphDB.saveVertexEntity(endpoint);
        log.debug("Added endpoint {} to graph({}).", new Object[]{endpoint.toString(), MappingDSGraphDB.getVertexMaxCursor()});
        return endpoint;
    }

    @Override
    public void delete(EndpointImpl endpoint) {
        MappingDSGraphDB.deleteEntity(endpoint);
        log.debug("Deleted endpoint {} from graph({}).", new Object[]{endpoint.toString(), MappingDSGraphDB.getVertexMaxCursor()});
    }

    @Override
    public Set<EndpointImpl> getAllEndpoints() {
        return MappingDSGraphDB.getEndpoints();
    }

    @Override
    public EndpointImpl findEndpointByID(long id) {
        EndpointImpl ret = null;
        MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity(id);
        if (entity != null) {
            if (entity instanceof EndpointImpl) {
                ret = (EndpointImpl) entity;
            } else {
                log.error("DAEDALUS CONSISTENCY ERROR : entity {} is not an endpoint.", entity.getElement().getId());
            }
        }
        return ret;
    }

    @Override
    public EndpointImpl findEndpointByURL(String url) {
        return MappingDSGraphDB.getIndexedEndpoint(url);
    }

    @Override
    public Set<EndpointImpl> findEndpointsByProperties(String key, Object value) {
        return MappingDSGraphDB.getEndpoints(key, value);
    }
}