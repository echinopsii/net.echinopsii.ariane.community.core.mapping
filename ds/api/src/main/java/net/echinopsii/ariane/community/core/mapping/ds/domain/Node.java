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

import java.util.HashMap;
import java.util.Set;

public interface Node {
	public long   getNodeID();
	
	/*
	 * 	node name MUST be unique
	 */
	public String getNodeName();	
	public void   setNodeName(String name);
	
	public Container getNodeContainer();
	public void      setNodeContainer(Container container);
	
	public long getNodeDepth();
	
	public HashMap<String, Object> getNodeProperties();
	public void addNodeProperty(String propertyKey, Object value);
    public void removeNodeProperty(String propertyKey);
	
	public Node getNodeParentNode();
	public void setNodeParentNode(Node node);
	
	public Set<? extends Node> getNodeChildNodes();
	public boolean             addNodeChildNode(Node node);
	public boolean             removeNodeChildNode(Node node);

	public Set<? extends Node> getTwinNodes();
	public boolean             addTwinNode(Node node);
	public boolean             removeTwinNode(Node node);
	
	public Set<? extends Endpoint> getNodeEndpoints();
	public boolean                 addEnpoint(Endpoint endpoint);
	public boolean                 removeEndpoint(Endpoint endpoint);
}