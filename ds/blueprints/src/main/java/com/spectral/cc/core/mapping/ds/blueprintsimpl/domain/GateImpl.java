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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.domain;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSCacheEntity;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDBException;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.domain.Endpoint;
import com.spectral.cc.core.mapping.ds.domain.Gate;
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.tinkerpop.blueprints.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateImpl extends NodeImpl implements Gate {

    private final static Logger log = LoggerFactory.getLogger(GateImpl.class);

    private EndpointImpl gatePrimaryAdminEndpoint = null;

    private boolean isBeingSyncFromDB = false;

    @Override
    public boolean isAdminPrimary() {
        return (this.gatePrimaryAdminEndpoint != null);
    }

    @Override
    public EndpointImpl getNodePrimaryAdminEndpoint() {
        return this.gatePrimaryAdminEndpoint;
    }

    @Override
    public void setNodePrimaryAdminEnpoint(Endpoint endpoint) {
        if (this.gatePrimaryAdminEndpoint == null || !this.gatePrimaryAdminEndpoint.equals(endpoint)) {
            if (endpoint instanceof EndpointImpl) {
                this.gatePrimaryAdminEndpoint = (EndpointImpl) endpoint;
            }
        }
    }

    @Override
    public void setNodeParentNode(Node node) {
        // a container gate can't have a parent node
        // as it's contained by the container
        super.setNodeParentNode(null);
        // TODO : raise exception
    }

    @Override
    public void setElement(Element vertex) {
        super.setElement(vertex);
        super.getElement().setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE);
        log.debug("Gate vertex has been initialized ({},{}).", new Object[]{super.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                   super.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    public void synchronizeToDB() throws MappingDSGraphDBException {
        super.synchronizeToDB();
        synchronizeNodePrimaryAdminEndpointToDB();
    }

    private void synchronizeNodePrimaryAdminEndpointToDB() {
        if (super.getElement() != null && this.gatePrimaryAdminEndpoint != null) {
            log.debug("Synchronize gate primary endpoint to db...", new Object[]{this.gatePrimaryAdminEndpoint.getEndpointID()});
            super.getElement().setProperty(MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY, this.gatePrimaryAdminEndpoint.getEndpointID());
        }
    }

    public void synchronizeFromDB() {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            super.synchronizeFromDB();
            synchronizeNodePrimaryAdminEndpointFromDB();
            isBeingSyncFromDB = false;
        }
    }

    private void synchronizeNodePrimaryAdminEndpointFromDB() {
        if (super.getElement() != null) {
            Object paEndpointID = super.getElement().getProperty(MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY);
            if (paEndpointID != null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) paEndpointID);
                if (entity != null) {
                    if (entity instanceof EndpointImpl) {
                        this.gatePrimaryAdminEndpoint = (EndpointImpl) entity;
                    } else {
                        log.error("DAEDALUS CONSISTENCY ERROR : entity {} is not an endpoint.", entity.getElement().getId());
                    }
                }
            }
        }
    }
}
