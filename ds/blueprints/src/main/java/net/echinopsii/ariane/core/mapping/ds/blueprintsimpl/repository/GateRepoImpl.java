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

package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.repository;

import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.GateImpl;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import net.echinopsii.ariane.core.mapping.ds.repository.GateRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GateRepoImpl extends NodeRepoImpl implements GateRepo<NodeImpl, GateImpl> {
    private final static Logger log = LoggerFactory.getLogger(GateRepoImpl.class);

    public static Set<GateImpl> getGateRepository() {
        return MappingDSGraphDB.getGates();
    }

    @Override
    public GateImpl save(GateImpl containerGate) {
        super.saveNode(containerGate);
        log.debug("Added gate {} to gate repository({}).", new Object[]{containerGate.toString(),});
        return containerGate;
    }

    @Override
    public void delete(GateImpl containerGate) {
        if (containerGate.getNodeID() != 0) {
            super.deleteNode(containerGate);
        } else {
            //TODO : throw exception !!!
        }
    }

    @Override
    public GateImpl findGateByID(long ID) {
        GateImpl ret = null;
        MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity(ID);
        if (entity != null) {
            if (entity instanceof GateImpl) {
                ret = (GateImpl) entity;
            } else {
                log.error("CONSISTENCY ERROR : entity {} is not a gate.", entity.getElement().getId());
            }
        }
        return ret;
    }

    @Override
    public GateImpl findGateByEndpointURL(String URL) {
        GateImpl ret = null;
        EndpointImpl ep = MappingDSGraphDB.getIndexedEndpoint(URL);
        if (ep != null) {
            ret = (GateImpl) ep.getEndpointParentNode();
        }
        return ret;
    }
}