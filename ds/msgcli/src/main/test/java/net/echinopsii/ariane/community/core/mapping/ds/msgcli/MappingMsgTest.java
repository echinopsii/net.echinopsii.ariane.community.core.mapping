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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli;

import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cfg.MappingBlueprintsDSCfgLoader;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class MappingMsgTest {

    private static MappingSce blueprintsMappingSce = new net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.MappingSceImpl();
    private static MappingSce messagingMappingSce = new net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.MappingSceImpl();
    private static MappingMsgsrvBootstrap msgsrvBootstrap = new MappingMsgsrvBootstrap();

    @BeforeClass
    public static void testSetup() throws Exception {
        Properties nats_config = new Properties();
        nats_config.load(MappingMsgTest.class.getResourceAsStream("/nats-test.properties"));

        Properties mappingDS_config = new Properties();
        mappingDS_config.load(MappingMsgTest.class.getResourceAsStream("/net.echinopsii.ariane.community.core.MappingRimManagedService.properties"));
        mappingDS_config.setProperty("mapping.ds.blueprints.graphpath", ((String) mappingDS_config.get("mapping.ds.blueprints.graphpath")) + UUID.randomUUID());

        blueprintsMappingSce.init(mappingDS_config);
        blueprintsMappingSce.start();

        msgsrvBootstrap.bindMappingBSce(blueprintsMappingSce);
        msgsrvBootstrap.updated(nats_config);

        messagingMappingSce.init(nats_config);

        try {
            msgsrvBootstrap.validate();
            messagingMappingSce.start();
        } catch (Exception e) {
            e.printStackTrace();
            msgsrvBootstrap.invalidate();
            messagingMappingSce.stop();
            blueprintsMappingSce.stop();
            if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath()!=null) {
                File dir = new File(MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath());
                if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
            }
        }
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        msgsrvBootstrap.invalidate();
        messagingMappingSce.stop();
        blueprintsMappingSce.stop();
        if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath()!=null) {
            File dir = new File(MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath());
            if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
        }
    }

    @Test
    public void testConnection() {
        if (MappingMsgsrvMomSP.getShared_mom_con()!=null) {
            MomClient client = MappingMsgsrvMomSP.getShared_mom_con();
            assertTrue(client.isConnected());
            assertNotNull(client.getConnection());
            assertNotNull(client.createRequestExecutor());
            assertNotNull(client.getServiceFactory());
        }

        if (MappingMsgcliMomSP.getShared_mom_con()!=null) {
            MomClient client = MappingMsgcliMomSP.getShared_mom_con();
            assertTrue(client.isConnected());
            assertNotNull(client.getConnection());
            assertNotNull(client.createRequestExecutor());
            assertNotNull(client.getServiceFactory());
        }
    }
}
