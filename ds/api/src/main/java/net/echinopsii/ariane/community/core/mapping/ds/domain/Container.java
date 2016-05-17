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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

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
	public String getContainerID();

    public String getContainerName();
	public void setContainerName(Session session, String name) throws MappingDSException;
    public void setContainerName(String name);
	
	public String getContainerCompany();
	public void   setContainerCompany(Session session, String company) throws MappingDSException;
    public void   setContainerCompany(String company);

    public String getContainerProduct();
	public void   setContainerProduct(Session session, String product) throws MappingDSException;
    public void   setContainerProduct(String product);

    public String getContainerType();
	public void   setContainerType(Session session, String type) throws MappingDSException;
	public void   setContainerType(String type);
	
	/*
	 * Primary admin URL MUST be unique. 
	 * This URL is the AMQP container globally unique name 
	 */
	public String getContainerPrimaryAdminGateURL();	
	public Gate   getContainerPrimaryAdminGate();
	public void   setContainerPrimaryAdminGate(Session session, Gate gate) throws MappingDSException;
	public void   setContainerPrimaryAdminGate(Gate gate);
	
	public Cluster getContainerCluster();
	public void    setContainerCluster(Session session, Cluster cluster) throws MappingDSException;
	public void    setContainerCluster(Cluster cluster);
	
	public HashMap<String, Object> getContainerProperties();
	public void addContainerProperty(Session session, String propertyKey, Object value) throws MappingDSException;
	public void addContainerProperty(String propertyKey, Object value);
	public void removeContainerProperty(Session session, String propertyKey) throws MappingDSException;
    public void removeContainerProperty(String propertyKey);

    public Container getContainerParentContainer();
	public void      setContainerParentContainer(Session session, Container container) throws MappingDSException;
    public void      setContainerParentContainer(Container container);

    public Set<? extends Container> getContainerChildContainers();
	public boolean                  addContainerChildContainer(Session session, Container container) throws MappingDSException;
    public boolean                  addContainerChildContainer(Container container);
	public boolean                  removeContainerChildContainer(Session session, Container container) throws MappingDSException;
    public boolean                  removeContainerChildContainer(Container container);
	
	/**
	 * 
	 * return ALL the nodes in this container until we rich the depth limit.
	 * if depth limit == 0 => NO DEPTH LIMIT 
	 * if depth limit == 1 => return only node behind the container
	 * if depth limit == 2 => return node behind the container and also node behind the first nodes 
	 * ...
	 * 
	 * @param depth
	 * @return
	 */
	public Set<? extends Node>  getContainerNodes(long depth);
	public boolean              addContainerNode(Session session, Node node) throws MappingDSException;
	public boolean              addContainerNode(Node node);
	public boolean              removeContainerNode(Session session, Node node) throws MappingDSException;
	public boolean              removeContainerNode(Node node);
	
	public Set<? extends Gate>  getContainerGates();
	public boolean              addContainerGate(Session session, Gate service) throws MappingDSException;
	public boolean              addContainerGate(Gate service);
	public boolean              removeContainerGate(Session session, Gate service) throws MappingDSException;
    public boolean              removeContainerGate(Gate service);
}
