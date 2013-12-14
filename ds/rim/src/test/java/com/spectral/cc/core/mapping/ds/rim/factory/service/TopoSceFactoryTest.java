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

package com.spectral.cc.core.mapping.ds.rim.factory.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.service.TopoSceImpl;
import com.spectral.cc.core.mapping.ds.rim.cfg.TopoDSCfgLoader;
import com.spectral.cc.core.mapping.ds.rim.factory.service.TopoSceFactory;
import com.spectral.cc.core.mapping.ds.rim.registry.TopoDSRegistryService;
import com.spectral.cc.core.mapping.ds.service.TopoSce;

public class TopoSceFactoryTest {

	@BeforeClass 
	public static void testSetup() throws JsonParseException, JsonMappingException, IOException{
		String topoDScfgFileName = "topo.ds.rim.cfg.json";
		InputStream   testCfgIS  = new Object().getClass().getResourceAsStream("/"+topoDScfgFileName);
		TopoDSCfgLoader.load(testCfgIS);
	}
	  
	@AfterClass 
	public static void testCleanup(){
	    // Nothing todo currently
	}
	
	/**
	 * TODO : 
	 * 		test on non registered bundle
	 * 		test each exceptions raised
 	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testMakeString() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		TopoSce topoSce = TopoSceFactory.make(TopoDSCfgLoader.getDefaultCfgEntity().getBundleName());		
		String instantiatedClassName = TopoDSRegistryService.getRegisteredTopoDS(TopoDSCfgLoader.getDefaultCfgEntity().getBundleName()).getTopoSceFactoryClassName();
		@SuppressWarnings("unchecked")
		/*
		 * the following dependency to TopoSceImpl is just for testing purpose 
		 */
		Class<TopoSceImpl> instantiatedClass =  (Class<TopoSceImpl>) ClassLoader.getSystemClassLoader().loadClass(instantiatedClassName);
		assertTrue(instantiatedClass.isAssignableFrom(topoSce.getClass()));
	}

	@Test
	public void testMake() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		TopoSce topoSce = TopoSceFactory.make();		
		String instantiatedClassName = TopoDSRegistryService.getRegisteredTopoDS(TopoDSCfgLoader.getDefaultCfgEntity().getBundleName()).getTopoSceFactoryClassName();
		@SuppressWarnings("unchecked")
		Class<TopoSceImpl> instantiatedClass =  (Class<TopoSceImpl>) ClassLoader.getSystemClassLoader().loadClass(instantiatedClassName);
		assertTrue(instantiatedClass.isAssignableFrom(topoSce.getClass()));
	}

}