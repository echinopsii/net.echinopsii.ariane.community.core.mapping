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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain;

import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Vertex;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxGate;
import com.tinkerpop.blueprints.Element;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

public class GateImpl extends NodeImpl implements SProxGate {

    private final static Logger log = MomLoggerFactory.getLogger(GateImpl.class);

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
    public void setNodePrimaryAdminEnpoint(Session session, Endpoint endpoint) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, OP_SET_NODE_PRIMARY_ADMIN_ENDPOINT, new Object[]{endpoint});
    }

    @Override
    public void setNodePrimaryAdminEnpoint(Endpoint endpoint) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setNodePrimaryAdminEnpoint(session, endpoint);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (this.gatePrimaryAdminEndpoint == null || !this.gatePrimaryAdminEndpoint.equals(endpoint)) {
                if (endpoint instanceof EndpointImpl) {
                    this.gatePrimaryAdminEndpoint = (EndpointImpl) endpoint;
                    synchronizeNodePrimaryAdminEndpointToDB();
                }
            }
        }
    }

    @Override
    public void setNodeParentNode(Node node) throws MappingDSException {
        // a container gate can't have a parent node
        // as it's contained by the container
        super.setNodeParentNode(null);
        throw new MappingDSException("Modelisation error : Gate can be contained by containers only.");
    }

    @Override
    public void setElement(Element vertex) {
        super.setElement(vertex);
        if (MappingDSGraphDB.isBlueprintsNeo4j() && super.getElement() instanceof Neo4j2Vertex)
            ((Neo4j2Vertex) super.getElement()).addLabel(MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE);
        super.getElement().setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE);
        log.debug("Gate vertex has been initialized ({},{}).", new Object[]{super.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                                                                                   super.getElement().getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
    }

    public void synchronizeToDB() throws MappingDSException {
        super.synchronizeToDB();
        synchronizeNodePrimaryAdminEndpointToDB();
    }

    private void synchronizeNodePrimaryAdminEndpointToDB() {
        if (super.getElement() != null && this.gatePrimaryAdminEndpoint != null) {
            log.debug("Synchronize gate primary endpoint to db...", new Object[]{this.gatePrimaryAdminEndpoint.getEndpointID()});
            super.getElement().setProperty(MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY, this.gatePrimaryAdminEndpoint.getEndpointID());
            MappingDSGraphDB.autocommit();
        }
    }

    public void synchronizeFromDB() throws MappingDSException {
        if (!isBeingSyncFromDB) {
            isBeingSyncFromDB = true;
            super.synchronizeFromDB();
            synchronizeNodePrimaryAdminEndpointFromDB();
            isBeingSyncFromDB = false;
        }
    }

    private void synchronizeNodePrimaryAdminEndpointFromDB() throws MappingDSException {
        if (super.getElement() != null) {
            Object paEndpointID = super.getElement().getProperty(MappingDSGraphPropertyNames.DD_GATE_PAEP_KEY);
            if (paEndpointID != null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String)paEndpointID);
                if (entity != null) {
                    if (entity instanceof EndpointImpl) {
                        this.gatePrimaryAdminEndpoint = (EndpointImpl) entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not an endpoint.", entity.getElement().getId());
                    }
                }
            }
        }
    }
}
