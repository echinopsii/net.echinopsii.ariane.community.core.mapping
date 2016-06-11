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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
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
import java.util.ArrayList;
import java.util.HashMap;
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
            container.setContainerName("a.server");
            container.setContainerCompany("RedHat");
            container.setContainerProduct("RedHat Linux x86 7");
            container.setContainerType("Operating System");
            assertEquals(container.getContainerName(), "a.server");
            assertEquals(container.getContainerCompany(), "RedHat");
            assertEquals(container.getContainerProduct(), "RedHat Linux x86 7");
            assertEquals(container.getContainerType(), "Operating System");
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
    public void testCreateContainer3() throws MappingDSException {
        if (momTest!=null) {
            Container server = messagingMappingSce.getContainerSce().createContainer("a.server", "ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Container container = messagingMappingSce.getContainerSce().createContainer("a.container", "ssh://a.server.fqdn:a.container", "SERVER SSH DAEMON", server);
            assertTrue(server.getContainerChildContainers().contains(container));
            assertTrue(container.getContainerParentContainer().equals(server));
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 2);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn:a.container");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).size() == 0);
        }
    }

    @Test
    public void testContainerProperties() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            container.addContainerProperty("stringProp", "a string");
            assertTrue(container.getContainerProperties().containsKey("stringProp"));
            assertTrue(container.getContainerProperties().get("stringProp").equals("a string"));
            Container bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertTrue(bis.getContainerProperties().containsKey("stringProp"));
            assertTrue(bis.getContainerProperties().get("stringProp").equals("a string"));
            container.removeContainerProperty("stringProp");
            assertFalse(container.getContainerProperties().containsKey("stringProp"));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertFalse(bis.getContainerProperties().containsKey("stringProp"));
            container.addContainerProperty("boolProp", false);
            assertTrue(container.getContainerProperties().containsKey("boolProp"));
            assertTrue(container.getContainerProperties().get("boolProp").equals(false));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertTrue(bis.getContainerProperties().containsKey("boolProp"));
            assertTrue(bis.getContainerProperties().get("boolProp").equals(false));
            container.removeContainerProperty("boolProp");
            assertFalse(container.getContainerProperties().containsKey("boolProp"));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertFalse(bis.getContainerProperties().containsKey("boolProp"));
            container.addContainerProperty("intProp", 1);
            assertTrue(container.getContainerProperties().containsKey("intProp"));
            assertTrue(container.getContainerProperties().get("intProp").equals(1));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertTrue(bis.getContainerProperties().containsKey("intProp"));
            assertTrue(bis.getContainerProperties().get("intProp").equals(1));
            container.removeContainerProperty("intProp");
            assertFalse(container.getContainerProperties().containsKey("intProp"));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertFalse(bis.getContainerProperties().containsKey("intProp"));
            container.addContainerProperty("doubleProp", 2.1);
            assertTrue(container.getContainerProperties().containsKey("doubleProp"));
            assertTrue(container.getContainerProperties().get("doubleProp").equals(2.1));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertTrue(bis.getContainerProperties().containsKey("doubleProp"));
            assertTrue(bis.getContainerProperties().get("doubleProp").equals(2.1));
            container.removeContainerProperty("doubleProp");
            assertFalse(container.getContainerProperties().containsKey("doubleProp"));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertFalse(bis.getContainerProperties().containsKey("doubleProp"));
            ArrayList<String> listProp = new ArrayList<>();
            listProp.add("test1");
            listProp.add("test2");
            container.addContainerProperty("listProp", listProp);
            assertTrue(container.getContainerProperties().containsKey("listProp"));
            assertTrue(container.getContainerProperties().get("listProp").equals(listProp));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertTrue(bis.getContainerProperties().containsKey("listProp"));
            assertTrue(bis.getContainerProperties().get("listProp").equals(listProp));
            container.removeContainerProperty("listProp");
            assertFalse(container.getContainerProperties().containsKey("listProp"));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertFalse(bis.getContainerProperties().containsKey("listProp"));
            HashMap<String, Object> mapProp = new HashMap<>();
            mapProp.put("boolVal", true);
            mapProp.put("stringVal", "test");
            container.addContainerProperty("mapProp", mapProp);
            assertTrue(container.getContainerProperties().containsKey("mapProp"));
            assertTrue(container.getContainerProperties().get("mapProp").equals(mapProp));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertTrue(bis.getContainerProperties().containsKey("mapProp"));
            assertTrue(bis.getContainerProperties().get("mapProp").equals(mapProp));
            container.removeContainerProperty("mapProp");
            assertFalse(container.getContainerProperties().containsKey("mapProp"));
            bis = messagingMappingSce.getContainerSce().getContainer(container.getContainerID());
            assertFalse(bis.getContainerProperties().containsKey("mapProp"));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
        }
    }

    @Test
    public void testClusterJoinContainer() throws MappingDSException {
        if (momTest!=null) {
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            cluster.addClusterContainer(container);
            assertTrue(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertTrue(cluster.getClusterContainers().contains(container));
            assertTrue(((ContainerImpl) container).getClusterID().equals(cluster.getClusterID()));
            assertTrue(container.getContainerCluster().equals(cluster));
            cluster.removeClusterContainer(container);
            assertFalse(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertFalse(cluster.getClusterContainers().contains(container));
            assertTrue(((ContainerImpl) container).getClusterID() == null);
            assertTrue(container.getContainerCluster() == null);
            container.setContainerCluster(cluster);
            assertTrue(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertTrue(cluster.getClusterContainers().contains(container));
            assertTrue(((ContainerImpl) container).getClusterID().equals(cluster.getClusterID()));
            assertTrue(container.getContainerCluster().equals(cluster));
            container.setContainerCluster(null);
            assertFalse(((ClusterImpl) cluster).getClusterContainersID().contains(container.getContainerID()));
            assertFalse(cluster.getClusterContainers().contains(container));
            assertTrue(((ContainerImpl) container).getClusterID() == null);
            assertTrue(container.getContainerCluster() == null);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
        }
    }

    @Test
    public void testContainerJoinContainer() throws MappingDSException {
        if (momTest!=null) {
            Container containerA = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Container containerB = messagingMappingSce.getContainerSce().createContainer("ssh://b.server.fqdn", "SERVER SSH DAEMON");
            containerA.addContainerChildContainer(containerB);
            assertTrue(((ContainerImpl) containerA).getChildContainersID().contains(containerB.getContainerID()));
            assertTrue(containerA.getContainerChildContainers().contains(containerB));
            assertTrue(((ContainerImpl) containerB).getParentContainerID().equals(containerA.getContainerID()));
            assertTrue(containerB.getContainerParentContainer().equals(containerA));
            containerA.removeContainerChildContainer(containerB);
            assertFalse(((ContainerImpl) containerA).getChildContainersID().contains(containerB.getContainerID()));
            assertFalse(containerA.getContainerChildContainers().contains(containerB));
            assertTrue(((ContainerImpl) containerB).getParentContainerID() == null);
            assertTrue(containerB.getContainerParentContainer() == null);
            containerB.setContainerParentContainer(containerA);
            assertTrue(((ContainerImpl) containerA).getChildContainersID().contains(containerB.getContainerID()));
            assertTrue(containerA.getContainerChildContainers().contains(containerB));
            assertTrue(((ContainerImpl) containerB).getParentContainerID().equals(containerA.getContainerID()));
            assertTrue(containerB.getContainerParentContainer().equals(containerA));
            containerB.setContainerParentContainer(null);
            assertFalse(((ContainerImpl) containerA).getChildContainersID().contains(containerB.getContainerID()));
            assertFalse(containerA.getContainerChildContainers().contains(containerB));
            assertTrue(((ContainerImpl) containerB).getParentContainerID() == null);
            assertTrue(containerB.getContainerParentContainer() == null);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.server.fqdn");
        }
    }

    @Test
    public void testCreateNode1() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process", container.getContainerID(), null);
            assertNotNull(process.getNodeID());
            assertTrue(process.getNodeContainer().equals(container));
            assertTrue(container.getContainerNodes(0).contains(process));
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
        }
    }

    @Test
    public void testTransacCreateNode1() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process", container.getContainerID(), null);
            assertNotNull(process.getNodeID());
            assertTrue(process.getNodeContainer().equals(container));
            assertTrue(container.getContainerNodes(0).contains(process));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).size() == 2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 2);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 2);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testCreateNode2() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process", container.getContainerID(), null);
            Node thread = messagingMappingSce.getNodeSce().createNode("a thread", container.getContainerID(), process.getNodeID());
            assertNotNull(thread.getNodeID());
            assertTrue(process.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(process));
            messagingMappingSce.getNodeSce().deleteNode(thread.getNodeID());
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
        }
    }

    @Test
    public void testTransacCreateNode2() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process", container.getContainerID(), null);
            Node thread = messagingMappingSce.getNodeSce().createNode("a thread", container.getContainerID(), process.getNodeID());
            assertNotNull(thread.getNodeID());
            assertTrue(process.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(process));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).size() == 3);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 3);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getNodeSce().deleteNode(thread.getNodeID());
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).size() == 2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 3);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 2);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testNodeProperties() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process", container.getContainerID(), null);
            process.addNodeProperty("stringProp", "a string");
            assertTrue(process.getNodeProperties().containsKey("stringProp"));
            assertTrue(process.getNodeProperties().get("stringProp").equals("a string"));
            Node bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertTrue(bis.getNodeProperties().containsKey("stringProp"));
            assertTrue(bis.getNodeProperties().get("stringProp").equals("a string"));
            process.removeNodeProperty("stringProp");
            assertFalse(process.getNodeProperties().containsKey("stringProp"));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertFalse(bis.getNodeProperties().containsKey("stringProp"));
            process.addNodeProperty("boolProp", false);
            assertTrue(process.getNodeProperties().containsKey("boolProp"));
            assertTrue(process.getNodeProperties().get("boolProp").equals(false));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertTrue(bis.getNodeProperties().containsKey("boolProp"));
            assertTrue(bis.getNodeProperties().get("boolProp").equals(false));
            process.removeNodeProperty("boolProp");
            assertFalse(process.getNodeProperties().containsKey("boolProp"));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertFalse(bis.getNodeProperties().containsKey("boolProp"));
            process.addNodeProperty("intProp", 1);
            assertTrue(process.getNodeProperties().containsKey("intProp"));
            assertTrue(process.getNodeProperties().get("intProp").equals(1));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertTrue(bis.getNodeProperties().containsKey("intProp"));
            assertTrue(bis.getNodeProperties().get("intProp").equals(1));
            process.removeNodeProperty("intProp");
            assertFalse(process.getNodeProperties().containsKey("intProp"));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertFalse(bis.getNodeProperties().containsKey("intProp"));
            process.addNodeProperty("doubleProp", 2.1);
            assertTrue(process.getNodeProperties().containsKey("doubleProp"));
            assertTrue(process.getNodeProperties().get("doubleProp").equals(2.1));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertTrue(bis.getNodeProperties().containsKey("doubleProp"));
            assertTrue(bis.getNodeProperties().get("doubleProp").equals(2.1));
            process.removeNodeProperty("doubleProp");
            assertFalse(process.getNodeProperties().containsKey("doubleProp"));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertFalse(bis.getNodeProperties().containsKey("doubleProp"));
            ArrayList<String> listProp = new ArrayList<>();
            listProp.add("test1");
            listProp.add("test2");
            process.addNodeProperty("listProp", listProp);
            assertTrue(process.getNodeProperties().containsKey("listProp"));
            assertTrue(process.getNodeProperties().get("listProp").equals(listProp));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertTrue(bis.getNodeProperties().containsKey("listProp"));
            assertTrue(bis.getNodeProperties().get("listProp").equals(listProp));
            process.removeNodeProperty("listProp");
            assertFalse(process.getNodeProperties().containsKey("listProp"));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertFalse(bis.getNodeProperties().containsKey("listProp"));
            HashMap<String, Object> mapProp = new HashMap<>();
            mapProp.put("boolVal", true);
            mapProp.put("stringVal", "test");
            process.addNodeProperty("mapProp", mapProp);
            assertTrue(process.getNodeProperties().containsKey("mapProp"));
            assertTrue(process.getNodeProperties().get("mapProp").equals(mapProp));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertTrue(bis.getNodeProperties().containsKey("mapProp"));
            assertTrue(bis.getNodeProperties().get("mapProp").equals(mapProp));
            process.removeNodeProperty("mapProp");
            assertFalse(process.getNodeProperties().containsKey("mapProp"));
            bis = messagingMappingSce.getNodeSce().getNode(process.getNodeID());
            assertFalse(bis.getNodeProperties().containsKey("mapProp"));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
        }
    }

    @Test
    public void testContainerJoinNode() {
        if (momTest!=null) {
        }
    }

    @Test
    public void testNodeJoinChildNode() {
        if (momTest!=null) {

        }
    }

    @Test
    public void testNodeJoinTwinNode() {
        if (momTest!=null) {

        }
    }

    @Test
    public void testCreateGate1() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Gate daemon = messagingMappingSce.getGateSce().createGate("tcp://myserviceurl:6969", "myservice", container.getContainerID(), false);
            assertNotNull(daemon.getNodeID());
            assertTrue(daemon.getNodeContainer().equals(container));
            assertTrue(container.getContainerGates().contains(daemon));
            assertTrue(container.getContainerNodes(0).contains(daemon));
            messagingMappingSce.getGateSce().deleteGate(daemon.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
        }
    }

    @Test
    public void testTransacCreateGate1() throws MappingDSException, InterruptedException {
        if (momTest != null) {
            Session session = messagingMappingSce.openSession("this is a test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn", "SERVER SSH DAEMON");
            Gate daemon = messagingMappingSce.getGateSce().createGate("tcp://myserviceurl:6969", "myservice", container.getContainerID(), false);
            assertNotNull(daemon.getNodeID());
            assertTrue(daemon.getNodeContainer().equals(container));
            assertTrue(container.getContainerGates().contains(daemon));
            assertTrue(container.getContainerNodes(0).contains(daemon));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).size() == 2);
            assertTrue(messagingMappingSce.getGateSce().getGates(null).size() == 2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 0);
                        assertTrue(blueprintsMappingSce.getGateSce().getGates(null).size() == 0);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 2);
                        assertTrue(blueprintsMappingSce.getGateSce().getGates(null).size() == 2);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getGateSce().deleteGate(daemon.getNodeID());
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).size() == 1);
            assertTrue(messagingMappingSce.getGateSce().getGates(null).size() == 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 2);
                        assertTrue(blueprintsMappingSce.getGateSce().getGates(null).size() == 2);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertTrue(blueprintsMappingSce.getNodeSce().getNodes(null).size() == 1);
                        assertTrue(blueprintsMappingSce.getGateSce().getGates(null).size() == 1);
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn");
            messagingMappingSce.closeSession();
        }
    }
}
