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

package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.domain;

import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cache.MappingDSCacheEntity;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBObjectProps;
import net.echinopsii.ariane.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.core.mapping.ds.domain.Transport;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

public class TransportImpl implements Transport, MappingDSCacheEntity {

    private static final Logger log = LoggerFactory.getLogger(TransportImpl.class);

	private long   transportID     = 0;
	private String transportName   = null;	
	private Vertex transportVertex = null;

    private HashMap<String,Object> transportProperties = null;
	
	@Override
	public long getTransportID() {
		return this.transportID;
	}

	public String getTransportName() {
		return this.transportName;
	}

	public void setTransportName(String name) {
		if (this.transportName==null || !this.transportName.equals(name)) {
			this.transportName = name;
			synchronizeToDB();
		}
	}

    @Override
    public HashMap<String, Object> getTransportProperties() {
        return transportProperties;
    }

    @Override
    public void addTransportProperty(String propertyKey, Object value) {
        if (transportProperties == null)
            transportProperties = new HashMap<String, Object>();
        transportProperties.put(propertyKey,value);
        synchronizePropertyToDB(propertyKey, value);
        log.debug("Set transport {} property : ({},{})", new Object[]{this.transportID,
                                                                             propertyKey,
                                                                             this.transportProperties.get(propertyKey)});

    }

    @Override
    public void removeTransportProperty(String propertyKey) {
        if (transportProperties!=null) {
            transportProperties.remove(propertyKey);
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
		this.transportVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE);
		this.transportID = this.transportVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
	}

	@Override
	public void synchronizeToDB() {
        synchronizeNameToDB();
        synchronizePropertiesToDB();
	}

    private void synchronizeNameToDB() {
        if (this.transportVertex!=null) {
            this.transportVertex.setProperty(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, this.transportName);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizePropertiesToDB() {
        if (transportProperties!=null && transportVertex!=null) {
            Iterator<String> iterK = this.transportProperties.keySet().iterator();
            while (iterK.hasNext()) {
                String key = iterK.next();
                Object value = transportProperties.get(key);
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
	public void synchronizeFromDB() {
        synchronizeIDFromDB();
        synchronizeNameFromDB();
        synchronizePropertiesFromDB();
	}

    private void synchronizeIDFromDB() {
        if (this.transportVertex!=null)
            this.transportID = this.transportVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
    }

    private void synchronizeNameFromDB() {
        if (this.transportVertex!=null)
            this.transportName = this.transportVertex.getProperty(MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY);
    }

    private void synchronizePropertiesFromDB() {
        if (transportVertex!=null) {
            if (transportProperties==null) {
                transportProperties=new HashMap<String,Object>();
            } else {
                transportProperties.clear();
            }
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(transportVertex, transportProperties, MappingDSGraphPropertyNames.DD_TRANSPORT_PROPS_KEY);
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
        if (this.getTransportID() == 0) return super.equals(o);
        return (this.getTransportID() == tmp.getTransportID() || this.transportName.equals(tmp.getTransportName()));
    }
	
    @Override
    public int hashCode() {
        return this.getTransportID() != 0 ? new Long(this.getTransportID()).hashCode() : super.hashCode();
    }
    
	@Override
    public String toString() {
        return String.format("Transport{ID='%d', transport name='%s'}", this.getTransportID(), this.transportName);
    }
}