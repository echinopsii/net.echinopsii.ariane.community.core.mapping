/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.repository.ContainerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ContainerRepoImpl implements ContainerRepo<ContainerImpl> {

    private final static Logger log = LoggerFactory.getLogger(ContainerRepoImpl.class);

    public static Set<ContainerImpl> getRepository() throws MappingDSException {
        return MappingDSGraphDB.getContainers();
    }

    @Override
    public ContainerImpl save(ContainerImpl container) {
        MappingDSGraphDB.saveVertexEntity(container);
        log.debug("Added container {} to graph.", new Object[]{container.toString()});
        return container;
    }

    @Override
    public void delete(ContainerImpl container) {
        MappingDSGraphDB.deleteEntity(container);
        log.debug("Deleted container {} from graph.", new Object[]{container.toString()});
    }

    @Override
    public ContainerImpl findContainerByID(String id) throws MappingDSException {
        ContainerImpl ret = null;
        MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity(id);
        if (entity != null) {
            if (entity instanceof ContainerImpl) {
                ret = (ContainerImpl) entity;
            } else {
                log.error("CONSISTENCY ERROR : entity " + id + " is not a container.");
            }
        }
        return ret;
    }

    @Override
    public ContainerImpl findContainersByPrimaryAdminURL(String primaryAdminURL) throws MappingDSException {
        ContainerImpl ret = null;
        EndpointImpl ep = MappingDSGraphDB.getIndexedEndpoint(primaryAdminURL);
        if (ep != null) {
            ret = (ContainerImpl) ep.getEndpointParentNode().getNodeContainer();
        }
        return ret;
    }
}