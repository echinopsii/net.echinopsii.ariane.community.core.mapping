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

import java.util.Set;

public interface Cluster {

	String TOKEN_CL_ID = MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE+"ID";
	String TOKEN_CL_NAME = MappingDSGraphPropertyNames.DD_CLUSTER_NAME_KEY;
	String TOKEN_CL_CONT = MappingDSGraphPropertyNames.DD_CLUSTER_EDGE_CONT_KEY+"ID";

	String OP_SET_CLUSTER_NAME = "setClusterName";
	String OP_ADD_CLUSTER_CONTAINER = "addClusterContainer";
	String OP_REMOVE_CLUSTER_CONTAINER = "removeClusterContainer";

	String getClusterID();
	void   setClusterID(String ID);
	
	/*
	 * cluster name MUST be unique
	 */
	String  getClusterName();
	void    setClusterName(String name) throws MappingDSException;
	
	Set<? extends Container> getClusterContainers();
	boolean                  addClusterContainer(Container container) throws MappingDSException;
    boolean                  removeClusterContainer(Container container) throws MappingDSException;
}