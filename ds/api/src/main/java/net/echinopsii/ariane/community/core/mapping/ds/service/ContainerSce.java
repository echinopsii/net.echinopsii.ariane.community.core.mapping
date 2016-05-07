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

package net.echinopsii.ariane.community.core.mapping.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;

import java.util.Set;

public interface ContainerSce<C extends Container> {
    public final static String CREATE_CONTAINER = "createContainer";
    public C createContainer(String primaryAdminURL, String primaryAdminGateName) throws MappingDSException;

    public C createContainer(String name, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException;

    public C createContainer(String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException;

    public C createContainer(String name, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException;

    public final static String DELETE_CONTAINER = "deleteContainer";
    public void deleteContainer(String primaryAdminURL) throws MappingDSException;

    public final static String GET_CONTAINER = "getContainer";
    public C getContainer(Long id);

    public C getContainer(String primaryAdminURL);

    public final static String GET_CONTAINERS = "getContainers";
    public Set<C> getContainers(String selector);
}