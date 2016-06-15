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
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
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

public class MappingMsgNATSTest {

    private static SProxMappingSce blueprintsMappingSce = new net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.MappingSceImpl();
    private static SProxMappingSce messagingMappingSce = new net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.MappingSceImpl();
    private static MappingMsgsrvBootstrap msgsrvBootstrap = new MappingMsgsrvBootstrap();

    private static MomClient momTest = null;

    @BeforeClass
    public static void testSetup() throws Exception {
        Properties natsConfig = new Properties();
        natsConfig.load(MappingMsgNATSTest.class.getResourceAsStream("/nats-test.properties"));

        momTest = MomClientFactory.make(natsConfig.getProperty(MomClient.MOM_CLI));
        try {
            momTest.init(natsConfig);
        } catch (Exception e) {
            System.err.println("No local NATS to test");
            momTest = null;
        }

        if (momTest!=null) {
            Properties mappingDSConfig = new Properties();
            mappingDSConfig.load(MappingMsgNATSTest.class.getResourceAsStream("/net.echinopsii.ariane.community.core.MappingRimManagedService-nats.properties"));
            mappingDSConfig.setProperty("mapping.ds.blueprints.graphpath", ((String) mappingDSConfig.get("mapping.ds.blueprints.graphpath")) + UUID.randomUUID());

            try {
                blueprintsMappingSce.init(mappingDSConfig);
                blueprintsMappingSce.init(mappingDSConfig);
                if (blueprintsMappingSce.start()) {
                    msgsrvBootstrap.bindMappingBSce(blueprintsMappingSce);
                    msgsrvBootstrap.updated(natsConfig);

                    messagingMappingSce.init(natsConfig);

                    msgsrvBootstrap.validate();
                    messagingMappingSce.start();
                } else throw new MappingDSException("Error while starting blueprint mappingdb");
            } catch (Exception e) {
                e.printStackTrace();
                momTest.close();
                momTest=null;
                //msgsrvBootstrap.invalidate();
                //messagingMappingSce.stop();
                //blueprintsMappingSce.stop();
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
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("testClusterCreate-test");
            assertTrue(cluster.getClusterID() != null);
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 1);
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 0);
        }
    }

    @Test
    public void testTransacClusterCreate1() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("testTransacClusterCreate1-this is a test");
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("testTransacClusterCreate1-test");
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
            Thread.sleep(2);
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
            Thread.sleep(2);
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
            Thread.sleep(2);
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
            Session session = messagingMappingSce.openSession("testTransacClusterCreate2-this is a test");
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("testTransacClusterCreate2-test");
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 1);
            session.rollback();
            assertTrue(messagingMappingSce.getClusterSce().getClusters(null).size() == 0);
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testTransacClusterGet() throws MappingDSException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("testTransacClusterGet-this is a test");
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("testTransacClusterGet-test");
            assertNotNull(messagingMappingSce.getClusterSce().getCluster(cluster.getClusterID()));
            assertNotNull(messagingMappingSce.getClusterSce().getClusterByName(cluster.getClusterName()));
            cluster.setClusterName("testTransacClusterGet-test2");
            assertEquals(cluster.getClusterName(), "testTransacClusterGet-test2");
            session.commit();
            assertNull(messagingMappingSce.getClusterSce().getClusterByName("testTransacClusterGet-test"));
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
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testCreateContainer1", "SERVER SSH DAEMON");
            assertNotNull(container.getContainerID());
            assertNull(container.getContainerName());
            container.setContainerName("a.server-testCreateContainer1");
            container.setContainerCompany("RedHat");
            container.setContainerProduct("RedHat Linux x86 7");
            container.setContainerType("Operating System");
            assertEquals(container.getContainerName(), "a.server-testCreateContainer1");
            assertEquals(container.getContainerCompany(), "RedHat");
            assertEquals(container.getContainerProduct(), "RedHat Linux x86 7");
            assertEquals(container.getContainerType(), "Operating System");
            //assertEquals(container.getContainerPrimaryAdminGateURL(), "ssh://a.server.fqdn");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateContainer1");
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(container));
        }
    }

    @Test
    public void testTransacCreateContainer1() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("testTransacCreateContainer1-this is a test");
            final Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testTransacCreateContainer1", "SERVER SSH DAEMON");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            session.commit();
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testTransacCreateContainer1");
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            session.commit();
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
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
            Container container = messagingMappingSce.getContainerSce().createContainer("a.server-testCreateContainer2", "ssh://a.server.fqdn-testCreateContainer2", "SERVER SSH DAEMON");
            assertNotNull(container.getContainerID());
            assertNotNull(container.getContainerName());
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateContainer2");
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(container));
        }
    }

    @Test
    public void testTransacCreateContainer2() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("testTransacCreateContainer2-this is a test");
            final Container container = messagingMappingSce.getContainerSce().createContainer("a.server-testTransacCreateContainer2", "ssh://a.server.fqdn-testTransacCreateContainer2", "SERVER SSH DAEMON");
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            session.commit();
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testTransacCreateContainer2");
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            session.commit();
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getContainerSce().getContainer(container.getContainerID()));
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
            Container server = messagingMappingSce.getContainerSce().createContainer("a.server-testCreateContainer3", "ssh://a.server.fqdn-testCreateContainer3", "SERVER SSH DAEMON");
            Container container = messagingMappingSce.getContainerSce().createContainer("a.container-testCreateContainer3", "ssh://a.server.fqdn:a.container-testCreateContainer3", "SERVER SSH DAEMON", server);
            assertTrue(server.getContainerChildContainers().contains(container));
            assertTrue(container.getContainerParentContainer().equals(server));
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(server));
            assertTrue(messagingMappingSce.getContainerSce().getContainers(null).contains(container));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn:a.container-testCreateContainer3");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateContainer3");
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(server));
            assertTrue(!messagingMappingSce.getContainerSce().getContainers(null).contains(server));
        }
    }

    @Test
    public void testContainerProperties() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testContainerProperties", "SERVER SSH DAEMON");
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
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testContainerProperties");
        }
    }

    @Test
    public void testClusterJoinContainer() throws MappingDSException {
        if (momTest!=null) {
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("testClusterJoinContainer-test");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testClusterJoinContainer", "SERVER SSH DAEMON");
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
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testClusterJoinContainer");
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
        }
    }

    @Test
    public void testContainerJoinContainer() throws MappingDSException {
        if (momTest!=null) {
            Container containerA = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testContainerJoinContainer", "SERVER SSH DAEMON");
            Container containerB = messagingMappingSce.getContainerSce().createContainer("ssh://b.server.fqdn-testContainerJoinContainer", "SERVER SSH DAEMON");
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
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testContainerJoinContainer");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.server.fqdn-testContainerJoinContainer");
        }
    }

    @Test
    public void testCreateNode1() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testCreateNode1", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process-testCreateNode1", container.getContainerID(), null);
            assertNotNull(process.getNodeID());
            assertTrue(process.getNodeContainer().equals(container));
            assertTrue(container.getContainerNodes().contains(process));
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateNode1");
        }
    }

    @Test
    public void testTransacCreateNode1() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test-testTransacCreateNode1");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testTransacCreateNode1", "SERVER SSH DAEMON");
            final Node process = messagingMappingSce.getNodeSce().createNode("a process-testTransacCreateNode1", container.getContainerID(), null);
            assertNotNull(process.getNodeID());
            assertTrue(process.getNodeContainer().equals(container));
            assertTrue(container.getContainerNodes().contains(process));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).contains(process));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            assertTrue(!messagingMappingSce.getNodeSce().getNodes(null).contains(process));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testTransacCreateNode1");
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testCreateNode2() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testCreateNode2", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process-testCreateNode2", container.getContainerID(), null);
            Node thread = messagingMappingSce.getNodeSce().createNode("a thread-testCreateNode2", container.getContainerID(), process.getNodeID());
            assertNotNull(thread.getNodeID());
            assertTrue(process.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(process));
            messagingMappingSce.getNodeSce().deleteNode(thread.getNodeID());
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateNode2");
        }
    }

    @Test
    public void testTransacCreateNode2() throws MappingDSException, InterruptedException {
        if (momTest!=null) {
            Session session = messagingMappingSce.openSession("this is a test-testTransacCreateNode2");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testTransacCreateNode2", "SERVER SSH DAEMON");
            final Node process = messagingMappingSce.getNodeSce().createNode("a process-testTransacCreateNode2", container.getContainerID(), null);
            final Node thread = messagingMappingSce.getNodeSce().createNode("a thread-testTransacCreateNode2", container.getContainerID(), process.getNodeID());
            assertNotNull(thread.getNodeID());
            assertTrue(process.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(process));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).contains(process));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).contains(thread));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(thread.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(thread.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            messagingMappingSce.getNodeSce().deleteNode(thread.getNodeID());
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).contains(process));
            assertTrue(!messagingMappingSce.getNodeSce().getNodes(null).contains(thread));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(thread.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(2);
            session.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(process.getNodeID()));
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(thread.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getNodeSce().deleteNode(process.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testTransacCreateNode2");
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testNodeProperties() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testNodeProperties", "SERVER SSH DAEMON");
            Node process = messagingMappingSce.getNodeSce().createNode("a process-testNodeProperties", container.getContainerID(), null);
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
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testNodeProperties");
        }
    }

    @Test
    public void testContainerJoinNode() throws MappingDSException {
        if (momTest!=null) {
            Container ahypervisor = messagingMappingSce.getContainerSce().createContainer("ssh://a.hypervisor.fqdn-testContainerJoinNode", "SERVER SSH DAEMON");
            Container bhypervisor = messagingMappingSce.getContainerSce().createContainer("ssh://b.hypervisor.fqdn-testContainerJoinNode", "SERVER SSH DAEMON");
            Node avm = messagingMappingSce.getNodeSce().createNode("a vm-testContainerJoinNode", ahypervisor.getContainerID(), null);
            assertTrue(ahypervisor.getContainerNodes().contains(avm));
            assertFalse(bhypervisor.getContainerNodes().contains(avm));
            assertTrue(avm.getNodeContainer().equals(ahypervisor));
            avm.setNodeContainer(bhypervisor);
            assertTrue(avm.getNodeContainer().equals(bhypervisor));
            assertTrue(bhypervisor.getContainerNodes().contains(avm));
            assertFalse(ahypervisor.getContainerNodes().contains(avm));
            ahypervisor.addContainerNode(avm);
            assertTrue(avm.getNodeContainer().equals(ahypervisor));
            assertTrue(ahypervisor.getContainerNodes().contains(avm));
            assertFalse(bhypervisor.getContainerNodes().contains(avm));
            ahypervisor.removeContainerNode(avm);
            assertTrue(avm.getNodeContainer()==null);
            assertFalse(ahypervisor.getContainerNodes().contains(avm));
            assertFalse(bhypervisor.getContainerNodes().contains(avm));
            avm.setNodeContainer(bhypervisor);
            assertTrue(avm.getNodeContainer().equals(bhypervisor));
            assertTrue(bhypervisor.getContainerNodes().contains(avm));
            assertFalse(ahypervisor.getContainerNodes().contains(avm));
            messagingMappingSce.getNodeSce().deleteNode(avm.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.hypervisor.fqdn-testContainerJoinNode");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.hypervisor.fqdn-testContainerJoinNode");
        }
    }

    @Test
    public void testNodeJoinChildNode() throws MappingDSException {
        if (momTest!=null) {
            Container acontainer = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testNodeJoinChildNode", "SERVER SSH DAEMON");
            Node aprocess = messagingMappingSce.getNodeSce().createNode("a process-testNodeJoinChildNode", acontainer.getContainerID(), null);
            Node thread = messagingMappingSce.getNodeSce().createNode("a thread-testNodeJoinChildNode", acontainer.getContainerID(), aprocess.getNodeID());
            assertTrue(aprocess.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(aprocess));
            aprocess.removeNodeChildNode(thread);
            assertFalse(aprocess.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode() == null);
            aprocess.addNodeChildNode(thread);
            assertTrue(aprocess.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(aprocess));
            aprocess.removeNodeChildNode(thread);
            thread.setNodeParentNode(aprocess);
            assertTrue(aprocess.getNodeChildNodes().contains(thread));
            assertTrue(thread.getNodeParentNode().equals(aprocess));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testNodeJoinChildNode");
        }
    }

    @Test
    public void testNodeJoinTwinNode() throws MappingDSException {
        if (momTest!=null) {
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test-testNodeJoinTwinNode");
            Container acontainer = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testNodeJoinTwinNode", "SERVER SSH DAEMON");
            cluster.addClusterContainer(acontainer);
            Node aprocess = messagingMappingSce.getNodeSce().createNode("a process-testNodeJoinTwinNode", acontainer.getContainerID(), null);
            Container bcontainer = messagingMappingSce.getContainerSce().createContainer("ssh://b.server.fqdn-testNodeJoinTwinNode", "SERVER SSH DAEMON");
            cluster.addClusterContainer(bcontainer);
            Node bprocess = messagingMappingSce.getNodeSce().createNode("b process-testNodeJoinTwinNode", acontainer.getContainerID(), null);
            aprocess.addTwinNode(bprocess);
            assertTrue(aprocess.getTwinNodes().contains(bprocess));
            assertTrue(bprocess.getTwinNodes().contains(aprocess));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testNodeJoinTwinNode");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.server.fqdn-testNodeJoinTwinNode");
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
        }
    }

    @Test
    public void testCreateGate1() throws MappingDSException {
        if (momTest!=null) {
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testCreateGate1", "SERVER SSH DAEMON");
            Gate daemon = messagingMappingSce.getGateSce().createGate("tcp://myserviceurl-testCreateGate1:6969", "myservice-testCreateGate1", container.getContainerID(), false);
            assertNotNull(daemon.getNodeID());
            assertTrue(daemon.getNodeContainer().equals(container));
            assertTrue(container.getContainerGates().contains(daemon));
            assertTrue(container.getContainerNodes().contains(daemon));
            messagingMappingSce.getGateSce().deleteGate(daemon.getNodeID());
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateGate1");
        }
    }

    @Test
    public void testTransacCreateGate1() throws MappingDSException, InterruptedException {
        if (momTest != null) {
            Session session = messagingMappingSce.openSession("this is a test-testTransacCreateGate1");
            Container container = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testTransacCreateGate1", "SERVER SSH DAEMON");
            final Gate daemon = messagingMappingSce.getGateSce().createGate("tcp://myserviceurl-testTransacCreateGate1:6969", "myservice-testTransacCreateGate1", container.getContainerID(), false);
            assertNotNull(daemon.getNodeID());
            assertTrue(daemon.getNodeContainer().equals(container));
            assertTrue(container.getContainerGates().contains(daemon));
            assertTrue(container.getContainerNodes().contains(daemon));
            assertTrue(messagingMappingSce.getNodeSce().getNodes(null).contains(daemon));
            assertTrue(messagingMappingSce.getGateSce().getGates(null).contains(daemon));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(daemon.getNodeID()));
                        assertNull(blueprintsMappingSce.getGateSce().getGate(daemon.getNodeID()));
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
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(daemon.getNodeID()));
                        assertNotNull(blueprintsMappingSce.getGateSce().getGate(daemon.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1);
            messagingMappingSce.getGateSce().deleteGate(daemon.getNodeID());
            assertTrue(!messagingMappingSce.getNodeSce().getNodes(null).contains(daemon));
            assertTrue(!messagingMappingSce.getGateSce().getGates(null).contains(daemon));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        assertNotNull(blueprintsMappingSce.getNodeSce().getNode(daemon.getNodeID()));
                        assertNotNull(blueprintsMappingSce.getGateSce().getGate(daemon.getNodeID()));
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
                        assertNull(blueprintsMappingSce.getNodeSce().getNode(daemon.getNodeID()));
                        assertNull(blueprintsMappingSce.getGateSce().getGate(daemon.getNodeID()));
                    } catch (MappingDSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testTransacCreateGate1");
            messagingMappingSce.closeSession();
        }
    }

    @Test
    public void testCreateEndpointAndJoinNode() throws MappingDSException {
        if (momTest!=null) {
            Container acontainer = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testCreateEndpointAndJoinNode", "SERVER SSH DAEMON");
            Node aprocess = messagingMappingSce.getNodeSce().createNode("a process-testCreateEndpointAndJoinNode", acontainer.getContainerID(), null);
            Endpoint endpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testCreateEndpointAndJoinNode:1234", aprocess.getNodeID());
            assertTrue(endpoint.getEndpointID()!=null);
            assertTrue(endpoint.getEndpointParentNode().equals(aprocess));
            assertTrue(aprocess.getNodeEndpoints().contains(endpoint));
            assertTrue(endpoint.getEndpointURL().equals("tcp://process-endpoint-testCreateEndpointAndJoinNode:1234"));
            endpoint.setEndpointURL("tcp://process-endpoint-testCreateEndpointAndJoinNode:2345");
            assertTrue(endpoint.getEndpointURL().equals("tcp://process-endpoint-testCreateEndpointAndJoinNode:2345"));
            aprocess.removeEndpoint(endpoint);
            assertTrue(!aprocess.getNodeEndpoints().contains(endpoint));
            assertTrue(endpoint.getEndpointParentNode() == null);
            aprocess.addEndpoint(endpoint);
            assertTrue(aprocess.getNodeEndpoints().contains(endpoint));
            assertTrue(endpoint.getEndpointParentNode().equals(aprocess));
            aprocess.removeEndpoint(endpoint);
            assertTrue(!aprocess.getNodeEndpoints().contains(endpoint));
            assertTrue(endpoint.getEndpointParentNode() == null);
            endpoint.setEndpointParentNode(aprocess);
            assertTrue(aprocess.getNodeEndpoints().contains(endpoint));
            assertTrue(endpoint.getEndpointParentNode().equals(aprocess));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testCreateEndpointAndJoinNode");
        }
    }

    @Test
    public void testEndpointJoinTwinEP() throws MappingDSException {
        if (momTest!=null) {
            Cluster cluster = messagingMappingSce.getClusterSce().createCluster("test-testEndpointJoinTwinEP");
            Container acontainer = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testEndpointJoinTwinEP", "SERVER SSH DAEMON");
            cluster.addClusterContainer(acontainer);
            Node aprocess = messagingMappingSce.getNodeSce().createNode("a process-testEndpointJoinTwinEP", acontainer.getContainerID(), null);
            Container bcontainer = messagingMappingSce.getContainerSce().createContainer("ssh://b.server.fqdn-testEndpointJoinTwinEP", "SERVER SSH DAEMON");
            cluster.addClusterContainer(bcontainer);
            Node bprocess = messagingMappingSce.getNodeSce().createNode("b process-testEndpointJoinTwinEP", acontainer.getContainerID(), null);
            aprocess.addTwinNode(bprocess);
            Endpoint aendpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testEndpointJoinTwinEP:1234", aprocess.getNodeID());
            Endpoint bendpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testEndpointJoinTwinEP:2345", bprocess.getNodeID());
            aendpoint.addTwinEndpoint(bendpoint);
            assertTrue(aendpoint.getTwinEndpoints().contains(bendpoint));
            assertTrue(bendpoint.getTwinEndpoints().contains(aendpoint));
            bendpoint.removeTwinEndpoint(aendpoint);
            assertTrue(!aendpoint.getTwinEndpoints().contains(bendpoint));
            assertTrue(!bendpoint.getTwinEndpoints().contains(aendpoint));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testEndpointJoinTwinEP");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.server.fqdn-testEndpointJoinTwinEP");
            messagingMappingSce.getClusterSce().deleteCluster(cluster.getClusterName());
        }
    }

    @Test
    public void testUnicastLink() throws MappingDSException {
        if (momTest!=null) {
            Container acontainer = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testUnicastLink", "SERVER SSH DAEMON");
            Node aprocess = messagingMappingSce.getNodeSce().createNode("a process-testUnicastLink", acontainer.getContainerID(), null);
            Container bcontainer = messagingMappingSce.getContainerSce().createContainer("ssh://b.server.fqdn-testUnicastLink", "SERVER SSH DAEMON");
            Node bprocess = messagingMappingSce.getNodeSce().createNode("b process-testUnicastLink", acontainer.getContainerID(), null);
            Endpoint aendpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testUnicastLink:1234", aprocess.getNodeID());
            Endpoint bendpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testUnicastLink:2345", bprocess.getNodeID());
            Transport transport = messagingMappingSce.getTransportSce().createTransport("tcp-testUnicastLink://");
            Link link = messagingMappingSce.getLinkSce().createLink(aendpoint.getEndpointID(), bendpoint.getEndpointID(), transport.getTransportID());
            assertTrue(link.getLinkEndpointSource().equals(aendpoint));
            assertTrue(link.getLinkEndpointTarget().equals(bendpoint));
            assertTrue(link.getLinkTransport().equals(transport));
            assertTrue(messagingMappingSce.getLinkSce().getLinks(null).contains(link));
            assertTrue(messagingMappingSce.getTransportSce().getTransports(null).contains(transport));
            messagingMappingSce.getLinkSce().deleteLink(link.getLinkID());
            messagingMappingSce.getTransportSce().deleteTransport(transport.getTransportID());
            assertTrue(!messagingMappingSce.getLinkSce().getLinks(null).contains(link));
            assertTrue(!messagingMappingSce.getTransportSce().getTransports(null).contains(transport));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testUnicastLink");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.server.fqdn-testUnicastLink");
        }
    }

    @Test
    public void testMulticastLink() throws MappingDSException {
        if (momTest!=null) {
            Container acontainer = messagingMappingSce.getContainerSce().createContainer("ssh://a.server.fqdn-testMulticastLink", "SERVER SSH DAEMON");
            Node aprocess = messagingMappingSce.getNodeSce().createNode("a process-testUnicastLink", acontainer.getContainerID(), null);
            Container bcontainer = messagingMappingSce.getContainerSce().createContainer("ssh://b.server.fqdn-testMulticastLink", "SERVER SSH DAEMON");
            Node bprocess = messagingMappingSce.getNodeSce().createNode("b process-testUnicastLink", acontainer.getContainerID(), null);
            Endpoint aendpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testMulticastLink:1234", aprocess.getNodeID());
            Endpoint bendpoint = messagingMappingSce.getEndpointSce().createEndpoint("tcp://process-endpoint-testMulticastLink:2345", bprocess.getNodeID());
            Transport transport = messagingMappingSce.getTransportSce().createTransport("multicast-udp-testMulticastLink://");
            Link alink = messagingMappingSce.getLinkSce().createLink(aendpoint.getEndpointID(), null, transport.getTransportID());
            assertTrue(alink.getLinkEndpointSource().equals(aendpoint));
            assertNull(alink.getLinkEndpointTarget());
            assertTrue(alink.getLinkTransport().equals(transport));
            Link blink = messagingMappingSce.getLinkSce().createLink(bendpoint.getEndpointID(), null, transport.getTransportID());
            assertTrue(blink.getLinkEndpointSource().equals(bendpoint));
            assertNull(blink.getLinkEndpointTarget());
            assertTrue(blink.getLinkTransport().equals(transport));
            assertTrue(messagingMappingSce.getLinkSce().getLinks(null).contains(alink));
            assertTrue(messagingMappingSce.getLinkSce().getLinks(null).contains(blink));
            assertTrue(messagingMappingSce.getTransportSce().getTransports(null).contains(transport));
            messagingMappingSce.getLinkSce().deleteLink(alink.getLinkID());
            messagingMappingSce.getLinkSce().deleteLink(blink.getLinkID());
            messagingMappingSce.getTransportSce().deleteTransport(transport.getTransportID());
            assertTrue(!messagingMappingSce.getLinkSce().getLinks(null).contains(alink));
            assertTrue(!messagingMappingSce.getLinkSce().getLinks(null).contains(blink));
            assertTrue(!messagingMappingSce.getTransportSce().getTransports(null).contains(transport));
            messagingMappingSce.getContainerSce().deleteContainer("ssh://a.server.fqdn-testMulticastLink");
            messagingMappingSce.getContainerSce().deleteContainer("ssh://b.server.fqdn-testMulticastLink");
        }
    }
}
