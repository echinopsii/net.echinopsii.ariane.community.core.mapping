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

/**
 * AMQP 1.0 Containers:
 * Containers contain nodes
 * Containers have a globally unique name
 * Within a container a node name will resolve to at most one node
 * Authentication is always with respect to a container
 * Observable Container state is consistent
 *
 * @author Mathilde Ffrench
 */
public interface Container {
	String TOKEN_CT_ID = MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"ID";
	String TOKEN_CT_NAME = MappingDSGraphPropertyNames.DD_CONTAINER_NAME_KEY;
	String TOKEN_CT_COMPANY = MappingDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY;
	String TOKEN_CT_PRODUCT = MappingDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY;
	String TOKEN_CT_TYPE = MappingDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY;
	String TOKEN_CT_PAGTID = MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY+"ID";
	String TOKEN_CT_GATE_URI = MappingDSGraphPropertyNames.DD_CONTAINER_GATEURI_KEY;
	String TOKEN_CT_CLUSTER = MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY+"ID";
	String TOKEN_CT_PCID = MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY+"ID";
	String TOKEN_CT_CCID = MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY+"ID";
	String TOKEN_CT_NID = MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY+"ID";
	String TOKEN_CT_GID = MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY+"ID";
	String TOKEN_CT_PRP = MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY;

	String OP_SET_CONTAINER_NAME = "setContainerName";
	String OP_SET_CONTAINER_COMPANY = "setContainerCompany";
	String OP_SET_CONTAINER_PRODUCT = "setContainerProduct";
	String OP_SET_CONTAINER_TYPE = "setContainerType";
	String OP_SET_CONTAINER_PRIMARY_ADMIN_GATE = "setContainerPrimaryAdminGate";
	String OP_SET_CONTAINER_CLUSTER = "setContainerCluster";
	String OP_ADD_CONTAINER_PROPERTY = "addContainerProperty";
	String OP_REMOVE_CONTAINER_PROPERTY = "removeContainerProperty";
	String OP_SET_CONTAINER_PARENT_CONTAINER = "setContainerParentContainer";
	String OP_ADD_CONTAINER_CHILD_CONTAINER = "addContainerChildContainer";
	String OP_REMOVE_CONTAINER_CHILD_CONTAINER = "removeContainerChildContainer";
	String OP_ADD_CONTAINER_NODE = "addContainerNode";
	String OP_REMOVE_CONTAINER_NODE = "removeContainerNode";
	String OP_ADD_CONTAINER_GATE = "addContainerGate";
	String OP_REMOVE_CONTAINER_GATE = "removeContainerGate";

	String JOIN_PREVIOUS_CLUSTER = MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY+"Previous";
	String JOIN_CURRENT_CLUSTER = MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY+"Current";

	String getContainerID();
	void   setContainerID(String ID);

    String getContainerName();
    void setContainerName(String name) throws MappingDSException;
	
	String getContainerCompany();
    void   setContainerCompany(String company) throws MappingDSException;

    String getContainerProduct();
    void   setContainerProduct(String product) throws MappingDSException;

    String getContainerType();
	void   setContainerType(String type) throws MappingDSException;
	
	/*
	 * Primary admin URL MUST be unique. 
	 * This URL is the AMQP container globally unique name 
	 */
	String getContainerPrimaryAdminGateURL();
	Gate   getContainerPrimaryAdminGate();
	void   setContainerPrimaryAdminGate(Gate gate) throws MappingDSException;
	
	Cluster getContainerCluster();
	void    setContainerCluster(Cluster cluster) throws MappingDSException;
	
	HashMap<String, Object> getContainerProperties();
	void addContainerProperty(String propertyKey, Object value) throws MappingDSException;
    void removeContainerProperty(String propertyKey) throws MappingDSException;

    Container getContainerParentContainer();
    void      setContainerParentContainer(Container container) throws MappingDSException;

    Set<? extends Container> getContainerChildContainers();
    boolean                  addContainerChildContainer(Container container) throws MappingDSException;
    boolean                  removeContainerChildContainer(Container container) throws MappingDSException;
	
	/**
	 * 
	 * return ALL the nodes in this container until we rich the depth limit.
	 * if depth limit == 0 => NO DEPTH LIMIT 
	 * if depth limit == 1 => return only node behind the container
	 * if depth limit == 2 => return node behind the container and also node behind the first nodes 
	 * ...
	 * 
	 * @param depth : depth of nodes you want. default = 0 (means all depth)
	 * @return nodes with specified depth
	 */
	Set<? extends Node>  getContainerNodes(long depth);
	boolean              addContainerNode(Node node) throws MappingDSException;
	boolean              removeContainerNode(Node node) throws MappingDSException;
	
	Set<? extends Gate>  getContainerGates();
	boolean              addContainerGate(Gate service) throws MappingDSException;
    boolean              removeContainerGate(Gate service) throws MappingDSException;
}
