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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface ContainerSce<C extends Container> {
    String MAPPING_CONTAINER_SERVICE_Q = "ARIANE_MAPPING_CLUSTER_SERVICE_Q";

    String CONTAINER_SCE_OP_CREATE = "createContainer";
    String CONTAINER_SCE_OP_DELETE = "deleteContainer";
    String CONTAINER_SCE_OP_GET = "getContainer";
    String CONTAINER_SCE_OP_GET_BY_PRIMARY_ADMIN_URL = "getContainerByPrimaryAdminURL";
    String CONTAINER_SCE_OP_GETS = "getContainers";

    String CONTAINER_SCE_PARAM_CONTAINER_NAME = "name";
    String CONTAINER_SCE_PARAM_CONTAINER_PAURL = "primaryAdminURL";
    String CONTAINER_SCE_PARAM_CONTAINER_PAG_NAME = "primaryAdminGateName";


    C createContainer(String primaryAdminURL, String primaryAdminGateName) throws MappingDSException;
    C createContainer(String name, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException;
    C createContainer(String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException;
    C createContainer(String name, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException;

    void deleteContainer(String primaryAdminURL) throws MappingDSException;

    C getContainer(String id) throws MappingDSException;
    C getContainerByPrimaryAdminURL(String primaryAdminURL) throws MappingDSException;

    Set<C> getContainers(String selector) throws MappingDSException;
}