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
package com.spectral.cc.core.mapping.ds.blueprintsimpl.cfg;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

public class MappingDSCfgLoader {

    private static final Logger log = LoggerFactory.getLogger(MappingDSCfgLoader.class);

    private final static String MAPPING_DS_INTERNAL_DEFAULT_CONF_FILE       = "mapping.ds.blueprints.cfg.json";

    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_IMPL_KEY = "mapping.ds.blueprints.implementation";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_URL_KEY  = "mapping.ds.blueprints.url";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_USER_KEY = "mapping.ds.blueprints.user";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_PAWD_KEY = "mapping.ds.blueprints.password";
    private static final String MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_DIR_KEY  = "mapping.ds.blueprints.directory";

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    private static MappingDSCfgEntity defaultCfgEntity = null;

    public static boolean load() throws JsonParseException, JsonMappingException, IOException {
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        defaultCfgEntity = jsonMapper.readValue(new MappingDSGraphDB().getClass().getResourceAsStream("/" + MAPPING_DS_INTERNAL_DEFAULT_CONF_FILE),
                                                       MappingDSCfgEntity.class);
        if (defaultCfgEntity != null) {
            log.debug(defaultCfgEntity.toString());
        } else {
            log.error("no default cfg entity !!!");
            return false;
        }
        return true;
    }

    public static boolean load(Dictionary<Object, Object> properties) {
        Object oimpl = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_IMPL_KEY);
        String impl  = null;
        Object ourl  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_URL_KEY);
        String url   = null;
        Object ouser = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_USER_KEY);
        String user  = null;
        Object opwd  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_PAWD_KEY);
        String pwd   = null;
        Object odir  = properties.get(MAPPING_DS_CFG_FROM_RIM_BLUEPRINTS_DIR_KEY);
        String dir   = null;

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

        if (impl != null && (url != null || dir != null)) {
            defaultCfgEntity = new MappingDSCfgEntity();
            defaultCfgEntity.setBlueprintsImplementation(impl);
            defaultCfgEntity.setBlueprintsURL(url);
            defaultCfgEntity.setBlueprintsUser(user);
            defaultCfgEntity.setBlueprintsPassword(pwd);
            defaultCfgEntity.setBlueprintsDirectory(dir);
            log.debug("{}", new Object[]{defaultCfgEntity.toString()});
            return true;
        } else {
            log.error("impl ({}) and (url ({}) or dir({})) shouldn't be null!", new Object[]{impl,url,dir});
            return false;
        }
    }

    public static MappingDSCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}