/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
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

package net.echinopsii.ariane.community.core.mapping.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;

public interface Gate extends Node {
	String TOKEN_GT_ADMPEP = MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY+"ID";
	String TOKEN_GT_NODE = MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE;

	String OP_GET_NODE_PRIMARY_ADMIN_ENDPOINT = "getNodePrimaryAdminEndpoint";
	String OP_SET_NODE_PRIMARY_ADMIN_ENDPOINT = "setNodePrimaryAdminEndpoint";

	String JOIN_PREVIOUS_PAEP = MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY+"Previous";
	String JOIN_CURRENT_PAEP = MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY+"Current";

	boolean isAdminPrimary();
	
	Endpoint getNodePrimaryAdminEndpoint();

	void     setNodePrimaryAdminEnpoint(Endpoint endpoint) throws MappingDSException;
}