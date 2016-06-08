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

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cfg.MappingBlueprintsDSCfgLoader;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.ClusterImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.ContainerImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.common.MomClientFactory;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class MappingMsgTest {

    private static SProxMappingSce blueprintsMappingSce = new net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.MappingSceImpl();
    private static SProxMappingSce messagingMappingSce = new net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.MappingSceImpl();
    private static MappingMsgsrvBootstrap msgsrvBootstrap = new MappingMsgsrvBootstrap();

    private static MomClient momTest = null;

    @BeforeClass
    public static void testSetup() throws Exception {
        Properties natsConfig = new Properties();
        natsConfig.load(MappingMsgTest.class.getResourceAsStream("/nats-test.properties"));

        momTest = MomClientFactory.make(natsConfig.getProperty(MomClient.MOM_CLI));
        try {
            momTest.init(natsConfig);
        } catch (Exception e) {
            System.err.println("No local NATS to test");
            momTest = null;
        }

        if (momTest!=null) {
            Properties mappingDSConfig = new Properties();
            mappingDSConfig.load(MappingMsgTest.class.getResourceAsStream("/net.echinopsii.ariane.community.core.MappingRimManagedService.properties"));
            mappingDSConfig.setProperty("mapping.ds.blueprints.graphpath", ((String) mappingDSConfig.get("mapping.ds.blueprints.graphpath")) + UUID.randomUUID());

            blueprintsMappingSce.init(mappingDSConfig);
            blueprintsMappingSce.start();

            msgsrvBootstrap.bindMappingBSce(blueprintsMappingSce);
            msgsrvBootstrap.updated(natsConfig);

            messagingMappingSce.init(natsConfig);

            try {
                msgsrvBootstrap.validate();
                messagingMappingSce.start();
            } catch (Exception e) {
                e.printStackTrace();
                msgsrvBootstrap.invalidate();
                messagingMappingSce.stop();
                blueprintsMappingSce.stop();
                if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath() != null) {
                    File dir = new File(MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath());
                    if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
                }
            }
        }
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        if (momTest!=null) {
            msgsrvBootstrap.invalidate();
            messagingMappingSce.stop();
            blueprintsMappingSce.stop();
            if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath() != null) {
                File dir = new File(MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath());
                if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
            }
            momTest.close();
        }
    }

    @Test
    public void testConnection() {
        if (momTest!=null) {
            if (MappingMsgsrvMomSP.getSharedMoMConnection() != null) {
                MomClient client = MappingMsgsrvMomSP.getSharedMoMConnection();
                assertTrue(client.isConnected());
                assertNotNull(client.getConnection());
                assertNotNull(client.createRequestExecutor());
                assertNotNull(client.getServiceFactory());
            }

            if (MappingMsgcliMomSP.getSharedMoMConnection() != null) {
                MomClient client = MappingMsgcliMomSP.getSharedMoMConnection();
                assertTrue(client.isConnected());
                assertNotNull(client.getConnection());
                assertNotNull(client.createRequestExecutor());
                assertNotNull(client.getServiceFactory());
            }
        }
    }

    @Test
    public void testOpenCloseSession() {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            assertTrue(session.isRunning());
            assertTrue(session.getSessionID() != null);
            assertTrue(messagingMappingSce.getSessionRegistry().get(session.getSessionID()) != null);
            assertTrue(blueprintsMappingSce.getSessionRegistry().get(session.getSessionID()) != null);
            messagingMappingSce.closeSession();
            assertTrue(!session.isRunning());
        }
    }

    @Test
    public void testClusterCreate() throws MappingDSException {
        if (momTest!=null) {
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test");
            assertTrue(cluster.getClusterID() != null);
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 1);
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 0);
        }
    }

    @Test
    public void testTransacClusterCreate1() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test");
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getClusterSce().getClusters(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getClusterSce().getClusters(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getClusterSce().getClusters(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getClusterSce().getClusters(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Test
    public void testTransacClusterCreate2() throws MappingDSException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test");
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 1);
            session.rollback();
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 0);
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testTransacClusterGet() throws MappingDSException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test");
            assertNotNull(messagingMappingSce.getClusterSce().getCluster(cluster.getClusterID()));
            assertNotNull(messagingMappingSce.getClusterSce().getClusterByName(cluster.getClusterName()));
            cluster.setClusterName("test2");
            assertEquals(cluster.getClusterName(), "test2");
            session.commit();
            assertNull(messagingMappingSce.getClusterSce().getClusterByName("test"));
            assertNotNull(messagingMappingSce.getClusterSce().getCluster(cluster.getClusterID()));
            assertNotNull(messagingMappingSce.getClusterSce().getClusterByName(cluster.getClusterName()));
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
            session.commit();
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testCreateContainer1() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            assertNotNull(container.getContainerID());
            assertNull(container.getContainerName());
            //assertEquals(container.getContainerPrimaryAdminGateURL(), "ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 1);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
        }
    }

    @Test
    public void testTransacCreateContainer1() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            session.commit();
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.closeSession();
        }
    }


    @Test
    public void testCreateContainer2() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("a.server", "ssh://a.server.fqdn", "SERVER SSH DAEMON");
            assertNotNull(container.getContainerID());
            assertNotNull(container.getContainerName());
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 1);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
        }
    }

    @Test
    public void testTransacCreateContainer2() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Container container = messagingMappingSce.getContainerSce().createContainer("a.server", "ssh://a.server.fqdn", "SERVER SSH DAEMON");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            session.commit();
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getContainerSce().getContainers(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testClusterJoinContainer() throws MappingDSException {
        if (momTest!=null) {
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            cluster.addClusterContainer(container);
            assertTrue(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertTrue(((ContainerImpl) container).getClusterID().equals(cluster.getClusterID()));
            assertTrue(container.getContainerCluster().equals(cluster));
            cluster.removeClusterContainer(container);
            assertFalse(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertTrue(((ContainerImpl) container).getClusterID() == null);
            container.setContainerCluster(cluster);
            assertTrue(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertTrue(((ContainerImpl) container).getClusterID().equals(cluster.getClusterID()));
            container.setContainerCluster(null);
            assertFalse(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertTrue(((ContainerImpl) container).getClusterID() == null);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
        }
    }
}
