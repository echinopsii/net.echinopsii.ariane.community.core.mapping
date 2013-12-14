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

package com.spectral.cc.core.mapping.ds.rim.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TopoDSRegisteredClassType {
	CLUSTER_FACTORY ("clusterClass"),
	CONTAINER_FACTORY ("containerClass"),
	ENDPOINT_FACTORY ("endpointClass"),
	GATE_FACTORY ("gateClass"),
	LINK_FACTORY ("linkClass"),
	NODE_FACTORY ("nodeClass"),
	TRANSPORT_FACTORY ("transportClass"),
	TOPO_SCE_FACTORY ("topoSceClass");
	
	@JsonProperty("FACTORY_SCOPE")
	private final String id;
	
	TopoDSRegisteredClassType(String jsonName){
		id = jsonName;
	}

	@JsonValue
	public String getId() {
		return id;
	}
}