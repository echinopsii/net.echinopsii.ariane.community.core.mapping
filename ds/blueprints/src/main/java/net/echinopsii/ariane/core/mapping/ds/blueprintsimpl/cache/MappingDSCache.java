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

package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cache;

import net.echinopsii.ariane.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cfg.MappingDSCfgLoader;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBException;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.ClusterImpl;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.*;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Properties;
import java.util.Set;

public class MappingDSCache {
    private static final Logger log = LoggerFactory.getLogger(MappingDSCache.class);
    private static String MAPPING_INFINISPAN_CACHE_ID = "ariane.core.mapping.cache";
    private static String MAPPING_INFINISPAN_CACHE_NAME = "Ariane Mapping Cache";

    private static EmbeddedCacheManager manager;
    private static Cache ddL2cache;

    private static InputStream config;

    public static boolean init(Dictionary<Object, Object> properties) throws FileNotFoundException {
        if (properties != null) {
            MappingDSCfgLoader.load(properties);
            String configPath = MappingDSCfgLoader.getDefaultCfgEntity().getCacheConfigFile();
            if (configPath!=null) {
                File configFile = new File(configPath);
                if (configFile.isFile() && configFile.canRead())
                    config = new FileInputStream(configFile);
                else
                    log.warn("File " + configPath + " doesn't exists or is not readable... Mapping cache will use default configuration.");
            }

            if (config == null)
                config = MappingDSCache.class.getResourceAsStream("/META-INF/infinispan.mapping.cache.xml");

            return true;
        } else {
            return false;
        }
    }

    public static boolean start() {
        try {
            manager = new DefaultCacheManager(config);
        } catch (Exception e) {
            log.error("Error while initializing Infinispan Embedded Cache Manager !");
            e.printStackTrace();
            return false;
        }
        ddL2cache = manager.getCache(MAPPING_INFINISPAN_CACHE_ID);
        log.info("{} is started", MAPPING_INFINISPAN_CACHE_NAME);
        return true;
    }

    public static boolean stop() {
        ddL2cache.stop();
        manager.stop();
        log.info("{} is stopped", MAPPING_INFINISPAN_CACHE_NAME);
        return true;
    }

    public static Properties getConfiguration() {
        Properties ret = new Properties();
        Configuration conf = ddL2cache.getCacheConfiguration();

        ret.put("Cache name", ddL2cache.getName());
        ret.put("Cache size", ddL2cache.size());
        ret.put("Cache status", ddL2cache.getStatus().toString());

        ret.put("Cluster enabled", manager.getCacheManagerConfiguration().isClustered());
        if (manager.getCacheManagerConfiguration().isClustered())
            ret.put("Cluster cache mode",conf.clustering().cacheModeString());

        EvictionConfiguration evictionConfiguration = conf.eviction();
        ret.put("Eviction max entries",evictionConfiguration.maxEntries());
        ret.put("Eviction strategy enabled",evictionConfiguration.strategy().isEnabled());
        ret.put("Eviction strategy name",evictionConfiguration.strategy().name());

        ExpirationConfiguration expirationConfiguration = conf.expiration();
        ret.put("Expiration lifespan",expirationConfiguration.lifespan());
        ret.put("Expiration max idle", expirationConfiguration.maxIdle());
        ret.put("Expiration reaper enabled", expirationConfiguration.reaperEnabled());
        ret.put("Expiration wake up interval", expirationConfiguration.wakeUpInterval());

        PersistenceConfiguration persistenceConfiguration = conf.persistence();
        ret.put("Persistence fetch state",persistenceConfiguration.fetchPersistentState());
        ret.put("Persistence passivation",persistenceConfiguration.passivation());
        ret.put("Persistence preload",persistenceConfiguration.preload());
        ret.put("Persistence use async store", persistenceConfiguration.usingAsyncStore());
        for (StoreConfiguration storeConfiguration : persistenceConfiguration.stores()) {
            ret.put("Persistent store async enabled",storeConfiguration.async().enabled());
            ret.put("Persistent store fetch state",storeConfiguration.fetchPersistentState());
            ret.put("Persistent store ignore modification", storeConfiguration.ignoreModifications());
            ret.put("Persistent store preload", storeConfiguration.preload());
            if (storeConfiguration.properties()!=null)
                for (Object key : storeConfiguration.properties().keySet())
                    ret.put("Persistent store property " + key.toString(), storeConfiguration.properties().get(key));
            ret.put("Persistent store purge on startup", storeConfiguration.purgeOnStartup());
        }

        ret.put("Versioning enabled", conf.versioning().enabled());
        return ret;
    }


    public static synchronized void synchronizeToDB() throws MappingDSGraphDBException {
        if (ddL2cache!=null) {
            for (String key : (Set<String>)ddL2cache.keySet()) {
                MappingDSCacheEntity entity = (MappingDSCacheEntity)ddL2cache.get(key);
                entity.synchronizeToDB();
            }
        }
    }

    public static synchronized MappingDSCacheEntity getCachedEntity(String id) {
        MappingDSCacheEntity ret = null;
        if (ddL2cache!=null) {
            ret = (MappingDSCacheEntity) ddL2cache.get(id);
        }
        return ret;
    }

    public static synchronized void putEntityToCache(MappingDSCacheEntity entity) {
        if (ddL2cache!=null) {
            Element element = entity.getElement();
            if (element instanceof Vertex) {
                ddL2cache.put("V" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID), entity);
            } else if (element instanceof Edge) {
                ddL2cache.put("E" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID), entity);
            }
        }
    }

    public static synchronized void removeEntityFromCache(MappingDSCacheEntity entity) {
        if (ddL2cache!=null) {
            Element element = entity.getElement();
            if (element instanceof Vertex) {
                ddL2cache.remove("V" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
            } else if (element instanceof Edge) {
                ddL2cache.remove("E" + entity.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID));
            }
        }
    }

    public static synchronized ClusterImpl getClusterFromCache(String clusterName) {
        ClusterImpl ret = null;
        for (String key : (Set<String>)ddL2cache.keySet()) {
            MappingDSCacheEntity entity = (MappingDSCacheEntity)ddL2cache.get(key);
            if (entity instanceof ClusterImpl) {
                if (((ClusterImpl) entity).getClusterName().equals(clusterName)) {
                    ret = (ClusterImpl) entity;
                    break;
                }
            }
        }
        return ret;
    }

    public static synchronized EndpointImpl getEndpointFromCache(String url) {
        EndpointImpl ret = null;
        for (String key : (Set<String>)ddL2cache.keySet()) {
            MappingDSCacheEntity entity = (MappingDSCacheEntity) ddL2cache.get(key);
            if (entity instanceof EndpointImpl) {
                if (((EndpointImpl) entity).getEndpointURL().equals(url)) {
                    ret = (EndpointImpl) entity;
                    break;
                }
            }
        }
        return ret;
    }

    public static synchronized TransportImpl getTransportFromCache(String transportName) {
        TransportImpl ret = null;
        for (String key : (Set<String>)ddL2cache.keySet()) {
            MappingDSCacheEntity entity = (MappingDSCacheEntity) ddL2cache.get(key);
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
