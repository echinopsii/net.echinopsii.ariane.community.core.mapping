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
import com.spectral.cc.core.mapping.ds.blueprintsimpl.TopoDSGraphDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

public class TopoDSCfgLoader {

    private static final Logger log = LoggerFactory.getLogger(TopoDSCfgLoader.class);

    private final static String TOPO_DS_INTERNAL_DEFAULT_CONF_FILE = "topo.ds.blueprints.cfg.json";

    private static final String TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_IMPL_KEY = "topo.ds.blueprints.implementation";
    private static final String TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_URL_KEY = "topo.ds.blueprints.url";
    private static final String TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_USER_KEY = "topo.ds.blueprints.user";
    private static final String TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_PAWD_KEY = "topo.ds.blueprints.password";

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    private static TopoDSCfgEntity defaultCfgEntity = null;

    public static boolean load() throws JsonParseException, JsonMappingException, IOException {
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        defaultCfgEntity = jsonMapper.readValue(new TopoDSGraphDB().getClass().getResourceAsStream("/" + TOPO_DS_INTERNAL_DEFAULT_CONF_FILE),
                                                       TopoDSCfgEntity.class);
        if (defaultCfgEntity != null) {
            log.debug(defaultCfgEntity.toString());
        } else {
            log.error("no default cfg entity !!!");
            return false;
        }
        return true;
    }

    public static boolean load(Dictionary<Object, Object> properties) {
        Object oimpl = properties.get(TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_IMPL_KEY);
        String impl = null;
        Object ourl = properties.get(TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_URL_KEY);
        String url = null;
        Object ouser = properties.get(TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_USER_KEY);
        String user = null;
        Object opwd = properties.get(TOPO_DS_CFG_FROM_RIM_BLUEPRINTS_PAWD_KEY);
        String pwd = null;

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

        if (impl != null && url != null) {
            defaultCfgEntity = new TopoDSCfgEntity();
            defaultCfgEntity.setBlueprintsImplementation(impl);
            defaultCfgEntity.setBlueprintsURL(url);
            defaultCfgEntity.setBlueprintsUser(user);
            defaultCfgEntity.setBlueprintsPassword(pwd);
            log.debug("{}", new Object[]{defaultCfgEntity.toString()});
            return true;
        } else {
            return false;
        }
    }

    public static TopoDSCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}