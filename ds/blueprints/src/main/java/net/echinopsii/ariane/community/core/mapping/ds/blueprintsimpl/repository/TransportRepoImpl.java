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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.TransportImpl;
import net.echinopsii.ariane.community.core.mapping.ds.repository.TransportRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TransportRepoImpl implements TransportRepo<TransportImpl> {

    private final static Logger log = LoggerFactory.getLogger(TransportRepoImpl.class);

    public static Set<TransportImpl> getTransportRepository() throws MappingDSException {
        return MappingDSGraphDB.getTransports();
    }

    @Override
    public TransportImpl save(TransportImpl transport) {
        MappingDSGraphDB.saveVertexEntity(transport);
        log.debug("Added transport {} to graph.", new Object[]{transport.toString()});
        return transport;
    }

    @Override
    public void delete(TransportImpl transport) {
        MappingDSGraphDB.deleteEntity(transport);
        log.debug("Deleted transport {} from graph.", new Object[]{transport.toString()});
    }

    @Override
    public TransportImpl findTransportByID(String ID) throws MappingDSException {
        TransportImpl ret = null;
        log.debug("search transport for ID {}", new Object[]{ID});
        MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity(ID);
        if (entity != null) {
            if (entity instanceof TransportImpl) {
                ret = (TransportImpl) entity;
            } else {
                log.error("CONSISTENCY ERROR : entity {} is not a transport.", entity.getElement().getId());
                log.error(entity.getClass().toString());
                throw new MappingDSException("CONSISTENCY ERROR : entity " + entity.getElement().getId() + " is not a transport.");
            }
        }
        log.debug("return {}", new Object[]{(ret!=null?ret.toString():"null")});
        return ret;
    }

    @Override
    public TransportImpl findTransportByName(String name) throws MappingDSException {
        return MappingDSGraphDB.getIndexedTransport(name);
    }
}