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

package com.spectral.cc.core.mapping.ds.blueprintsimpl;

import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.ClusterImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

import java.util.HashMap;

public class MappingDSSimpleCache {
    private static HashMap<String, MappingDSCacheEntity> ddL2cache = new HashMap<String, MappingDSCacheEntity>();

    protected static synchronized void synchronizeToDB() throws MappingDSGraphDBException {
        for (MappingDSCacheEntity entity : ddL2cache.values()) {
            entity.synchronizeToDB();
        }
    }

    protected static synchronized void synchronizeFromDB() throws MappingDSGraphDBException {
        for (MappingDSCacheEntity entity : ddL2cache.values()) {
            entity.synchronizeFromDB();
        }
    }

    protected static synchronized MappingDSCacheEntity getCachedEntity(String id) {
        return ddL2cache.get(id);
    }

    protected static synchronized void putEntityToCache(MappingDSCacheEntity entity) {
        Element element = entity.getElement();
        if (element instanceof Vertex) {
            ddL2cache.put("V" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID), entity);
        } else if (element instanceof Edge) {
            ddL2cache.put("E" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID), entity);
        }
    }

    protected static synchronized void removeEntityFromCache(MappingDSCacheEntity entity) {
        Element element = entity.getElement();
        if (element instanceof Vertex) {
            ddL2cache.remove("V" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        } else if (element instanceof Edge) {
            ddL2cache.remove("E" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID));
        }
    }

    protected static synchronized ClusterImpl getClusterFromCache(String clusterName) {
        ClusterImpl ret = null;
        for (MappingDSCacheEntity entity : ddL2cache.values()) {
            if (entity instanceof ClusterImpl) {
                if (((ClusterImpl) entity).getClusterName().equals(clusterName)) {
                    ret = (ClusterImpl) entity;
                    break;
                }
            }
        }
        return ret;
    }

    protected static synchronized EndpointImpl getEndpointFromCache(String url) {
        EndpointImpl ret = null;
        for (MappingDSCacheEntity entity : ddL2cache.values()) {
            if (entity instanceof EndpointImpl) {
                if (((EndpointImpl) entity).getEndpointURL().equals(url)) {
                    ret = (EndpointImpl) entity;
                    break;
                }
            }
        }
        return ret;
    }

    protected static synchronized TransportImpl getTransportFromCache(String transportName) {
        TransportImpl ret = null;
        for (MappingDSCacheEntity entity : ddL2cache.values()) {
            if (entity instanceof TransportImpl) {
                if (((TransportImpl) entity).getTransportName().equals(transportName)) {
                    ret = (TransportImpl) entity;
                    break;
                }
            }
        }
        return ret;
    }
}
