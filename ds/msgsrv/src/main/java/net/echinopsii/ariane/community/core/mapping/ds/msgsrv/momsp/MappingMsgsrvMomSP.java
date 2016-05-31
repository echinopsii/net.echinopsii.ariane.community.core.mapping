/**
 * Mapping Messaging Server
 * MoM Service provider
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
package net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp;

import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.cfg.MappingMsgsrvCfgLoader;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.common.MomClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

public class MappingMsgsrvMomSP {
    private final static Logger log = LoggerFactory.getLogger(MappingMsgsrvMomSP.class);

    private static MomClient sharedMoMConnection = null;

    public static boolean init(Dictionary<Object, Object> properties) throws IOException {
        return properties != null && MappingMsgsrvCfgLoader.load(properties);
    }

    public static boolean start() {
        boolean ret = true;

        try {
            sharedMoMConnection = MomClientFactory.make(MappingMsgsrvCfgLoader.getDefaultCfgEntity().getMomc());
        } catch (Exception e) {
            System.err.println("Error while loading MoM client : " + e.getMessage());
            System.err.println("Provided MoM client : " + MappingMsgsrvCfgLoader.getDefaultCfgEntity().getMomc());
            ret = false;
        }

        try {
            sharedMoMConnection.init(MappingMsgsrvCfgLoader.getDefaultCfgEntity().getMomCliConf());
        } catch (Exception e) {
            System.err.println("Error while initializing MoM client : " + e.getMessage());
            System.err.println("Provided MoM host : " + MappingMsgsrvCfgLoader.getDefaultCfgEntity().getMomCliConf().get(MomClient.MOM_HOST));
            System.err.println("Provided MoM port : " + MappingMsgsrvCfgLoader.getDefaultCfgEntity().getMomCliConf().get(MomClient.MOM_PORT));
            sharedMoMConnection = null;
            ret = false;
        }



        return ret;
    }

    public static void stop() throws Exception {
        if (sharedMoMConnection !=null)
            sharedMoMConnection.close();
    }

    public static MomClient getSharedMoMConnection() {
        return sharedMoMConnection;
    }
}
