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

import com.spectral.cc.core.mapping.ds.blueprintsimpl.TopoDSCacheEntity;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.TopoDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.domain.Transport;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

public class TransportImpl implements Transport, TopoDSCacheEntity {
	
	private long   transportID     = 0;
	private String transportName   = null;	
	private Vertex transportVertex = null;
	
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
	public Vertex getElement() {
		return this.transportVertex;
	}

	@Override
	public void setElement(Element vertex) {
		this.transportVertex = (Vertex) vertex;
		this.transportVertex.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, TopoDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE);
		this.transportID = this.transportVertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
	}

	@Override
	public void synchronizeToDB() {
		if (this.transportVertex!=null) {
			this.transportVertex.setProperty(TopoDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY, this.transportName);
		}
	}

	@Override
	public void synchronizeFromDB() {
		if (this.transportVertex!=null) { 
			this.transportID   = this.transportVertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
			this.transportName = this.transportVertex.getProperty(TopoDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY);			
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