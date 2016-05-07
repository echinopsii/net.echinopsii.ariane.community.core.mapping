package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cfg.MappingBlueprintsDSCfgLoader;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class MappingSceTest {

	private static MappingSce mappingSce = new MappingSceImpl(); //working for this junit test only - real instantiation should be done
																//thanks RIM or OSGI
	private static Container   rvrdLan     = null;
	private static Gate        gateLan     = null;
	private static Node        nodeLan     = null;
	private static Endpoint    endpointLan = null;
	
	private static Container   rvrdMan     = null;
	private static Gate        gateMan     = null;
	private static Node        nodeMan     = null;
	private static Endpoint    endpointMan = null;

    private static Transport   transport   = null;
	private static Link        link        = null;
	
	@BeforeClass
	public static void testSetup() throws IOException {
        Properties prop = new Properties();
        prop.load(MappingSceTest.class.getResourceAsStream("/net.echinopsii.ariane.community.core.MappingRimManagedService.properties"));
        //randomize target graphdb directory to avoid test collapse
        prop.setProperty("mapping.ds.blueprints.graphpath", ((String)prop.get("mapping.ds.blueprints.graphpath.")) + UUID.randomUUID());


        mappingSce.init(prop);
		mappingSce.start();
		String urlLan       = "http://tibrvrdl03prd01.lab01.dev.dekatonshivr.echinopsii.net:7580";
		String gateNameLan  = "webadmingate.tibrvrdl03prd01";
		String urlGLan      = "tcp-tibrvd://tibrvrdl03prd01.lab01.dev.dekatonshivr.echinopsii.net:7500";
		String gateNameGLan = "cligate.tibrvrdl03prd01";			
		String urlELan      = "tcp-tibrvrd://tibrvrdl03prd01.lab01.dev.dekatonshivr.echinopsii.net:6969";
		
		String urlMan       = "http://tibrvrdmprd01.lab01.dev.dekatonshivr.echinopsii.net:7580";
		String gateNameMan  = "webadmingate.tibrvrdmprd01";
		String urlGMan      = "tcp-tibrvd://tibrvrdmprd01.lab01.dev.dekatonshivr.echinopsii.net:7500";
		String gateNameGMan = "cligate.tibrvrdmprd01";			
		String urlEMan      = "tcp-tibrvrd://tibrvrdmprd01.lab01.dev.dekatonshivr.echinopsii.net:6969";
        String transportName= "tcp-tibrvrd";

        try {
            rvrdLan = mappingSce.getContainerSce().createContainer(urlLan, gateNameLan);
            rvrdLan.setContainerType("RV Router Daemon");
            rvrdLan.addContainerProperty("RVRD_HOSTNAME", "tibrvrdl03prd01");
            gateLan = mappingSce.getGateSce().createGate(urlGLan, gateNameGLan, rvrdLan.getContainerID(), false);
            nodeLan = mappingSce.getNodeSce().createNode("APP6969.tibrvrdl03prd01", rvrdLan.getContainerID(), (long)0);
            endpointLan = mappingSce.getEndpointSce().createEndpoint(urlELan, nodeLan.getNodeID());
            endpointLan.addEndpointProperty("RVRD_NEIGHBD_LPORT", 6969);

            rvrdMan = mappingSce.getContainerSce().createContainer(urlMan, gateNameMan);
            rvrdMan.setContainerType("RV Router Daemon");
            rvrdMan.addContainerProperty("RVRD_HOSTNAME", "tibrvrdmprd01");
            gateMan = mappingSce.getGateSce().createGate(urlGMan, gateNameGMan, rvrdMan.getContainerID(), false);
            nodeMan = mappingSce.getNodeSce().createNode("APP6969.tibrvrdmprd01", rvrdMan.getContainerID(), (long)0);
            endpointMan = mappingSce.getEndpointSce().createEndpoint(urlEMan, nodeMan.getNodeID());
            endpointMan.addEndpointProperty("RVRD_NEIGHBD_LPORT", 6969);

            transport = mappingSce.getTransportSce().createTransport(transportName);

            link = mappingSce.getLinkSce().createLink(endpointLan.getEndpointID(), endpointMan.getEndpointID(), transport.getTransportID());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@AfterClass
	public static void testCleanup() throws IOException {
		mappingSce.stop();
        if (MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath()!=null) {
            File dir = new File(MappingBlueprintsDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath());
            if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
        }
	}
	
	@Test
	public void testContainerSce1() {
		Container test = mappingSce.getContainerSce().getContainer(rvrdLan.getContainerID());
		assertTrue(rvrdLan.equals(test));
		assertTrue(!rvrdMan.equals(test));
	}

	@Test
	public void testContainerSce2() {
		Container test = mappingSce.getContainerSce().getContainer(rvrdLan.getContainerPrimaryAdminGateURL());
		assertTrue(rvrdLan.equals(test));
		assertTrue(!rvrdMan.equals(test));
	}
	
	@Test
	public void testContainerSce3() {
		assertTrue(mappingSce.getContainerSce().getContainers(null).contains(rvrdLan));
		assertTrue(mappingSce.getContainerSce().getContainers(null).contains(rvrdMan));
	}
	
	@Test
	public void testGateSce1() {
		Gate test = mappingSce.getGateSce().getGate(gateLan.getNodeID());
		assertTrue(gateLan.equals(test));
		assertTrue(!gateMan.equals(test));
	}
	
	@Test
	public void testNodeSce1() {
		Node test = mappingSce.getNodeSce().getNode(nodeLan.getNodeID());
		assertTrue(nodeLan.equals(test));
	}
	
	@Test
	public void testNodeSce2() {
		assertTrue(mappingSce.getNodeSce().getNodes(null).contains(nodeLan));
		assertTrue(mappingSce.getNodeSce().getNodes(null).contains(nodeMan));
	}

	@Test
	public void testNodeSce3() {
		String request = "nodeName = 'APP6969.tibrvrdl03prd01'";
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertTrue(mappingSce.getNodeSce().getNodes(request).size()==1);
	}

	@Test
	public void testNodeSce4() {
		String request = "nodeID >= 0";
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeMan));
	}

	@Test
	public void testNodeSce5() {
		String request = "nodeID > 0";
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeMan));
	}

	@Test
	public void testNodeSce6() {
		String request = "nodeID < 0";
		assertFalse(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertFalse(mappingSce.getNodeSce().getNodes(request).contains(nodeMan));
	}

	@Test
	public void testNodeSce7() {
		String request = "nodeID <= 0";
		assertFalse(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertFalse(mappingSce.getNodeSce().getNodes(request).contains(nodeMan));
	}

	@Test
	public void testNodeSce8() {
		String request = "nodeName =~ 'APP6969.*tibrvrdl03prd01.*'";
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertTrue(mappingSce.getNodeSce().getNodes(request).size() == 1);
	}

	@Test
	public void testNodeSce9() {
		String request = "nodeName LIKE 'APP6969.*tibrvrdl03prd01.*'";
		assertTrue(mappingSce.getNodeSce().getNodes(request).contains(nodeLan));
		assertTrue(mappingSce.getNodeSce().getNodes(request).size() == 1);
	}

	@Test
	public void testEndpointSce1() {
		Endpoint test = mappingSce.getEndpointSce().getEndpoint(endpointLan.getEndpointID());
		assertTrue(endpointLan.equals(test));
		assertTrue(!endpointMan.equals(test));
	}
	
	@Test
	public void testEndpointSce2() {
		Endpoint test = mappingSce.getEndpointSce().getEndpoint(endpointLan.getEndpointURL());
		assertTrue(endpointLan.equals(test));
		assertTrue(!endpointMan.equals(test));
	}

	@Test
	public void testEndpointSce3() {
		assertTrue(mappingSce.getEndpointSce().getEndpoints(null).contains(endpointLan));
		assertTrue(mappingSce.getEndpointSce().getEndpoints(null).contains(endpointMan));
	}

	@Test
	public void testEndpointSce4() {
		String request = "endpointURL =~ '.*tibrvrdl03prd01.*'";
		Set<Endpoint> test = (Set<Endpoint>) mappingSce.getEndpointSce().getEndpoints(request);
		assertTrue(test.contains(endpointLan));
		assertFalse(test.contains(endpointMan));
		assertTrue(test.size() == 3);
	}

	@Test
	public void testLinkSce1() {
		Link test =  mappingSce.getLinkSce().getLink(link.getLinkID());
		assertTrue(link.equals(test));
	}
	
	@Test
	public void testLinkSce2() {
		assertTrue(mappingSce.getLinkSce().getLinks(null).contains(link));
	}

	/*
	@Test
	public void testSessionOnClusterSce() throws MappingDSException {
	}
	*/

	@Test
	public void testSessionOnContainerSce() throws MappingDSException {
		String containerAdminURL = "http://container:2342/";
		String containerAdminGateName = "container.admingat";
		Session mySession = mappingSce.openSession("testClient");
		Container container = mappingSce.getContainerSce().createContainer(mySession, containerAdminURL, containerAdminGateName);
		mySession.commit();
		assertEquals(mappingSce.getContainerSce().getContainer(mySession, container.getContainerID()), container);
		mappingSce.getContainerSce().deleteContainer(mySession, containerAdminURL);
		mySession.commit();
		assertNull(mappingSce.getContainerSce().getContainer(mySession, container.getContainerID()));
		mappingSce.closeSession(mySession);
		assertFalse(mySession.isRunning());
	}

	@Test
	public void testSessionOnNodeSce() throws MappingDSException {
		String nodeName = "BPP6969.tibrvrdl03prd01";
		Session mySession = mappingSce.openSession("testClient");
		Node node = mappingSce.getNodeSce().createNode(mySession, nodeName, rvrdLan.getContainerID(), (long) 0);
		mySession.commit();
		assertEquals(mappingSce.getNodeSce().getNode(mySession, node.getNodeID()), node);
		mappingSce.getNodeSce().deleteNode(node.getNodeID());
		mySession.commit();
		assertNull(mappingSce.getNodeSce().getNode(mySession, node.getNodeID()));
		mappingSce.closeSession(mySession);
		assertFalse(mySession.isRunning());
	}

	@Test
	public void testSessionOnGateSce() throws MappingDSException {
		String gateURL = "http://someurl:someport";
		String gateName = "someGate";
		Session mySession = mappingSce.openSession("testClient");
		Gate gate = mappingSce.getGateSce().createGate(mySession, gateURL, gateName, rvrdLan.getContainerID(), false);
		mySession.commit();
		assertEquals(mappingSce.getGateSce().getGate(mySession, gate.getNodeID()), gate);
		mappingSce.getGateSce().deleteGate(mySession, gate.getNodeID());
		mySession.commit();
		assertNull(mappingSce.getGateSce().getGate(mySession, gate.getNodeID()));
		mappingSce.closeSession(mySession);
		assertFalse(mySession.isRunning());
	}

	@Test
	public void testSessionOnEndpointSce() throws MappingDSException {
		String endpointURL = "tcp://someurl:someport";
		Session mySession = mappingSce.openSession("testClient");
		Endpoint endpoint = mappingSce.getEndpointSce().createEndpoint(mySession, endpointURL, nodeLan.getNodeID());
		mySession.commit();
		assertEquals(mappingSce.getEndpointSce().getEndpoint(mySession, endpointURL), endpoint);
		mappingSce.getEndpointSce().deleteEndpoint(mySession, endpoint.getEndpointID());
		mySession.commit();
		assertNull(mappingSce.getEndpointSce().getEndpoint(mySession, endpointURL));
		mappingSce.closeSession(mySession);
		assertFalse(mySession.isRunning());
	}

	@Test
	public void testSessionOnLinkSce() throws MappingDSException {
		String endpointURL1 = "tcp://someurl1:someport";
		String endpointURL2 = "tcp://someurl2:someport";
		Session mySession = mappingSce.openSession("testClient");
		Endpoint endpoint1 = mappingSce.getEndpointSce().createEndpoint(mySession, endpointURL1, nodeLan.getNodeID());
		Endpoint endpoint2 = mappingSce.getEndpointSce().createEndpoint(mySession, endpointURL2, nodeLan.getNodeID());
		Link link1 = mappingSce.getLinkSce().createLink(mySession, endpoint1.getEndpointID(), endpoint2.getEndpointID(), transport.getTransportID());
		mySession.commit();
		assertEquals(mappingSce.getLinkSce().getLink(mySession, link1.getLinkID()), link1);
		mappingSce.getLinkSce().deleteLink(mySession, link1.getLinkID());
		mySession.commit();
		assertNull(mappingSce.getLinkSce().getLink(mySession, link1.getLinkID()));
		mappingSce.getEndpointSce().deleteEndpoint(mySession, endpoint1.getEndpointID());
		mappingSce.getEndpointSce().deleteEndpoint(mySession, endpoint2.getEndpointID());
		mySession.commit();
		mappingSce.closeSession(mySession);
		assertFalse(mySession.isRunning());
	}

	@Test
	public void testSessionOnTransportSce() throws MappingDSException {
		String transportName = "a_transport://";
		Session mySession = mappingSce.openSession("testClient");
		Transport transport = mappingSce.getTransportSce().createTransport(mySession, transportName);
		mySession.commit();
		assertEquals(mappingSce.getTransportSce().getTransport(mySession, transport.getTransportID()), transport);
		mappingSce.getTransportSce().deleteTransport(mySession, transport.getTransportID());
		mySession.commit();
		assertNull(mappingSce.getTransportSce().getTransport(mySession, transport.getTransportID()));
		mappingSce.closeSession(mySession);
		assertFalse(mySession.isRunning());
	}

	/*
	@Test
	public void testSessionOnMappingSce() throws MappingDSException {
	}
	*/
}