/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
 * Author: mathilde.ffrench@echinopsii.net
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp;

import net.echinopsii.ariane.community.core.mapping.ds.msgcli.cfg.MappingMsgcliCfgLoader;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.api.MomRequestExecutor;
import net.echinopsii.ariane.community.messaging.common.MomClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

public class MappingMsgcliMomSP {
    private final static Logger log = LoggerFactory.getLogger(MappingMsgcliMomSP.class);

    private static MomClient sharedMoMConnection = null;
    private static MomRequestExecutor sharedMoMReqExec = null;

    public static boolean init(Dictionary<Object, Object> properties) throws IOException {
        return properties != null && MappingMsgcliCfgLoader.load(properties);
    }

    public static boolean start(String version) {
        boolean ret = true;

        try {
            sharedMoMConnection = MomClientFactory.make(MappingMsgcliCfgLoader.getDefaultCfgEntity().getMomc());
        } catch (Exception e) {
            System.err.println("Error while loading MoM client : " + e.getMessage());
            System.err.println("Provided MoM client : " + MappingMsgcliCfgLoader.getDefaultCfgEntity().getMomc());
            ret = false;
        }

        try {
            Dictionary properties = MappingMsgcliCfgLoader.getDefaultCfgEntity().getMomCliConf();
            String hostname =  java.net.InetAddress.getLocalHost().getHostName();
            if (properties.get(MomClient.ARIANE_PGURL_KEY)==null) properties.put(MomClient.ARIANE_PGURL_KEY, "http://"+hostname+":6969/ariane");
            if (properties.get(MomClient.ARIANE_OSI_KEY)==null) properties.put(MomClient.ARIANE_OSI_KEY, hostname);
            if (properties.get(MomClient.ARIANE_APP_KEY)==null) properties.put(MomClient.ARIANE_APP_KEY, "Ariane");
            if (properties.get(MomClient.ARIANE_OTM_KEY)==null) properties.put(MomClient.ARIANE_OTM_KEY, MomClient.ARIANE_OTM_NOT_DEFINED);
            if (properties.get(MomClient.ARIANE_CMP_KEY)==null) properties.put(MomClient.ARIANE_CMP_KEY, "echinopsii");

            if (properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.rabbitmq.Client")) {
                if (properties.get(MomClient.RBQ_PRODUCT_KEY) == null || properties.get(MomClient.RBQ_PRODUCT_KEY).equals("")) properties.put(MomClient.RBQ_PRODUCT_KEY, "Ariane");
                if (properties.get(MomClient.RBQ_INFORMATION_KEY) == null || properties.get(MomClient.RBQ_INFORMATION_KEY).equals("")) properties.put(MomClient.RBQ_INFORMATION_KEY, "Ariane Remote Mapping Messaging Client");
                if ((properties.get(MomClient.RBQ_VERSION_KEY) == null || properties.get(MomClient.RBQ_VERSION_KEY).equals(""))&& version != null) properties.put(MomClient.RBQ_VERSION_KEY, version);
                if (properties.get(MomClient.RBQ_COPYRIGHT_KEY)==null || properties.get(MomClient.RBQ_COPYRIGHT_KEY).equals("")) properties.put(MomClient.RBQ_COPYRIGHT_KEY, "AGPLv3 / Free2Biz");
            }

            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            properties.put(MomClient.ARIANE_PID_KEY, pid);
            sharedMoMConnection.init(properties);
        } catch (Exception e) {
            System.err.println("Error while initializing MoM client : " + e.getMessage());
            System.err.println("Provided MoM host : " + MappingMsgcliCfgLoader.getDefaultCfgEntity().getMomCliConf().get(MomClient.MOM_HOST));
            System.err.println("Provided MoM port : " + MappingMsgcliCfgLoader.getDefaultCfgEntity().getMomCliConf().get(MomClient.MOM_PORT));
            sharedMoMConnection = null;
            ret = false;
        }

        if (ret)
            sharedMoMReqExec = sharedMoMConnection.createRequestExecutor();

        return ret;
    }

    public static void stop() throws Exception {
        if (sharedMoMConnection !=null)
            sharedMoMConnection.close();
    }

    public static MomClient getSharedMoMConnection() {
        return sharedMoMConnection;
    }

    public static MomRequestExecutor getSharedMoMReqExec() {
        return sharedMoMReqExec;
    }
}
