/**
 * Mapping Messaging Server
 * Configuration loader
 * Copyright (C) 27/05/16 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.msgsrv.cfg;

import net.echinopsii.ariane.community.messaging.api.MomClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

public class MappingMsgsrvCfgLoader {

    private static final Logger log = LoggerFactory.getLogger(MappingMsgsrvCfgLoader.class);

    private static MappingMsgsrvCfgEntity defaultCfgEntity = new MappingMsgsrvCfgEntity();

    public static boolean load(Dictionary<Object, Object> properties) {
        boolean ret = true;
        if (properties!=null) {
            Object momc_spec = properties.get(MomClient.MOM_CLI);
            if (momc_spec != null && momc_spec instanceof String) {
                defaultCfgEntity.setMomc((String) momc_spec);
                defaultCfgEntity.setMomCliConf(properties);
            } else ret = false;
        } else ret = false;
        return ret;
    }

    public static MappingMsgsrvCfgEntity getDefaultCfgEntity() {
        return defaultCfgEntity;
    }
}
