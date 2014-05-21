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

package net.echinopsii.ariane.core.mapping.ds.rim.factory.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.service.MappingSceImpl;
import net.echinopsii.ariane.core.mapping.ds.rim.cfg.MappingDSCfgLoader;
import net.echinopsii.ariane.core.mapping.ds.rim.registry.MappingDSRegistryService;
import net.echinopsii.ariane.core.mapping.ds.service.MappingSce;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MappingSceFactoryTest {

	@BeforeClass 
	public static void testSetup() throws JsonParseException, JsonMappingException, IOException{
		String mappingDScfgFileName = "mapping.ds.rim.cfg.json";
		InputStream   testCfgIS  = new Object().getClass().getResourceAsStream("/"+mappingDScfgFileName);
		MappingDSCfgLoader.load(testCfgIS);
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
		MappingSce mappingSce = MappingSceFactory.make(MappingDSCfgLoader.getDefaultCfgEntity().getBundleName());
		String instantiatedClassName = MappingDSRegistryService.getRegisteredMappingDS(MappingDSCfgLoader.getDefaultCfgEntity().getBundleName()).getMappingSceFactoryClassName();
		@SuppressWarnings("unchecked")
		/*
		 * the following dependency to MappingSceImpl is just for testing purpose
		 */
		Class<MappingSceImpl> instantiatedClass =  (Class<MappingSceImpl>) ClassLoader.getSystemClassLoader().loadClass(instantiatedClassName);
		assertTrue(instantiatedClass.isAssignableFrom(mappingSce.getClass()));
	}

	@Test
	public void testMake() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		MappingSce mappingSce = MappingSceFactory.make();
		String instantiatedClassName = MappingDSRegistryService.getRegisteredMappingDS(MappingDSCfgLoader.getDefaultCfgEntity().getBundleName()).getMappingSceFactoryClassName();
		@SuppressWarnings("unchecked")
		Class<MappingSceImpl> instantiatedClass =  (Class<MappingSceImpl>) ClassLoader.getSystemClassLoader().loadClass(instantiatedClassName);
		assertTrue(instantiatedClass.isAssignableFrom(mappingSce.getClass()));
	}

}