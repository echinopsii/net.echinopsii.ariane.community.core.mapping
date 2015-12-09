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
package net.echinopsii.ariane.community.core.mapping.ds.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

public abstract class MappingDSCfgLoader {

    private static final Logger log = LoggerFactory.getLogger(MappingDSCfgLoader.class);
    private static final String MAPPING_DS_CFG_FROM_RIM_CACHE_CONFFILE_KEY = "mapping.ds.cache.configfile";

    public static MappingDSCfgEntity defaultCfgEntity = null;

    public static boolean load(Dictionary<Object, Object> properties) {
        Object occf  = properties.get(MAPPING_DS_CFG_FROM_RIM_CACHE_CONFFILE_KEY);
        String ccf   = null;
        if (occf != null && occf instanceof String) ccf = (String) occf;
        defaultCfgEntity.setCacheConfigFile(ccf);
        log.debug("{}", new Object[]{defaultCfgEntity.toString()});
        return true;
    }

    public static MappingDSCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}