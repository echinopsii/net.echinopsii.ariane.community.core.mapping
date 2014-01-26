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
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.ClusterImpl;
import com.spectral.cc.core.mapping.ds.repository.ClusterRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterRepoImpl implements ClusterRepo<ClusterImpl> {

    private final static Logger log = LoggerFactory.getLogger(ClusterRepoImpl.class);

    @Override
    public ClusterImpl save(ClusterImpl cluster) {
        MappingDSGraphDB.saveVertexEntity(cluster);
        log.debug("Added cluster {} to graph({}).", new Object[]{cluster.toString(), MappingDSGraphDB.getVertexMaxCursor()});
        return cluster;
    }

    @Override
    public void delete(ClusterImpl cluster) {
        MappingDSGraphDB.deleteEntity(cluster);
        log.debug("Deleted cluster {} from graph({}).", new Object[]{cluster.toString(), MappingDSGraphDB.getVertexMaxCursor()});
    }

    @Override
    public ClusterImpl findClusteByID(long id) {
        ClusterImpl ret = null;
        MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity(id);
        if (entity != null) {
            if (entity instanceof ClusterImpl) {
                ret = (ClusterImpl) ret;
            } else {
                log.error("DAEDALUS CONSISTENCY ERROR : entity {} is not a cluster.", entity.getElement().getId());
            }
        }
        return ret;
    }

    @Override
    public ClusterImpl findClusterByName(String name) {
        ClusterImpl ret = MappingDSGraphDB.getIndexedCluster(name);
        return ret;
    }
}