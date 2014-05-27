/**
 * MappingGraph Datastore Runtime Injectection Manager :
 * provide a Mappinggraph DS configuration parser, factories and registry to inject
 * Mapping DS interface implementation dependencies.
 *
 * Copyright (C) 2013  Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package net.echinopsii.ariane.community.core.mapping.ds.rim.factory.service;

import net.echinopsii.ariane.community.core.mapping.ds.rim.cfg.MappingDSCfgLoader;
import net.echinopsii.ariane.community.core.mapping.ds.rim.registry.MappingDSRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingSceFactory {

    private static final Logger log = LoggerFactory.getLogger(MappingSceFactory.class);
		
	public static MappingSce make(String bundleName_) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		MappingSce ret = null;
		String mappingClassName = MappingDSRegistry.getEntityFromRegistry(bundleName_).getMappingSceFactoryClassName();
        log.debug("Mapping class name to instanciate according bundle name : ({},{})", new Object[]{bundleName_,mappingClassName});
		ClassLoader loader = new MappingSceFactory().getClass().getClassLoader();
        log.debug("Class loader {} retrieved...", new Object[]{loader.toString()});
        @SuppressWarnings("unchecked")
		Class<? extends MappingSce> mappingSceClass = (Class<? extends MappingSce>) loader.loadClass(mappingClassName);
        log.debug("Class {} from class loader has been retrieved...", new Object[]{mappingClassName});
		ret = mappingSceClass.newInstance();
        log.debug("New MappingSce instance has been built : ({},{})", new Object[]{mappingClassName, (ret!=null)?ret.toString() : "null"});
		return ret;
	}
	
	public static MappingSce make() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return MappingSceFactory.make(MappingDSCfgLoader.getDefaultCfgEntity().getBundleName());
	}
}