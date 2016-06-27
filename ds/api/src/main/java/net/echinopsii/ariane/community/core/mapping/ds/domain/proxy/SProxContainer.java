/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016 echinopsii
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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.HashMap;
import java.util.Set;

public interface SProxContainer extends Container {
	void setContainerName(Session session, String name) throws MappingDSException;
	void setContainerCompany(Session session, String company) throws MappingDSException;
	void setContainerProduct(Session session, String product) throws MappingDSException;
	void setContainerType(Session session, String type) throws MappingDSException;
	void setContainerPrimaryAdminGate(Session session, Gate gate) throws MappingDSException;
	void setContainerCluster(Session session, Cluster cluster) throws MappingDSException;
	void addContainerProperty(Session session, String propertyKey, Object value) throws MappingDSException;
	void removeContainerProperty(Session session, String propertyKey) throws MappingDSException;
	void setContainerParentContainer(Session session, Container container) throws MappingDSException;
	boolean addContainerChildContainer(Session session, Container container) throws MappingDSException;
	boolean removeContainerChildContainer(Session session, Container container) throws MappingDSException;
	boolean addContainerNode(Session session, Node node) throws MappingDSException;
	boolean removeContainerNode(Session session, Node node) throws MappingDSException;
	boolean addContainerGate(Session session, Gate service) throws MappingDSException;
	boolean removeContainerGate(Session session, Gate service) throws MappingDSException;
}
