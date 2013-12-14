/**
 * Mapping Datastore Runtime Injectection Manager :
 * provide a Mapping DS configuration parser, factories and registry to inject
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

package com.spectral.cc.core.mapping.ds.rim.factory.domain;

import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.rim.registry.TopoDSRegistry;

public class EndpointFactory {
	
	public static Endpoint make(String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Endpoint ret = null;
		String endpointClassName = TopoDSRegistry.getEntityFromRegistry(type).getEndpointFactoryClassName();
		ClassLoader loader = new EndpointFactory().getClass().getClassLoader();
		@SuppressWarnings("unchecked")
		Class<? extends Endpoint> endpointClass = (Class<? extends Endpoint>) loader.loadClass(endpointClassName); 
		ret = endpointClass.newInstance();
		return ret;
	}
}
