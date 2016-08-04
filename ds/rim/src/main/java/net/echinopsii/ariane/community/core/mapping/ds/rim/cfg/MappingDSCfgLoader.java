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

package net.echinopsii.ariane.community.core.mapping.ds.rim.cfg;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.ds.rim.registry.MappingDSRegistryService;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;

public class MappingDSCfgLoader {

    private static final String MAPPING_DS_RIM_CFG_BUNDLE_NAME_KEY = "mapping.ds.bundle.name";

    private static final String MAPPING_DS_CFG_LOADER_BLUEPRINTS_BDL_NAME = "net.echinopsii.ariane.community.core.mapping.ds.blueprints";
    private static final String MAPPING_DS_CFG_LOADER_BLUEPRINTS_CFG_FILE = "mapping.ds.rim.blueprints.json";

    private static final String MAPPING_DS_CFG_LOADER_MSGCLI_BDL_NAME = "net.echinopsii.ariane.community.core.mapping.ds.msgcli";
    private static final String MAPPING_DS_CFG_LOADER_MSGCLI_CFG_FILE = "mapping.ds.rim.msgcli.json";

    private static final Logger log = LoggerFactory.getLogger(MappingDSCfgLoader.class);

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    private static MappingDSCfgEntity defaultCfgEntity = null;

    public static boolean isValid(final Dictionary properties, String version) {
        if (properties.get(MAPPING_DS_RIM_CFG_BUNDLE_NAME_KEY)==null) return false;
        else {
            if (properties.get(MAPPING_DS_RIM_CFG_BUNDLE_NAME_KEY).equals(MAPPING_DS_CFG_LOADER_MSGCLI_BDL_NAME)) {
                String hostname = null;
                String connectionName ;
                try {
                    hostname = InetAddress.getLocalHost().getHostName();
                    connectionName = "Ariane Mapping Proxy @ " + hostname;
                } catch (UnknownHostException e) {
                    log.warn("Problem while getting hostname : " + e.getCause());
                    hostname = "";
                    connectionName = "Ariane Mapping Proxy";
                }
                if (properties.get(MomClient.ARIANE_PGURL_KEY)==null) properties.put(MomClient.ARIANE_PGURL_KEY, "http://"+hostname+":6969/ariane");
                if (properties.get(MomClient.ARIANE_OSI_KEY)==null) properties.put(MomClient.ARIANE_OSI_KEY, hostname);
                if (properties.get(MomClient.ARIANE_APP_KEY)==null) properties.put(MomClient.ARIANE_APP_KEY, "Ariane");
                if (properties.get(MomClient.ARIANE_OTM_KEY)==null) properties.put(MomClient.ARIANE_OTM_KEY, MomClient.ARIANE_OTM_NOT_DEFINED);
                if (properties.get(MomClient.ARIANE_CMP_KEY)==null) properties.put(MomClient.ARIANE_CMP_KEY, "echinopsii");
                String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
                properties.put(MomClient.ARIANE_PID_KEY, pid);

                if (properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.nats.Client")) {
                    if (properties.get(MomClient.NATS_CONNECTION_NAME) == null || properties.get(MomClient.NATS_CONNECTION_NAME).equals("")) properties.put(MomClient.NATS_CONNECTION_NAME,connectionName);
                } else if (properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.rabbitmq.Client")) {
                    if (properties.get(MomClient.RBQ_PRODUCT_KEY) == null || properties.get(MomClient.RBQ_PRODUCT_KEY).equals("")) properties.put(MomClient.RBQ_PRODUCT_KEY, "Ariane");
                    if (properties.get(MomClient.RBQ_INFORMATION_KEY) == null || properties.get(MomClient.RBQ_INFORMATION_KEY).equals("")) properties.put(MomClient.RBQ_INFORMATION_KEY, connectionName);
                    if ((properties.get(MomClient.RBQ_VERSION_KEY) == null || properties.get(MomClient.RBQ_VERSION_KEY).equals(""))&& version != null) properties.put(MomClient.RBQ_VERSION_KEY, version);
                    if (properties.get(MomClient.RBQ_COPYRIGHT_KEY)==null || properties.get(MomClient.RBQ_COPYRIGHT_KEY).equals("")) properties.put(MomClient.RBQ_COPYRIGHT_KEY, "AGPLv3 / Free2Biz");
                }
            }
            return true;
        }
    }

    public static boolean load(InputStream is) throws JsonParseException, JsonMappingException, IOException {
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        defaultCfgEntity = jsonMapper.readValue(is, MappingDSCfgEntity.class);
        if (defaultCfgEntity != null) {
            log.debug(defaultCfgEntity.toString());
        } else {
            log.error("no default cfg entity !!!");
            return false;
        }
        InputStream inStream = new MappingDSCfgLoader().getClass().getResourceAsStream("/" + defaultCfgEntity.getBundleCfgFile());
        MappingDSRegistryService.registerMappingDS(defaultCfgEntity.getBundleName(), inStream);
        return true;
    }

    public static boolean load(Dictionary<Object, Object> properties) throws JsonParseException, JsonMappingException, IOException {
        String bundleName = (String) properties.get(MAPPING_DS_RIM_CFG_BUNDLE_NAME_KEY);
        switch (bundleName) {
            case MAPPING_DS_CFG_LOADER_BLUEPRINTS_BDL_NAME:
                defaultCfgEntity = new MappingDSCfgEntity();
                defaultCfgEntity.setBundleName(bundleName);
                defaultCfgEntity.setBundleCfgFile(MAPPING_DS_CFG_LOADER_BLUEPRINTS_CFG_FILE);
                break;
            case MAPPING_DS_CFG_LOADER_MSGCLI_BDL_NAME:
                defaultCfgEntity = new MappingDSCfgEntity();
                defaultCfgEntity.setBundleName(bundleName);
                defaultCfgEntity.setBundleCfgFile(MAPPING_DS_CFG_LOADER_MSGCLI_CFG_FILE);
                break;
            default:
                log.error("This target MappingDS bundle name {} is not managed by MappingDS RIM !", new Object[]{bundleName});
                log.error("List of valid target MappingDS bundle name : {}, {}", new Object[]{MAPPING_DS_CFG_LOADER_BLUEPRINTS_BDL_NAME});
                return false;
        }
        log.debug(defaultCfgEntity.toString());
        InputStream inStream = new MappingDSCfgLoader().getClass().getResourceAsStream("/" + defaultCfgEntity.getBundleCfgFile());
        MappingDSRegistryService.registerMappingDS(defaultCfgEntity.getBundleName(), inStream);
        return true;
    }

    public static MappingDSCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}