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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBObjectProps;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransport;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransportAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

public class TransportImpl extends SProxTransportAbs implements SProxTransport, MappingDSBlueprintsCacheEntity {

    private static final Logger log = MomLoggerFactory.getLogger(TransportImpl.class);

    private transient Vertex transportVertex = null;

    @Override
    public void setTransportName(String name) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setTransportName(session, name);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getTransportName() == null || !super.getTransportName().equals(name)) {
                super.setTransportName(name);
                synchronizeToDB();
            }
        }
	}

    @Override
    public void addTransportProperty(String propertyKey, Object value) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.addTransportProperty(session, propertyKey, value);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            super.addTransportProperty(propertyKey, value);
            synchronizePropertyToDB(propertyKey, value);
            log.debug("Set transport {} property : ({},{})", new Object[]{super.getTransportID(),
                    propertyKey,
                    super.getTransportProperties().get(propertyKey)});
        }

    }

    @Override
    public void removeTransportProperty(String propertyKey) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.removeTransportProperty(session, propertyKey);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            super.removeTransportProperty(propertyKey);
            removePropertyFromDB(propertyKey);
        }
    }

    @Override
	public Vertex getElement() {
		return this.transportVertex;
	}

	@Override
	public void setElement(Element vertex) {
		this.transportVertex = (Vertex) vertex;
        if (MappingDSGraphDB.isBlueprintsNeo4j() && this.transportVertex instanceof Neo4j2Vertex)
            ((Neo4j2Vertex) this.transportVertex).addLabel(MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE);
		this.transportVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE);
		super.setTransportID((String) this.transportVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
	}

    @Override
    public String getEntityCacheID() {
        return "V" + super.getTransportID();
    }

    @Override
	public void synchronizeToDB() {
        synchronizeNameToDB();
        synchronizePropertiesToDB();
	}

    private void synchronizeNameToDB() {
        if (this.transportVertex!=null) {
            this.transportVertex.setProperty(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, super.getTransportName());
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizePropertiesToDB() {
        if (transportVertex!=null) {
            for (String key : super.getTransportProperties().keySet()) {
                Object value = super.getTransportProperties().get(key);
                synchronizePropertyToDB(key, value);
            }
        }
    }

    private void synchronizePropertyToDB(String key, Object value) {
        if (transportVertex!=null) {
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyToDB(transportVertex, key, value, MappingDSGraphPropertyNames.DD_TRANSPORT_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

	@Override
	public void synchronizeFromDB() throws MappingDSException {
        synchronizeIDFromDB();
        synchronizeNameFromDB();
        synchronizePropertiesFromDB();
	}

    private void synchronizeIDFromDB() {
        if (this.transportVertex!=null)
            super.setTransportID((String)this.transportVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
    }

    private void synchronizeNameFromDB() throws MappingDSException {
        if (this.transportVertex!=null)
            super.setTransportName((String) this.transportVertex.getProperty(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY));
    }

    private void synchronizePropertiesFromDB() {
        if (transportVertex!=null) {
            super.getTransportProperties().clear();
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(transportVertex, super.getTransportProperties(),
                    MappingDSGraphPropertyNames.DD_TRANSPORT_PROPS_KEY);
        }
    }

    private void removePropertyFromDB(String key) {
        if (transportVertex != null) {
            log.debug("Remove transport property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(transportVertex, key, MappingDSGraphPropertyNames.DD_TRANSPORT_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransportImpl tmp = (TransportImpl) o;
        if (this.getTransportID() == null) return super.equals(o);
        return (this.getTransportID().equals(tmp.getTransportID()) || super.getTransportName().equals(tmp.getTransportName()));
    }
	
    @Override
    public int hashCode() {
        return this.getTransportID() != null ? this.getTransportID().hashCode() : super.hashCode();
    }
    
	@Override
    public String toString() {
        return String.format("Transport{ID='%s', transport name='%s'}", this.getTransportID(), super.getTransportName());
    }
}