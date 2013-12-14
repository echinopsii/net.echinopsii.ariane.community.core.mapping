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

package com.spectral.cc.core.mapping.ds.rim.registry;

import java.util.HashMap;

/**
 * TODO: manage multi threaded usage
 */
public class TopoDSRegistry {
	
	private static HashMap<String, TopoDSRegistryEntity> registry = new HashMap<String,TopoDSRegistryEntity>();
	
	public static void addEntityToRegistry(TopoDSRegistryEntity entity) {
		registry.put(entity.getBundleName(), entity);
	}
	
	public static void delEntityFromRegistry(String bundleName_) {
		registry.remove(bundleName_);
	}
	
	public static TopoDSRegistryEntity getEntityFromRegistry(String bundleName_) {
		return registry.get(bundleName_);
	}
	
	public static boolean containsEntity(String bundleName_) {
		return registry.containsKey(bundleName_);
	}
}