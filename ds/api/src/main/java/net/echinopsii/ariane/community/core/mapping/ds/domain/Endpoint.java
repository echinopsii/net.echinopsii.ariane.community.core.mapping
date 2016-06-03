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

import java.util.HashMap;
import java.util.Set;

public interface Endpoint {
	String TOKEN_EP_ID = MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"ID";
	String TOKEN_EP_URL = MappingDSGraphPropertyNames.DD_ENDPOINT_URL_KEY;
	String TOKEN_EP_PNODEID = MappingDSGraphPropertyNames.DD_ENDPOINT_PNODE_KEY+"ID";
	String TOKEN_EP_TWNEPID = MappingDSGraphPropertyNames.DD_ENDPOINT_EDGE_TWIN_KEY+"ID";
	String TOKEN_EP_PRP = MappingDSGraphPropertyNames.DD_ENDPOINT_PROPS_KEY;

	String OP_SET_ENDPOINT_URL = "setEndpointURL";
	String OP_SET_ENDPOINT_PARENT_NODE = "setEndpointParentNode";
	String OP_ADD_ENDPOINT_PROPERTY = "addEndpointProperty";
	String OP_REMOVE_ENDPOINT_PROPERTY = "removeEndpointProperty";
	String OP_ADD_TWIN_ENDPOINT = "addTwinEndpoint";
	String OP_REMOVE_TWIN_ENDPOINT = "removeTwinEndpoint";

	String getEndpointID();
	void   setEndpointID(String ID);
	
	/*
	 * MUST be unique
	 */
	String getEndpointURL();
	void   setEndpointURL(String url) throws MappingDSException;
	
	Node getEndpointParentNode();
	void setEndpointParentNode(Node node) throws MappingDSException;

	Set<? extends Endpoint> getTwinEndpoints();
	boolean                 addTwinEndpoint(Endpoint endpoint) throws MappingDSException;
	boolean                 removeTwinEndpoint(Endpoint endpoint) throws MappingDSException;
	
	HashMap<String, Object> getEndpointProperties();
	void addEndpointProperty(String propertyKey, Object value) throws MappingDSException;
    void removeEndpointProperty(String propertyKey) throws MappingDSException;
}