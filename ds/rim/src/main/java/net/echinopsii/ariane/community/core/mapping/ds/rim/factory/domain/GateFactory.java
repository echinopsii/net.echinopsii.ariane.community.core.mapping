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

package net.echinopsii.ariane.community.core.mapping.ds.rim.factory.domain;

import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.rim.registry.MappingDSRegistry;

public class GateFactory {

	public static Gate make(String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Gate ret = null;
		String gateClassName = MappingDSRegistry.getEntityFromRegistry(type).getTransportFactoryClassName();
		ClassLoader loader = new GateFactory().getClass().getClassLoader();
		@SuppressWarnings("unchecked")
		Class<? extends Gate> gateClass = (Class<? extends Gate>) loader.loadClass(gateClassName); 
		ret = gateClass.newInstance();
		return ret;
	}
}