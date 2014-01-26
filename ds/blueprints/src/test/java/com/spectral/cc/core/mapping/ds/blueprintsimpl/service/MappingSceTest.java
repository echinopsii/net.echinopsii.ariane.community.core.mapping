package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.domain.*;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
	
	private static Link        link        = null;
	
	@BeforeClass 
	public static void testSetup() {
		mappingSce.init(null);
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
		
		rvrdLan = mappingSce.getContainerSce().createContainer(urlLan,gateNameLan);
		rvrdLan.setContainerType("RV Router Daemon");
		rvrdLan.setContainerProperty("RVRD_HOSTNAME", "tibrvrdl03prd01");
		gateLan = mappingSce.getGateSce().createGate(urlGLan, gateNameGLan, rvrdLan.getContainerID(), false);
		nodeLan = mappingSce.getNodeSce().createNode("APP6969.tibrvrdl03prd01", rvrdLan.getContainerID(), 0);
		endpointLan = mappingSce.getEndpointSce().createEndpoint(urlELan, nodeLan.getNodeID());
		endpointLan.setEndpointProperty("RVRD_NEIGHBD_LPORT", 6969);
		
		rvrdMan = mappingSce.getContainerSce().createContainer(urlMan,gateNameMan);
		rvrdMan.setContainerType("RV Router Daemon");
		rvrdMan.setContainerProperty("RVRD_HOSTNAME", "tibrvrdmprd01");
		gateMan = mappingSce.getGateSce().createGate(urlGMan, gateNameGMan, rvrdMan.getContainerID(), false);
		nodeMan = mappingSce.getNodeSce().createNode("APP6969.tibrvrdmprd01", rvrdMan.getContainerID(), 0);
		endpointMan = mappingSce.getEndpointSce().createEndpoint(urlEMan, nodeMan.getNodeID());
		endpointMan.setEndpointProperty("RVRD_NEIGHBD_LPORT", 6969);
		
		link = mappingSce.getLinkSce().createLink(endpointLan.getEndpointID(), endpointMan.getEndpointID(), 0, 0);
	}
	
	@AfterClass
	public static void testCleanup() {
		//MappingDSGraphDB.clear();
		//mappingSce.stop();
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
	public void testLinkSce1() {
		Link test =  mappingSce.getLinkSce().getLink(link.getLinkID());
		assertTrue(link.equals(test));
	}
	
	@Test
	public void testLinkSce2() {
		assertTrue(mappingSce.getLinkSce().getLinks(null).contains(link));
	}
}