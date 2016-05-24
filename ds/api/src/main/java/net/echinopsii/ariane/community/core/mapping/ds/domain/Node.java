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

public interface Node {
	public String   getNodeID();
	
	/*
	 * 	node name MUST be unique
	 */
	public String getNodeName();
	public void   setNodeName(String name) throws MappingDSException;
	
	public Container getNodeContainer();
	public void      setNodeContainer(Container container) throws MappingDSException;
	
	public long getNodeDepth();
	
	public HashMap<String, Object> getNodeProperties();
	public void addNodeProperty(String propertyKey, Object value) throws MappingDSException;
    public void removeNodeProperty(String propertyKey) throws MappingDSException;
	
	public Node getNodeParentNode();
	public void setNodeParentNode(Node node) throws MappingDSException;
	
	public Set<? extends Node> getNodeChildNodes();
	public boolean             addNodeChildNode(Node node) throws MappingDSException;
	public boolean             removeNodeChildNode(Node node) throws MappingDSException;

	public Set<? extends Node> getTwinNodes();
	public boolean             addTwinNode(Node node) throws MappingDSException;
	public boolean             removeTwinNode(Node node) throws MappingDSException;

	public Set<? extends Endpoint> getNodeEndpoints();
	public boolean addEndpoint(Session session, Endpoint endpoint) throws MappingDSException;
	public boolean addEndpoint(Endpoint endpoint) throws MappingDSException;
	public boolean                 removeEndpoint(Endpoint endpoint) throws MappingDSException;
}