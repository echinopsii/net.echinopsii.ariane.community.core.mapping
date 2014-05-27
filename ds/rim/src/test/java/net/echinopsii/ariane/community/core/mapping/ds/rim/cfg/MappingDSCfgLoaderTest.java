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

package net.echinopsii.ariane.community.core.mapping.ds.rim.cfg;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import net.echinopsii.ariane.community.core.mapping.ds.rim.registry.MappingDSRegistryService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MappingDSCfgLoaderTest {

	@BeforeClass 
	public static void testSetup(){
	    // Nothing todo currently
	}
	  
	@AfterClass 
	public static void testCleanup(){
	    // Nothing todo currently
	}
	
	@Test
	public void testISLoad() throws JsonParseException, JsonMappingException, IOException {
		String mappingDScfgFileName = "mapping.ds.rim.cfg.json";
		InputStream   testCfgIS  = new Object().getClass().getResourceAsStream("/"+mappingDScfgFileName);
		MappingDSCfgLoader.load(testCfgIS);
		assertTrue(MappingDSRegistryService.isRegistered(MappingDSCfgLoader.getDefaultCfgEntity().getBundleName()));
	}
}
