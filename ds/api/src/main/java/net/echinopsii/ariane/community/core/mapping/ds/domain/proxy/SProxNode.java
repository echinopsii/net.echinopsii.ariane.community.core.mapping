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

package net.echinopsii.ariane.community.core.mapping.ds.domain.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public interface SProxNode extends Node{
	void setNodeName(Session session, String name) throws MappingDSException;
	void setNodeContainer(Session session, Container container) throws MappingDSException;
	void addNodeProperty(Session session, String propertyKey, Object value) throws MappingDSException;
	void removeNodeProperty(Session session, String propertyKey) throws MappingDSException;
	void setNodeParentNode(Session session, Node node) throws MappingDSException;
	boolean addNodeChildNode(Session session, Node node) throws MappingDSException;
	boolean removeNodeChildNode(Session session, Node node) throws MappingDSException;
	boolean addTwinNode(Session session, Node node) throws MappingDSException;
	boolean removeTwinNode(Session session, Node node) throws MappingDSException;
	boolean addEndpoint(Session session, Endpoint endpoint) throws MappingDSException;
	boolean removeEndpoint(Session session, Endpoint endpoint) throws MappingDSException;
}