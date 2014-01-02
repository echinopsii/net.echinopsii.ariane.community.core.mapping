/**
 * TopoGraph Datastore Runtime Injectection Manager : 
 * provide a Topograph DS configuration parser, factories and registry to inject
 * Topo DS interface implementation dependencies.
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

package com.spectral.cc.core.mapping.ds.rim.factory.service;

import com.spectral.cc.core.mapping.ds.rim.cfg.TopoDSCfgLoader;
import com.spectral.cc.core.mapping.ds.rim.registry.TopoDSRegistry;
import com.spectral.cc.core.mapping.ds.service.TopoSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoSceFactory {

    private static final Logger log = LoggerFactory.getLogger(TopoSceFactory.class);
		
	public static TopoSce make(String bundleName_) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		TopoSce ret = null;
		String topoClassName = TopoDSRegistry.getEntityFromRegistry(bundleName_).getTopoSceFactoryClassName();
        log.debug("Topo class name to instanciate according bundle name : ({},{})", new Object[]{bundleName_,topoClassName});
		ClassLoader loader = new TopoSceFactory().getClass().getClassLoader();
        log.debug("Class loader {} retrieved...", new Object[]{loader.toString()});
        @SuppressWarnings("unchecked")
		Class<? extends TopoSce> topoSceClass = (Class<? extends TopoSce>) loader.loadClass(topoClassName);
        log.debug("Class {} from class loader has been retrieved...", new Object[]{topoClassName});
		ret = topoSceClass.newInstance();
        log.debug("New TopoSce instance has been built : ({},{})", new Object[]{topoClassName, (ret!=null)?ret.toString() : "null"});
		return ret;
	}
	
	public static TopoSce make() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return TopoSceFactory.make(TopoDSCfgLoader.getDefaultCfgEntity().getBundleName());
	}
}