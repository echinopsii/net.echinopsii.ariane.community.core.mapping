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
package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

public class MappingDSCfgLoader {

    private static final Logger log = LoggerFactory.getLogger(MappingDSCfgLoader.class);

    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_IMPL_KEY           = "mapping.ds.blueprints.implementation";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_URL_KEY            = "mapping.ds.blueprints.url";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_USER_KEY           = "mapping.ds.blueprints.user";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_PAWD_KEY           = "mapping.ds.blueprints.password";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_GP_KEY             = "mapping.ds.blueprints.graphpath";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_NEO4J_CONFFILE_KEY = "mapping.ds.blueprints.neo4j.configfile";
    private static final String MAPPING_DS_CFG_FROM_RIM_CACHE_CONFFILE_KEY            = "mapping.ds.cache.configfile";

    private static MappingDSCfgEntity defaultCfgEntity = null;

    public static boolean load(Dictionary<Object, Object> properties) {
        Object oimpl = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_IMPL_KEY);
        String impl  = null;
        Object ourl  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_URL_KEY);
        String url   = null;
        Object ouser = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_USER_KEY);
        String user  = null;
        Object opwd  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_PAWD_KEY);
        String pwd   = null;
        Object odir  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_GP_KEY);
        String dir   = null;
        Object oncf  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_NEO4J_CONFFILE_KEY);
        String ncf   = null;
        Object occf  = properties.get(MAPPING_DS_CFG_FROM_RIM_CACHE_CONFFILE_KEY);
        String ccf   = null;


        if (oimpl != null && oimpl instanceof String) {
            impl = (String) oimpl;
        }
        if (ourl != null && ourl instanceof String) {
            url = (String) ourl;
        }
        if (ouser != null && ouser instanceof String) {
            user = (String) ouser;
        }
        if (opwd != null && opwd instanceof String) {
            pwd = (String) opwd;
        }
        if (odir != null && odir instanceof String) {
            dir = (String) odir;
        }
        if (oncf != null && oncf instanceof String) {
            ncf = (String) oncf;
        }
        if (occf != null && occf instanceof String) {
            ccf = (String) occf;
        }

        if (impl != null && (url != null || dir != null || ncf != null)) {
            defaultCfgEntity = new MappingDSCfgEntity();
            defaultCfgEntity.setCacheConfigFile(ccf);
            defaultCfgEntity.setBlueprintsImplementation(impl);
            defaultCfgEntity.setBlueprintsURL(url);
            defaultCfgEntity.setBlueprintsUser(user);
            defaultCfgEntity.setBlueprintsPassword(pwd);
            defaultCfgEntity.setBlueprintsGraphPath(dir);
            defaultCfgEntity.setBlueprintsNeoConfigFile(ncf);
            log.debug("{}", new Object[]{defaultCfgEntity.toString()});
            return true;
        } else {
            log.error("impl ({}) and (url ({}) or dir({}) or ncf({})) shouldn't be null!", new Object[]{impl,url,dir,ncf});
            return false;
        }
    }

    public static MappingDSCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}