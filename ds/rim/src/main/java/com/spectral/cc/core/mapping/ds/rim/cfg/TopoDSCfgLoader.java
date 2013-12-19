/**
 * Mapping Datastore Runtime Injectection Manager :
 * provide a mapping DS configuration parser, factories and registry to inject
 * Mapping DS interface implementation dependencies.
 *
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

package com.spectral.cc.core.mapping.ds.rim.cfg;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectral.cc.core.mapping.ds.rim.registry.TopoDSRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;

public class TopoDSCfgLoader {

    private static final String TOPO_DS_RIM_CFG_BUNDLE_NAME_KEY = "topo.ds.bundle.name";

    private static final String TOPO_DS_CFG_LOADER_BLUEPRINTS_BDL_NAME = "com.spectral.cc.core.mapping.ds.blueprints";
    private static final String TOPO_DS_CFG_LOADER_BLUEPRINTS_CFG_FILE = "topo.ds.rim.blueprints.json";

    private static final Logger log = LoggerFactory.getLogger(TopoDSCfgLoader.class);

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    private static TopoDSCfgEntity defaultCfgEntity = null;

    public static boolean load(InputStream is) throws JsonParseException, JsonMappingException, IOException {
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        defaultCfgEntity = jsonMapper.readValue(is, TopoDSCfgEntity.class);
        if (defaultCfgEntity != null) {
            log.debug(defaultCfgEntity.toString());
        } else {
            log.error("no default cfg entity !!!");
            return false;
        }
        InputStream inStream = new TopoDSCfgLoader().getClass().getResourceAsStream("/" + defaultCfgEntity.getBundleCfgFile());
        TopoDSRegistryService.registerTopoDS(defaultCfgEntity.getBundleName(), inStream);
        return true;
    }

    public static boolean load(Dictionary<Object, Object> properties) throws JsonParseException, JsonMappingException, IOException {
        String bundleName = (String) properties.get(TOPO_DS_RIM_CFG_BUNDLE_NAME_KEY);
        switch (bundleName) {
            case TOPO_DS_CFG_LOADER_BLUEPRINTS_BDL_NAME:
                defaultCfgEntity = new TopoDSCfgEntity();
                defaultCfgEntity.setBundleName(bundleName);
                defaultCfgEntity.setBundleCfgFile(TOPO_DS_CFG_LOADER_BLUEPRINTS_CFG_FILE);
                break;
            default:
                log.error("This target TopoDS bundle name {} is not managed by TopoDS RIM !", new Object[]{bundleName});
                log.error("List of valid target TopoDS bundle name : {}, {}", new Object[]{TOPO_DS_CFG_LOADER_BLUEPRINTS_BDL_NAME});
                return false;
        }
        log.debug(defaultCfgEntity.toString());
        InputStream inStream = new TopoDSCfgLoader().getClass().getResourceAsStream("/" + defaultCfgEntity.getBundleCfgFile());
        TopoDSRegistryService.registerTopoDS(defaultCfgEntity.getBundleName(), inStream);
        return true;
    }

    public static TopoDSCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}