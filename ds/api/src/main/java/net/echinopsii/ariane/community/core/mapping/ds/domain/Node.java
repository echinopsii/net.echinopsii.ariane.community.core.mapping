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

public interface Node {
	String TOKEN_ND_ID = MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"ID";
	String TOKEN_ND_NAME = MappingDSGraphPropertyNames.DD_NODE_NAME_KEY;
	String TOKEN_ND_DEPTH = MappingDSGraphPropertyNames.DD_NODE_DEPTH_KEY;
	String TOKEN_ND_CONID = MappingDSGraphPropertyNames.DD_NODE_CONT_KEY+"ID";
	String TOKEN_ND_PNDID = MappingDSGraphPropertyNames.DD_NODE_PNODE_KEY+"ID";
	String TOKEN_ND_CNDID = MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY+"ID";
	String TOKEN_ND_TWNID = MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY+"ID";
	String TOKEN_ND_EPSID = MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY+"ID";
	String TOKEN_ND_PRP = MappingDSGraphPropertyNames.DD_NODE_PROPS_KEY;

	String OP_SET_NODE_NAME = "setNodeName";
	String OP_SET_NODE_CONTAINER = "setNodeContainer";
	String OP_ADD_NODE_PROPERTY = "addNodeProperty";
	String OP_REMOVE_NODE_PROPERTY = "removeNodeProperty";
	String OP_SET_NODE_PARENT_NODE = "setNodeParentNode";
	String OP_ADD_NODE_CHILD_NODE = "addNodeChildNode";
	String OP_REMOVE_NODE_CHILD_NODE = "removeNodeChildNode";
	String OP_ADD_TWIN_NODE = "addTwinNode";
	String OP_REMOVE_TWIN_NODE = "removeTwinNode";
	String OP_ADD_ENDPOINT = "addEndpoint";
	String OP_REMOVE_ENDPOINT = "removeEndpoint";

	String getNodeID();
	void   setNodeID(String ID);
	
	/*
	 * 	node name MUST be unique
	 */
	String getNodeName();
	void   setNodeName(String name) throws MappingDSException;
	
	Container getNodeContainer();
	void      setNodeContainer(Container container) throws MappingDSException;
	
	long getNodeDepth();
	
	HashMap<String, Object> getNodeProperties();
	void addNodeProperty(String propertyKey, Object value) throws MappingDSException;
    void removeNodeProperty(String propertyKey) throws MappingDSException;
	
	Node getNodeParentNode();
	void setNodeParentNode(Node node) throws MappingDSException;
	
	Set<? extends Node> getNodeChildNodes();
	boolean             addNodeChildNode(Node node) throws MappingDSException;
	boolean             removeNodeChildNode(Node node) throws MappingDSException;

	Set<? extends Node> getTwinNodes();
	boolean             addTwinNode(Node node) throws MappingDSException;
	boolean             removeTwinNode(Node node) throws MappingDSException;

	Set<? extends Endpoint> getNodeEndpoints();
	boolean addEndpoint(Endpoint endpoint) throws MappingDSException;
	boolean removeEndpoint(Endpoint endpoint) throws MappingDSException;
}